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
package org.cccnext.tesuto.importer.qti.assembler.assessment;

import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;
import uk.ac.ed.ph.jqtiplus.node.test.BranchRule;
import uk.ac.ed.ph.jqtiplus.node.test.PreCondition;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;

import java.util.LinkedList;
import java.util.List;

import org.cccnext.tesuto.importer.qti.assembler.section.AssessmentSectionQtiDtoAssembler;
import org.cccnext.tesuto.importer.qti.assembler.section.AssessmentBranchRuleQtiDtoAssembler;
import org.cccnext.tesuto.importer.qti.assembler.section.AssessmentItemSessionControlQtiDtoAssembler;
import org.cccnext.tesuto.importer.qti.assembler.section.AssessmentPreconditionQtiDtoAssembler;
import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.content.dto.AssessmentPartDto;
import org.cccnext.tesuto.content.dto.AssessmentPartNavigationMode;
import org.cccnext.tesuto.content.dto.AssessmentPartSubmissionMode;
import org.cccnext.tesuto.content.dto.expression.AssessmentBranchRuleDto;
import org.cccnext.tesuto.content.dto.expression.AssessmentPreConditionDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemSessionControlDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "assessmentPartQtiDtoAssembler")
public class AssessmentPartQtiDtoAssemblerImpl implements AssessmentPartQtiDtoAssembler {

    @Autowired
    AssessmentSectionQtiDtoAssembler assessmentSectionQtiDtoAssembler;
    @Autowired
    AssessmentBranchRuleQtiDtoAssembler branchRuleQtiDtoAssembler;
    @Autowired
    AssessmentItemSessionControlQtiDtoAssembler itemSessionControlQtiDtoAssembler;
    @Autowired
    AssessmentPreconditionQtiDtoAssembler preconditionQtiDtoAssembler;

    @Override
    public AssessmentPartDto assembleDto(TestPart testPart) {
        // Drop out immediately if there is nothing to assemble.
        if (testPart == null) {
            return null;
        }

        AssessmentPartDto assessmentPartDto = new AssessmentPartDto();
        List<AssessmentSectionDto> assessmentSections = new LinkedList<AssessmentSectionDto>();
        if (CollectionUtils.isNotEmpty(testPart.getAssessmentSections())) {
            for (AssessmentSection assessmentSection : testPart.getAssessmentSections()) {
                AssessmentSectionDto assessmentSectionDto = assessmentSectionQtiDtoAssembler
                        .assembleDto(assessmentSection);
                assessmentSections.add(assessmentSectionDto);
            }
        }
        assessmentPartDto.setAssessmentSections(assessmentSections);
        List<AssessmentBranchRuleDto> branchRules = new LinkedList<AssessmentBranchRuleDto>();
        for (BranchRule branchRule : testPart.getBranchRules()) {
            AssessmentBranchRuleDto branchRuleDto = branchRuleQtiDtoAssembler.assembleDto(branchRule);
            branchRules.add(branchRuleDto);
        }
        assessmentPartDto.setBranchRules(branchRules);
        if (testPart.getTimeLimits() != null) {
            assessmentPartDto.setDuration(testPart.getTimeLimits().getMaximum());
        }
        assessmentPartDto.setId(testPart.getIdentifier().toString());
        AssessmentItemSessionControlDto itemSessionControlDto = itemSessionControlQtiDtoAssembler
                .assembleDto(testPart.getItemSessionControl());
        assessmentPartDto.setItemSessionControl(itemSessionControlDto);
        assessmentPartDto.setAssessmentPartNavigationMode(
                AssessmentPartNavigationMode.valueOf(testPart.getNavigationMode().toString()));
        assessmentPartDto.setAssessmentPartSubmissionMode(
                AssessmentPartSubmissionMode.valueOf(testPart.getSubmissionMode().toString()));
        List<AssessmentPreConditionDto> preConditions = new LinkedList<AssessmentPreConditionDto>();
        for (PreCondition preCondition : testPart.getPreConditions()) {
            AssessmentPreConditionDto preConditionDto = preconditionQtiDtoAssembler.assembleDto(preCondition);
            preConditions.add(preConditionDto);
        }
        assessmentPartDto.setPreConditions(preConditions);

        return assessmentPartDto;
    }

    @Override
    public TestPart disassembleDto(AssessmentPartDto assessmentPartDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
