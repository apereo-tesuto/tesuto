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

import uk.ac.ed.ph.jqtiplus.node.item.interaction.TextEntryInteraction;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;

import java.util.UUID;

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionType;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentTextEntryInteractionDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "textEntryInteractionQtiDtoAssembler")
public class AssessmentTextEntryInteractionQtiDtoAssemblerImpl
        implements AssessmentTextEntryInteractionQtiDtoAssembler {

    @Autowired
    QtiSerializer qtiSerializer;

    @Override
    public AssessmentTextEntryInteractionDto assembleDto(TextEntryInteraction textEntryInteraction) {
        // Drop out immediately if there is nothing to assemble.
        if (textEntryInteraction == null) {
            return null;
        }

        AssessmentTextEntryInteractionDto textEntryInteractionDto = new AssessmentTextEntryInteractionDto();
        textEntryInteractionDto.setType(AssessmentInteractionType.TEXT_ENTRY_INTERACTION);
        if (textEntryInteraction.getId() != null) {
            textEntryInteractionDto.setId(textEntryInteraction.getId().toString());
        }
        if (textEntryInteraction.getPlaceholderText() != null) {
            textEntryInteractionDto.setPlaceholderText(textEntryInteraction.getPlaceholderText());
        }

        textEntryInteractionDto.setResponseIdentifier(textEntryInteraction.getResponseIdentifier().toString());
        textEntryInteractionDto.setExpectedLength(textEntryInteraction.getExpectedLength());
        textEntryInteractionDto.setPatternMask(textEntryInteraction.getPatternMask());
        textEntryInteractionDto.setUiid(UUID.randomUUID().toString());

        return textEntryInteractionDto;
    }

    @Override
    public TextEntryInteraction disassembleDto(AssessmentTextEntryInteractionDto textEntryInteractionDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }

}
