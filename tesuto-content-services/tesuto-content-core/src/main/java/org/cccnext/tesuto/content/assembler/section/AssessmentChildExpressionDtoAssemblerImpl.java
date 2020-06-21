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

import org.cccnext.tesuto.content.dto.expression.AssessmentChildExpressionDto;
import org.cccnext.tesuto.content.model.expression.AssessmentChildExpression;
import org.springframework.stereotype.Component;

/**
 * Created by jasonbrown on 2/6/16.
 */
@Component(value = "childExpressionDtoAssembler")
public class AssessmentChildExpressionDtoAssemblerImpl implements AssessmentChildExpressionDtoAssembler {
    @Override
    public AssessmentChildExpressionDto assembleDto(AssessmentChildExpression assessmentChildExpression) {
        if (assessmentChildExpression == null) {
            return null;
        }
        AssessmentChildExpressionDto assessmentChildExpressionDto = new AssessmentChildExpressionDto();
        assessmentChildExpressionDto.setExpressionType(assessmentChildExpression.getExpressionType());
        assessmentChildExpressionDto.setVariable(assessmentChildExpression.getVariable());
        assessmentChildExpressionDto.setBaseValue(assessmentChildExpression.getBaseValue());
        return assessmentChildExpressionDto;
    }

    @Override
    public AssessmentChildExpression disassembleDto(AssessmentChildExpressionDto assessmentChildExpressionDto) {
        if (assessmentChildExpressionDto == null) {
            return null;
        }
        AssessmentChildExpression assessmentChildExpression = new AssessmentChildExpression();
        assessmentChildExpression.setExpressionType(assessmentChildExpressionDto.getExpressionType());
        assessmentChildExpression.setVariable(assessmentChildExpressionDto.getVariable());
        assessmentChildExpression.setBaseValue(assessmentChildExpressionDto.getBaseValue());
        return assessmentChildExpression;
    }
}
