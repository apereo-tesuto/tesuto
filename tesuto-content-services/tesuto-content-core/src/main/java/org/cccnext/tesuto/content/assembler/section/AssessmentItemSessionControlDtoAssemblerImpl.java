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

import org.cccnext.tesuto.content.dto.section.AssessmentItemSessionControlDto;
import org.cccnext.tesuto.content.model.section.AssessmentItemSessionControl;


import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "itemSessionControlDtoAssembler")
public class AssessmentItemSessionControlDtoAssemblerImpl implements AssessmentItemSessionControlDtoAssembler {

    @Override
    public AssessmentItemSessionControlDto assembleDto(AssessmentItemSessionControl itemSessionControl) {
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
    public AssessmentItemSessionControl disassembleDto(AssessmentItemSessionControlDto itemSessionControlDto) {
        // Drop out immediately if there is nothing to assemble.
        if (itemSessionControlDto == null) {
            return null;
        }

        AssessmentItemSessionControl itemSessionControl = new AssessmentItemSessionControl();
        itemSessionControl.setAllowComment(itemSessionControlDto.getAllowComment());
        itemSessionControl.setAllowReview(itemSessionControlDto.getAllowReview());
        itemSessionControl.setAllowSkipping(itemSessionControlDto.getAllowSkipping());
        itemSessionControl.setMaxAttempts(itemSessionControlDto.getMaxAttempts());
        itemSessionControl.setShowFeedback(itemSessionControlDto.getShowFeedback());
        itemSessionControl.setShowSolution(itemSessionControlDto.getShowSolution());
        itemSessionControl.setValidateResponses(itemSessionControlDto.getValidateResponses());

        return itemSessionControl;
    }
}
