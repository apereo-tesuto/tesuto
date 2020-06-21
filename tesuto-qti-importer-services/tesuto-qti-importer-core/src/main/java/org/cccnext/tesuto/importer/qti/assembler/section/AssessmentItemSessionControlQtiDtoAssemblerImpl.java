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

import uk.ac.ed.ph.jqtiplus.node.test.ItemSessionControl;

import org.cccnext.tesuto.content.dto.section.AssessmentItemSessionControlDto;


import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "itemSessionControlQtiDtoAssembler")
public class AssessmentItemSessionControlQtiDtoAssemblerImpl implements AssessmentItemSessionControlQtiDtoAssembler {
    @Override
    public AssessmentItemSessionControlDto assembleDto(ItemSessionControl itemSessionControl) {
        // Drop out immediately if there is nothing to assemble.
        if (itemSessionControl == null) {
            return null;
        }

        AssessmentItemSessionControlDto itemSessionControlDto = new AssessmentItemSessionControlDto();
        itemSessionControlDto.setAllowComment(itemSessionControl.getAllowComment());
        itemSessionControlDto.setAllowReview(itemSessionControl.getAllowReview());
        itemSessionControlDto.setAllowSkipping(itemSessionControl.getAllowSkipping());
        itemSessionControlDto.setMaxAttempts(itemSessionControl.getMaxAttempts());
        itemSessionControlDto.setShowFeedback(itemSessionControl.getShowFeedback());
        itemSessionControlDto.setShowSolution(itemSessionControl.getShowSolution());
        itemSessionControlDto.setValidateResponses(itemSessionControl.getValidateResponses());

        return itemSessionControlDto;
    }

    @Override
    public ItemSessionControl disassembleDto(AssessmentItemSessionControlDto itemSessionControlDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
