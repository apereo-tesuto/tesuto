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
package org.cccnext.tesuto.content.assembler.item.interaction;

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInlineChoiceDto;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentInlineChoice;


import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "inlineChoiceDtoAssembler")
public class AssessmentInlineChoiceDtoAssemblerImpl implements AssessmentInlineChoiceDtoAssembler {
    @Override
    public AssessmentInlineChoiceDto assembleDto(AssessmentInlineChoice assessmentInlineChoice) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentInlineChoice == null) {
            return null;
        }

        AssessmentInlineChoiceDto assessmentInlineChoiceDto = new AssessmentInlineChoiceDto();
        assessmentInlineChoiceDto.setContent(assessmentInlineChoice.getContent());
        assessmentInlineChoiceDto.setId(assessmentInlineChoice.getId());
        assessmentInlineChoiceDto.setIdentifier(assessmentInlineChoice.getIdentifier());
        assessmentInlineChoiceDto.setType(assessmentInlineChoice.getType());
        assessmentInlineChoiceDto.setUiid(assessmentInlineChoice.getUiid());
        return assessmentInlineChoiceDto;
    }

    @Override
    public AssessmentInlineChoice disassembleDto(AssessmentInlineChoiceDto assessmentInlineChoiceDto) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentInlineChoiceDto == null) {
            return null;
        }

        AssessmentInlineChoice assessmentInlineChoice = new AssessmentInlineChoice();
        assessmentInlineChoice.setContent(assessmentInlineChoiceDto.getContent());
        assessmentInlineChoice.setId(assessmentInlineChoiceDto.getId());
        assessmentInlineChoice.setIdentifier(assessmentInlineChoiceDto.getIdentifier());
        assessmentInlineChoice.setType(assessmentInlineChoiceDto.getType());
        assessmentInlineChoice.setUiid(assessmentInlineChoiceDto.getUiid());
        return assessmentInlineChoice;
    }
}
