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

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentTextEntryInteractionDto;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentTextEntryInteraction;


import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "textEntryInteractionDtoAssembler")
public class AssessmentTextEntryInteractionDtoAssemblerImpl implements AssessmentTextEntryInteractionDtoAssembler {

    @Override
    public AssessmentTextEntryInteractionDto assembleDto(AssessmentTextEntryInteraction textEntryInteraction) {
        // Drop out immediately if there is nothing to assemble.
        if (textEntryInteraction == null) {
            return null;
        }

        AssessmentTextEntryInteractionDto textEntryInteractionDto = new AssessmentTextEntryInteractionDto();
        textEntryInteractionDto.setType(textEntryInteraction.getType());
        textEntryInteractionDto.setId(textEntryInteraction.getId());

        textEntryInteractionDto.setPlaceholderText(textEntryInteraction.getPlaceholderText());

        textEntryInteractionDto.setResponseIdentifier(textEntryInteraction.getResponseIdentifier());
        textEntryInteractionDto.setExpectedLength(textEntryInteraction.getExpectedLength());
        textEntryInteractionDto.setPatternMask(textEntryInteraction.getPatternMask());
        textEntryInteractionDto.setUiid(textEntryInteraction.getUiid());

        return textEntryInteractionDto;
    }

    @Override
    public AssessmentTextEntryInteraction disassembleDto(AssessmentTextEntryInteractionDto textEntryInteractionDto) {
        // Drop out immediately if there is nothing to assemble.
        if (textEntryInteractionDto == null) {
            return null;
        }

        AssessmentTextEntryInteraction textEntryInteraction = new AssessmentTextEntryInteraction();
        textEntryInteraction.setType(textEntryInteractionDto.getType());
        textEntryInteraction.setId(textEntryInteractionDto.getId());

        textEntryInteraction.setPlaceholderText(textEntryInteractionDto.getPlaceholderText());

        textEntryInteraction.setResponseIdentifier(textEntryInteractionDto.getResponseIdentifier());
        textEntryInteraction.setExpectedLength(textEntryInteractionDto.getExpectedLength());
        textEntryInteraction.setPatternMask(textEntryInteractionDto.getPatternMask());
        textEntryInteraction.setUiid(textEntryInteractionDto.getUiid());

        return textEntryInteraction;
    }

}
