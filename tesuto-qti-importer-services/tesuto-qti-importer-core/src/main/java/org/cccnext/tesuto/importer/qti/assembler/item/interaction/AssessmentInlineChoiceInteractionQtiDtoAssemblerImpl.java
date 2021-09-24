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

import uk.ac.ed.ph.jqtiplus.node.item.interaction.InlineChoiceInteraction;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;

import java.util.UUID;

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInlineChoiceInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionType;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "inlineChoiceInteractionQtiDtoAssembler")
public class AssessmentInlineChoiceInteractionQtiDtoAssemblerImpl
        implements AssessmentInlineChoiceInteractionQtiDtoAssembler {

    @Autowired
    AssessmentInlineChoiceQtiDtoAssembler assessmentInlineChoiceQtiDtoAssembler;
    @Autowired
    QtiSerializer qtiSerializer;

    @Override
    public AssessmentInlineChoiceInteractionDto assembleDto(InlineChoiceInteraction inlineChoiceInteraction) {
        // Drop out immediately if there is nothing to assemble.
        if (inlineChoiceInteraction == null) {
            return null;
        }

        AssessmentInlineChoiceInteractionDto inlineChoiceInteractionDto = new AssessmentInlineChoiceInteractionDto();
        inlineChoiceInteractionDto.setType(AssessmentInteractionType.INLINE_CHOICE_INTERACTION);
        if (inlineChoiceInteraction.getId() != null) {
            inlineChoiceInteractionDto.setId(inlineChoiceInteraction.getId().toString());
        }

        inlineChoiceInteractionDto.setInlineChoices(
                assessmentInlineChoiceQtiDtoAssembler.assembleDto(inlineChoiceInteraction.getInlineChoices()));

        return inlineChoiceInteractionDto;
    }

    @Override
    public InlineChoiceInteraction disassembleDto(AssessmentInlineChoiceInteractionDto inlineChoiceInteractionDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
