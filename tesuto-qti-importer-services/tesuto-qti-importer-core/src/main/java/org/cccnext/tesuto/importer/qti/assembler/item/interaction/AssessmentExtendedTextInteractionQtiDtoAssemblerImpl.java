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

import uk.ac.ed.ph.jqtiplus.node.item.interaction.ExtendedTextInteraction;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;

import java.util.UUID;

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentExtendedTextInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionType;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "extendedTextInteractionQtiDtoAssembler")
public class AssessmentExtendedTextInteractionQtiDtoAssemblerImpl
        implements AssessmentExtendedTextInteractionQtiDtoAssembler {

    @Autowired
    QtiSerializer qtiSerializer;

    @Override
    public AssessmentExtendedTextInteractionDto assembleDto(ExtendedTextInteraction extendedTextInteraction) {
        // Drop out immediately if there is nothing to assemble.
        if (extendedTextInteraction == null) {
            return null;
        }

        AssessmentExtendedTextInteractionDto extendedTextInteractionDto = new AssessmentExtendedTextInteractionDto();
        extendedTextInteractionDto.setType(AssessmentInteractionType.EXTENDED_TEXT_INTERACTION);
        if (extendedTextInteraction.getId() != null) {
            extendedTextInteractionDto.setId(extendedTextInteraction.getId().toString());
        }

        extendedTextInteractionDto.setResponseIdentifier(extendedTextInteraction.getResponseIdentifier().toString());
        extendedTextInteractionDto.setExpectedLength(extendedTextInteraction.getExpectedLength());
        if (extendedTextInteraction.getId() != null) {
            extendedTextInteractionDto.setId(extendedTextInteraction.getId().toString());
        }
        extendedTextInteractionDto.setExpectedLines(extendedTextInteraction.getExpectedLines());
        extendedTextInteractionDto.setMaxStrings(extendedTextInteraction.getMaxStrings());
        extendedTextInteractionDto.setMinStrings(extendedTextInteraction.getMinStrings());
        extendedTextInteractionDto.setUiid(UUID.randomUUID().toString()); // I'm not sure I really like this. -scott smith
        return extendedTextInteractionDto;
    }

    @Override
    public ExtendedTextInteraction disassembleDto(AssessmentExtendedTextInteractionDto extendedTextinteractionDto) {
        return null;
    }
}
