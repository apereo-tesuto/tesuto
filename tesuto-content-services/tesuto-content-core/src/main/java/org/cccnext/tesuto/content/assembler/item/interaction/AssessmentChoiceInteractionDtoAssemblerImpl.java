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

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentChoiceInteractionDto;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentChoiceInteraction;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "choiceInteractionDtoAssembler")
public class AssessmentChoiceInteractionDtoAssemblerImpl implements AssessmentChoiceInteractionDtoAssembler {

    @Autowired
    AssessmentSimpleChoiceDtoAssembler simpleChoiceDtoAssembler;

    @Override
    public AssessmentChoiceInteractionDto assembleDto(AssessmentChoiceInteraction choiceInteraction) {
        // Drop out immediately if there is nothing to assemble.
        if (choiceInteraction == null) {
            return null;
        }

        AssessmentChoiceInteractionDto choiceInteractionDto = new AssessmentChoiceInteractionDto();
        choiceInteractionDto.setType(choiceInteraction.getType());

        choiceInteractionDto.setChoices(simpleChoiceDtoAssembler.assembleDto(choiceInteraction.getChoices()));

        choiceInteractionDto.setMaxChoices(choiceInteraction.getMaxChoices());
        choiceInteractionDto.setMinChoices(choiceInteraction.getMinChoices());
        choiceInteractionDto.setPrompt(choiceInteraction.getPrompt());
        choiceInteractionDto.setResponseIdentifier(choiceInteraction.getResponseIdentifier());
        choiceInteractionDto.setUiid(choiceInteraction.getUiid());

        return choiceInteractionDto;
    }

    @Override
    public AssessmentChoiceInteraction disassembleDto(AssessmentChoiceInteractionDto choiceInteractionDto) {
        // Drop out immediately if there is nothing to assemble.
        if (choiceInteractionDto == null) {
            return null;
        }

        AssessmentChoiceInteraction choiceInteraction = new AssessmentChoiceInteraction();
        choiceInteraction.setType(choiceInteractionDto.getType());
        choiceInteractionDto.setId(choiceInteractionDto.getId());

        choiceInteraction.setChoices(simpleChoiceDtoAssembler.disassembleDto(choiceInteractionDto.getChoices()));

        choiceInteraction.setMaxChoices(choiceInteractionDto.getMaxChoices());
        choiceInteraction.setMinChoices(choiceInteraction.getMinChoices());
        choiceInteraction.setPrompt(choiceInteractionDto.getPrompt());
        choiceInteraction.setResponseIdentifier(choiceInteractionDto.getResponseIdentifier());
        choiceInteraction.setUiid(choiceInteractionDto.getUiid());

        return choiceInteraction;
    }
}
