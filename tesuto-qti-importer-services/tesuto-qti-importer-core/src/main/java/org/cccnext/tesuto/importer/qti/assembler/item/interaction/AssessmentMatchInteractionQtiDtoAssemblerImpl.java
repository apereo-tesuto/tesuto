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

import uk.ac.ed.ph.jqtiplus.node.item.interaction.MatchInteraction;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;

import java.util.UUID;

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionType;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentMatchInteractionDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "assessmentMatchInteractionQtiDtoAssembler")
public class AssessmentMatchInteractionQtiDtoAssemblerImpl implements AssessmentMatchInteractionQtiDtoAssembler {

    @Autowired
    AssessmentSimpleMatchSetQtiDtoAssembler assessmentSimpleMatchSetQtiDtoAssembler;
    @Autowired
    QtiSerializer qtiSerializer;

    @Override
    public AssessmentMatchInteractionDto assembleDto(MatchInteraction matchInteraction) {
        // Drop out immediately if there is nothing to assemble.
        if (matchInteraction == null) {
            return null;
        }

        AssessmentMatchInteractionDto assessmentMatchInteractionDto = new AssessmentMatchInteractionDto();

        assessmentMatchInteractionDto.setMaxAssociations(matchInteraction.getMaxAssociations());
        assessmentMatchInteractionDto.setMinAssociations(matchInteraction.getMinAssociations());

        if (matchInteraction.getPrompt() != null) {
            assessmentMatchInteractionDto.setPrompt(qtiSerializer.serializeJqtiObject(matchInteraction.getPrompt()));
        }

        assessmentMatchInteractionDto.setResponseIdentifier(matchInteraction.getResponseIdentifier().toString());
        assessmentMatchInteractionDto.setShuffle(matchInteraction.getShuffle());
        if (matchInteraction.getId() != null) {
            assessmentMatchInteractionDto.setId(matchInteraction.getId().toString());
        }
        assessmentMatchInteractionDto.setType(AssessmentInteractionType.MATCH_INTERACTION);
        assessmentMatchInteractionDto.setUiid(UUID.randomUUID().toString());

        assessmentMatchInteractionDto.setMatchSets(
                assessmentSimpleMatchSetQtiDtoAssembler.assembleDto(matchInteraction.getSimpleMatchSets()));

        return assessmentMatchInteractionDto;
    }

    @Override
    public MatchInteraction disassembleDto(AssessmentMatchInteractionDto assessmentMatchInteractionDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
