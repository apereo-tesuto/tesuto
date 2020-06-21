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

import org.apache.commons.lang3.StringUtils;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class DynamoDBTableService implements InitializingBean {
    @Autowired
    public AmazonDynamoDB client;

    @Autowired
    private Environment env;
   
    private long readCapacity = 5;

    private long writeCapacity = 5;

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    private DynamoDBMapper db() {
        return new DynamoDBMapper(client);
    }

    @SuppressWarnings("rawtypes")
    public void createTable(String tableName, Class clazz) {
        String tablePrefix = env.getProperty("aws.tablePrefix", "");
        if (!StringUtils.isBlank(tablePrefix)) {
            tableName = tablePrefix + "-" + tableName;
        }
        ListTablesResult tables = client.listTables();
        if (!tables.getTableNames().contains(tableName)) {
            if (log.isDebugEnabled()) {
                log.error("Table [" + tableName + "] is missing, recreating");
            }
            CreateTableRequest request = db().generateCreateTableRequest(clazz);
            request.setTableName(tableName);
            request.setProvisionedThroughput(new ProvisionedThroughput(readCapacity, writeCapacity));
            if (request.getGlobalSecondaryIndexes() != null && request.getGlobalSecondaryIndexes().size() > 0) {
                for (GlobalSecondaryIndex index : request.getGlobalSecondaryIndexes()) {
                    Long readCapacity = 5l;
                    Long writeCapacity = 5l;
                    index.setProvisionedThroughput(new ProvisionedThroughput(readCapacity, writeCapacity));
                }
            }
            client.createTable(request);
        }
    }

}
