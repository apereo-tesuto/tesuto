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

import java.util.List;

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInlineChoiceDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInlineChoiceInteractionDto;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentInlineChoice;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentInlineChoiceInteraction;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "inlineChoiceInteractionDtoAssembler")
public class AssessmentInlineChoiceInteractionDtoAssemblerImpl
        implements AssessmentInlineChoiceInteractionDtoAssembler {

    @Autowired
    AssessmentInlineChoiceDtoAssembler assessmentInlineChoiceDtoAssembler;

    @Override
    public AssessmentInlineChoiceInteractionDto assembleDto(
            AssessmentInlineChoiceInteraction assessmentInlineChoiceInteraction) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentInlineChoiceInteraction == null) {
            return null;
        }

        AssessmentInlineChoiceInteractionDto assessmentInlineChoiceInteractionDto = new AssessmentInlineChoiceInteractionDto();
        assessmentInlineChoiceInteractionDto.setType(assessmentInlineChoiceInteraction.getType());

        assessmentInlineChoiceInteractionDto.setInlineChoices(
                assessmentInlineChoiceDtoAssembler.assembleDto(assessmentInlineChoiceInteraction.getInlineChoices()));

        assessmentInlineChoiceInteractionDto.setPrompt(assessmentInlineChoiceInteraction.getPrompt());
        assessmentInlineChoiceInteractionDto
                .setResponseIdentifier(assessmentInlineChoiceInteraction.getResponseIdentifier());
        assessmentInlineChoiceInteractionDto.setUiid(assessmentInlineChoiceInteraction.getUiid());

        return assessmentInlineChoiceInteractionDto;
    }

    @Override
    public AssessmentInlineChoiceInteraction disassembleDto(
            AssessmentInlineChoiceInteractionDto assessmentInlineChoiceInteractionDto) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentInlineChoiceInteractionDto == null) {
            return null;
        }

        AssessmentInlineChoiceInteraction assessmentInlineChoiceInteraction = new AssessmentInlineChoiceInteraction();
        assessmentInlineChoiceInteraction.setType(assessmentInlineChoiceInteractionDto.getType());
        assessmentInlineChoiceInteractionDto.setId(assessmentInlineChoiceInteractionDto.getId());

        List<AssessmentInlineChoiceDto> assessmentInlineChoiceDtoList = assessmentInlineChoiceInteractionDto
                .getInlineChoices();
        List<AssessmentInlineChoice> assessmentInlineChoiceList = assessmentInlineChoiceDtoAssembler
                .disassembleDto(assessmentInlineChoiceDtoList);
        assessmentInlineChoiceInteraction.setInlineChoices(assessmentInlineChoiceList);

        assessmentInlineChoiceInteraction.setPrompt(assessmentInlineChoiceInteractionDto.getPrompt());
        assessmentInlineChoiceInteraction
                .setResponseIdentifier(assessmentInlineChoiceInteractionDto.getResponseIdentifier());
        assessmentInlineChoiceInteraction.setUiid(assessmentInlineChoiceInteractionDto.getUiid());

        return assessmentInlineChoiceInteraction;
    }
}
