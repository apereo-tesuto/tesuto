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
package org.cccnext.tesuto.service.multiplemeasures;

import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.*;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.domain.multiplemeasures.Student;
import org.cccnext.tesuto.domain.multiplemeasures.VariableSet;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig.TableNameOverride;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class OperationalDataStoreServiceImpl implements OperationalDataStoreService {

    private Mapper dozer = new DozerBeanMapper();

    private AmazonDynamoDB client;

    DynamoDBMapperConfig studentTableConfig;
    DynamoDBMapperConfig variableSetTableConfig;

    private long readCapacity = 5;
    private long writeCapacity = 5;

    public OperationalDataStoreServiceImpl() {
        client  = AmazonDynamoDBClientBuilder.defaultClient();
    }

    public  OperationalDataStoreServiceImpl(AmazonDynamoDB client) {
        this.client = client;
    }

    public void setStudentTableName(String studentTableName, String prefix) {
        if(StringUtils.isBlank(prefix)) {
          log.debug("Setting studentTableName to " + studentTableName);
          studentTableConfig = (new TableNameOverride(studentTableName)).config();
        } else {
          log.debug("Setting studentTableName to " + prefix+studentTableName);
          studentTableConfig = new TableNameOverride(prefix+studentTableName).config();
         }
    }

    public void setStudentTableName(String studentTableName) {
       setStudentTableName(studentTableName, null);
    }

    public void setVariableSetTableName(String variableSetTableName) {
        setVariableSetTableName( variableSetTableName, null) ;
    }

    public void setVariableSetTableName(String variableSetTableName, String prefix) {
      if(StringUtils.isBlank(prefix)) {
        log.warn("Setting variableSetTableName to "+ variableSetTableName);
        variableSetTableConfig  = (new TableNameOverride(variableSetTableName)).config();
      } else {
        log.warn("Setting variableSetTableName to " + prefix+variableSetTableName);
        variableSetTableConfig  = new TableNameOverride(prefix+variableSetTableName).config();
      }
    }

    public long getReadCapacity() {
        return readCapacity;
    }

    public void setReadCapacity(long readCapacity) {
        this.readCapacity = readCapacity;
    }

    public long getWriteCapacity() {
        return writeCapacity;
    }

    public void setWriteCapacity(long writeCapacity) {
        this.writeCapacity = writeCapacity;
    }

    public void setClient(AmazonDynamoDB client) {
        this.client = client;
    }


    private DynamoDBMapper studentDb() {
        return new DynamoDBMapper(client, studentTableConfig);
    }

    private DynamoDBMapper variableSetDb() {
        return new DynamoDBMapper(client, variableSetTableConfig);
    }

    private void doAddVariableSet(String cccId, VariableSet variableSet) {
        StudentMapper mapper = studentDb().load(StudentMapper.class, cccId);
        mapper.getVariableSets().add(variableSet);
        studentDb().save(mapper);
    }

    @Override
    public void addVariableSet(String cccId, VariableSet variableSet) {
        VariableSetMapper mapper = dozer.map(variableSet, VariableSetMapper.class);
        mapper.setCccId(cccId);
        mapper.setFacts(new HashSet<>(variableSet.getFacts().values()));
        variableSetDb().save(mapper);
        VariableSet variableSetClone = dozer.map(variableSet,VariableSet.class);
        variableSetClone.setFacts(new HashMap<>()); //don't store facts with student
        try {
            doAddVariableSet(cccId, variableSetClone);
        } catch (ConditionalCheckFailedException e) {
            //In case this is an optimistic locking error, retry once
            doAddVariableSet(cccId, variableSetClone);
        }
    }

    @Override
    public void setRegionByName(String regionName) {
        client.setRegion(RegionUtils.getRegion(regionName));
    }

    @Override
    public void createStudent(Student student) {
        StudentMapper mapper = dozer.map(student, StudentMapper.class);
        mapper.setVariableSets(new HashSet<>());
        studentDb().save(mapper);
    }

    private void createTable(Class clazz, DynamoDBMapperConfig config) {
                ListTablesResult tables = client.listTables();
        if (!tables.getTableNames().contains(config.getTableNameOverride().getTableName())) {
            DynamoDBMapper db = new DynamoDBMapper(client, studentTableConfig);
            CreateTableRequest request = db.generateCreateTableRequest(clazz, config);
            request.setProvisionedThroughput(new ProvisionedThroughput(readCapacity, writeCapacity));
            client.createTable(request);
        }
    }

    @Override
    public void createStudentTable() {
        createTable(StudentMapper.class, studentTableConfig);
    }

    @Override
    public void createVariableSetTable() {
        createTable(VariableSetMapper.class, variableSetTableConfig);
    }

    @Override
    public Student fetchStudent(String cccId) {
        StudentMapper mapper = studentDb().load(StudentMapper.class, cccId);
        if (mapper == null) {
            return null;
        } else {
            return dozer.map(mapper, Student.class);
        }
    }

    private VariableSet assembleVariableSet(VariableSetMapper mapper) {
        VariableSet variableSet = dozer.map(mapper, VariableSet.class);
        variableSet.setFacts(new HashMap<>());
        variableSet.setId(mapper.getId());
        mapper.getFacts().forEach(fact -> {
            variableSet.getFacts().put(fact.getName(), fact);
        });
        return variableSet;
    }

    @Override
    public Set<VariableSet> fetchStudentFacts(String cccId) {
        Map<String,AttributeValue> attributeValues = new HashMap<String,AttributeValue>();
        attributeValues.put(":cccId", new AttributeValue().withS(cccId));
        DynamoDBQueryExpression<VariableSetMapper> expression = new DynamoDBQueryExpression<VariableSetMapper>()
                .withKeyConditionExpression("cccId=:cccId")
                .withExpressionAttributeValues(attributeValues);
        return variableSetDb().query(VariableSetMapper.class, expression)
                .stream().map(item -> assembleVariableSet(item))
                .collect(Collectors.toSet());
    }

    @Override
    public VariableSet fetchFacts(String cccId, VariableSet variableSet) {
        return fetchVariableSetById(cccId, variableSet.getId());
    }

    @Override
    public VariableSet fetchVariableSetById(String cccId, String variableSetId) {
        VariableSetMapper mapper = variableSetDb()
                .load(VariableSetMapper.class, cccId, variableSetId);
        return assembleVariableSet(mapper);
    }

}
