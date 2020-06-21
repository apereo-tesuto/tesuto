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

import java.util.Set;

import org.springframework.data.annotation.Id;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName =Application.TABLE_NAME)
public class Application {
    
    public static final String TABLE_NAME ="drools-editor-application";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_STATUS = "status";
    private static final String ATTR_DATA_SOURCE = "dataSource";
    private static final String ATTR_GROUP_ID = "groupId";
    private static final String ATTR_ARTIFACT_ID = "artifactId";
    private static final String ATTR_VERSION = "version";
    
    @Id
    @DynamoDBHashKey(attributeName = ATTR_NAME)
    private String name;
    @DynamoDBAttribute(attributeName = ATTR_STATUS)
    private String status;
    @DynamoDBAttribute(attributeName = ATTR_DATA_SOURCE)
    private String dataSource;
    @DynamoDBAttribute(attributeName = ATTR_GROUP_ID)
    private String groupId;
    @DynamoDBAttribute(attributeName = ATTR_ARTIFACT_ID)
    private String artifactId;
    @DynamoDBAttribute(attributeName = ATTR_VERSION)
    private String version;
    
    private Set<String> events;
    private Set<String> categories;

   
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getDataSource() {
        return dataSource;
    }
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getArtifactId() {
        return artifactId;
    }
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }
    public String getVersion() {
        return version;
    }
    public Set<String> getEvents() {
        return events;
    }
    public void setEvents(Set<String> events) {
        this.events = events;
    }
    public Set<String> getCategories() {
        return categories;
    }
    public void setCategories(Set<String> categories) {
        this.categories = categories;
    }
    public void setVersion(String versionId) {
        this.version = versionId;
    }
}
