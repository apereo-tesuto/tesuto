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

import org.cccnext.tesuto.content.dto.expression.AssessmentPreConditionDto;
import org.cccnext.tesuto.content.model.expression.AssessmentPreCondition;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "preconditionDtoAssembler")
public class AssessmentPreconditionDtoAssemblerImpl implements AssessmentPreconditionDtoAssembler {

    @Autowired
    AssessmentParentExpressionDtoAssembler assessmentParentExpressionDtoAssembler;
    @Override
    public AssessmentPreConditionDto assembleDto(AssessmentPreCondition preCondition) {
        // Drop out immediately if there is nothing to assemble.
        if (preCondition == null) {
            return null;
        }

        AssessmentPreConditionDto preConditionDto = new AssessmentPreConditionDto();
        preConditionDto.setAssessmentParentExpression(
                assessmentParentExpressionDtoAssembler.assembleDto(preCondition.getAssessmentParentExpression()) );
        return preConditionDto;
    }

    @Override
    public AssessmentPreCondition disassembleDto(AssessmentPreConditionDto preConditionDto) {
        // Drop out immediately if there is nothing to assemble.
        if (preConditionDto == null) {
            return null;
        }

        AssessmentPreCondition preCondition = new AssessmentPreCondition();
        preCondition.setAssessmentParentExpression(assessmentParentExpressionDtoAssembler.disassembleDto(preConditionDto.getAssessmentParentExpression()));
        return preCondition;
    }
}
