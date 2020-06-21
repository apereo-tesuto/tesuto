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
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolsdb.TestConfig;
import org.ccctc.common.droolsdb.services.RuleSetService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.amazonaws.services.dynamodbv2.exceptions.DynamoDBLocalServiceException;

@RunWith(SpringRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={TestConfig.class})
public class RuleSetServiceTest extends DynamoDBTest {   
    private final static String DEFAULT_RULE_NAME = "/sampleRuleVersioned.json";
    private final static String DEFAULT_RULE_WITH_VARIABLE_NAME = "/sampleRuleWithVariables.json";
    private final static String DEFAULT_RULESET_NAME = "/sampleRuleSetWithVersionedRule.json";
    private final static String DEFAULT_RULESETROW_NAME = "/sampleRuleSetRowForRuleSet.json";
    private final static String DEFAULT_RULESETROW_VERSIONED_NAME = "/sampleRuleSetRowWithVersionedRule.json";
    
    @Autowired
    private RuleSetService ruleSetService;
    
    @Before
    public void setup() {       
        try {
        	createRuleTableForTest();
        	createRuleSetRowTableForTest();
        	createRuleSetTableForTest();
            loadRule(DEFAULT_RULE_NAME);
            loadRule(DEFAULT_RULE_WITH_VARIABLE_NAME);
            loadRuleSetRow(DEFAULT_RULESETROW_NAME);
            loadRuleSetRow(DEFAULT_RULESETROW_VERSIONED_NAME);
            loadRuleSet(DEFAULT_RULESET_NAME);
        } catch (DynamoDBLocalServiceException e) {
            System.out.println("RuleSereviceTest setup error:[" + e.getMessage() + "]");
        } catch (Exception e) {
            System.out.println("RuleSereviceTest setup error:[" + e.getMessage() + "]");
        }            
    }
    
    private void loadMultipleRuleSets() {
    	createRuleSetTableForTest();
        loadSingleRuleSet("sns-listener", DRAFT, "ZZ1");
        loadSingleRuleSet("sns-listener", PUBLISHED, "ZZ1");
        loadSingleRuleSet("sns-listener", RETIRED, "ZZ2");
        loadSingleRuleSet("sns-listener", DRAFT, "ZZ2");
        loadSingleRuleSet("sns-listener", PUBLISHED, "ZZ3");
        loadSingleRuleSet("sns-listener", RETIRED, "ZZ3");
        loadSingleRuleSet("sns-listener", PUBLISHED, "ZZ4");
        loadSingleRuleSet("sns-listener", DRAFT, "ZZ4");
        loadSingleRuleSet("sns-listener", RETIRED, "ZZ4");
        RuleSetDTO dummyRuleSetDTO = (RuleSetDTO) new RuleSetDTO()
                .setEngine("dummy-appl")
                .setStatus(DRAFT)
                .setFamily("QQQ");
        ruleSetService.save(dummyRuleSetDTO);
    }
    
    private void loadSingleRuleSet(String application, String status, String cccMisCode) {
        RuleSetDTO ruleSetDTO = this.readObjectFromFile(DEFAULT_RULESET_NAME, RuleSetDTO.class);
        ruleSetDTO.setId(null);
        ruleSetDTO.setEngine(application);
        ruleSetDTO.setStatus(status);
        ruleSetDTO.setFamily(cccMisCode);
        ruleSetService.save(ruleSetDTO);        
    }
    
    @Test
    public void getRuleSetsByParameters() {
        loadMultipleRuleSets();
        assertEquals(10, ruleSetService.getRuleSetsByParameters(null, null, null).size());
        assertEquals(1, ruleSetService.getRuleSetsByParameters("dummy-appl", null, null).size());
        assertEquals(9, ruleSetService.getRuleSetsByParameters("sns-listener", null, null).size());
        assertEquals(3, ruleSetService.getRuleSetsByParameters("sns-listener", PUBLISHED, null).size());
        assertEquals(2, ruleSetService.getRuleSetsByParameters("sns-listener", null, "ZZ1").size());
        assertEquals(1, ruleSetService.getRuleSetsByParameters(null, DRAFT, "ZZ4").size());
        assertEquals(4, ruleSetService.getRuleSetsByParameters(null, DRAFT, null).size());
        assertEquals(3, ruleSetService.getRuleSetsByParameters(null, null, "ZZ4").size());
        assertEquals(1, ruleSetService.getRuleSetsByParameters("sns-listener", DRAFT, "ZZ2").size());
        assertEquals(0, ruleSetService.getRuleSetsByParameters("sns-listener", DRAFT, "ZZ5").size());
    }
    
    @Test
    public void getAllRuleSets() {
        List<RuleSetDTO> ruleSets = ruleSetService.getAllRuleSets();
        assertEquals(1, ruleSets.size());
    }
    
    @Test
    public void getRuleSetByEngine() {
        List<RuleSetDTO> ruleSetDTOs = ruleSetService.getRuleSetsByEngine("sns-listener");
        assertEquals(1, ruleSetDTOs.size());
    }
    
    @Test
    public void getRuleSetByApplicationNotFound() {
        List<RuleSetDTO> ruleSetDTOs = ruleSetService.getRuleSetsByEngine("not-found");
        assertEquals(0, ruleSetDTOs.size());
    }
    
    @Test
    public void getRuleSetByStatus() {
        List<RuleSetDTO> ruleSetDTOs = ruleSetService.getRuleSetsByStatus(DRAFT);
        assertEquals(1, ruleSetDTOs.size());        
    }
    
    @Test
    public void getRuleSetByStatusNotFound() {
        List<RuleSetDTO> ruleSetDTOs = ruleSetService.getRuleSetsByStatus("not-found");
        assertEquals(0, ruleSetDTOs.size());
    }
    
    @Test
    public void getRuleSetsByEngineAndStatus() {
        List<RuleSetDTO> ruleSetDTOs = ruleSetService.getRulesSetByEngineAndStatus("sns-listener",  DRAFT);
        assertEquals(1, ruleSetDTOs.size());
    }
    
    @Test
    public void getRuleSet() {
        RuleSetDTO ruleSetDTO = this.readObjectFromFile(DEFAULT_RULESET_NAME, RuleSetDTO.class);
        RuleSetDTO ruleSetFromDbDTO = ruleSetService.getRuleSet(ruleSetDTO.getId());
        assertEquals(ruleSetDTO.getEngine(), ruleSetFromDbDTO.getEngine());        
    }
    
    @Test
    public void getRuleSetBadId() {
        try {
            ruleSetService.getRuleSet("invalid-id");
            fail("Should throw ObjectNotFoundException");
        } catch (ObjectNotFoundException e) {
            // success
        } catch (Exception e) {
            fail("Should not get here:[" + e.getMessage() + "]");
        }
    }
    
    @Test
    public void getRuleSetNoId() {
        try {
            ruleSetService.getRuleSet(null);
            fail("Should throw ObjectNotFoundException");
        } catch (ObjectNotFoundException e) {
            // success
        } catch (Exception e) {
            fail("Should not get here:[" + e.getMessage() + "]");
        }
    }
    
    @Test
    public void getRuleSetNotFound() {
        try {
            ruleSetService.getRuleSet("invalid-UUID");
            fail("Should have hit ObjectNotFoundException");
        } catch (ObjectNotFoundException e) {
            // success
        } catch (Exception e) {
            fail("should not get here:[" + e.getMessage() + "]");
        }
    }
    
    @Test
    public void checkDatabaseBeforeSavingSuccess() {
        RuleSetDTO ruleSetDTO = this.readObjectFromFile(DEFAULT_RULESET_NAME, RuleSetDTO.class);
        RuleSetServiceImpl ruleSetServiceImpl = (RuleSetServiceImpl) ruleSetService;
        ruleSetServiceImpl.checkDatabaseBeforeSaving(ruleSetDTO.getId());
    }
    
    @Test
    public void checkDatabaseBeforeSavingHasVersion() {
        RuleSetDTO ruleSetDTO = this.readObjectFromFile(DEFAULT_RULESET_NAME, RuleSetDTO.class);
        ruleSetDTO.setVersion("1");
        ruleSetService.save(ruleSetDTO);
        RuleSetServiceImpl ruleSetServiceImpl = (RuleSetServiceImpl) ruleSetService;
        try {
            ruleSetServiceImpl.checkDatabaseBeforeSaving(ruleSetDTO.getId());
            fail("should fail because ruleSetDTO is versioned");
        } catch (SaveException e) {
            // success
        } catch (Exception e) {
            fail("should not get here:[" + e.getMessage() + "]");
        }        
    }
    
    @Test
    public void checkDatabaseBeforeSavingStatusVersioned() {
        RuleSetDTO ruleSetDTO = this.readObjectFromFile(DEFAULT_RULESET_NAME, RuleSetDTO.class);
        ruleSetDTO.setStatus(PUBLISHED);
        ruleSetService.save(ruleSetDTO);
        RuleSetServiceImpl ruleSetServiceImpl = (RuleSetServiceImpl) ruleSetService;
        try {
            ruleSetServiceImpl.checkDatabaseBeforeSaving(ruleSetDTO.getId());
            fail("should fail because ruleSetDTO is versioned");
        } catch (SaveException e) {
            // success
        } catch (Exception e) {
            fail("should not get here:[" + e.getMessage() + "]");
        }        
    }
    
    @Test
    public void checkDatabaseBeforeSavingStatusDeleted() {
        RuleSetDTO ruleSetDTO = this.readObjectFromFile(DEFAULT_RULESET_NAME, RuleSetDTO.class);
        ruleSetDTO.setStatus(RETIRED);
        ruleSetService.save(ruleSetDTO);
        RuleSetServiceImpl ruleSetServiceImpl = (RuleSetServiceImpl) ruleSetService;
        try {
            ruleSetServiceImpl.checkDatabaseBeforeSaving(ruleSetDTO.getId());
            fail("should fail because ruleSetDTO is retired");
        } catch (SaveException e) {
            // success
        } catch (Exception e) {
            fail("should not get here:[" + e.getMessage() + "]");
        }        
    }
    
    @Test
    public void checkDatabaseBeforeSavingMismatchedApplication() {
        RuleSetDTO ruleSetDTO = this.readObjectFromFile(DEFAULT_RULESET_NAME, RuleSetDTO.class);
        ruleSetDTO.setEngine("different-application");
        try {
            ruleSetService.save(ruleSetDTO);
            fail("should fail because ruleset and rule applications are different");
        } catch (SaveException e) {
            // success
        } catch (Exception e) {
            fail("should not get here:[" + e.getMessage() + "]");
        }        
    }
    
    @Test
    public void checkDTOBeforeSaving() {
        RuleSetDTO ruleSetDTO = this.readObjectFromFile(DEFAULT_RULESET_NAME, RuleSetDTO.class);
        RuleSetServiceImpl ruleSetServiceImpl = (RuleSetServiceImpl) ruleSetService;
        ruleSetServiceImpl.checkDTOBeforeSaving(ruleSetDTO);
    }
    
    @Test
    public void checkDTOBeforeSavingMinimal() {
        RuleSetDTO ruleSetDTO = (RuleSetDTO) new RuleSetDTO()
                .setEngine("minimal-application")
                .setFamily("ZZ1");
        RuleSetServiceImpl ruleSetServiceImpl = (RuleSetServiceImpl) ruleSetService;
        ruleSetServiceImpl.checkDTOBeforeSaving(ruleSetDTO);
        assertTrue(!StringUtils.isBlank(ruleSetDTO.getId()));
        assertEquals(DRAFT, ruleSetDTO.getStatus());
    }
    
    @Test
    public void checkDTOBeforeSavingNoApplication() {
        RuleSetDTO ruleSetDTO = new RuleSetDTO();
        RuleSetServiceImpl ruleSetServiceImpl = (RuleSetServiceImpl) ruleSetService;
        try {
            ruleSetServiceImpl.checkDTOBeforeSaving(ruleSetDTO);
            fail("Should throw SaveException");
        } catch (SaveException e) {
            // success
        } catch (Exception e) {
            fail("Should not get here:[" + e.getMessage() + "]");
        }
    }
    
    @Test
    public void checkDTOBeforeSavingNoMisCode() {
        RuleSetDTO ruleSetDTO = (RuleSetDTO) new RuleSetDTO()
            .setEngine("minimal-application");
        RuleSetServiceImpl ruleSetServiceImpl = (RuleSetServiceImpl) ruleSetService;
        try {
            ruleSetServiceImpl.checkDTOBeforeSaving(ruleSetDTO);
            fail("Should throw SaveException");
        } catch (SaveException e) {
            // success
        } catch (Exception e) {
            fail("Should not get here:[" + e.getMessage() + "]");
        }
    }
    
    @Test
    public void checkDTOBeforeSavingNoRules() {
        RuleSetDTO ruleSetDTO = (RuleSetDTO) new RuleSetDTO()
                .setEngine("minimal-application")
                .setVersion(PUBLISHED)
                .setFamily("ZZ1");
        RuleSetServiceImpl ruleSetServiceImpl = (RuleSetServiceImpl) ruleSetService;
        try {
            ruleSetServiceImpl.checkDTOBeforeSaving(ruleSetDTO);
            fail("Should throw SaveException");
        } catch (SaveException e) {
            // success
        } catch (Exception e) {
            fail("Should not get here:[" + e.getMessage() + "]");
        }
        
    }
    
    @Test
    public void checkDTOBeforeSavingInvaliddStatus() {
        RuleSetDTO ruleSetDTO = (RuleSetDTO) new RuleSetDTO()
                .setEngine("minimal-application")
                .setFamily("ZZ1")
                .setStatus("invalid-status");
        RuleSetServiceImpl ruleSetServiceImpl = (RuleSetServiceImpl) ruleSetService;
        try {
            ruleSetServiceImpl.checkDTOBeforeSaving(ruleSetDTO);
            fail("Should have failed for invalid status");
        } catch (SaveException e) {
            // success
        } catch (Exception e) {
            fail("should not get here:[" + e.getMessage() + "]");
        }
    }
    
    @Test
    public void duplicate() {
        RuleSetDTO ruleSetDTO = this.readObjectFromFile(DEFAULT_RULESET_NAME, RuleSetDTO.class);
        RuleSetDTO duplicateRuleSetDTO = ruleSetService.duplicate(ruleSetDTO.getId());
        assertTrue(!ruleSetDTO.getId().equals(duplicateRuleSetDTO.getId()));
        assertEquals(ruleSetDTO.getEngine(), duplicateRuleSetDTO.getEngine());
        assertEquals(DRAFT, duplicateRuleSetDTO.getStatus());
        assertTrue(StringUtils.isBlank(duplicateRuleSetDTO.getVersion()));
        assertEquals(ruleSetDTO.getRuleSetRowIds().size(), duplicateRuleSetDTO.getRuleSetRowIds().size());
    }
    
    @Test
    public void delete() {
        RuleSetDTO ruleSetDTO = this.readObjectFromFile(DEFAULT_RULESET_NAME, RuleSetDTO.class);
        ruleSetDTO.setVersion("1");
        ruleSetDTO.setStatus(PUBLISHED);
        ruleSetService.save(ruleSetDTO);
        ruleSetService.delete(ruleSetDTO.getId());
        RuleSetDTO retiredRuleSetDTO = ruleSetService.getRuleSet(ruleSetDTO.getId());
        assertEquals(RETIRED, retiredRuleSetDTO.getStatus());
        assertEquals(ruleSetDTO.getId(), retiredRuleSetDTO.getId());
    }
    
    @Test
    public void deleteBlank() {
        // nothing should happen
        ruleSetService.delete(null);
        ruleSetService.delete("");
        ruleSetService.delete("invalid-ruleset-id");
    }    
}
