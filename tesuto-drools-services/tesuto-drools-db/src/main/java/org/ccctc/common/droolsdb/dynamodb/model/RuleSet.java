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

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = RuleSet.TABLE_NAME)
public class RuleSet extends RuleAttributes {
    
	public static final String TABLE_NAME = "drools-editor-rule-set";
	
    private List<String> ruleSetRowIds;
    
    private String ruleSetDrl;
    
    private String ruleSetDrlValidationCsv;
    
    public List<String> getRuleSetRowIds() {
        return ruleSetRowIds;
    }
    public void setRuleSetRowIds(List<String> ruleSetRowIds) {
        this.ruleSetRowIds = ruleSetRowIds;
    }
    public String getRuleSetDrl() {
        return ruleSetDrl;
    }
    public void setRuleSetDrl(String ruleSetDrl) {
        this.ruleSetDrl = ruleSetDrl;
    }
    public String getRuleSetDrlValidationCsv() {
        return ruleSetDrlValidationCsv;
    }
    public void setRuleSetDrlValidationCsv(String ruleSetDrlValidationCsv) {
        this.ruleSetDrlValidationCsv = ruleSetDrlValidationCsv;
    }
}
