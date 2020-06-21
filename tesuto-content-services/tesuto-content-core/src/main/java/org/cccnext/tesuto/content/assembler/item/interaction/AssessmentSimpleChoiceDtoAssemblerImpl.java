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

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleChoiceDto;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentSimpleChoice;
import org.dozer.Mapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "simpleChoiceDtoAssembler")
public class AssessmentSimpleChoiceDtoAssemblerImpl implements AssessmentSimpleChoiceDtoAssembler {

    @Autowired
    Mapper mapper;

    @Override
    public AssessmentSimpleChoiceDto assembleDto(AssessmentSimpleChoice simpleChoice) {
        // Drop out immediately if there is nothing to assemble.
        if (simpleChoice == null) {
            return null;
        }

        return mapper.map(simpleChoice, AssessmentSimpleChoiceDto.class);
    }

    @Override
    public AssessmentSimpleChoice disassembleDto(AssessmentSimpleChoiceDto simpleChoiceDto) {
        // Drop out immediately if there is nothing to assemble.
        if (simpleChoiceDto == null) {
            return null;
        }

        return mapper.map(simpleChoiceDto, AssessmentSimpleChoice.class);
    }
}
