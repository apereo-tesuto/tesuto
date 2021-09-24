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

import java.util.List;

/**
 * Created by jasonbrown on 2/5/16.
 */
public class AssessmentParentExpressionDto implements AbstractAssessmentDto {
    private static final long serialVersionUID = 1l;

    private ExpressionType expressionType;
    private List<AssessmentParentExpressionDto> assessmentParentExpressionDtoList;
    private List<AssessmentChildExpressionDto> assessmentChildExpressionDtoList;

    public ExpressionType getExpressionType() {
        return expressionType;
    }

    public void setExpressionType(ExpressionType expressionType) {
        this.expressionType = expressionType;
    }

    public List<AssessmentParentExpressionDto> getAssessmentParentExpressionDtoList() {
        return assessmentParentExpressionDtoList;
    }

    public void setAssessmentParentExpressionDtoList(
            List<AssessmentParentExpressionDto> assessmentParentExpressionDtoList) {
        this.assessmentParentExpressionDtoList = assessmentParentExpressionDtoList;
    }

    public List<AssessmentChildExpressionDto> getAssessmentChildExpressionDtoList() {
        return assessmentChildExpressionDtoList;
    }

    public void setAssessmentChildExpressionDtoList(
            List<AssessmentChildExpressionDto> assessmentChildExpressionDtoList) {
        this.assessmentChildExpressionDtoList = assessmentChildExpressionDtoList;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
