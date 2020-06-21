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

import org.cccnext.tesuto.content.dto.section.AssessmentSelectionDto;
import org.cccnext.tesuto.content.model.section.AssessmentSelection;


import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "selectionDtoAssembler")
public class AssessmentSelectionDtoAssemblerImpl implements AssessmentSelectionDtoAssembler {
    @Override
    public AssessmentSelectionDto assembleDto(AssessmentSelection selection) {
        // Drop out immediately if there is nothing to assemble.
        if (selection == null) {
            return null;
        }

        AssessmentSelectionDto selectionDto = new AssessmentSelectionDto();
        selectionDto.setExtensions(selection.getExtensions());
        selectionDto.setSelect(selection.getSelect());
        selectionDto.setWithReplacement(selection.getWithReplacement());

        return selectionDto;
    }

    @Override
    public AssessmentSelection disassembleDto(AssessmentSelectionDto selectionDto) {
        // Drop out immediately if there is nothing to assemble.
        if (selectionDto == null) {
            return null;
        }

        AssessmentSelection selection = new AssessmentSelection();
        selection.setExtensions(selectionDto.getExtensions());
        selection.setSelect(selectionDto.getSelect());
        selection.setWithReplacement(selectionDto.getWithReplacement());

        return selection;
    }
}
