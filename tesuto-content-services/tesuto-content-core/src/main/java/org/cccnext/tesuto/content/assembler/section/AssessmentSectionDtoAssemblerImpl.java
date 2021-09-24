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

import java.util.List;

import org.cccnext.tesuto.content.assembler.item.AssessmentStimulusRefDtoAssembler;
import org.cccnext.tesuto.content.dto.AssessmentComponentDto;
import org.cccnext.tesuto.content.dto.expression.AssessmentBranchRuleDto;
import org.cccnext.tesuto.content.dto.expression.AssessmentPreConditionDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemSessionControlDto;
import org.cccnext.tesuto.content.dto.section.AssessmentOrderingDto;
import org.cccnext.tesuto.content.dto.section.AssessmentRubricBlockDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSelectionDto;
import org.cccnext.tesuto.content.model.expression.AssessmentBranchRule;
import org.cccnext.tesuto.content.model.section.AssessmentItemRef;
import org.cccnext.tesuto.content.model.section.AssessmentItemSessionControl;
import org.cccnext.tesuto.content.model.section.AssessmentOrdering;
import org.cccnext.tesuto.content.model.expression.AssessmentPreCondition;
import org.cccnext.tesuto.content.model.section.AssessmentRubricBlock;
import org.cccnext.tesuto.content.model.section.AssessmentSection;
import org.cccnext.tesuto.content.model.section.AssessmentSelection;
import org.cccnext.tesuto.content.model.shared.AssessmentComponent;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "assessmentSectionDtoAssembler")
public class AssessmentSectionDtoAssemblerImpl implements AssessmentSectionDtoAssembler {
    @Autowired
    AssessmentItemSessionControlDtoAssembler itemSessionControlDtoAssembler;
    @Autowired
    AssessmentOrderingDtoAssembler orderingDtoAssembler;
    @Autowired
    AssessmentPreconditionDtoAssembler preconditionDtoAssembler;
    @Autowired
    AssessmentRubricBlockDtoAssembler rubricBlockDtoAssembler;
    @Autowired
    AssessmentSelectionDtoAssembler assessmentSelectionDtoAssembler;
    @Autowired
    AssessmentBranchRuleDtoAssembler branchRuleDtoAssembler;
    @Autowired
    AssessmentItemRefDtoAssembler assessmentItemRefDtoAssembler;
    @Autowired
    AssessmentStimulusRefDtoAssembler assessmentStimulusRefAssembler;
    @Autowired
    AssessmentComponentDtoAssembler assessmentComponentAssembler;

    @Override
    public AssessmentSectionDto assembleDto(AssessmentSection assessmentSection) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentSection == null) {
            return null;
        }

        AssessmentSectionDto assessmentSectionDto = new AssessmentSectionDto();

        List<AssessmentBranchRule> branchRuleList = assessmentSection.getBranchRules();
        List<AssessmentBranchRuleDto> branchRules = branchRuleDtoAssembler.assembleDto(branchRuleList);
        assessmentSectionDto.setBranchRules(branchRules);

        List<AssessmentItemRef> assessmentItemRef = assessmentSection.getAssessmentItemRefs();
        List<AssessmentItemRefDto> assessmentItemRefDto = assessmentItemRefDtoAssembler.assembleDto(assessmentItemRef);
        assessmentSectionDto.setAssessmentItemRefs(assessmentItemRefDto);

        assessmentSectionDto.setId(assessmentSection.getId());
        assessmentSectionDto.setIsFixed(assessmentSection.isFixed());
        assessmentSectionDto.setIsRequired(assessmentSection.isRequired());
        AssessmentItemSessionControlDto itemSessionControlDto = itemSessionControlDtoAssembler
                .assembleDto(assessmentSection.getItemSessionControl());
        assessmentSectionDto.setItemSessionControlDto(itemSessionControlDto);
        assessmentSectionDto.setKeepTogether(assessmentSection.getKeepTogether());
        AssessmentOrderingDto orderingDto = orderingDtoAssembler.assembleDto(assessmentSection.getOrdering());
        assessmentSectionDto.setOrdering(orderingDto);

        assessmentSectionDto.setAssessmentStimulusRef(
                assessmentStimulusRefAssembler.assembleDto(assessmentSection.getAssessmentStimulusRef()));

        List<AssessmentPreCondition> preConditionList = assessmentSection.getPreConditions();
        List<AssessmentPreConditionDto> preConditions = preconditionDtoAssembler.assembleDto(preConditionList);
        assessmentSectionDto.setPreConditions(preConditions);

        List<AssessmentRubricBlock> rubricBlockList = assessmentSection.getRubricBlocks();
        List<AssessmentRubricBlockDto> rubricBlocks = rubricBlockDtoAssembler.assembleDto(rubricBlockList);
        assessmentSectionDto.setRubricBlocks(rubricBlocks);

        AssessmentSelectionDto selectionDto = assessmentSelectionDtoAssembler
                .assembleDto(assessmentSection.getSelection());
        assessmentSectionDto.setSelection(selectionDto);
        assessmentSectionDto.setTimeLimits(assessmentSection.getTimeLimits());

        assessmentSectionDto.setTitle(assessmentSection.getTitle());
        assessmentSectionDto.setVisible(assessmentSection.getVisible());

        List<AssessmentSection> assessmentSectionList = assessmentSection.getAssessmentSections();
        List<AssessmentSectionDto> assessmentSectionDtoList = assembleDto(assessmentSectionList);
        assessmentSectionDto.setAssessmentSections(assessmentSectionDtoList);

        List<AssessmentComponent> assessmentComponentsList = assessmentSection.getAssessmentComponents();
        List<AssessmentComponentDto> assessmentComponentsDtoList = assessmentComponentAssembler
                .assembleDto(assessmentComponentsList);
        assessmentSectionDto.setAssessmentComponents(assessmentComponentsDtoList);
        return assessmentSectionDto;
    }

    @Override
    public AssessmentSection disassembleDto(AssessmentSectionDto assessmentSectionDto) {
        if (assessmentSectionDto == null) {
            return null;
        }

        AssessmentSection assessmentSection = new AssessmentSection();

        List<AssessmentItemRefDto> assessmentItemRefDto = assessmentSectionDto.getAssessmentItemRefs();
        List<AssessmentItemRef> assessmentItemRef = assessmentItemRefDtoAssembler.disassembleDto(assessmentItemRefDto);
        assessmentSection.setAssessmentItemRefs(assessmentItemRef);

        List<AssessmentBranchRuleDto> branchRules = assessmentSectionDto.getBranchRules();
        List<AssessmentBranchRule> branchRuleList = branchRuleDtoAssembler.disassembleDto(branchRules);
        assessmentSection.setBranchRules(branchRuleList);

        assessmentSection.setId(assessmentSectionDto.getId());
        assessmentSection.setIsFixed(assessmentSectionDto.isFixed());
        assessmentSection.setIsRequired(assessmentSectionDto.isRequired());
        AssessmentItemSessionControl itemSessionControl = itemSessionControlDtoAssembler
                .disassembleDto(assessmentSectionDto.getItemSessionControl());
        assessmentSection.setItemSessionControl(itemSessionControl);

        assessmentSection.setKeepTogether(assessmentSectionDto.getKeepTogether());
        AssessmentOrdering ordering = orderingDtoAssembler.disassembleDto(assessmentSectionDto.getOrdering());
        assessmentSection.setOrdering(ordering);

        assessmentSection.setAssessmentStimulusRef(
                assessmentStimulusRefAssembler.disassembleDto(assessmentSectionDto.getAssessmentStimulusRef()));

        List<AssessmentPreConditionDto> preConditions = assessmentSectionDto.getPreConditions();
        List<AssessmentPreCondition> preConditionList = preconditionDtoAssembler.disassembleDto(preConditions);
        assessmentSection.setPreConditions(preConditionList);

        List<AssessmentRubricBlockDto> rubricBlocks = assessmentSectionDto.getRubricBlocks();
        List<AssessmentRubricBlock> rubricBlockList = rubricBlockDtoAssembler.disassembleDto(rubricBlocks);
        assessmentSection.setRubricBlocks(rubricBlockList);

        AssessmentSelection selection = assessmentSelectionDtoAssembler
                .disassembleDto(assessmentSectionDto.getSelection());
        assessmentSection.setSelection(selection);
        assessmentSection.setTimeLimits(assessmentSectionDto.getTimeLimits());

        assessmentSection.setTitle(assessmentSectionDto.getTitle());
        assessmentSection.setVisible(assessmentSectionDto.getVisible());

        List<AssessmentSectionDto> assessmentSectionDtoList = assessmentSectionDto.getAssessmentSections();
        List<AssessmentSection> assessmentSectionList = disassembleDto(assessmentSectionDtoList);
        assessmentSection.setAssessmentSections(assessmentSectionList);

        List<AssessmentComponentDto> assessmentComponentsDtoList = assessmentSectionDto.getAssessmentComponents();
        List<AssessmentComponent> assessmentComponentsList = assessmentComponentAssembler
                .disassembleDto(assessmentComponentsDtoList);
        assessmentSection.setAssessmentComponents(assessmentComponentsList);

        return assessmentSection;
    }
}
