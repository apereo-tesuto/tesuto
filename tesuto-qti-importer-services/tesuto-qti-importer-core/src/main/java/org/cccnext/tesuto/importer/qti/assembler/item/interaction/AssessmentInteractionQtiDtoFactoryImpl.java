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
package org.cccnext.tesuto.importer.qti.assembler.item.interaction;

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionDto;
import org.cccnext.tesuto.util.TesutoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.ac.ed.ph.jqtiplus.node.item.interaction.ChoiceInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.ExtendedTextInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.InlineChoiceInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.MatchInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.TextEntryInteraction;

@Component("assessmentInteractionQtiDtoFactory")
public class AssessmentInteractionQtiDtoFactoryImpl implements AssessmentInteractionQtiDtoFactory {

    @Autowired
    AssessmentChoiceInteractionQtiDtoAssembler choiceInteractionQtiDtoAssembler;
    @Autowired
    AssessmentInlineChoiceInteractionQtiDtoAssembler inlineChoiceInteractionQtiDtoAssembler;
    @Autowired
    AssessmentTextEntryInteractionQtiDtoAssembler textEntryInteractionQtiDtoAssembler;
    @Autowired
    AssessmentExtendedTextInteractionQtiDtoAssembler extendedTextInteractionQtiDtoAssembler;
    @Autowired
    AssessmentMatchInteractionQtiDtoAssembler assessmentMatchInteractionQtiDtoAssembler;

    @Override
    public AssessmentInteractionDto createAssessment(Interaction interaction) {
        AssessmentInteractionDto interactionDto = null;
        if (interaction instanceof ChoiceInteraction) {
            ChoiceInteraction choiceInteraction = (ChoiceInteraction) interaction;
            interactionDto = choiceInteractionQtiDtoAssembler.assembleDto(choiceInteraction);
        } else if (interaction instanceof TextEntryInteraction) {
            TextEntryInteraction textEntryInteraction = (TextEntryInteraction) interaction;
            interactionDto = textEntryInteractionQtiDtoAssembler.assembleDto(textEntryInteraction);
        } else if (interaction instanceof ExtendedTextInteraction) {
            ExtendedTextInteraction extendedTextInteraction = (ExtendedTextInteraction) interaction;
            interactionDto = extendedTextInteractionQtiDtoAssembler.assembleDto(extendedTextInteraction);
        } else if (interaction instanceof InlineChoiceInteraction) {
            InlineChoiceInteraction inlineChoiceInteraction = (InlineChoiceInteraction) interaction;
            interactionDto = inlineChoiceInteractionQtiDtoAssembler.assembleDto(inlineChoiceInteraction);
        } else if (interaction instanceof MatchInteraction) {
            MatchInteraction matchInteraction = (MatchInteraction) interaction;
            interactionDto = assessmentMatchInteractionQtiDtoAssembler.assembleDto(matchInteraction);
        }

        String uiid = TesutoUtils.newId();

        interactionDto.setUiid(uiid.toString());
        if (interaction.getResponseIdentifier() != null) {
            interactionDto.setResponseIdentifier(interaction.getResponseIdentifier().toString());
        }
        if (interaction.getId() != null) {
            interactionDto.setId(interaction.getId().toString());
        }

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

}
