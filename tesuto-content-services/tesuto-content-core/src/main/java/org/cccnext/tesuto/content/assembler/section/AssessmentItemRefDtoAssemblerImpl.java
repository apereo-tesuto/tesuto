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

import java.util.LinkedList;
import java.util.List;

import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemSessionControlDto;
import org.cccnext.tesuto.content.model.section.AssessmentItemRef;
import org.cccnext.tesuto.content.model.section.AssessmentItemSessionControl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "assessmentItemRefDtoAssembler")
public class AssessmentItemRefDtoAssemblerImpl implements AssessmentItemRefDtoAssembler {

    @Autowired
    AssessmentBranchRuleDtoAssembler branchRuleDtoAssembler;
    @Autowired
    AssessmentItemSessionControlDtoAssembler itemSessionControlDtoAssembler;
    @Autowired
    AssessmentPreconditionDtoAssembler preconditionDtoAssembler;

    @Override
    public AssessmentItemRefDto assembleDto(AssessmentItemRef assessmentItemRef) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentItemRef == null) {
            return null;
        }

        AssessmentItemRefDto assessmentItemRefDto = new AssessmentItemRefDto();
        assessmentItemRefDto.setBranchRules(branchRuleDtoAssembler.assembleDto(assessmentItemRef.getBranchRules()));
        List<String> categoryListDto = new LinkedList<String>();
        if (assessmentItemRef.getCategories() != null) {
            for (String category : assessmentItemRef.getCategories()) {
                categoryListDto.add(category);
            }
        }
        assessmentItemRefDto.setCategories(categoryListDto);
        assessmentItemRefDto.setItemIdentifier(assessmentItemRef.getItemIdentifier());
        assessmentItemRefDto.setIdentifier(assessmentItemRef.getIdentifier());
        AssessmentItemSessionControlDto itemSessionControlDto = itemSessionControlDtoAssembler
                .assembleDto(assessmentItemRef.getItemSessionControl());
        assessmentItemRefDto.setItemSessionControl(itemSessionControlDto);
        assessmentItemRefDto.setIsFixed(assessmentItemRef.isFixed());
        assessmentItemRefDto.setIsRequired(assessmentItemRef.isRequired());

        assessmentItemRefDto
                .setPreConditions(preconditionDtoAssembler.assembleDto(assessmentItemRef.getPreConditions()));
        List<String> templateDefaultsList = new LinkedList<String>();
        if (!CollectionUtils.isNullOrEmpty(assessmentItemRef.getTemplateDefaultss())) {
            for (String templateDefault : assessmentItemRef.getTemplateDefaultss()) {
                // TODO: Since this is a mathematical expression template, it
                // might need more work.
                templateDefaultsList.add(templateDefault);
            }
        }
        assessmentItemRefDto.setTemplateDefaultss(templateDefaultsList);
        assessmentItemRefDto.setTimeLimits(assessmentItemRef.getTimeLimits());

        assessmentItemRefDto.setVariableMapping(assessmentItemRef.getVariableMapping());
        assessmentItemRefDto.setWeight(assessmentItemRef.getWeight());

        return assessmentItemRefDto;
    }

    @Override
    public AssessmentItemRef disassembleDto(AssessmentItemRefDto assessmentItemRefDto) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentItemRefDto == null) {
            return null;
        }

        AssessmentItemRef assessmentItemRef = new AssessmentItemRef();

        assessmentItemRef.setBranchRules(branchRuleDtoAssembler.disassembleDto(assessmentItemRefDto.getBranchRules()));
        List<String> categoryList = new LinkedList<String>();
        if (assessmentItemRefDto.getCategories() != null) {
            for (String category : assessmentItemRefDto.getCategories()) {
                categoryList.add(category);
            }
        }
        assessmentItemRef.setCategories(categoryList);
        assessmentItemRef.setItemIdentifier(assessmentItemRefDto.getItemIdentifier());
        assessmentItemRef.setIdentifier(assessmentItemRefDto.getIdentifier());
        AssessmentItemSessionControl itemSessionControl = itemSessionControlDtoAssembler
                .disassembleDto(assessmentItemRefDto.getItemSessionControl());
        assessmentItemRef.setItemSessionControl(itemSessionControl);
        assessmentItemRef.setIsFixed(assessmentItemRefDto.isFixed());
        assessmentItemRef.setIsRequired(assessmentItemRefDto.isRequired());

        assessmentItemRef
                .setPreConditions(preconditionDtoAssembler.disassembleDto(assessmentItemRefDto.getPreConditions()));
        List<String> templateDefaultsList = new LinkedList<String>();
        if (!CollectionUtils.isNullOrEmpty(assessmentItemRefDto.getTemplateDefaultss())) {
            for (String templateDefault : assessmentItemRefDto.getTemplateDefaultss()) {
                templateDefaultsList.add(templateDefault);
            }
        }
        assessmentItemRef.setTemplateDefaultss(templateDefaultsList);
        assessmentItemRef.setTimeLimits(assessmentItemRefDto.getTimeLimits());

        assessmentItemRef.setVariableMapping(assessmentItemRefDto.getVariableMapping());
        assessmentItemRef.setWeight(assessmentItemRefDto.getWeight());

        return assessmentItemRef;
    }
}
