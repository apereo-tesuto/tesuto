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

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleAssociableChoiceDto;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentSimpleAssociableChoice;


import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "assessmentSimpleAssociableChoiceDtoAssembler")
public class AssessmentSimpleAssociableChoiceDtoAssemblerImpl implements AssessmentSimpleAssociableChoiceDtoAssembler {

    @Override
    public AssessmentSimpleAssociableChoiceDto assembleDto(
            AssessmentSimpleAssociableChoice assessmentSimpleAssociableChoice) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentSimpleAssociableChoice == null) {
            return null;
        }

        AssessmentSimpleAssociableChoiceDto assessmentSimpleAssociableChoiceDto = new AssessmentSimpleAssociableChoiceDto();
        assessmentSimpleAssociableChoiceDto.setContent(assessmentSimpleAssociableChoice.getContent());
        assessmentSimpleAssociableChoiceDto.setId(assessmentSimpleAssociableChoice.getId());
        assessmentSimpleAssociableChoiceDto.setIdentifier(assessmentSimpleAssociableChoice.getIdentifier());
        assessmentSimpleAssociableChoiceDto.setType(assessmentSimpleAssociableChoice.getType());
        assessmentSimpleAssociableChoiceDto.setUiid(assessmentSimpleAssociableChoice.getUiid());
        assessmentSimpleAssociableChoiceDto.setMatchMin(assessmentSimpleAssociableChoice.getMatchMin());
        assessmentSimpleAssociableChoiceDto.setMatchMax(assessmentSimpleAssociableChoice.getMatchMax());
        return assessmentSimpleAssociableChoiceDto;
    }

    @Override
    public AssessmentSimpleAssociableChoice disassembleDto(
            AssessmentSimpleAssociableChoiceDto assessmentSimpleAssociableChoiceDto) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentSimpleAssociableChoiceDto == null) {
            return null;
        }

        AssessmentSimpleAssociableChoice assessmentSimpleAssociableChoice = new AssessmentSimpleAssociableChoice();
        assessmentSimpleAssociableChoice.setContent(assessmentSimpleAssociableChoiceDto.getContent());
        assessmentSimpleAssociableChoice.setId(assessmentSimpleAssociableChoiceDto.getId());
        assessmentSimpleAssociableChoice.setIdentifier(assessmentSimpleAssociableChoiceDto.getIdentifier());
        assessmentSimpleAssociableChoice.setType(assessmentSimpleAssociableChoiceDto.getType());
        assessmentSimpleAssociableChoice.setUiid(assessmentSimpleAssociableChoiceDto.getUiid());
        assessmentSimpleAssociableChoice.setMatchMax(assessmentSimpleAssociableChoiceDto.getMatchMax());
        assessmentSimpleAssociableChoice.setMatchMin(assessmentSimpleAssociableChoiceDto.getMatchMin());
        return assessmentSimpleAssociableChoice;
    }
}
