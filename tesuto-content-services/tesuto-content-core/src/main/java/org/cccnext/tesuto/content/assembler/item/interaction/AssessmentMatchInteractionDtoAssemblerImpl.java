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

import java.util.List;

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentMatchInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleMatchSetDto;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentMatchInteraction;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentSimpleMatchSet;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "assessmentMatchInteractionDtoAssembler")
public class AssessmentMatchInteractionDtoAssemblerImpl implements AssessmentMatchInteractionDtoAssembler {

    @Autowired
    AssessmentSimpleMatchSetDtoAssembler assessmentSimpleMatchSetDtoAssembler;

    @Override
    public AssessmentMatchInteractionDto assembleDto(AssessmentMatchInteraction assessmentMatchInteraction) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentMatchInteraction == null) {
            return null;
        }

        AssessmentMatchInteractionDto assessmentMatchInteractionDto = new AssessmentMatchInteractionDto();
        assessmentMatchInteractionDto.setId(assessmentMatchInteraction.getId());
        assessmentMatchInteractionDto.setType(assessmentMatchInteraction.getType());
        assessmentMatchInteractionDto.setUiid(assessmentMatchInteraction.getUiid());
        assessmentMatchInteractionDto.setResponseIdentifier(assessmentMatchInteraction.getResponseIdentifier());
        assessmentMatchInteractionDto.setShuffle(assessmentMatchInteraction.isShuffle());
        assessmentMatchInteractionDto.setPrompt(assessmentMatchInteraction.getPrompt());
        assessmentMatchInteractionDto.setMaxAssociations(assessmentMatchInteraction.getMaxAssociations());
        assessmentMatchInteractionDto.setMinAssociations(assessmentMatchInteraction.getMinAssociations());
        List<AssessmentSimpleMatchSetDto> assessmentSimpleMatchSetDtoList = assessmentSimpleMatchSetDtoAssembler
                .assembleDto(assessmentMatchInteraction.getMatchSets());
        assessmentMatchInteractionDto.setMatchSets(assessmentSimpleMatchSetDtoList);
        return assessmentMatchInteractionDto;
    }

    @Override
    public AssessmentMatchInteraction disassembleDto(AssessmentMatchInteractionDto assessmentMatchInteractionDto) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentMatchInteractionDto == null) {
            return null;
        }

        AssessmentMatchInteraction assessmentMatchInteraction = new AssessmentMatchInteraction();
        assessmentMatchInteraction.setId(assessmentMatchInteractionDto.getId());
        assessmentMatchInteraction.setType(assessmentMatchInteractionDto.getType());
        assessmentMatchInteraction.setUiid(assessmentMatchInteractionDto.getUiid());
        assessmentMatchInteraction.setResponseIdentifier(assessmentMatchInteractionDto.getResponseIdentifier());
        assessmentMatchInteraction.setShuffle(assessmentMatchInteractionDto.isShuffle());
        assessmentMatchInteraction.setPrompt(assessmentMatchInteractionDto.getPrompt());
        assessmentMatchInteraction.setMaxAssociations(assessmentMatchInteractionDto.getMaxAssociations());
        assessmentMatchInteraction.setMinAssociations(assessmentMatchInteractionDto.getMinAssociations());

        List<AssessmentSimpleMatchSet> assessmentSimpleMatchSetList = assessmentSimpleMatchSetDtoAssembler
                .disassembleDto(assessmentMatchInteractionDto.getMatchSets());
        assessmentMatchInteraction.setMatchSets(assessmentSimpleMatchSetList);
        return assessmentMatchInteraction;
    }
}
