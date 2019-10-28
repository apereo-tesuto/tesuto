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
package org.cccnext.tesuto.content.dto.section;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;
import org.cccnext.tesuto.content.dto.AssessmentComponentDto;
import org.cccnext.tesuto.content.dto.expression.AssessmentBranchRuleDto;
import org.cccnext.tesuto.content.dto.expression.AssessmentPreConditionDto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentItemRefDto implements AbstractAssessmentDto, AssessmentComponentDto {
    private static final long serialVersionUID = 1l;

    private String identifier;
    private boolean isRequired;
    private boolean isFixed;
    private String itemIdentifier;
    private List<String> categories;
    private List<AssessmentPreConditionDto> preConditions; // TODO: Consider a
                                                           // listing of
                                                           // enumerations for
                                                           // this.
    private List<AssessmentBranchRuleDto> branchRules; // TODO: Consider a
                                                       // listing of
                                                       // enumerations for this.
    private AssessmentItemSessionControlDto itemSessionControl;// TODO: Consider
                                                               // a listing of
                                                               // enumerations
                                                               // for this.
    private Double timeLimits; // Not for pilot
    private String variableMapping; // Not for pilot
    private double weight; // Not for pilot
    private List<String> templateDefaultss; // Not for pilot

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getId() {
        return getIdentifier();
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setIsRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public void setIsFixed(boolean isFixed) {
        this.isFixed = isFixed;
    }

    public String getItemIdentifier() {
        return itemIdentifier;
    }

    public void setItemIdentifier(String itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<AssessmentPreConditionDto> getPreConditions() {
        return preConditions;
    }

    public void setPreConditions(List<AssessmentPreConditionDto> preConditions) {
        this.preConditions = preConditions;
    }

    public List<AssessmentBranchRuleDto> getBranchRules() {
        return branchRules;
    }

    public void setBranchRules(List<AssessmentBranchRuleDto> branchRules) {
        this.branchRules = branchRules;
    }

    public AssessmentItemSessionControlDto getItemSessionControl() {
        return itemSessionControl;
    }

    public void setItemSessionControl(AssessmentItemSessionControlDto itemSessionControl) {
        this.itemSessionControl = itemSessionControl;
    }

    public Double getTimeLimits() {
        return timeLimits;
    }

    public void setTimeLimits(Double timeLimits) {
        this.timeLimits = timeLimits;
    }

    public String getVariableMapping() {
        return variableMapping;
    }

    public void setVariableMapping(String variableMapping) {
        this.variableMapping = variableMapping;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public List<String> getTemplateDefaultss() {
        return templateDefaultss;
    }

    public void setTemplateDefaultss(List<String> templateDefaultss) {
        this.templateDefaultss = templateDefaultss;
    }

    @JsonIgnore
    public Stream<AssessmentComponentDto> getChildren() {
        return Stream.empty();
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(o, this);

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
