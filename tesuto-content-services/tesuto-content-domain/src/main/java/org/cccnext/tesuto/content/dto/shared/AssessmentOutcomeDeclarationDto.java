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
package org.cccnext.tesuto.content.dto.shared;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;
import org.cccnext.tesuto.content.dto.AssessmentBaseType;
import org.cccnext.tesuto.content.dto.item.AssessmentCardinality;
import org.cccnext.tesuto.content.dto.item.AssessmentDefaultValueDto;

/**
 * Alias: outcomeDeclaration
 *
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentOutcomeDeclarationDto implements AbstractAssessmentDto {
    private static final long serialVersionUID = 1l;

    private String identifier;
    private AssessmentCardinality cardinality;
    private AssessmentBaseType baseType;
    private List<String> views; // Not for pilot
    private String interpretation;
    private String longInterpretation; // Not for pilot
    private Double normalMaximum;
    private Double normalMinimum;
    private Double masteryValue; // Not for pilot
    private String externalScored;  // TODO: Update QTIWorks to 2.2
    private String variableIdentifierRef; //TODO: Update QTIWorks to 2.2
    private AssessmentDefaultValueDto defaultValue; // TODO: Switch to enum
                                                    // instead of class?
    private String lookupTable; // Not for pilot

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public AssessmentCardinality getCardinality() {
        return cardinality;
    }

    public void setCardinality(AssessmentCardinality cardinality) {
        this.cardinality = cardinality;
    }

    public AssessmentBaseType getBaseType() {
        return baseType;
    }

    public void setBaseType(AssessmentBaseType baseType) {
        this.baseType = baseType;
    }

    public List<String> getViews() {
        return views;
    }

    public void setViews(List<String> views) {
        this.views = views;
    }

    public String getInterpretation() {
        return interpretation;
    }

    public void setInterpretation(String interpretation) {
        this.interpretation = interpretation;
    }

    public String getLongInterpretation() {
        return longInterpretation;
    }

    public void setLongInterpretation(String longInterpretation) {
        this.longInterpretation = longInterpretation;
    }

    public Double getNormalMaximum() {
        return normalMaximum;
    }

    public void setNormalMaximum(Double normalMaximum) {
        this.normalMaximum = normalMaximum;
    }

    public Double getNormalMinimum() {
        return normalMinimum;
    }

    public void setNormalMinimum(Double normalMinimum) {
        this.normalMinimum = normalMinimum;
    }

    public Double getMasteryValue() {
        return masteryValue;
    }

    public void setMasteryValue(Double masteryValue) {
        this.masteryValue = masteryValue;
    }

    public String getExternalScored() {
        return externalScored;
    }

    public void setExternalScored(String externalScored) {
        this.externalScored = externalScored;
    }

    public String getVariableIdentifierRef() {
        return variableIdentifierRef;
    }

    public void setVariableIdentifierRef(String variableIdentifierRef) {
        this.variableIdentifierRef = variableIdentifierRef;
    }

    public AssessmentDefaultValueDto getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(AssessmentDefaultValueDto defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getLookupTable() {
        return lookupTable;
    }

    public void setLookupTable(String lookupTable) {
        this.lookupTable = lookupTable;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).reflectionToString(this);
    }
}
