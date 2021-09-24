/*******************************************************************************
 * Copyright © 2019 by California Community Colleges Chancellor's Office
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.ccctc.common.droolsdb.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccctc.common.droolscommon.KieContainerFactory;
import org.ccctc.common.droolscommon.RulesAction;
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolscommon.model.RuleSetRowDTO;
import org.ccctc.common.droolsdb.TestConfig;
import org.ccctc.common.droolsdb.dynamodb.services.DynamoDBTest;
import org.ccctc.common.droolsdb.model.RuleDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.command.Command;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.internal.command.CommandFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;

@RunWith(SpringRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { TestConfig.class })
public class DrlServiceTest extends DynamoDBTest {
    private final static String DEFAULT_RULE_NAME = "/sampleRuleWithVariables.json";

    private static final String DEFAULT_RULE2_NAME = "/sampleRule2.json";

    private static final String DEFAULT_RULESET_NAME = "/sampleRuleSetWithVariables.json";

    private static final String DEFAULT_RULESETROW_NAME = "/sampleRuleSetRowWithVariables.json";

    @Autowired
    private KieContainerFactory containerFactory;

    @Autowired
    private DrlService drlService;

    private DynamoDBProxyServer server;

    @Test
    public void buildTitle() {
        RuleDTO ruleDTO = this.readObjectFromFile(DEFAULT_RULE_NAME, RuleDTO.class);
        StringBuffer title1 = drlService.buildTitle(ruleDTO);
        assertEquals("rule \"" + ruleDTO.getTitle() + ":" + ruleDTO.getId(), title1.toString());
        StringBuffer title2 = drlService.buildTitle(ruleDTO, "smith");
        assertEquals("rule \"" + ruleDTO.getTitle() + ":" + ruleDTO.getId() + ":smith", title2.toString());

        StringBuffer title3 = drlService.buildTitle(ruleDTO, "name", "rowName", "ruleSetId", "ruleSetRowId", 1);
        assertEquals("rule \"" + ruleDTO.getTitle() + ":" + ruleDTO.getId() + ":name" + ":rowname-rowName"
                        + ":rulesetrowid-ruleSetRowId" + ":rulesetid-ruleSetId:" + 1, title3.toString());
    }

    private List<RulesAction> executeRules(KieContainer kContainer, Map<String, Object> facts) {
        StatelessKieSession kSession = kContainer.newStatelessKieSession();
        List<Command<?>> cmds = new ArrayList<Command<?>>();
        cmds.add(CommandFactory.newInsert(facts, "myfacts"));
        cmds.add(CommandFactory.newFireAllRules());
        cmds.add(CommandFactory.newQuery("actions", "actions"));
        ExecutionResults results = kSession.execute(CommandFactory.newBatchExecution(cmds));

        List<RulesAction> actions = new ArrayList<RulesAction>();
        QueryResults queryResults = (QueryResults) results.getValue("actions");
        for (QueryResultsRow row : queryResults) {
            Object obj = row.get("rulesActions");
            if (obj instanceof RulesAction) {
                RulesAction action = (RulesAction) obj;
                actions.add(action);
            } else {
                fail("[actions] query returned a value that was not a RulesAction");
            }
        }
        return actions;
    }

    @Test
    public void generateAndTestValidRuleDrl() {
        RuleSetRowDTO ruleSetRowDTO = this.readObjectFromFile(DEFAULT_RULESETROW_NAME, RuleSetRowDTO.class);
        RuleDTO ruleDTO = this.readObjectFromFile(DEFAULT_RULE_NAME, RuleDTO.class);
        StringBuffer drl = drlService.generateDrl(ruleDTO, ruleSetRowDTO.getVariableRows());

        KieContainer kContainer = containerFactory.createKieContainerFromDrlString(drl.toString());
        Map<String, Object> facts = initializeFacts();
        List<RulesAction> actions = this.executeRules(kContainer, facts);

        assertEquals(1, actions.size());
        RulesAction action = actions.get(0);
        assertEquals("MESSAGE", action.getActionName());
    }

    @Test
    public void generateAndTestValidRulesetDrl() {
        RuleSetDTO ruleSetDTO = this.readObjectFromFile(DEFAULT_RULESET_NAME, RuleSetDTO.class);
        StringBuffer drl = drlService.generateDrl(ruleSetDTO);
        KieContainer kContainer = containerFactory.createKieContainerFromDrlString(drl.toString());
        Map<String, Object> facts = initializeFacts();
        List<RulesAction> actions = this.executeRules(kContainer, facts);

        assertEquals(1, actions.size());
        RulesAction action = actions.get(0);
        assertEquals("MESSAGE", action.getActionName());
    }

    @Test
    public void generateAndTestValidRulesetDrlRuleNoMatch() {
        RuleSetDTO ruleSetDTO = this.readObjectFromFile(DEFAULT_RULESET_NAME, RuleSetDTO.class);
        StringBuffer drl = drlService.generateDrl(ruleSetDTO);
        KieContainer kContainer = containerFactory.createKieContainerFromDrlString(drl.toString());
        Map<String, Object> facts = initializeFacts();
        facts.put("cccid", "nomatchCCCID");
        List<RulesAction> actions = this.executeRules(kContainer, facts);

        assertEquals(0, actions.size());
    }

    private Map<String, Object> initializeFacts() {
        Map<String, Object> attributeMap = new HashMap<String, Object>();
        attributeMap.put("veterans_services", true);
        Map<String, Object> attrs = new HashMap<String, Object>();
        attrs.put("attributeMap", attributeMap);
        Map<String, Object> studentProfile = new HashMap<String, Object>();
        studentProfile.put("attrs", attrs);
        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put("studentProfile", studentProfile);
        facts.put("cccid", "AAA4444");
        facts.put("age", 1.5);
        return facts;
    }

    @Before
    public void setup() {
        try {
            loadRule(DEFAULT_RULE_NAME);
            loadRule(DEFAULT_RULE2_NAME);
            loadRuleSetRow(DEFAULT_RULESETROW_NAME);
            loadRuleSet(DEFAULT_RULESET_NAME);
        }
        catch (Exception e) {
            fail("Setup failed:[" + e.getMessage() + "]");
        }
    }

    @After
    public void teardown() {
        if (server != null) {
            try {
                server.stop();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
