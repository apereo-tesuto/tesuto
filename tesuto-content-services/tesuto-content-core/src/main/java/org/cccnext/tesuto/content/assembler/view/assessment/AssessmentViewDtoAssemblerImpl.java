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
package org.cccnext.tesuto.content.assembler.view.assessment;

import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.viewdto.AssessmentViewDto;
import org.springframework.stereotype.Component;

@Component(value = "assessmentViewDtoAssembler")
public class AssessmentViewDtoAssemblerImpl implements AssessmentViewDtoAssembler {

    @Override
    public AssessmentViewDto assembleViewDto(AssessmentDto assessmentDto) {
        if (assessmentDto == null) {
            return null;
        }

        AssessmentViewDto assessmentViewDto = new AssessmentViewDto();

        assessmentViewDto.setDuration(assessmentDto.getDuration());
        assessmentViewDto.setId(assessmentDto.getId());
        assessmentViewDto.setIdentifier(assessmentDto.getIdentifier());
        assessmentViewDto.setLanguage(assessmentDto.getLanguage());
        assessmentViewDto.setTitle(assessmentDto.getTitle());
        assessmentViewDto.setNamespace(assessmentDto.getNamespace());
        assessmentViewDto.setVersion(assessmentDto.getVersion());
        assessmentViewDto.setPaper(assessmentDto.isPaper());
        assessmentViewDto.setOnline(assessmentDto.isOnline());
        assessmentViewDto.setDisciplines(assessmentDto.getAssessmentMetadata() == null ? null : assessmentDto.getAssessmentMetadata().getCompetencyMapDisciplines());
        return assessmentViewDto;
    }

    @Override
    public AssessmentDto disassembleViewDto(AssessmentViewDto dto) {
        throw new UnsupportedOperationException(
                "If you're calling this method, you will need to complete this method.");
    }

}
