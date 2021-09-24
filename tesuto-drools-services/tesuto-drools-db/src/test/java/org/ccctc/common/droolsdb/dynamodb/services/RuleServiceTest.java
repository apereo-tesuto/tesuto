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
package org.ccctc.common.droolsdb.dynamodb.services;

import static org.ccctc.common.droolscommon.model.RuleStatus.DRAFT;
import static org.ccctc.common.droolscommon.model.RuleStatus.PUBLISHED;
import static org.ccctc.common.droolscommon.model.RuleStatus.RETIRED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.exceptions.ObjectNotFoundException;
import org.ccctc.common.droolscommon.exceptions.SaveException;
import org.ccctc.common.droolsdb.TestConfig;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.common.droolsdb.model.RuleDTO;
import org.ccctc.common.droolsdb.services.RuleService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.amazonaws.services.dynamodbv2.exceptions.DynamoDBLocalServiceException;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;

@RunWith(SpringRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { TestConfig.class })
public class RuleServiceTest extends DynamoDBTest {
    private final static String DEFAULT_RULE_NAME = "/sampleRuleWithVariables.json";
    private final static String DEFAULT_RULE2_NAME = "/sampleRule2.json";

    private DynamoDBProxyServer server;

    @Autowired
    private RuleService ruleService;

    @Before
    public void setup() {
        try {
            createRuleTableForTest();
            loadRule(DEFAULT_RULE_NAME);
            loadRule(DEFAULT_RULE2_NAME);
        }
        catch (DynamoDBLocalServiceException e) {
            fail("Setup failed:[" + e.getMessage() + "]");
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Test
    public void getRuleNoId() {
        try {
            ruleService.getRule(null);
            fail("Should get ObjectNotFoundException");
        }
        catch (ObjectNotFoundException e) {
            // success
        }
        catch (Exception e) {
            fail("Should not get here:[" + e.getMessage() + "]");
        }
    }

    @Test
    public void getRuleNoBadId() {
        try {
            ruleService.getRule("bad-rule-id");
            fail("Should get ObjectNotFoundException");
        }
        catch (ObjectNotFoundException e) {
            // successRuleServiceTest.findRuleBySearchForm
        }
        catch (Exception e) {
            fail("Should not get here:[" + e.getMessage() + "]");
        }
    }

    @Test
    public void getAllRules() {
        List<RuleDTO> rules = ruleService.getAllRules();
        assertEquals(2, rules.size());
    }

    @Test
    public void getRulesByEngine() {
        List<RuleDTO> rules = ruleService.getRulesByEngine("sns-listener");
        assertEquals(1, rules.size());
    }

    @Test
    public void getRulesByEngineAndStatus() {
        List<RuleDTO> rules = ruleService.getRulesByEngineAndStatus("sns-listener", PUBLISHED);
        assertEquals(1, rules.size());
    }

    private void loadMultipleRules() {
        createRuleTableForTest();
        loadSingleRule("sns-listener", DRAFT, "ZZ1", "category1", "english");
        loadSingleRule("sns-listener", PUBLISHED, "ZZ1", "category2", "math");
        loadSingleRule("sns-listener", RETIRED, "ZZ2", "category1", "math");
        loadSingleRule("sns-listener", DRAFT, "ZZ2", "category2", "english");
        loadSingleRule("sns-listener", PUBLISHED, "ZZ3", "category1", "english");
        loadSingleRule("sns-listener", RETIRED, "ZZ3", "category2", "math");
        loadSingleRule("sns-listener", PUBLISHED, "ZZ4", "category1", "english");
        loadSingleRule("sns-listener", DRAFT, "ZZ4", "category2", "math");
        loadSingleRule("sns-listener", RETIRED, "ZZ4", "category1", "english");
    }

    private RuleDTO loadSingleRule(String application, String status, String cccMisCode, String category,
                    String competencyMapDiscipline) {
        RuleDTO ruleDTO = this.readObjectFromFile(DEFAULT_RULE_NAME, RuleDTO.class);
        ruleDTO.setId(null);
        ruleDTO.setEngine(application);
        ruleDTO.setStatus(status);
        if (status.endsWith("published")) {
            ruleDTO.setVersion("1");
        } else {
            ruleDTO.setVersion("");
        }
        ruleDTO.setFamily(cccMisCode);
        ruleDTO.setCompetencyMapDiscipline(competencyMapDiscipline);
        ruleDTO.setCategory(category);
        RuleDTO savedRow = ruleService.save(ruleDTO);
        return savedRow;
    }

    @Test
    public void findRuleBySearchForm() {
        loadMultipleRules();
        RuleAttributeFacetSearchForm form = new RuleAttributeFacetSearchForm();
        assertEquals(9, ruleService.find(form).size());
        form.setEngine("sns-listener");
        assertEquals(9, ruleService.find(form).size());
        form.setStatus("published");
        assertEquals(3, ruleService.find(form).size());
        form.setStatus(null);
        form.setFamily("ZZ1");
        assertEquals(2, ruleService.find(form).size());
        form.setEngine(null);
        form.setStatus("draft");
        form.setFamily("ZZ4");
        assertEquals(1, ruleService.find(form).size());
        form.setEngine(null);
        form.setStatus(DRAFT);
        form.setFamily(null);
        assertEquals(3, ruleService.find(form).size());
        form.setEngine(null);
        form.setStatus("");
        form.setFamily("ZZ4");
        assertEquals(3, ruleService.find(form).size());
        form.setEngine("sns-listener");
        form.setStatus(DRAFT);
        form.setFamily("ZZ2");
        assertEquals(1, ruleService.find(form).size());
        form.setEngine("sns-listener");
        form.setStatus(DRAFT);
        form.setFamily("ZZ5");
        assertEquals(0, ruleService.find(form).size());
        form.setEngine("");
        form.setStatus("");
        form.setFamily("");
        form.setCategory("category1");
        assertEquals(5, ruleService.find(form).size());
        form.setCategory("");
        form.setCompetencyMapDiscipline("english");
        assertEquals(5, ruleService.find(form).size());

        form.setCategory("category1");
        form.setCompetencyMapDiscipline("math");
        assertEquals(1, ruleService.find(form).size());

        form.setCategory("category2");
        form.setCompetencyMapDiscipline("math");
        assertEquals(3, ruleService.find(form).size());

        form.setEngine("sns-listener");
        form.setStatus(DRAFT);
        form.setFamily("ZZ4");
        form.setCategory("category2");
        form.setCompetencyMapDiscipline("math");
        assertEquals(1, ruleService.find(form).size());
    }

    @Test
    public void duplicate() {
        RuleDTO ruleDTO = readObjectFromFile(DEFAULT_RULE2_NAME, RuleDTO.class);
        RuleDTO duplicateRuleDTO = ruleService.duplicate(ruleDTO.getId());
        assertTrue(!ruleDTO.getId().equals(duplicateRuleDTO.getId()));
        assertEquals(ruleDTO.getEngine(), duplicateRuleDTO.getEngine());
        assertEquals(DRAFT, duplicateRuleDTO.getStatus());
        assertTrue(StringUtils.isBlank(duplicateRuleDTO.getVersion()));
    }

    @Test(expected = ObjectNotFoundException.class)
    public void deleteActive() {
        RuleDTO ruleDTO = readObjectFromFile(DEFAULT_RULE2_NAME, RuleDTO.class);
        ruleService.delete(ruleDTO.getId());
        ruleService.getRule(ruleDTO.getId());
    }

    @Test
    public void deleteVersioned() {
        RuleDTO ruleDTO = readObjectFromFile(DEFAULT_RULE_NAME, RuleDTO.class);
        ruleService.delete(ruleDTO.getId());
        RuleDTO retiredRuleDTO = ruleService.getRule(ruleDTO.getId());
        assertEquals("retired", retiredRuleDTO.getStatus());
        assertEquals(ruleDTO.getId(), retiredRuleDTO.getId());
    }

    @Test
    public void deleteBlank() {
        // nothing should happen
        ruleService.delete(null);
        ruleService.delete("");
        ruleService.delete("invalid-ruleset-id");
    }

    @Test
    public void save() {
        RuleDTO ruleDTO = readObjectFromFile(DEFAULT_RULE2_NAME, RuleDTO.class);
        assertTrue(StringUtils.isBlank(ruleDTO.getVersion()));
        ruleDTO.setVersion("1.0.0");
        ruleService.save(ruleDTO);

        try {
            ruleDTO.setVersion("1.0.1");
            ruleService.save(ruleDTO);
            fail("should throw exception, since rule had a version");
        }
        catch (SaveException e) {
            // success
        }
        catch (Exception e) {
            fail("should not get here:[" + e.getMessage() + "]");
        }
    }

    @Test
    public void checkDatabaseBeforeSaving1() {
        RuleDTO ruleDTO = readObjectFromFile(DEFAULT_RULE2_NAME, RuleDTO.class);
        String ruleId = ruleDTO.getId();
        ruleDTO.setVersion("1.0.0");
        ruleService.save(ruleDTO);

        try {
            RuleServiceImpl ruleServiceImpl = (RuleServiceImpl) ruleService;
            ruleServiceImpl.checkDatabaseBeforeSaving(ruleId);
            fail("should not be able to save a published file");
        }
        catch (SaveException e) {
            // success
        }
        catch (Exception e) {
            fail("should not get here:[" + e.getMessage() + "]");
        }
    }

    @Test
    public void checkSavingActiveDeletesRow() {
        RuleDTO ruleDTO = readObjectFromFile(DEFAULT_RULE2_NAME, RuleDTO.class);
        String ruleId = ruleDTO.getId();
        ruleService.delete(ruleId);
        try {
            ruleService.getRule(ruleId);
            fail("should not be able to save a retired file");
        }
        catch (ObjectNotFoundException e) {
            // success
        }
        catch (Exception e) {
            fail("should not get here:[" + e.getMessage() + "]");
        }
    }

    @Test
    public void checkDTOBeforeSavingNoApplication() {
        RuleDTO ruleDTO = new RuleDTO();
        RuleServiceImpl ruleServiceImpl = (RuleServiceImpl) ruleService;
        try {
            ruleServiceImpl.checkDTOBeforeSaving(ruleDTO);
            fail("Should throw save exception");
        }
        catch (SaveException e) {
            // success
        }
        catch (Exception e) {
            fail("Should not get here:[" + e.getMessage() + "]");
        }
    }

    @Test
    public void checkDTOBeforeSavingInvalidStatus() {
        RuleDTO ruleDTO = (RuleDTO) new RuleDTO().setEngine("myapplication").setStatus("invalid-status");
        RuleServiceImpl ruleServiceImpl = (RuleServiceImpl) ruleService;
        try {
            ruleServiceImpl.checkDTOBeforeSaving(ruleDTO);
            fail("Should throw save exception");
        }
        catch (SaveException e) {
            // success
        }
        catch (Exception e) {
            fail("Should not get here:[" + e.getMessage() + "]");
        }
    }
}
