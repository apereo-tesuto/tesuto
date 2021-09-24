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
package org.ccctc.common.droolsdb.dynamodb.model;

import java.util.Map;

import org.springframework.data.annotation.Id;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;


@DynamoDBTable(tableName =College.TABLE_NAME)
public class College {
	
	public static final String TABLE_NAME ="drools-editor-college";
    private static final String ATTR_ID = "id";
    private static final String ATTR_CCCID = "cccMisCode";
    private static final String ATTR_DESCRIPTION = "description";
    private static final String ATTR_STATUS = "status";
    private static final String ATTR_APPLICATION = "applications";

    @Id
    @DynamoDBHashKey(attributeName = ATTR_ID)
    private String id;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    @DynamoDBAttribute(attributeName = ATTR_CCCID)
//    @DynamoDBIndexHashKey(attributeName = ATTR_CCCID, globalSecondaryIndexName="cccMisCode-index")
    private String cccMisCode;
    public String getFamily() {
        return this.cccMisCode;
    }
    public void setFamily(String cccMisCode) {
        this.cccMisCode = cccMisCode;
    }
    
    @DynamoDBAttribute(attributeName = ATTR_DESCRIPTION)
    private String description;
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
    @DynamoDBAttribute(attributeName = ATTR_STATUS)
//    @DynamoDBIndexHashKey(attributeName = ATTR_STATUS, globalSecondaryIndexName="status-index")
    private String status;
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    
    @DynamoDBAttribute(attributeName = ATTR_APPLICATION)
    private Map<String, Application> applications;
    public Map<String, Application> getEngines() {
        return this.applications;        
    }
    public Application getEngine(String applicationName) {
        return applications.get(applicationName);
    }
    public void setEngines(Map<String, Application> applications) {
        this.applications = applications;
    }
}
