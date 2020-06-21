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
package org.cccnext.tesuto.content.model.item;

import org.cccnext.tesuto.content.dto.AssessmentBaseType;
import org.cccnext.tesuto.content.dto.item.AssessmentCardinality;
import org.cccnext.tesuto.content.model.AbstractAssessment;

/**
 * QTI: ResponseDeclaration
 *
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentResponseVar implements AbstractAssessment {
    private static final long serialVersionUID = 1l;

    private String identifier;
    private AssessmentCardinality cardinality;
    private AssessmentBaseType baseType;
    private AssessmentDefaultValue defaultValue;
    private AssessmentCorrectResponse correctResponse;
    private AssessmentItemResponseMapping mapping;

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

    public AssessmentDefaultValue getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(AssessmentDefaultValue defaultValue) {
        this.defaultValue = defaultValue;
    }

    public AssessmentCorrectResponse getCorrectResponse() {
        return correctResponse;
    }

    public void setCorrectResponse(AssessmentCorrectResponse correctResponse) {
        this.correctResponse = correctResponse;
    }

    public AssessmentItemResponseMapping getMapping() {
        return mapping;
    }

    public void setMapping(AssessmentItemResponseMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((baseType == null) ? 0 : baseType.hashCode());
        result = prime * result + ((cardinality == null) ? 0 : cardinality.hashCode());
        result = prime * result + ((correctResponse == null) ? 0 : correctResponse.hashCode());
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((mapping == null) ? 0 : mapping.hashCode());
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
        AssessmentResponseVar other = (AssessmentResponseVar) obj;
        if (baseType != other.baseType)
            return false;
        if (cardinality != other.cardinality)
            return false;
        if (correctResponse == null) {
            if (other.correctResponse != null)
                return false;
        } else if (!correctResponse.equals(other.correctResponse))
            return false;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;

        if (mapping == null) {
            if (other.mapping != null)
                return false;
        } else if (!mapping.equals(other.mapping))
            return false;
        return true;
    }
}
