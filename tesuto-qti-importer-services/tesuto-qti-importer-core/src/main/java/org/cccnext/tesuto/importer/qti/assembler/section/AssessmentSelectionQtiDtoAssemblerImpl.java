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
package org.cccnext.tesuto.importer.qti.assembler.section;

import uk.ac.ed.ph.jqtiplus.node.test.Selection;

import org.cccnext.tesuto.content.dto.section.AssessmentSelectionDto;


import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "selectionQtiDtoAssembler")
public class AssessmentSelectionQtiDtoAssemblerImpl implements AsessmentSelectionQtiDtoAssembler {

    @Override
    public AssessmentSelectionDto assembleDto(Selection selection) {
        // Drop out immediately if there is nothing to assemble.
        if (selection == null) {
            return null;
        }

        AssessmentSelectionDto selectionDto = new AssessmentSelectionDto();
        // selectionDto.setExtensions(selection.get()); // Custom field not for
        // pilot.
        selectionDto.setSelect(selection.getSelect());
        selectionDto.setWithReplacement(selection.getWithReplacement());

        return selectionDto;
    }

    @Override
    public Selection disassembleDto(AssessmentSelectionDto selectionDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
