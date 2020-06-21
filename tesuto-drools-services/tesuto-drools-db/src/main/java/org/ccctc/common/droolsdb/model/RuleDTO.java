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
package org.ccctc.common.droolsdb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.ccctc.common.droolscommon.model.RuleAttributesDTO;

public class RuleDTO extends RuleAttributesDTO {
    private static final long serialVersionUID = 1L;
    
    private String conditionFreetext;
    private List<ConditionRowDTO> conditionRows;
    private String actionFreetext;
    private List<ActionRowDTO> actionRows;
    private Map<String, String> variableTypes;
    private List<String> customImports;
    private String testTokenValues;
    private String customFunctions;

    public List<ConditionRowDTO> getConditionRows() {
        if (this.conditionRows == null) {
            this.conditionRows = new ArrayList<ConditionRowDTO>();
        }
        return conditionRows;
    }

    public RuleDTO setConditionRows(List<ConditionRowDTO> conditionRows) {
        this.conditionRows = conditionRows;
        return this;
    }

    public List<ActionRowDTO> getActionRows() {
        if (this.actionRows == null) {
            this.actionRows = new ArrayList<ActionRowDTO>();
        }
        return this.actionRows;
    }

    public RuleDTO setActionRows(List<ActionRowDTO> actionRows) {
        this.actionRows = actionRows;
        return this;
    }

    public String getConditionFreetext() {
        return conditionFreetext;
    }

    public RuleDTO setConditionFreetext(String conditionFreetext) {
        this.conditionFreetext = conditionFreetext;
        return this;
    }

    public String getActionFreetext() {
        return actionFreetext;
    }

    public RuleDTO setActionFreetext(String actionFreetext) {
        this.actionFreetext = actionFreetext;
        return this;
    }

    public Map<String, String> getVariableTypes() {
        if (variableTypes == null) {
            variableTypes = new HashMap<String, String>();
        }
        return variableTypes;
    }

    public RuleDTO setVariableTypes(Map<String, String> variableTypes) {
        this.variableTypes = variableTypes;
        return this;
    }

    public RuleDTO setCustomImports(List<String> customImports) {
        this.customImports = customImports;
        return this;
    }

    public List<String> getCustomImports() {
        if (customImports == null) {
            this.customImports = new ArrayList<String>();
        }
        return this.customImports;
    }

    public String getTestTokenValues() {
        return testTokenValues;
    }

    public void setTestTokenValues(String testTokenValues) {
        this.testTokenValues = testTokenValues;
    }

    public String getCustomFunctions() {
        return customFunctions;
    }

    public void setCustomFunctions(String customFunctions) {
        this.customFunctions = customFunctions;
    }

    @Override
    public String toString() {
        return "{" + getId() + ":" + getEngine() + ":" + getTitle() + ":" + getVersion() + ":" + getStatus() + ":"
                        + conditionFreetext + ":" + "[" + StringUtils.join(conditionRows, ",") + "]:" + actionFreetext + ":" + "["
                        + StringUtils.join(actionRows, ",") + "]}";
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
