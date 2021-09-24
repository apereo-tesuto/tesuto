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
package org.cccnext.tesuto.content.service.scoring;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.content.dto.expression.AssessmentChildExpressionDto;
import org.cccnext.tesuto.content.dto.expression.AssessmentExpressionDto;
import org.cccnext.tesuto.content.dto.expression.AssessmentParentExpressionDto;
import org.cccnext.tesuto.content.model.scoring.EvaluationScoringModel;

import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionType;

/**
 * Created by jasonbrown on 2/8/16.
 */
public class ExpressionEvaluationService {

    private ChildExpressionEvaluationService childExpressionEvaluationService;

    public ChildExpressionEvaluationService getChildExpressionEvaluationService() {
        return childExpressionEvaluationService;
    }

    public void setChildExpressionEvaluationService(ChildExpressionEvaluationService childExpressionEvaluationService) {
        this.childExpressionEvaluationService = childExpressionEvaluationService;
    }

    public boolean evaluate(AssessmentExpressionDto expressionDto, EvaluationScoringModel evaluationScoringModel) {
        if (expressionDto == null || expressionDto.getAssessmentParentExpression() == null) {
            return false;
        }

        return evaluateParentExpression(expressionDto.getAssessmentParentExpression(), evaluationScoringModel);
    }

    private boolean evaluateParentExpression(AssessmentParentExpressionDto assessmentParentExpressionDto,
            EvaluationScoringModel evaluationScoringModel) {
        if (assessmentParentExpressionDto == null
                || evaluationScoringModel.getType() == null
                || (evaluationScoringModel.getType() == EvaluationScoringModel.EvaluationType.DELIVERY && evaluationScoringModel.getSectionScores() == null)
                || (evaluationScoringModel.getType() == EvaluationScoringModel.EvaluationType.DELIVERY && evaluationScoringModel.getSectionScores().isEmpty()) ) {
            return false;
        }

        if (assessmentParentExpressionDto.getExpressionType() == ExpressionType.AND) {
            // Everyone of the lists must return true
            if (CollectionUtils.isNotEmpty(assessmentParentExpressionDto.getAssessmentParentExpressionDtoList())) {
                for (AssessmentParentExpressionDto expressionDto : assessmentParentExpressionDto.getAssessmentParentExpressionDtoList()) {
                    if (!evaluateParentExpression(expressionDto, evaluationScoringModel)) {
                        return false;
                    }
                }
            }

            if (CollectionUtils.isNotEmpty(assessmentParentExpressionDto.getAssessmentChildExpressionDtoList())) {
                for (AssessmentChildExpressionDto childExpressionDto : assessmentParentExpressionDto.getAssessmentChildExpressionDtoList()) {
                    if (!childExpressionEvaluationService.evaluateChildExpression(childExpressionDto, evaluationScoringModel)) {
                        return false;
                    }
                }
            }
            return true;
        } else if (assessmentParentExpressionDto.getExpressionType() == ExpressionType.OR) {
            // only one of the lists must return true
            if (CollectionUtils.isNotEmpty(assessmentParentExpressionDto.getAssessmentParentExpressionDtoList())) {
                for (AssessmentParentExpressionDto expressionDto : assessmentParentExpressionDto.getAssessmentParentExpressionDtoList()) {
                    if (evaluateParentExpression(expressionDto, evaluationScoringModel)) {
                        return true;
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(assessmentParentExpressionDto.getAssessmentChildExpressionDtoList())) {
                for (AssessmentChildExpressionDto childExpressionDto : assessmentParentExpressionDto.getAssessmentChildExpressionDtoList()) {
                    if (childExpressionEvaluationService.evaluateChildExpression(childExpressionDto, evaluationScoringModel)) {
                        return true;
                    }
                }
            }
            return false;
        } else if (assessmentParentExpressionDto.getExpressionType() == null) {
            if (CollectionUtils.isNotEmpty(assessmentParentExpressionDto.getAssessmentChildExpressionDtoList())) {
                for (AssessmentChildExpressionDto childExpressionDto : assessmentParentExpressionDto.getAssessmentChildExpressionDtoList()) {
                    if (!childExpressionEvaluationService.evaluateChildExpression(childExpressionDto, evaluationScoringModel)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        } else {
            throw new IllegalArgumentException(
                    String.format("Parent Expressions do not support a QTIWorks expression type of %s",
                            assessmentParentExpressionDto.getExpressionType()));
        }
    }
}
