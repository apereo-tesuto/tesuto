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
package org.cccnext.tesuto.importer.qti.service.validate;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.content.dto.expression.AssessmentChildExpressionDto;
import org.cccnext.tesuto.content.model.scoring.EvaluationScoringModel;
import org.cccnext.tesuto.content.service.scoring.ChildExpressionEvaluationService;
import org.springframework.beans.factory.annotation.Value;

import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionType;

import java.util.SortedSet;

/**
 * Created by jasonbrown on 5/12/16.
 */
public class ChildExpressionEvaluationServiceImport implements ChildExpressionEvaluationService {

    @Value("${full.branch.rule.evaluation:false}")
    private Boolean useFullBranchRuleEvaluation;

    public boolean evaluateChildExpression(AssessmentChildExpressionDto childExpressionDto,
            EvaluationScoringModel evaluationScoringModel) {
        return evaluateChildExpressionValidation(childExpressionDto, evaluationScoringModel.getPossibleScores());
    }

    /**
     * @param childExpressionDto
     * @param possibleScores     all possible scores for a section
     * @return
     */
    private boolean evaluateChildExpressionValidation(AssessmentChildExpressionDto childExpressionDto,
            SortedSet<Double> possibleScores) {
        if (childExpressionDto == null || CollectionUtils.isEmpty(possibleScores)) {
            return false;
        }

        ExpressionType expressionType = childExpressionDto.getExpressionType();
        switch (expressionType) {
        //student received: sectionDetails.get(childExpressionDto.getVariable()
        //branch rule value to compare to: childExpressionDto.getBaseValue()
        case GT:
            return (Double.compare(possibleScores.last(), childExpressionDto.getBaseValue()) == 1);
        case GTE:
            return (Double.compare(possibleScores.last(), childExpressionDto.getBaseValue()) >= 0);
        case EQUAL:
            if(useFullBranchRuleEvaluation) {
                return possibleScores.contains(childExpressionDto.getBaseValue());
            }else{
                return (Double.compare(possibleScores.first(), childExpressionDto.getBaseValue()) <= 0) && (
                        Double.compare(childExpressionDto.getBaseValue(), possibleScores.last()) <= 0);
            }
        case LTE:
            return (Double.compare(possibleScores.first(), childExpressionDto.getBaseValue()) <= 0);
        case LT:
            return (Double.compare(possibleScores.first(), childExpressionDto.getBaseValue()) == -1);
        default:
            throw new IllegalArgumentException(
                    String.format("Child Expressions do not support a QTIWorks expression type of %s", expressionType));
        }
    }
}
