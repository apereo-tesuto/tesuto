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
package org.ccctc.droolseditor.services;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolscommon.model.RuleAttributesDTO;
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolscommon.model.RuleSetRowDTO;
import org.ccctc.common.droolsdb.dynamodb.model.College;
import org.ccctc.common.droolsdb.dynamodb.model.Rule;
import org.ccctc.common.droolsdb.dynamodb.model.RuleSet;
import org.ccctc.common.droolsdb.dynamodb.model.RuleSetRow;
import org.ccctc.common.droolsdb.dynamodb.services.DynamoDBTableService;
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
import com.amazonaws.services.dynamodbv2.model.DeleteTableResult;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class DynamoDBTest implements InitializingBean {
    private static final String COLLEGE_TABLENAME = "drools-editor-college";
    private static final String RULE_TABLENAME = "drools-editor-rule";
    private static final String RULESET_TABLENAME = "drools-editor-ruleset";

    static private void validateAttributes(RuleAttributesDTO attributes) {
        if (StringUtils.isBlank(attributes.getEngine())) {
            throw new RuntimeException(String.format("Object does not have non null attribute values", attributes));
        }
        if (StringUtils.isBlank(attributes.getCategory())) {
            throw new RuntimeException(String.format("Object does not have non null attribute values", attributes));
        }
        if (StringUtils.isBlank(attributes.getFamily())) {
            throw new RuntimeException(String.format("Object does not have non null attribute values", attributes));
        }
        if (StringUtils.isBlank(attributes.getCompetencyMapDiscipline())) {
            throw new RuntimeException(String.format("Object does not have non null attribute values", attributes));
        }
        if (StringUtils.isBlank(attributes.getEvent())) {
            throw new RuntimeException(String.format("Object does not have non null attribute values", attributes));
        }
        if (StringUtils.isBlank(attributes.getId())) {
            throw new RuntimeException(String.format("Object does not have non null attribute values", attributes));
        }
        if (StringUtils.isBlank(attributes.getStatus())) {
            throw new RuntimeException(String.format("Object does not have non null attribute values", attributes));
        }
        if (StringUtils.isBlank(attributes.getTitle())) {
            throw new RuntimeException(String.format("Object does not have non null attribute values", attributes));
        }

        if (StringUtils.isBlank(attributes.getDescription())) {
            throw new RuntimeException(String.format("Object does not have non null attribute values", attributes));
        }
    }

    @Autowired
    public AmazonDynamoDB client;

    @Autowired
    private IFamilyService familyService;

    private DynamoDB dynamoDB;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private RuleService ruleService;

    @Autowired
    private RuleSetRowService ruleSetRowService;

    @Autowired
    private RuleSetService ruleSetService;

    @Autowired
    private DynamoDBTableService tableService;

    @Override
    public void afterPropertiesSet() throws Exception {
        dynamoDB = new DynamoDB(client);
    }

    public void createCollegeTableForTest() {
        createTableForTest(College.TABLE_NAME, College.class);
    }

    public void createRuleSetRowTableForTest() {
        this.createTableForTest(RuleSetRow.TABLE_NAME, RuleSetRow.class);
    }

    public void createRuleSetTableForTest() {
        createTableForTest(RuleSet.TABLE_NAME, RuleSet.class);
    }

    public void createRuleTableForTest() {
        createTableForTest(Rule.TABLE_NAME, Rule.class);
    }

    @SuppressWarnings("rawtypes")
    private void createTableForTest(String tableName, Class clazz) {
        deleteTable(tableName);
        tableService.createTable(tableName, clazz);
    }

    private void deleteTable(String tableName) {
        ListTablesResult tables = client.listTables();
        if (tables.getTableNames().contains(tableName)) {
            DeleteTableResult result = client.deleteTable(tableName);
            if (!result.getTableDescription().getTableStatus().equals("DELETING")) {

            }
        }
    }

    public FamilyDTO loadFamily(String fileName) {
        FamilyDTO familyDTO = readObjectFromFile(fileName, FamilyDTO.class);
        if (!StringUtils.isBlank(familyDTO.getId())) {
            Table table = dynamoDB.getTable(COLLEGE_TABLENAME);
            table.deleteItem("id", familyDTO.getId());
        }

        try {
            familyService.save(familyDTO);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
        return familyDTO;

    }

    public RuleDTO loadRule(String fileName) {
        RuleDTO ruleDTO = readObjectFromFile(fileName, RuleDTO.class);
        if (!StringUtils.isBlank(ruleDTO.getId())) {
            Table table = dynamoDB.getTable(RULE_TABLENAME);
            table.deleteItem("id", ruleDTO.getId());
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
            Table table = dynamoDB.getTable(RULESET_TABLENAME);
            table.deleteItem("id", ruleSetDTO.getId());
        }

        try {
            ruleSetService.save(ruleSetDTO);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
        return ruleSetDTO;
    }

    public RuleSetRowDTO loadRuleSetRow(String fileName) {
        RuleSetRowDTO ruleSetRowDTO = readObjectFromFile(fileName, RuleSetRowDTO.class);
        if (!StringUtils.isBlank(ruleSetRowDTO.getId())) {
            Table table = dynamoDB.getTable(RuleSetRow.TABLE_NAME);
            table.deleteItem("id", ruleSetRowDTO.getId());
        }

        try {
            return ruleSetRowService.save(ruleSetRowDTO);
        }
        catch (Exception e) {
            fail(e.getMessage());
        }
        return null;
    }

    public <T> T readObjectFromFile(String fileName, Class<T> clazz) {
        T obj = null;
        try {
            InputStream ruleDTOIs = getClass().getResourceAsStream(fileName);
            obj = mapper.readValue(ruleDTOIs, clazz);
        }
        catch (JsonParseException e) {
            fail(e.getMessage());
        }
        catch (JsonMappingException e) {
            fail(e.getMessage());
        }
        catch (IOException e) {
            fail(e.getMessage());
        }
        if (obj instanceof RuleAttributesDTO) {
            validateAttributes((RuleAttributesDTO) obj);
        }
        return obj;
    }
}
