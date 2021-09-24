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
package org.cccnext.tesuto.content.dto.expression;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;

import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionType;

/**
 * Created by jasonbrown on 2/6/16.
 */
public class AssessmentChildExpressionDto implements AbstractAssessmentDto {
    private static final long serialVersionUID = 1l;

    private ExpressionType expressionType;
    private String variable;  // QtiWorks Naming convention
    private Double baseValue; // QtiWorks Naming convention

    public ExpressionType getExpressionType() {
        return expressionType;
    }

    public void setExpressionType(ExpressionType expressionType) {
        this.expressionType = expressionType;
    }

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public Double getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(Double baseValue) {
        this.baseValue = baseValue;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
