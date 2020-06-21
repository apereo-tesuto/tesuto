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

import uk.ac.ed.ph.jqtiplus.node.content.variable.RubricBlock;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentSection;
import uk.ac.ed.ph.jqtiplus.node.test.BranchRule;
import uk.ac.ed.ph.jqtiplus.node.test.PreCondition;
import uk.ac.ed.ph.jqtiplus.node.test.SectionPart;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.cccnext.tesuto.content.dto.AssessmentComponentDto;
import org.cccnext.tesuto.content.dto.expression.AssessmentBranchRuleDto;
import org.cccnext.tesuto.content.dto.expression.AssessmentPreConditionDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemSessionControlDto;
import org.cccnext.tesuto.content.dto.section.AssessmentOrderingDto;
import org.cccnext.tesuto.content.dto.section.AssessmentRubricBlockDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSelectionDto;
import org.cccnext.tesuto.importer.qti.assembler.item.AssessmentItemQtiDtoAssembler;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "assessmentSectionQtiDtoAssembler")
public class AssessmentSectionQtiDtoAssemblerImpl implements AssessmentSectionQtiDtoAssembler {

    @Autowired
    AssessmentItemQtiDtoAssembler assessmentItemQtiDtoAssembler;
    @Autowired
    AssessmentItemSessionControlQtiDtoAssembler itemSessionControlQtiDtoAssembler;
    @Autowired
    AssessmentOrderingQtiDtoAssembler orderingQtiDtoAssembler;
    @Autowired
    AssessmentPreconditionQtiDtoAssembler preconditionQtiDtoAssembler;
    @Autowired
    AssessmentRubricBlockQtiDtoAssembler rubricBlockQtiDtoAssembler;
    @Autowired
    AsessmentSelectionQtiDtoAssembler selectionQtiDtoAssembler;
    @Autowired
    AssessmentBranchRuleQtiDtoAssembler branchRuleQtiDtoAssembler;
    @Autowired
    AssessmentItemRefQtiDtoAssembler assessmentItemRefQtiDtoAssembler;

    @Override
    public AssessmentSectionDto assembleDto(AssessmentSection assessmentSection) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentSection == null) {
            return null;
        }

        AssessmentSectionDto assessmentSectionDto = new AssessmentSectionDto();

        // TODO: I cannot see where to get assessmentItems from anything in
        // assessmentSections!
        // assessmentSectionDto.setAssessmentItems(assessmentSection);
        // We are only going to support the first level of sections for now.
        // TODO: Again, not sure what to do with this one and it's not for pilot
        // assessmentSectionDto.setAssessmentStimulusRefDto(assessmentSection.get);
        List<AssessmentBranchRuleDto> branchRules = new LinkedList<AssessmentBranchRuleDto>();
        for (BranchRule branchRule : assessmentSection.getBranchRules()) {
            AssessmentBranchRuleDto branchRuleDto = branchRuleQtiDtoAssembler.assembleDto(branchRule);
            branchRules.add(branchRuleDto);
        }
        assessmentSectionDto.setBranchRules(branchRules);
        assessmentSectionDto.setId(assessmentSection.getIdentifier().toString());
        assessmentSectionDto.setIsFixed(assessmentSection.getFixed());
        assessmentSectionDto.setIsRequired(assessmentSection.getRequired());
        AssessmentItemSessionControlDto itemSessionControlDto = itemSessionControlQtiDtoAssembler
                .assembleDto(assessmentSection.getItemSessionControl());
        assessmentSectionDto.setItemSessionControlDto(itemSessionControlDto);
        assessmentSectionDto.setKeepTogether(assessmentSection.getKeepTogether());
        AssessmentOrderingDto orderingDto = orderingQtiDtoAssembler.assembleDto(assessmentSection.getOrdering());
        assessmentSectionDto.setOrdering(orderingDto);
        List<AssessmentPreConditionDto> preConditions = new LinkedList<AssessmentPreConditionDto>();
        for (PreCondition preCondition : assessmentSection.getPreConditions()) {
            AssessmentPreConditionDto preConditionDto = preconditionQtiDtoAssembler.assembleDto(preCondition);
            preConditions.add(preConditionDto);
        }
        assessmentSectionDto.setPreConditions(preConditions);
        List<AssessmentRubricBlockDto> rubricBlocks = new LinkedList<AssessmentRubricBlockDto>();
        for (RubricBlock rubricBlock : assessmentSection.getRubricBlocks()) {
            AssessmentRubricBlockDto rubricBlockDto = rubricBlockQtiDtoAssembler.assembleDto(rubricBlock);
            rubricBlocks.add(rubricBlockDto);
        }
        assessmentSectionDto.setRubricBlocks(rubricBlocks);
        AssessmentSelectionDto selectionDto = selectionQtiDtoAssembler.assembleDto(assessmentSection.getSelection());
        assessmentSectionDto.setSelection(selectionDto);
        if (assessmentSection.getTimeLimits() != null) {
            assessmentSectionDto.setTimeLimits(assessmentSection.getTimeLimits().toString());
        }
        assessmentSectionDto.setTitle(assessmentSection.getTitle());
        assessmentSectionDto.setVisible(assessmentSection.getVisible());

        List<AssessmentItemRefDto> assessmentItemRefs = new LinkedList<AssessmentItemRefDto>();
        List<AssessmentSectionDto> assessmentSectionDtoList = new LinkedList<>();
        List<AssessmentComponentDto> assessmentComponentsList = new ArrayList<AssessmentComponentDto>();
        for (SectionPart sectionPart : assessmentSection.getSectionParts()) {
            if (sectionPart instanceof AssessmentItemRef) {
                AssessmentItemRef assessmentItemRef = (AssessmentItemRef) sectionPart;
                AssessmentItemRefDto assessmentItemRefDto = assessmentItemRefQtiDtoAssembler
                        .assembleDto(assessmentItemRef);
                assessmentItemRefs.add(assessmentItemRefDto);
                assessmentComponentsList.add(assessmentItemRefDto);
            } else if (sectionPart instanceof AssessmentSection) {
                AssessmentSection assessmentSection1 = (AssessmentSection) sectionPart;
                AssessmentSectionDto assessmentSectionDto1 = assembleDto(assessmentSection1);
                assessmentSectionDtoList.add(assessmentSectionDto1);
                assessmentComponentsList.add(assessmentSectionDto1);
            }
        }
        assessmentSectionDto.setAssessmentItemRefs(assessmentItemRefs);
        assessmentSectionDto.setAssessmentSections(assessmentSectionDtoList);
        assessmentSectionDto.setAssessmentComponents(assessmentComponentsList);

        return assessmentSectionDto;
    }

    @Override
    public AssessmentSection disassembleDto(AssessmentSectionDto assessmentSectionDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
