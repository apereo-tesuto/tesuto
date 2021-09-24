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
package org.cccnext.tesuto.content.model.shared;

import java.util.List;

import org.cccnext.tesuto.content.dto.AssessmentBaseType;
import org.cccnext.tesuto.content.dto.item.AssessmentCardinality;
import org.cccnext.tesuto.content.model.AbstractAssessment;
import org.cccnext.tesuto.content.model.item.AssessmentDefaultValue;

/**
 * Alias: outcomeDeclaration
 *
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentOutcomeDeclaration implements AbstractAssessment {
    private static final long serialVersionUID = 1l;

    private String identifier;
    private AssessmentCardinality cardinality;
    private AssessmentBaseType baseType;
    private List<String> views; // Not for pilot
    private String interpretation;
    private String longInterpretation;
    private Double normalMaximum;
    private Double normalMinimum;
    private Double masteryValue;
    private String externalScored;  // TODO: Update QTIWorks to 2.2
    private String variableIdentifierRef; //TODO: Update QTIWorks to 2.2
    private AssessmentDefaultValue defaultValue; // TODO: Switch to enum instead
                                                 // of class?
    private String lookupTable; // Not for pilot

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String id) {
        this.identifier = id;
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

    public AssessmentDefaultValue getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(AssessmentDefaultValue defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getLookupTable() {
        return lookupTable;
    }

    public void setLookupTable(String lookupTable) {
        this.lookupTable = lookupTable;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((baseType == null) ? 0 : baseType.hashCode());
        result = prime * result + ((cardinality == null) ? 0 : cardinality.hashCode());
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((interpretation == null) ? 0 : interpretation.hashCode());
        result = prime * result + ((externalScored == null) ? 0 : externalScored.hashCode());
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((longInterpretation == null) ? 0 : longInterpretation.hashCode());
        result = prime * result + ((lookupTable == null) ? 0 : lookupTable.hashCode());
        result = prime * result + ((masteryValue == null) ? 0 : masteryValue.hashCode());
        result = prime * result + ((normalMaximum == null) ? 0 : normalMaximum.hashCode());
        result = prime * result + ((normalMinimum == null) ? 0 : normalMinimum.hashCode());
        result = prime * result + ((variableIdentifierRef == null) ? 0 : variableIdentifierRef.hashCode());
        result = prime * result + ((views == null) ? 0 : views.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AssessmentOutcomeDeclaration other = (AssessmentOutcomeDeclaration) obj;
        if (baseType != other.baseType)
            return false;
        if (cardinality != other.cardinality)
            return false;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (interpretation == null) {
            if (other.interpretation != null)
                return false;
        } else if (!interpretation.equals(other.interpretation))
            return false;
        if (externalScored == null) {
            if (other.externalScored != null)
                return false;
        } else if (!externalScored.equals(other.externalScored))
            return false;
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;
        if (longInterpretation == null) {
            if (other.longInterpretation != null)
                return false;
        } else if (!longInterpretation.equals(other.longInterpretation))
            return false;
        if (lookupTable == null) {
            if (other.lookupTable != null)
                return false;
        } else if (!lookupTable.equals(other.lookupTable))
            return false;
        if (masteryValue == null) {
            if (other.masteryValue != null)
                return false;
        } else if (!masteryValue.equals(other.masteryValue))
            return false;
        if (normalMaximum == null) {
            if (other.normalMaximum != null)
                return false;
        } else if (!normalMaximum.equals(other.normalMaximum))
            return false;
        if (normalMinimum == null) {
            if (other.normalMinimum != null)
                return false;
        } else if (!normalMinimum.equals(other.normalMinimum))
            return false;
        if (variableIdentifierRef == null) {
            if (other.variableIdentifierRef != null)
                return false;
        } else if (!variableIdentifierRef.equals(other.variableIdentifierRef))
            return false;
        if (views == null) {
            if (other.views != null)
                return false;
        } else if (!views.equals(other.views))
            return false;
        return true;
    }
}
