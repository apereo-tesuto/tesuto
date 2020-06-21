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
package org.ccctc.common.droolsengine.engine.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccctc.common.droolscommon.RulesAction;
import org.ccctc.common.droolscommon.action.result.ActionResult;
import org.ccctc.common.droolscommon.action.result.ErrorActionResult;
import org.ccctc.common.droolsengine.config.TestingConfiguration;
import org.ccctc.common.droolsengine.engine_actions.service.NoOpActionService;
import org.ccctc.common.droolsengine.facts.StudentProfileFacade;
import org.ccctc.common.droolsengine.facts.StudentProfileFactsPreProcessor;
import org.ccctc.common.droolsengine.utils.FactsUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { TestingConfiguration.class, StudentProfileFacade.class,
                               NoOpActionService.class, StudentProfileFactsPreProcessor.class })
@TestPropertySource("classpath:application-test.properties")
public class DroolsRulesServiceTest {
    @Autowired
    private DroolsRulesService rulesService;

    @Test
    public void executeEmptyFacts() {
        // This test will attempt to excute but will fail the student profile validator.
        Map<String, Object> facts = new HashMap<String, Object>();
        List<ActionResult> results = rulesService.execute(facts);
        assertEquals(1, results.size());
        ActionResult result = results.get(0);
        assertEquals(result.getActionName(), true, result instanceof ErrorActionResult);
    }

    @Test
    public void executeFailValidator() {
        // This test will attempt to execute but should fail the student profile validator.
        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put(FactsUtils.MISCODE_FIELD, "ZZ1");
        List<ActionResult> errors = rulesService.execute(facts);
        assertEquals(1, errors.size());
        ActionResult result = errors.get(0);
        assertEquals(result.getActionName(), true, result instanceof ErrorActionResult);
        assertEquals("FAILED_ON_PRE_PROCESS", result.getActionName());
        assertEquals("Cannot retrieve studentProfile with a blank cccid", result.getMessage());
    }

    @Test
    public void executeMatchIsVeteran() {
        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put(FactsUtils.MISCODE_FIELD, "ZZ1");
        facts.put(FactsUtils.CCCID_FIELD, "AAA5364");
        Map<String, Object> studentProfile = new HashMap<String, Object>();
        Map<String, Object> attrs = new HashMap<String, Object>();
        Map<String, Object> attributeMap = new HashMap<String, Object>();
        attributeMap.put("veterans_services", true);
        attrs.put("attributeMap", attributeMap);
        studentProfile.put("attrs", attrs);
        facts.put(FactsUtils.STUDENT_PROFILE_FIELD, studentProfile);
        List<ActionResult> results = rulesService.execute(facts);
        // this rule attempts to run four RulesActions
        // for this test, the actions are not configured and will fail
        assertEquals(2, results.size());
    }

    @Test
    public void executeNoCccMisCode() {
        // This test will attempt to execute but will fail the student profile validator.
        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put(FactsUtils.CCCID_FIELD, "AAA5364");
        List<ActionResult> results = rulesService.execute(facts);
        assertEquals(1, results.size());
        ActionResult result = results.get(0);
        assertEquals(result.getActionName(), true, result instanceof ErrorActionResult);
    }

    @Test
    public void executeNominal() {
        // This test will result in no actions, but it has enough data
        // to compare the facts to the rules loaded by the properties file.
        // The StudentProfileValidator is enabled, so the MISCode and CCCID are
        // required. After the Rules Engine executes, the list of actions
        // will be empty, and no errors have occurred.
        Map<String, Object> facts = new HashMap<String, Object>();
        facts.put(FactsUtils.MISCODE_FIELD, "ZZ1");
        facts.put(FactsUtils.CCCID_FIELD, "AAA5364");
        Map<String, Object> studentProfile = new HashMap<String, Object>();
        facts.put(FactsUtils.STUDENT_PROFILE_FIELD, studentProfile);
        List<ActionResult> results = rulesService.execute(facts);
        assertEquals(1, results.size());
        assertEquals("DroolsRulesService.takeActions(..)", results.get(0).getActionName());
        assertEquals("NO_ACTIONS_FOUND", results.get(0).getMessage());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void getInfo() {
        Map<String, Object> info = rulesService.getInfo();
        assertEquals(info.get("name"), "sns-listener");
        Collection actions = (Collection) info.get("actions");
        assertTrue(actions.contains("NOOP"));
        Collection preprocessors = (Collection) info.get("preprocessors");
        assertTrue(preprocessors.contains("NOOP"));
        assertTrue(preprocessors.contains("STUDENT_PROFILE"));
        assertEquals(info.get("status"), DroolsEngineStatus.ENABLED);
    }

    @Test
    public void takeActionNotFound() {
        Map<String, Object> facts = new HashMap<String, Object>();
        RulesAction action = new RulesAction("BAD-ACTION");
        List<ActionResult> errors = rulesService.takeAction(action, facts);
        assertEquals(1, errors.size());
        ActionResult error = errors.get(0);
        assertTrue(error instanceof ErrorActionResult);
        assertTrue("Error Message Incorrect:" + error.getActionName(), error.getMessage().contains("Service not found for action [BAD-ACTION]"));
    }

    @Test
    public void takeActionsBadAction() {
        Map<String, Object> facts = new HashMap<String, Object>();
        RulesAction action = new RulesAction("BAD_ACTION");
        List<RulesAction> rulesActions = new ArrayList<RulesAction>();
        rulesActions.add(action);
        List<ActionResult> errors = rulesService.takeActions(rulesActions, facts);
        assertEquals(1, errors.size());
        ActionResult error = errors.get(0);
        assertTrue(error instanceof ErrorActionResult);
        assertTrue("Error Message Incorrect:" + error.getActionName(), error.getMessage().contains("Service not found for action [BAD_ACTION]"));
    }

    @Test
    public void takeActionsNoActions() {
        Map<String, Object> facts = new HashMap<String, Object>();
        List<RulesAction> rulesActions = new ArrayList<RulesAction>();
        List<ActionResult> results = rulesService.takeActions(rulesActions, facts);
        assertEquals(1, results.size());
        assertEquals("DroolsRulesService.takeActions(..)", results.get(0).getActionName());
        assertEquals("NO_ACTIONS_FOUND", results.get(0).getMessage());
    }

    @Test
    public void takeActionsNominal() {
        Map<String, Object> facts = new HashMap<String, Object>();
        RulesAction action = new RulesAction("NOOP");
        List<RulesAction> rulesActions = new ArrayList<RulesAction>();
        rulesActions.add(action);
        List<ActionResult> errors = rulesService.takeActions(rulesActions, facts);
        assertEquals(1, errors.size());
        ActionResult error = errors.get(0);
        assertFalse(error instanceof ErrorActionResult);
        assertTrue("Error Message Incorrect:" + error.getActionName(), error.getMessage().contains("Service found and action taken for [NOOP]"));
    }
}
