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
package org.cccnext.tesuto.content.assembler.section;

import java.util.LinkedList;
import java.util.List;

import org.cccnext.tesuto.content.dto.section.AssessmentRubricBlockDto;
import org.cccnext.tesuto.content.model.section.AssessmentRubricBlock;


import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "rubricBlockDtoAssembler")
public class AssessmentRubricBlockDtoAssemblerImpl implements AssessmentRubricBlockDtoAssembler {

    @Override
    public AssessmentRubricBlockDto assembleDto(AssessmentRubricBlock rubricBlock) {
        // Drop out immediately if there is nothing to assemble.
        if (rubricBlock == null) {
            return null;
        }

        AssessmentRubricBlockDto rubricBlockDto = new AssessmentRubricBlockDto();

        rubricBlockDto.setContent(rubricBlock.getContent());
        rubricBlockDto.setUse(rubricBlock.getUse());
        List<String> viewList = new LinkedList<String>(rubricBlock.getViews());
        rubricBlockDto.setViews(viewList);
        return rubricBlockDto;
    }

    @Override
    public AssessmentRubricBlock disassembleDto(AssessmentRubricBlockDto rubricBlockDto) {
        // Drop out immediately if there is nothing to assemble.
        if (rubricBlockDto == null) {
            return null;
        }

        AssessmentRubricBlock rubricBlock = new AssessmentRubricBlock();

        rubricBlock.setContent(rubricBlockDto.getContent());
        rubricBlock.setUse(rubricBlockDto.getUse());
        List<String> viewList = new LinkedList<String>(rubricBlockDto.getViews());
        rubricBlock.setViews(viewList);
        return rubricBlock;
    }
}
