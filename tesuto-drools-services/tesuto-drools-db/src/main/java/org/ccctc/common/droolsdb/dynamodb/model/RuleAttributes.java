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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "drools-editor-rule-attributes")
public abstract class RuleAttributes  {

	private static final String ATTR_ID = "id";
    private static final String ATTR_CCCMISCODE = "cccMisCode";
    private static final String ATTR_APPLICATION = "application";
    private static final String ATTR_VERSION = "version";
    private static final String ATTR_STATUS = "status";
    private static final String ATTR_EVENT = "event";
    private static final String ATTR_CATEGORY = "category";
    private static final String ATTR_COMPETENCY_MAP_DISCIPLINE = "competencyMapDiscipline";
    
    @DynamoDBHashKey(attributeName = ATTR_ID)
    private String id;
    @DynamoDBAttribute(attributeName = ATTR_CCCMISCODE)
//    @DynamoDBIndexHashKey(attributeName = ATTR_CCCMISCODE, globalSecondaryIndexName="cccMisCode-index")
    private String cccMisCode;
    @DynamoDBAttribute(attributeName = ATTR_CATEGORY)
//    @DynamoDBIndexHashKey(attributeName = ATTR_CATEGORY, globalSecondaryIndexName="category-index")
    private String category;
    @DynamoDBAttribute(attributeName = ATTR_APPLICATION)
//    @DynamoDBIndexHashKey(attributeName = ATTR_APPLICATION, globalSecondaryIndexName="application-index")
    private String application;
    @DynamoDBAttribute(attributeName = ATTR_VERSION)
    private String version;
    @DynamoDBAttribute(attributeName = ATTR_STATUS)
//    @DynamoDBIndexHashKey(attributeName = ATTR_STATUS, globalSecondaryIndexName="status-index")
    private String status;
    @DynamoDBAttribute(attributeName = ATTR_EVENT)
    private String event;
    @DynamoDBAttribute(attributeName = ATTR_COMPETENCY_MAP_DISCIPLINE)
//    @DynamoDBIndexHashKey(attributeName = ATTR_COMPETENCY_MAP_DISCIPLINE, globalSecondaryIndexName="competencyMapDiscipline-index")
    private String competencyMapDiscipline;
    
    private String title;
    
    private String description;
    
    public String getId() {
        return id;
    }
    public RuleAttributes setId(String id) {
        this.id = id;
        return this;
    }
    public String getFamily() {
        return cccMisCode;
    }
    public RuleAttributes setFamily(String cccMisCode) {
        this.cccMisCode = cccMisCode;
        return this;
    }
    public String getEngine() {
        return application;
    }
    public RuleAttributes setEngine(String applicationId) {
        this.application = applicationId;
        return this;
    }
    public String getVersion() {
        return version;
    }
    public RuleAttributes setVersion(String version) {
        this.version = version;
        return this;
    }
    public String getStatus() {
        return status;
    }
    public RuleAttributes setStatus(String status) {
        this.status = status;
        return this;
    }
	public String getEvent() {
		return event;
	}
	public RuleAttributes setEvent(String event) {
		this.event = event;
		return this;
	}
	public String getCompetencyMapDiscipline() {
		return competencyMapDiscipline;
	}
	public void setCompetencyMapDiscipline(String competencyMapDiscipline) {
		this.competencyMapDiscipline = competencyMapDiscipline;
	}
	public String getTitle() {
		return title;
	}
	public RuleAttributes setTitle(String title) {
		this.title = title;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public RuleAttributes setDescription(String description) {
		this.description = description;
		return this;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
    
}
