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
package org.cccnext.tesuto.delivery.service;

import org.cccnext.tesuto.content.dto.expression.AssessmentChildExpressionDto;
import org.cccnext.tesuto.content.model.scoring.EvaluationScoringModel;
import org.cccnext.tesuto.content.service.scoring.ChildExpressionEvaluationService;

import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionType;

import java.util.HashMap;

/**
 * Created by jasonbrown on 5/12/16.
 */
public class ChildExpressionEvaluationServiceDelivery implements ChildExpressionEvaluationService {

    public boolean evaluateChildExpression(AssessmentChildExpressionDto childExpressionDto,
            EvaluationScoringModel evaluationScoringModel) {
        return evaluateChildExpressionDelivery(childExpressionDto, evaluationScoringModel.getSectionScores());
    }

    /**
     * @param childExpressionDto
     * @param sectionScores:    key is expected to be in the format of
     *                           assessmentExpressionDto.getVariable() (ie.
     *                           "a001_testlet5.Score")
     * @return
     */
    private boolean evaluateChildExpressionDelivery(AssessmentChildExpressionDto childExpressionDto,
            HashMap<String, Double> sectionScores) {
        if (childExpressionDto == null || sectionScores == null || sectionScores.isEmpty()
                || !sectionScores.containsKey(childExpressionDto.getVariable())
                || sectionScores.get(childExpressionDto.getVariable()) == null) {
            return false;
        }

        ExpressionType expressionType = childExpressionDto.getExpressionType();
        switch (expressionType) {
        //student received: sectionDetails.get(childExpressionDto.getVariable()
        //branch rule value to compare to: childExpressionDto.getBaseValue()
        case GT:
            return (Double.compare(childExpressionDto.getBaseValue(),
                    sectionScores.get(childExpressionDto.getVariable())) == -1);
        case GTE:
            return (Double.compare(childExpressionDto.getBaseValue(),
                    sectionScores.get(childExpressionDto.getVariable())) <= 0);
        case EQUAL:
            return (Double.compare(childExpressionDto.getBaseValue(),
                    sectionScores.get(childExpressionDto.getVariable())) == 0);
        case LTE:
            return (Double.compare(childExpressionDto.getBaseValue(),
                    sectionScores.get(childExpressionDto.getVariable())) >= 0);
        case LT:
            return (Double.compare(childExpressionDto.getBaseValue(),
                    sectionScores.get(childExpressionDto.getVariable())) == 1);
        default:
            throw new IllegalArgumentException(
                    String.format("Child Expressions do not support a QTIWorks expression type of %s", expressionType));
        }
    }
}
