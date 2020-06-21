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
package org.ccctc.common.droolscommon.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RuleSetDTO extends RuleAttributesDTO {
    private static final long serialVersionUID = 1L;

    private String ruleSetDrl;

    private String ruleSetDrlValidationCsv;

    private List<String> ruleSetRowIds;

    public void adjustValuesForStorage() {
        super.adjustValuesForStorage();
        ruleSetDrl = StringUtils.isBlank(ruleSetDrl) ? null : ruleSetDrl;
        ruleSetDrlValidationCsv = StringUtils.isBlank(ruleSetDrlValidationCsv) ? null : ruleSetDrlValidationCsv;
    }

    public void adjustValuesForUI() {
        super.adjustValuesForUI();
        ruleSetDrl = ruleSetDrl == null ? "" : ruleSetDrl;
        ruleSetDrlValidationCsv = ruleSetDrlValidationCsv == null ? "" : ruleSetDrlValidationCsv;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    public String getRuleSetDrl() {
        return ruleSetDrl;
    }

    public String getRuleSetDrlValidationCsv() {
        return ruleSetDrlValidationCsv;
    }

    public List<String> getRuleSetRowIds() {
        if (ruleSetRowIds == null) {
            ruleSetRowIds = new ArrayList<String>();
        }
        return ruleSetRowIds;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public void setRuleSetDrl(String ruleSetDrl) {
        this.ruleSetDrl = ruleSetDrl;
    }

    public void setRuleSetDrlValidationCsv(String ruleSetDrlValidationCsv) {
        this.ruleSetDrlValidationCsv = ruleSetDrlValidationCsv;
    }

    public RuleSetDTO setRuleSetRowIds(List<String> ruleSetRowIds) {
        this.ruleSetRowIds = ruleSetRowIds;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
