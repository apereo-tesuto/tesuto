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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.exceptions.ObjectNotFoundException;
import org.ccctc.common.droolscommon.exceptions.SaveException;
import org.ccctc.common.droolscommon.model.RuleSetRowDTO;
import org.ccctc.common.droolscommon.model.RuleVariableDTO;
import org.ccctc.common.droolscommon.model.RuleVariableRowDTO;
import org.ccctc.common.droolsdb.TestConfig;
import org.ccctc.common.droolsdb.form.RuleAttributeFacetSearchForm;
import org.ccctc.common.droolsdb.services.RuleSetRowService;
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
public class RuleSetRowServiceTest extends DynamoDBTest{
    private final static String DEFAULT_RULE_NAME = "/sampleRuleVersioned.json";
    private final static String DEFAULT_RULE_WITH_VARIABLE_NAME = "/sampleRuleWithVariables.json";
    private final static String DEFAULT_RULESETROW_NAME = "/sampleRuleSetRowForRuleSet.json";
    private final static String DEFAULT_RULESETROW_RULE_ID="efbb69b1-c2ac-4d1c-a562-c991426827bb";
    private final static String DEFAULT_RULESETROW_VERSIONED_NAME = "/sampleRuleSetRowWithVersionedRule.json";
    
    @Autowired
    private RuleSetRowService ruleSetRowService;
    
    
    @Before
    public void setup() {       
        try {
        	createRuleTableForTest();
        	createRuleSetRowTableForTest();
            loadRule(DEFAULT_RULE_NAME);
            loadRule(DEFAULT_RULE_WITH_VARIABLE_NAME);
            loadRuleSetRow(DEFAULT_RULESETROW_NAME);
            loadRuleSetRow(DEFAULT_RULESETROW_VERSIONED_NAME);
        } catch (DynamoDBLocalServiceException e) {
            System.out.println("RuleSetRowServiceTest setup error:[" + e.getMessage() + "]");
        } catch (Exception e) {
            System.out.println("RuleSetRowServiceTest setup error:[" + e.getMessage() + "]");
        }            
    }
    
    private void loadMultipleRuleSetRows() {
    	createRuleSetRowTableForTest();
        loadSingleRuleSetRow("sns-listener", DRAFT, "ZZ1", "category1","english");
        loadSingleRuleSetRow("sns-listener", PUBLISHED, "ZZ1", "category2","math");
        loadSingleRuleSetRow("sns-listener", RETIRED, "ZZ2", "category1","math");
        loadSingleRuleSetRow("sns-listener", DRAFT, "ZZ2", "category2","english");
        loadSingleRuleSetRow("sns-listener", PUBLISHED, "ZZ3", "category1","english");
        loadSingleRuleSetRow("sns-listener", RETIRED, "ZZ3","category2","math");
        loadSingleRuleSetRow("sns-listener", PUBLISHED, "ZZ4", "category1","english");
        loadSingleRuleSetRow("sns-listener", DRAFT, "ZZ4", "category2","math");
        loadSingleRuleSetRow("sns-listener", RETIRED, "ZZ4", "category1","english");
    }
    
    private RuleSetRowDTO loadSingleRuleSetRow(String application, String status, String cccMisCode, String category, String competencyMapDiscipline) {
    	RuleSetRowDTO ruleSetRowDTO = this.readObjectFromFile(DEFAULT_RULESETROW_NAME, RuleSetRowDTO.class);
    	ruleSetRowDTO.setId(null);
    	ruleSetRowDTO.setRuleId(DEFAULT_RULESETROW_RULE_ID);
    	ruleSetRowDTO.setEngine(application);
    	ruleSetRowDTO.setStatus(status);
    	ruleSetRowDTO.setFamily(cccMisCode);
    	ruleSetRowDTO.setCompetencyMapDiscipline(competencyMapDiscipline);
    	ruleSetRowDTO.setCategory(category);
    	RuleSetRowDTO savedRow  = ruleSetRowService.save(ruleSetRowDTO);        
    	return savedRow;
    }
    
    @Test
    public void findRuleSetRowBySearchForm() {
    	loadMultipleRuleSetRows();
        RuleAttributeFacetSearchForm form = new RuleAttributeFacetSearchForm();
        assertEquals(9, ruleSetRowService.find(form).size());
        form.setEngine("sns-listener");
        assertEquals(9, ruleSetRowService.find(form).size());
        form.setStatus(PUBLISHED);
        assertEquals(3, ruleSetRowService.find(form).size());
        form.setStatus(null);
        form.setFamily("ZZ1");
        assertEquals(2, ruleSetRowService.find(form).size());
        form.setEngine(null);
        form.setStatus(DRAFT);
        form.setFamily("ZZ4");
        assertEquals(1, ruleSetRowService.find(form).size());
        form.setEngine(null);
        form.setStatus(DRAFT);
        form.setFamily(null);
        assertEquals(3, ruleSetRowService.find(form).size());
        form.setEngine(null);
        form.setStatus("");
        form.setFamily("ZZ4");
        assertEquals(3, ruleSetRowService.find(form).size());
        form.setEngine("sns-listener");
        form.setStatus(DRAFT);
        form.setFamily("ZZ2");
        assertEquals(1, ruleSetRowService.find(form).size());
        form.setEngine("sns-listener");
        form.setStatus(DRAFT);
        form.setFamily("ZZ5");
        assertEquals(0, ruleSetRowService.find(form).size());
        form.setEngine("");
        form.setStatus("");
        form.setFamily("");
        form.setCategory("category1");
        assertEquals(5, ruleSetRowService.find(form).size());
        form.setCategory("");
        form.setCompetencyMapDiscipline("english");
        assertEquals(5, ruleSetRowService.find(form).size());
        
        form.setCategory("category1");
        form.setCompetencyMapDiscipline("math");
        assertEquals(1, ruleSetRowService.find(form).size());
        
        form.setCategory("category2");
        form.setCompetencyMapDiscipline("math");
        assertEquals(3, ruleSetRowService.find(form).size());
        
        form.setEngine("sns-listener");
        form.setStatus(DRAFT);
        form.setFamily("ZZ4");
        form.setCategory("category2");
        form.setCompetencyMapDiscipline("math");
        assertEquals(1, ruleSetRowService.find(form).size());
    }
    
    @Test
    public void getAllRuleSetRows() {
        List<RuleSetRowDTO> ruleSets = ruleSetRowService.getAllRuleSetRows();
        assertEquals(2, ruleSets.size());
    }
    
    @Test
    public void getRuleSetRow() {
        RuleSetRowDTO RuleSetRowDTO = this.readObjectFromFile(DEFAULT_RULESETROW_NAME, RuleSetRowDTO.class);
        RuleSetRowDTO ruleSetFromDbDTO = ruleSetRowService.getRuleSetRow(RuleSetRowDTO.getId());
        assertEquals(RuleSetRowDTO.getEngine(), ruleSetFromDbDTO.getEngine());        
    }
    
    @Test
    public void getRuleSetRowBadId() {
        try {
            ruleSetRowService.getRuleSetRow("invalid-id");
            fail("Should throw ObjectNotFoundException");
        } catch (ObjectNotFoundException e) {
            // success
        } catch (Exception e) {
            fail("Should not get here:[" + e.getMessage() + "]");
        }
    }
    
    @Test
    public void getRuleSetRowNoId() {
        try {
            ruleSetRowService.getRuleSetRow(null);
            fail("Should throw ObjectNotFoundException");
        } catch (ObjectNotFoundException e) {
            // success
        } catch (Exception e) {
            fail("Should not get here:[" + e.getMessage() + "]");
        }
    }
    
    @Test
    public void getRuleSetRowNotFound() {
        try {
            ruleSetRowService.getRuleSetRow("invalid-UUID");
            fail("Should have hit ObjectNotFoundException");
        } catch (ObjectNotFoundException e) {
            // success
        } catch (Exception e) {
            fail("should not get here:[" + e.getMessage() + "]");
        }
    }
    
    @Test
    public void checkDatabaseBeforeSavingSuccess() {
        RuleSetRowDTO RuleSetRowDTO = this.readObjectFromFile(DEFAULT_RULESETROW_NAME, RuleSetRowDTO.class);
        RuleSetRowServiceImpl ruleSetRowServiceImpl = (RuleSetRowServiceImpl) ruleSetRowService;
        ruleSetRowServiceImpl.checkDatabaseBeforeSaving(RuleSetRowDTO.getId());
    }
    
    @Test
    public void checkDatabaseBeforeSavingHasVersion() {
        RuleSetRowDTO RuleSetRowDTO = this.readObjectFromFile(DEFAULT_RULESETROW_NAME, RuleSetRowDTO.class);
        RuleSetRowDTO.setVersion("1");
        ruleSetRowService.save(RuleSetRowDTO);
        RuleSetRowServiceImpl ruleSetRowServiceImpl = (RuleSetRowServiceImpl) ruleSetRowService;
        try {
            ruleSetRowServiceImpl.checkDatabaseBeforeSaving(RuleSetRowDTO.getId());
            fail("should fail because RuleSetRowDTO is versioned");
        } catch (SaveException e) {
            // success
        } catch (Exception e) {
            fail("should not get here:[" + e.getMessage() + "]");
        }        
    }
    
    @Test
    public void checkDatabaseBeforeSavingStatusVersioned() {
        RuleSetRowDTO RuleSetRowDTO = this.readObjectFromFile(DEFAULT_RULESETROW_NAME, RuleSetRowDTO.class);
        RuleSetRowDTO.setStatus(PUBLISHED);
        ruleSetRowService.save(RuleSetRowDTO);
        RuleSetRowServiceImpl ruleSetRowServiceImpl = (RuleSetRowServiceImpl) ruleSetRowService;
        try {
            ruleSetRowServiceImpl.checkDatabaseBeforeSaving(RuleSetRowDTO.getId());
            fail("should fail because RuleSetRowDTO is versioned");
        } catch (SaveException e) {
            // success
        } catch (Exception e) {
            fail("should not get here:[" + e.getMessage() + "]");
        }        
    }
    
    @Test
    public void checkDatabaseBeforeSavingStatusAssigned() {
        RuleSetRowDTO RuleSetRowDTO = this.readObjectFromFile(DEFAULT_RULESETROW_NAME, RuleSetRowDTO.class);
        RuleSetRowDTO.setStatus(PUBLISHED);
        ruleSetRowService.save(RuleSetRowDTO);
        RuleSetRowServiceImpl ruleSetRowServiceImpl = (RuleSetRowServiceImpl) ruleSetRowService;
        try {
            ruleSetRowServiceImpl.checkDatabaseBeforeSaving(RuleSetRowDTO.getId());
            fail("should fail because RuleSetRowDTO is assigned");
        } catch (SaveException e) {
            // success
        } catch (Exception e) {
            fail("should not get here:[" + e.getMessage() + "]");
        }        
    }
    
    @Test
    public void checkDatabaseBeforeSavingStatusretired() {
        RuleSetRowDTO RuleSetRowDTO = this.readObjectFromFile(DEFAULT_RULESETROW_NAME, RuleSetRowDTO.class);
        RuleSetRowDTO.setStatus(RETIRED);
        ruleSetRowService.save(RuleSetRowDTO);
        RuleSetRowServiceImpl ruleSetRowServiceImpl = (RuleSetRowServiceImpl) ruleSetRowService;
        try {
            ruleSetRowServiceImpl.checkDatabaseBeforeSaving(RuleSetRowDTO.getId());
            fail("should fail because RuleSetRowDTO is retired");
        } catch (SaveException e) {
            // success
        } catch (Exception e) {
            fail("should not get here:[" + e.getMessage() + "]");
        }        
    }
    
    @Test
    public void checkDatabaseBeforeSavingMismatchedApplication() {
        RuleSetRowDTO RuleSetRowDTO = this.readObjectFromFile(DEFAULT_RULESETROW_NAME, RuleSetRowDTO.class);
        RuleSetRowDTO.setEngine("different-application");
        try {
            ruleSetRowService.save(RuleSetRowDTO);
            fail("should fail because rulesetrow and rule applications are different");
        } catch (SaveException e) {
            // success
        } catch (Exception e) {
            fail("should not get here:[" + e.getMessage() + "]");
        }        
    }
    
    @Test
    public void checkDTOBeforeSaving() {
        RuleSetRowDTO RuleSetRowDTO = this.readObjectFromFile(DEFAULT_RULESETROW_NAME, RuleSetRowDTO.class);
        RuleSetRowServiceImpl ruleSetRowServiceImpl = (RuleSetRowServiceImpl) ruleSetRowService;
        ruleSetRowServiceImpl.checkDTOBeforeSaving(RuleSetRowDTO);
    }
    
    @Test
    public void checkDTOBeforeSavingMinimal() {
        RuleSetRowDTO ruleSetRowDTO = (RuleSetRowDTO) new RuleSetRowDTO()
                .setEngine("minimal-application")
                .setFamily("ZZ1");
        RuleSetRowServiceImpl ruleSetRowServiceImpl = (RuleSetRowServiceImpl) ruleSetRowService;
        ruleSetRowServiceImpl.checkDTOBeforeSaving(ruleSetRowDTO);
        assertTrue(!StringUtils.isBlank(ruleSetRowDTO.getId()));
        assertEquals(DRAFT, ruleSetRowDTO.getStatus());
    }
    
    @Test
    public void checkDTOBeforeSavingNoApplication() {
        RuleSetRowDTO RuleSetRowDTO = new RuleSetRowDTO();
        RuleSetRowServiceImpl ruleSetRowServiceImpl = (RuleSetRowServiceImpl) ruleSetRowService;
        try {
            ruleSetRowServiceImpl.checkDTOBeforeSaving(RuleSetRowDTO);
            fail("Should throw SaveException");
        } catch (SaveException e) {
            // success
        } catch (Exception e) {
            fail("Should not get here:[" + e.getMessage() + "]");
        }
    }
    
    @Test
    public void checkDTOBeforeSavingNoRule() {
        RuleSetRowDTO RuleSetRowDTO = (RuleSetRowDTO) new RuleSetRowDTO()
                .setEngine("minimal-application")
                .setVersion(PUBLISHED)
                .setFamily("ZZ1");
        RuleSetRowDTO.setRuleId("invalid-id");
        RuleSetRowServiceImpl ruleSetRowServiceImpl = (RuleSetRowServiceImpl) ruleSetRowService;
        try {
            ruleSetRowServiceImpl.checkDTOBeforeSaving(RuleSetRowDTO);
            fail("Should throw SaveException");
        } catch (SaveException e) {
            // success
        } catch (Exception e) {
            fail("Should not get here:[" + e.getMessage() + "]");
        }
        
    }
    
    @Test
    public void checkDTOBeforeSavingInvalidStatus() {
        RuleSetRowDTO RuleSetRowDTO = (RuleSetRowDTO) new RuleSetRowDTO()
                .setEngine("minimal-application")
                .setFamily("ZZ1")
                .setStatus("invalid-status")
                .setVersion("");
        RuleSetRowServiceImpl ruleSetRowServiceImpl = (RuleSetRowServiceImpl) ruleSetRowService;
        try {
            ruleSetRowServiceImpl.checkDTOBeforeSaving(RuleSetRowDTO);
            fail("Should have failed for invalid status");
        } catch (SaveException e) {
            // success
        } catch (Exception e) {
            fail("should not get here:[" + e.getMessage() + "]");
        }
    }
    
    @Test
    public void duplicate() {
        RuleSetRowDTO RuleSetRowDTO = this.readObjectFromFile(DEFAULT_RULESETROW_NAME, RuleSetRowDTO.class);
        RuleSetRowDTO duplicateRuleSetRowDTO = ruleSetRowService.duplicate(RuleSetRowDTO.getId());
        assertTrue(!RuleSetRowDTO.getId().equals(duplicateRuleSetRowDTO.getId()));
        assertEquals(RuleSetRowDTO.getEngine(), duplicateRuleSetRowDTO.getEngine());
        assertEquals(DRAFT, duplicateRuleSetRowDTO.getStatus());
        assertTrue(StringUtils.isBlank(duplicateRuleSetRowDTO.getVersion()));
    }
    
    @Test(expected=ObjectNotFoundException.class)
    public void deleteActive() {
        RuleSetRowDTO RuleSetRowDTO = this.readObjectFromFile(DEFAULT_RULESETROW_NAME, RuleSetRowDTO.class);
        ruleSetRowService.delete(RuleSetRowDTO.getId());
        RuleSetRowDTO retiredRuleSetRowDTO = ruleSetRowService.getRuleSetRow(RuleSetRowDTO.getId());
        assertEquals(RETIRED, retiredRuleSetRowDTO.getStatus());
        assertEquals(RuleSetRowDTO.getId(), retiredRuleSetRowDTO.getId());
    }
    
    @Test
	public void deleteVersioned() {
        RuleSetRowDTO RuleSetRowDTO = this.readObjectFromFile(DEFAULT_RULESETROW_VERSIONED_NAME, RuleSetRowDTO.class);
        ruleSetRowService.delete(RuleSetRowDTO.getId());
        RuleSetRowDTO retiredRuleSetRowDTO = ruleSetRowService.getRuleSetRow(RuleSetRowDTO.getId());
        assertEquals(RETIRED, retiredRuleSetRowDTO.getStatus());
        assertEquals(RuleSetRowDTO.getId(), retiredRuleSetRowDTO.getId());
    }
    
    @Test
    public void deleteAssigned() {
        RuleSetRowDTO ruleSetRowDTO = this.readObjectFromFile(DEFAULT_RULESETROW_VERSIONED_NAME, RuleSetRowDTO.class);
        ruleSetRowDTO.setId("");
        ruleSetRowDTO.setStatus(PUBLISHED);
        ruleSetRowDTO = ruleSetRowService.save(ruleSetRowDTO);
        ruleSetRowService.delete(ruleSetRowDTO.getId());
        RuleSetRowDTO retiredRuleSetRowDTO = ruleSetRowService.getRuleSetRow(ruleSetRowDTO.getId());
        assertEquals(RETIRED, retiredRuleSetRowDTO.getStatus());
        assertEquals(ruleSetRowDTO.getId(), retiredRuleSetRowDTO.getId());
    }
    @Test
    public void updateVariables() {
        RuleSetRowDTO ruleSetRowDTO = this.readObjectFromFile(DEFAULT_RULESETROW_VERSIONED_NAME, RuleSetRowDTO.class);
        Map<String,String> variableValues = new HashMap<>();
        variableValues.put("cccid", "A123456");
        variableValues.put("lower", "20.0");
        String id = ruleSetRowService.updateVariables(ruleSetRowDTO.getId(), variableValues);
        RuleSetRowDTO updatedRuleSetRowDTO = ruleSetRowService.getRuleSetRow(id);
        RuleVariableRowDTO row = updatedRuleSetRowDTO.getVariableRows().get(0);
        for(RuleVariableDTO variable:row.getVariables()) {
        	assertEquals(variable.getValue(), variableValues.get(variable.getName()));
        }
    }
    @Test
    public void deleteBlank() {
        // nothing should happen
        ruleSetRowService.delete(null);
        ruleSetRowService.delete("");
        ruleSetRowService.delete("invalid-ruleset-id");
    }    
}
