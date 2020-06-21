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
package org.cccnext.tesuto.content.assembler.assessment;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.content.assembler.section.AssessmentBranchRuleDtoAssembler;
import org.cccnext.tesuto.content.assembler.section.AssessmentItemSessionControlDtoAssembler;
import org.cccnext.tesuto.content.assembler.section.AssessmentPreconditionDtoAssembler;
import org.cccnext.tesuto.content.assembler.section.AssessmentSectionDtoAssembler;
import org.cccnext.tesuto.content.dto.AssessmentPartDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemSessionControlDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto;
import org.cccnext.tesuto.content.model.AssessmentPart;
import org.cccnext.tesuto.content.model.section.AssessmentItemSessionControl;
import org.cccnext.tesuto.content.model.section.AssessmentSection;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "assessmentPartDtoAssembler")
public class AssessmentPartDtoAssemblerImpl implements AssessmentPartDtoAssembler {

    @Autowired
    AssessmentSectionDtoAssembler assessmentSectionDtoAssembler;
    @Autowired
    AssessmentBranchRuleDtoAssembler branchRuleDtoAssembler;
    @Autowired
    AssessmentItemSessionControlDtoAssembler itemSessionControlDtoAssembler;
    @Autowired
    AssessmentPreconditionDtoAssembler preconditionDtoAssembler;

    @Override
    public AssessmentPartDto assembleDto(AssessmentPart assessmentPart) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentPart == null) {
            return null;
        }

        AssessmentPartDto assessmentPartDto = new AssessmentPartDto();
        List<AssessmentSectionDto> assessmentSections = new LinkedList<AssessmentSectionDto>();
        if (CollectionUtils.isNotEmpty(assessmentPart.getAssessmentSections())) {
            for (AssessmentSection assessmentSection : assessmentPart.getAssessmentSections()) {
                AssessmentSectionDto assessmentSectionDto = assessmentSectionDtoAssembler
                        .assembleDto(assessmentSection);
                assessmentSections.add(assessmentSectionDto);
            }
        }
        assessmentPartDto.setAssessmentSections(assessmentSections);

        assessmentPartDto.setBranchRules(branchRuleDtoAssembler.assembleDto(assessmentPart.getBranchRules()));
        assessmentPartDto.setDuration(assessmentPart.getDuration());
        assessmentPartDto.setId(assessmentPart.getId());
        assessmentPartDto.setAssessmentPartNavigationMode(assessmentPart.getAssessmentPartNavigationMode());
        assessmentPartDto.setAssessmentPartSubmissionMode(assessmentPart.getAssessmentPartSubmissionMode());

        AssessmentItemSessionControlDto itemSessionControlDto = itemSessionControlDtoAssembler
                .assembleDto(assessmentPart.getItemSessionControl());
        assessmentPartDto.setItemSessionControl(itemSessionControlDto);

        assessmentPartDto.setAssessmentPartNavigationMode(assessmentPart.getAssessmentPartNavigationMode());

        assessmentPartDto.setPreConditions(preconditionDtoAssembler.assembleDto(assessmentPart.getPreConditions()));

        return assessmentPartDto;
    }

    @Override
    public AssessmentPart disassembleDto(AssessmentPartDto assessmentPartDto) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentPartDto == null) {
            return null;
        }

        AssessmentPart assessmentPart = new AssessmentPart();
        List<AssessmentSection> assessmentSectionList = new LinkedList<AssessmentSection>();
        if (CollectionUtils.isNotEmpty(assessmentPartDto.getAssessmentSections())) {
            for (AssessmentSectionDto assessmentSectionDto : assessmentPartDto.getAssessmentSections()) {
                AssessmentSection assessmentSection = assessmentSectionDtoAssembler
                        .disassembleDto(assessmentSectionDto);
                assessmentSectionList.add(assessmentSection);
            }
        }
        assessmentPart.setAssessmentSections(assessmentSectionList);

        assessmentPart.setBranchRules(branchRuleDtoAssembler.disassembleDto(assessmentPartDto.getBranchRules()));
        assessmentPart.setDuration(assessmentPartDto.getDuration());
        assessmentPart.setId(assessmentPartDto.getId());
        assessmentPart.setAssessmentPartNavigationMode(assessmentPartDto.getAssessmentPartNavigationMode());
        assessmentPart.setAssessmentPartSubmissionMode(assessmentPartDto.getAssessmentPartSubmission());

        AssessmentItemSessionControl itemSessionControl = itemSessionControlDtoAssembler
                .disassembleDto(assessmentPartDto.getItemSessionControl());
        assessmentPart.setItemSessionControl(itemSessionControl);

        assessmentPart.setPreConditions(preconditionDtoAssembler.disassembleDto(assessmentPartDto.getPreConditions()));

        return assessmentPart;
    }
}
