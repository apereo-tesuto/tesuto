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

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleMatchSetDto;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentSimpleMatchSet;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "assessmentSimpleMatchSetDtoAssembler")
public class AssessmentSimpleMatchSetDtoAssemblerImpl implements AssessmentSimpleMatchSetDtoAssembler {
    @Autowired
    AssessmentSimpleAssociableChoiceDtoAssembler assessmentSimpleAssociableChoiceDtoAssembler;

    @Override
    public AssessmentSimpleMatchSetDto assembleDto(AssessmentSimpleMatchSet assessmentSimpleMatchSet) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentSimpleMatchSet == null) {
            return null;
        }

        AssessmentSimpleMatchSetDto assessmentSimpleMatchSetDto = new AssessmentSimpleMatchSetDto();
        assessmentSimpleMatchSetDto.setId(assessmentSimpleMatchSet.getId());
        assessmentSimpleMatchSetDto.setType(assessmentSimpleMatchSet.getType());
        assessmentSimpleMatchSetDto.setUiid(assessmentSimpleMatchSet.getUiid());
        assessmentSimpleMatchSetDto.setMatchSet(
                assessmentSimpleAssociableChoiceDtoAssembler.assembleDto(assessmentSimpleMatchSet.getMatchSet()));
        return assessmentSimpleMatchSetDto;
    }

    @Override
    public AssessmentSimpleMatchSet disassembleDto(AssessmentSimpleMatchSetDto assessmentSimpleMatchSetDto) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentSimpleMatchSetDto == null) {
            return null;
        }

        AssessmentSimpleMatchSet assessmentSimpleMatchSet = new AssessmentSimpleMatchSet();
        assessmentSimpleMatchSet.setId(assessmentSimpleMatchSetDto.getId());
        assessmentSimpleMatchSet.setType(assessmentSimpleMatchSetDto.getType());
        assessmentSimpleMatchSet.setUiid(assessmentSimpleMatchSetDto.getUiid());
        assessmentSimpleMatchSet.setMatchSet(
                assessmentSimpleAssociableChoiceDtoAssembler.disassembleDto(assessmentSimpleMatchSetDto.getMatchSet()));
        return assessmentSimpleMatchSet;
    }
}
