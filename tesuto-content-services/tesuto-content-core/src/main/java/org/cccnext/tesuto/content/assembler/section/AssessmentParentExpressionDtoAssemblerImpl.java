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
package org.cccnext.tesuto.content.assembler.section;

import org.cccnext.tesuto.content.dto.expression.AssessmentParentExpressionDto;
import org.cccnext.tesuto.content.model.expression.AssessmentParentExpression;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "parentExpressionDtoAssembler")
public class AssessmentParentExpressionDtoAssemblerImpl implements AssessmentParentExpressionDtoAssembler {

    @Autowired
    AssessmentChildExpressionDtoAssembler childExpressionDtoAssembler;

    @Override
    public AssessmentParentExpressionDto assembleDto(AssessmentParentExpression assessmentParentExpression) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentParentExpression == null) {
            return null;
        }

        AssessmentParentExpressionDto assessmentParentExpressionDto = new AssessmentParentExpressionDto();
        assessmentParentExpressionDto.setExpressionType(assessmentParentExpression.getExpressionType());
        assessmentParentExpressionDto.setAssessmentParentExpressionDtoList(
                assembleDto(assessmentParentExpression.getAssessmentParentExpressionList()));
        assessmentParentExpressionDto.setAssessmentChildExpressionDtoList(
                childExpressionDtoAssembler.assembleDto(assessmentParentExpression.getAssessmentChildExpressionList()));
        return assessmentParentExpressionDto;
    }

    @Override
    public AssessmentParentExpression disassembleDto(AssessmentParentExpressionDto assessmentParentExpressionDto) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentParentExpressionDto == null) {
            return null;
        }

        AssessmentParentExpression assessmentParentExpression = new AssessmentParentExpression();
        assessmentParentExpression.setExpressionType(assessmentParentExpressionDto.getExpressionType());
        assessmentParentExpression.setAssessmentParentExpressionList(
                disassembleDto(assessmentParentExpressionDto.getAssessmentParentExpressionDtoList()));
        assessmentParentExpression.setAssessmentChildExpressionList(childExpressionDtoAssembler
                .disassembleDto(assessmentParentExpressionDto.getAssessmentChildExpressionDtoList()));
        return assessmentParentExpression;
    }
}
