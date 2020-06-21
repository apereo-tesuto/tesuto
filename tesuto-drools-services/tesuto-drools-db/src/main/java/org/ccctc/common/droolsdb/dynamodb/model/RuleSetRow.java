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

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = RuleSetRow.TABLE_NAME)
public class RuleSetRow extends RuleAttributes {

	public static final String TABLE_NAME = "drools-editor-rule-set-row";
	private static final String ATTR_RULE_ID = "ruleId";

	 @DynamoDBAttribute(attributeName = ATTR_RULE_ID)
	private String ruleId;

	private List<RuleVariableRow> variableRows;
	
	private String validationCsv;

	public String getRuleId() {
		return ruleId;
	}

	public RuleSetRow setRuleId(String ruleId) {
		this.ruleId = ruleId;
		return this;
	}

	public List<RuleVariableRow> getVariableRows() {
		return variableRows;
	}

	public void setVariableRows(List<RuleVariableRow> variableRows) {
		this.variableRows = variableRows;
	}
	
	public String getValidationCsv() {
		return validationCsv;
	}

	public void setValidationCsv(String validationCsv) {
		this.validationCsv = validationCsv;
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
