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

import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionDto;


import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "interactionQtiDtoAssembler")
public class AssessmentInteractionQtiDtoAssemblerImpl implements AssessmentInteractionQtiDtoAssembler {

    @Override
    public AssessmentInteractionDto assembleDto(Interaction interaction) {
        // Drop out immediately if there is nothing to assemble.
        if (interaction == null) {
            return null;
        }

        AssessmentInteractionDto interactionDto = new AssessmentInteractionDto();
        interactionDto.setResponseIdentifier(interaction.getResponseIdentifier().toString());
        return interactionDto;
    }

    @Override
    public Interaction disassembleDto(AssessmentInteractionDto interactionDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
