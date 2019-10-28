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
package org.cccnext.tesuto.rules.config;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreService;
import org.cccnext.tesuto.service.multiplemeasures.OperationalDataStoreServiceImpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Configuration
public class OperationalDataStoreConfig {
    
    @Value("${operationalDataStore.keyId}") private String keyId;

    @Value(value = "${ods.student.table.name}")
    private String studentTableName;
    
    @Value(value = "${ods.variableset.table.name}")
    private String variableSetTableName;
    
    @Value(value = "${aws.tablePrefix}")
    private String tablePrefix;
    
    @Value(value = "${aws.endpoint}")
    private String awsEndpoint;

    @Value(value = "${cloud.aws.region.static}")
    private String cloudAwsRegionStatic;
    
    @Value(value = "${spring.profiles.active}")
    private String activeProfiles;
    
    @Autowired
    AmazonDynamoDB amazonDynamoDB;
    
    
    @Bean(name="operationalDataStore")
    public OperationalDataStoreService operationalDataStore() throws Exception {
    	List<String> profiles = Arrays.asList(activeProfiles.split(","));
    	if(profiles.contains("local"))
    		return operationalDataStoreServiceLocal();
    	return operationalDataStoreServiceAWS();
    }
    
    public OperationalDataStoreService operationalDataStoreServiceLocal() throws Exception {
        OperationalDataStoreServiceImpl service  = new OperationalDataStoreServiceImpl();
        service.setClient(amazonDynamoDB);
        String prefix = StringUtils.isBlank(tablePrefix) ? "" : tablePrefix + "-";
        service.setStudentTableName(prefix + studentTableName);
        service.setVariableSetTableName(prefix + variableSetTableName);
        service.createStudentTable();
        service.createVariableSetTable();
        return service;
    }
    
    public OperationalDataStoreService operationalDataStoreServiceAWS() {

        if(StringUtils.isBlank(keyId)) {
            log.error("No key has been set for the aws dynamodb. Exiting,");
            System.exit(1);
        }
        if (StringUtils.isBlank(studentTableName)){
            log.error("studentMultipleMeasuresTable is empty and needs to be set");
            System.exit(1);
        }
        if (StringUtils.isBlank(variableSetTableName)) {
            log.error("variableSetTableName is empty and needs to be set");
            System.exit(1);
        }
        
        OperationalDataStoreServiceImpl ods = new OperationalDataStoreServiceImpl();
      
        ods.setClient(amazonDynamoDB);
        String prefix = StringUtils.isBlank(tablePrefix) ? "" : tablePrefix + "-" ;
        ods.setStudentTableName(prefix + studentTableName);
        ods.setVariableSetTableName(prefix + variableSetTableName);
        log.info("KEY FOR OPERATIONAL DATA STORE:" + keyId);
        return ods;
    }
    
}
