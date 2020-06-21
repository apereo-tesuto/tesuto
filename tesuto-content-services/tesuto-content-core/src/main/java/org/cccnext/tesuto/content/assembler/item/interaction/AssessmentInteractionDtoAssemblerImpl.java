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
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentExtendedTextInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInlineChoiceInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentMatchInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentTextEntryInteractionDto;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentChoiceInteraction;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentExtendedTextInteraction;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentInlineChoiceInteraction;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentInteraction;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentMatchInteraction;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentTextEntryInteraction;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "interactionDtoAssembler")
public class AssessmentInteractionDtoAssemblerImpl implements AssessmentInteractionDtoAssembler {
    @Autowired
    AssessmentChoiceInteractionDtoAssembler assessmentChoiceInteractionDtoAssembler;
    @Autowired
    AssessmentInlineChoiceInteractionDtoAssembler assessmentInlineChoiceInteractionDtoAssembler;
    @Autowired
    AssessmentTextEntryInteractionDtoAssembler assessmentTextEntryInteractionDtoAssembler;
    @Autowired
    AssessmentExtendedTextInteractionDtoAssembler assessmentExtendedTextInteractionDtoAssembler;
    @Autowired
    AssessmentMatchInteractionDtoAssembler assessmentMatchInteractionDtoAssembler;

    @Override
    public AssessmentInteractionDto assembleDto(AssessmentInteraction interaction) {
        // Drop out immediately if there is nothing to assemble.
        if (interaction == null) {
            return null;
        }
        AssessmentInteractionDto interactionDto = new AssessmentInteractionDto();
        switch (interaction.getType()) {
        case CHOICE_INTERACTION:
            interactionDto = assessmentChoiceInteractionDtoAssembler
                    .assembleDto((AssessmentChoiceInteraction) interaction);
            break;
        case INLINE_CHOICE_INTERACTION:
            interactionDto = assessmentInlineChoiceInteractionDtoAssembler
                    .assembleDto((AssessmentInlineChoiceInteraction) interaction);
            break;
        case EXTENDED_TEXT_INTERACTION:
            interactionDto = assessmentExtendedTextInteractionDtoAssembler
                    .assembleDto((AssessmentExtendedTextInteraction) interaction);
            break;
        case TEXT_ENTRY_INTERACTION:
            interactionDto = assessmentTextEntryInteractionDtoAssembler
                    .assembleDto((AssessmentTextEntryInteraction) interaction);
            break;
        case MATCH_INTERACTION:
            interactionDto = assessmentMatchInteractionDtoAssembler
                    .assembleDto((AssessmentMatchInteraction) interaction);
            break;
        default:
            break;
        }
        interactionDto.setId(interaction.getId());
        interactionDto.setType(interaction.getType());
        interactionDto.setUiid(interaction.getUiid());
        interactionDto.setResponseIdentifier(interaction.getResponseIdentifier());

        interactionDto.setAriaControls(interaction.getAriaControls());
        interactionDto.setAriaDescribedBy(interaction.getAriaDescribedBy());
        interactionDto.setAriaFlowsTo(interaction.getAriaFlowsTo());
        interactionDto.setAriaLabel(interaction.getAriaLabel());
        interactionDto.setAriaLabelledBy(interaction.getAriaLabelledBy());
        interactionDto.setAriaLevel(interaction.getAriaLevel());
        interactionDto.setAriaLive(interaction.getAriaLive());
        interactionDto.setAriaOrientation(interaction.getAriaOrientation());
        interactionDto.setAriaOwns(interaction.getAriaOwns());

        return interactionDto;
    }

    @Override
    public AssessmentInteraction disassembleDto(AssessmentInteractionDto interactionDto) {
        // Drop out immediately if there is nothing to assemble.
        if (interactionDto == null) {
            return null;
        }

        AssessmentInteraction assessmentInteraction = null;
        switch (interactionDto.getType()) {
        case CHOICE_INTERACTION:
            assessmentInteraction = assessmentChoiceInteractionDtoAssembler
                    .disassembleDto((AssessmentChoiceInteractionDto) interactionDto);
            break;
        case INLINE_CHOICE_INTERACTION:
            assessmentInteraction = assessmentInlineChoiceInteractionDtoAssembler
                    .disassembleDto((AssessmentInlineChoiceInteractionDto) interactionDto);
            break;
        case EXTENDED_TEXT_INTERACTION:
            assessmentInteraction = assessmentExtendedTextInteractionDtoAssembler
                    .disassembleDto((AssessmentExtendedTextInteractionDto) interactionDto);
            break;
        case TEXT_ENTRY_INTERACTION:
            assessmentInteraction = assessmentTextEntryInteractionDtoAssembler
                    .disassembleDto((AssessmentTextEntryInteractionDto) interactionDto);
            break;
        case MATCH_INTERACTION:
            assessmentInteraction = assessmentMatchInteractionDtoAssembler
                    .disassembleDto((AssessmentMatchInteractionDto) interactionDto);
            break;
        case NULL_INTERACTION:
            assessmentInteraction = new AssessmentInteraction();
            assessmentInteraction.setId(interactionDto.getId());
            assessmentInteraction.setType(interactionDto.getType());
            assessmentInteraction.setUiid(interactionDto.getUiid());
            assessmentInteraction.setResponseIdentifier(interactionDto.getResponseIdentifier());
            break;
        default:
            break;
        }

        assessmentInteraction.setAriaControls(interactionDto.getAriaControls());
        assessmentInteraction.setAriaDescribedBy(interactionDto.getAriaDescribedBy());
        assessmentInteraction.setAriaFlowsTo(interactionDto.getAriaFlowsTo());
        assessmentInteraction.setAriaLabel(interactionDto.getAriaLabel());
        assessmentInteraction.setAriaLabelledBy(interactionDto.getAriaLabelledBy());
        assessmentInteraction.setAriaLevel(interactionDto.getAriaLevel());
        assessmentInteraction.setAriaLive(interactionDto.getAriaLive());
        assessmentInteraction.setAriaOrientation(interactionDto.getAriaOrientation());
        assessmentInteraction.setAriaOwns(interactionDto.getAriaOwns());

        return assessmentInteraction;
    }
}
