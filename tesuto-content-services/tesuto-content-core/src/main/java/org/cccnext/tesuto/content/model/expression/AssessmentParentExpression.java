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
package org.cccnext.tesuto.content.model.expression;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cccnext.tesuto.content.model.AbstractAssessment;
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionType;

import java.util.List;

/**
 * Created by jasonbrown on 2/5/16.
 */
public class AssessmentParentExpression implements AbstractAssessment {
    private static final long serialVersionUID = 1l;

    private ExpressionType expressionType;
    private List<AssessmentParentExpression> assessmentParentExpressionList;
    private List<AssessmentChildExpression> assessmentChildExpressionList;

    public ExpressionType getExpressionType() {
        return expressionType;
    }

    public void setExpressionType(ExpressionType expressionType) {
        this.expressionType = expressionType;
    }

    public List<AssessmentParentExpression> getAssessmentParentExpressionList() {
        return assessmentParentExpressionList;
    }

    public void setAssessmentParentExpressionList(List<AssessmentParentExpression> assessmentParentExpressionList) {
        this.assessmentParentExpressionList = assessmentParentExpressionList;
    }

    public List<AssessmentChildExpression> getAssessmentChildExpressionList() {
        return assessmentChildExpressionList;
    }

    public void setAssessmentChildExpressionList(List<AssessmentChildExpression> assessmentChildExpressionList) {
        this.assessmentChildExpressionList = assessmentChildExpressionList;
    }

    @Override
    public boolean equals(Object o) {
        return new EqualsBuilder().reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().reflectionHashCode(this);
    }
}
