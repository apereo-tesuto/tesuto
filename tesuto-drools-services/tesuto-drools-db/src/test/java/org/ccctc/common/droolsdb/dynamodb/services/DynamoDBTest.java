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

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolscommon.model.RuleSetRowDTO;
import org.ccctc.common.droolsdb.dynamodb.model.College;
import org.ccctc.common.droolsdb.dynamodb.model.Rule;
import org.ccctc.common.droolsdb.dynamodb.model.RuleSet;
import org.ccctc.common.droolsdb.dynamodb.model.RuleSetRow;
import org.ccctc.common.droolsdb.model.RuleDTO;
import org.ccctc.common.droolsdb.services.IFamilyService;
import org.ccctc.common.droolsdb.services.RuleService;
import org.ccctc.common.droolsdb.services.RuleSetRowService;
import org.ccctc.common.droolsdb.services.RuleSetService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class DynamoDBTest implements InitializingBean {
    private ObjectMapper mapper = new ObjectMapper();
        
    @Autowired
    public AmazonDynamoDB client;
    
    private DynamoDB dynamoDB;
    
    @Autowired
    private RuleService ruleService;
    
    @Autowired
    private RuleSetRowService ruleSetRowService;
    
    @Autowired
    private IFamilyService collegeService;
    
    @Autowired
    private RuleSetService ruleSetService;
    
    @Autowired
    private DynamoDBTableService tableService;
    
    @Override
    public void afterPropertiesSet() throws Exception {
        dynamoDB = new DynamoDB(client);
    } 
    
    public void createRuleTableForTest() {
    	createTableForTest(Rule.TABLE_NAME, Rule.class);
    }
    
    public void createRuleSetTableForTest() {
    	createTableForTest(RuleSet.TABLE_NAME, RuleSet.class);
    }
    
    public void createCollegeTableForTest() {
    	createTableForTest(College.TABLE_NAME, College.class);
    }
    
    public void createRuleSetRowTableForTest() {
    	createTableForTest(RuleSetRow.TABLE_NAME, RuleSetRow.class);
    }

	private void deleteTable(String tableName) {
		ListTablesResult tables = client.listTables();
		if (tables.getTableNames().contains(tableName)) {
			 client.deleteTable(tableName);
		}
	}
	
	@SuppressWarnings("rawtypes")
    private void createTableForTest(String tableName, Class clazz) {
		deleteTable(tableName);
		tableService.createTable(tableName, clazz);
	}


    
    public FamilyDTO loadCollege(String fileName) {
        FamilyDTO collegeDTO = readObjectFromFile(fileName, FamilyDTO.class);
        if (!StringUtils.isBlank(collegeDTO.getId())) {
            Table table = dynamoDB.getTable(College.TABLE_NAME);        
            table.deleteItem("id", collegeDTO.getId());
        }
        
        try {
            collegeService.save(collegeDTO);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return collegeDTO;        
        
    }
    
    public RuleDTO loadRule(String fileName) {
        RuleDTO ruleDTO = readObjectFromFile(fileName, RuleDTO.class);
        if (!StringUtils.isBlank(ruleDTO.getId())) {
            Table table = dynamoDB.getTable(Rule.TABLE_NAME);
            try {
                table.deleteItem("id", ruleDTO.getId());
            }
            catch (Exception ignore) {
            }
        }

        try {
            ruleService.save(ruleDTO);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
        return ruleDTO;
    }
    

    public RuleSetDTO loadRuleSet(String fileName) {
        RuleSetDTO ruleSetDTO = readObjectFromFile(fileName, RuleSetDTO.class);
        if (!StringUtils.isBlank(ruleSetDTO.getId())) {
            Table table = dynamoDB.getTable(RuleSet.TABLE_NAME);
            try {
                table.deleteItem("id", ruleSetDTO.getId());
            }
            catch (Exception ignore) {} 
        }
        
        try {
            ruleSetService.save(ruleSetDTO);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return ruleSetDTO;
    }
    
    public RuleSetRowDTO loadRuleSetRow(String fileName) {
    	RuleSetRowDTO ruleSetRowDTO = readObjectFromFile(fileName, RuleSetRowDTO.class);
        if (!StringUtils.isBlank(ruleSetRowDTO.getId())) {
            Table table = dynamoDB.getTable(RuleSetRow.TABLE_NAME);
            try {
                table.deleteItem("id", ruleSetRowDTO.getId());
            }
            catch (Exception ignore) {} 
        }
        
        try {
            return ruleSetRowService.save(ruleSetRowDTO);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        return null;
    }
        
    public <T> T readObjectFromFile(String fileName, Class<T> clazz) {
        T obj = null;
        try {
            InputStream ruleDTOIs = getClass().getResourceAsStream(fileName);
            obj = mapper.readValue(ruleDTOIs, clazz);
        } catch (JsonParseException e) {
            fail(e.getMessage());
        } catch (JsonMappingException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        }
        return obj;
    }
}
