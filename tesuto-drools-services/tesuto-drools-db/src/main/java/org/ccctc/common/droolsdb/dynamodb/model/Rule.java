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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = Rule.TABLE_NAME)
public class Rule  extends RuleAttributes  {
	public static final String TABLE_NAME = "drools-editor-rule";
    private static final String ATTR_FREETEXT = "actionFreetext";
    
    @DynamoDBAttribute(attributeName = ATTR_FREETEXT)
    private String actionFreetext;
    private String conditionFreetext;
    private List<ConditionRow> conditionRows;
    private List<ActionRow> actionRows;
    private Map<String, String> variableTypes;
    private List<String> customImports;
    private String testTokenValues;
    
    public void setActionFreetext(String freetext) {
        this.actionFreetext = freetext;
    }
    public String getActionFreetext() {
        return this.actionFreetext;
    }
    public String getConditionFreetext() {
        return conditionFreetext;
    }
    public void setConditionFreetext(String conditionFreetext) {
        this.conditionFreetext = conditionFreetext;
    }
    public List<ConditionRow> getConditionRows() {
        return conditionRows;
    }
    public void setConditionRows(List<ConditionRow> conditionRows) {
        this.conditionRows = conditionRows;
    }
    public List<ActionRow> getActionRows() {
        return actionRows;
    }
    public void setActionRows(List<ActionRow> actionRows) {
        this.actionRows = actionRows;
    }
    public Map<String, String> getVariableTypes() {
        if (variableTypes == null) {
            variableTypes = new HashMap<String, String>();
        }
        return variableTypes;
    }
    public void setVariableTypes(Map<String, String> variableTypes) {
        this.variableTypes = variableTypes;
    }
    public List<String> getCustomImports() {
        if (customImports == null) {
            this.customImports = new ArrayList<String>();
        }
        return customImports;
    }
    public void setCustomImports(List<String> customImports) {
        this.customImports = customImports;
    }
    
	public String getTestTokenValues() {
		return testTokenValues;
	}
	public void setTestTokenValues(String testTokenValues) {
		this.testTokenValues = testTokenValues;
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
