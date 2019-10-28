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
package org.cccnext.tesuto.content.assembler.item;

import org.cccnext.tesuto.content.dto.item.AssessmentStimulusRefDto;
import org.cccnext.tesuto.content.model.item.AssessmentStimulusRef;


import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "assessmentStimulusRefDtoAssembler")
public class AssessmentStimulusRefDtoAssemblerImpl implements AssessmentStimulusRefDtoAssembler {

    @Override
    public AssessmentStimulusRefDto assembleDto(AssessmentStimulusRef assessmentItemRef) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentItemRef == null) {
            return null;
        }

        AssessmentStimulusRefDto assessmentStimulusRefDto = new AssessmentStimulusRefDto();
        assessmentStimulusRefDto.setHref(assessmentItemRef.getHref());
        assessmentStimulusRefDto.setId(assessmentItemRef.getId());

        return assessmentStimulusRefDto;
    }

    @Override
    public AssessmentStimulusRef disassembleDto(AssessmentStimulusRefDto assessmentStimulusRefDto) {
        if (assessmentStimulusRefDto == null) {
            return null;
        }

        AssessmentStimulusRef assessmentStimulusRef = new AssessmentStimulusRef();
        assessmentStimulusRef.setHref(assessmentStimulusRefDto.getHref());
        assessmentStimulusRef.setId(assessmentStimulusRefDto.getId());

        return assessmentStimulusRef;
    }
}
