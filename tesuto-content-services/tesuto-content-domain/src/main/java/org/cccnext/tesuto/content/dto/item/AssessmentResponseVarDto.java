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
package org.cccnext.tesuto.content.dto.item;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;
import org.cccnext.tesuto.content.dto.AssessmentBaseType;

/**
 * QTI: ResponseDeclaration
 *
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentResponseVarDto implements AbstractAssessmentDto {
    private static final long serialVersionUID = 1l;

    private String identifier;
    private AssessmentCardinality cardinality;
    private AssessmentBaseType baseType;
    private AssessmentDefaultValueDto defaultValue;
    private AssessmentCorrectResponseDto correctResponse;
    private AssessmentItemResponseMappingDto mapping;

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

    public AssessmentDefaultValueDto getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(AssessmentDefaultValueDto defaultValue) {
        this.defaultValue = defaultValue;
    }

    public AssessmentCorrectResponseDto getCorrectResponse() {
        return correctResponse;
    }

    public void setCorrectResponse(AssessmentCorrectResponseDto correctResponse) {
        this.correctResponse = correctResponse;
    }

    public AssessmentItemResponseMappingDto getMapping() {
        return mapping;
    }

    public void setMapping(AssessmentItemResponseMappingDto mapping) {
        this.mapping = mapping;
    }

    // TODO: Add areaMapping, not for pilot

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
