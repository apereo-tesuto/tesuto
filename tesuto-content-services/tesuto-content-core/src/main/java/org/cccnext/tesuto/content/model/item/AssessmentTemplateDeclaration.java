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
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentTemplateDeclaration implements AbstractAssessment {
    private static final long serialVersionUID = 1l;

    private String id;
    private AssessmentCardinality cardinality;
    private AssessmentBaseType baseType;
    private Boolean paramVariable;
    private Boolean isMathVar;
    private AssessmentDefaultValue defaultValue;

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

    public AssessmentDefaultValue getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(AssessmentDefaultValue defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((baseType == null) ? 0 : baseType.hashCode());
        result = prime * result + ((cardinality == null) ? 0 : cardinality.hashCode());
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((isMathVar == null) ? 0 : isMathVar.hashCode());
        result = prime * result + ((paramVariable == null) ? 0 : paramVariable.hashCode());
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
        AssessmentTemplateDeclaration other = (AssessmentTemplateDeclaration) obj;
        if (baseType != other.baseType)
            return false;
        if (cardinality != other.cardinality)
            return false;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (isMathVar == null) {
            if (other.isMathVar != null)
                return false;
        } else if (!isMathVar.equals(other.isMathVar))
            return false;
        if (paramVariable == null) {
            if (other.paramVariable != null)
                return false;
        } else if (!paramVariable.equals(other.paramVariable))
            return false;
        return true;
    }
}
