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

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentExtendedTextInteractionDto;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentExtendedTextInteraction;


import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "extendedTextInteractionDtoAssembler")
public class AssessmentExtendedTextInteractionDtoAssemblerImpl
        implements AssessmentExtendedTextInteractionDtoAssembler {

    @Override
    public AssessmentExtendedTextInteractionDto assembleDto(AssessmentExtendedTextInteraction extendedTextInteraction) {
        // Drop out immediately if there is nothing to assemble.
        if (extendedTextInteraction == null) {
            return null;
        }

        AssessmentExtendedTextInteractionDto extendedTextInteractionDto = new AssessmentExtendedTextInteractionDto();
        extendedTextInteractionDto.setType(extendedTextInteraction.getType());
        extendedTextInteractionDto.setId(extendedTextInteraction.getId());

        extendedTextInteractionDto.setResponseIdentifier(extendedTextInteraction.getResponseIdentifier());
        extendedTextInteractionDto.setExpectedLength(extendedTextInteraction.getExpectedLength());
        extendedTextInteractionDto.setId(extendedTextInteraction.getId());

        extendedTextInteractionDto.setExpectedLines(extendedTextInteraction.getExpectedLines());
        extendedTextInteractionDto.setMaxStrings(extendedTextInteraction.getMaxStrings());
        extendedTextInteractionDto.setMinStrings(extendedTextInteraction.getMinStrings());
        extendedTextInteractionDto.setUiid(extendedTextInteractionDto.getUiid());

        return extendedTextInteractionDto;
    }

    @Override
    public AssessmentExtendedTextInteraction disassembleDto(
            AssessmentExtendedTextInteractionDto extendedTextInteractionDto) {
        // Drop out immediately if there is nothing to assemble.
        if (extendedTextInteractionDto == null) {
            return null;
        }

        AssessmentExtendedTextInteraction extendedTextInteraction = new AssessmentExtendedTextInteraction();
        extendedTextInteraction.setType(extendedTextInteractionDto.getType());
        extendedTextInteraction.setId(extendedTextInteractionDto.getId());

        extendedTextInteraction.setResponseIdentifier(extendedTextInteractionDto.getResponseIdentifier());
        extendedTextInteraction.setExpectedLength(extendedTextInteractionDto.getExpectedLength());
        extendedTextInteraction.setId(extendedTextInteractionDto.getId());

        extendedTextInteraction.setExpectedLines(extendedTextInteractionDto.getExpectedLines());
        extendedTextInteraction.setMaxStrings(extendedTextInteractionDto.getMaxStrings());
        extendedTextInteraction.setMinStrings(extendedTextInteractionDto.getMinStrings());
        extendedTextInteraction.setUiid(extendedTextInteractionDto.getUiid());

        return extendedTextInteraction;
    }
}
