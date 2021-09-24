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

import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;
import org.cccnext.tesuto.content.dto.AssessmentBaseType;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentTemplateDeclarationDto implements AbstractAssessmentDto {
    private static final long serialVersionUID = 1l;

    private String id;
    private AssessmentCardinality cardinality;
    private AssessmentBaseType baseType;
    private Boolean paramVariable;
    private Boolean isMathVar;
    private AssessmentDefaultValueDto defaultValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Boolean isParamVariable() {
        return paramVariable;
    }

    public void setParamVariable(Boolean paramVariable) {
        this.paramVariable = paramVariable;
    }

    public Boolean isMathVar() {
        return isMathVar;
    }

    public void setIsMathVar(Boolean isMathVar) {
        this.isMathVar = isMathVar;
    }

    public AssessmentDefaultValueDto getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(AssessmentDefaultValueDto defaultValue) {
        this.defaultValue = defaultValue;
    }
}
