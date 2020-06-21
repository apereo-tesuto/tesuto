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

import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.BranchRule;
import uk.ac.ed.ph.jqtiplus.node.test.PreCondition;
import uk.ac.ed.ph.jqtiplus.node.test.TemplateDefault;

import java.util.LinkedList;
import java.util.List;

import org.cccnext.tesuto.content.dto.expression.AssessmentBranchRuleDto;
import org.cccnext.tesuto.content.dto.expression.AssessmentPreConditionDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemSessionControlDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "assessmentItemRefQtiDtoAssembler")
public class AssessmentItemRefQtiDtoAssemblerImpl implements AssessmentItemRefQtiDtoAssembler {

    @Autowired
    AssessmentBranchRuleQtiDtoAssembler branchRuleQtiDtoAssembler;
    @Autowired
    AssessmentItemSessionControlQtiDtoAssembler itemSessionControlQtiDtoAssembler;
    @Autowired
    AssessmentPreconditionQtiDtoAssembler preconditionQtiDtoAssembler;

    @Override
    public AssessmentItemRefDto assembleDto(AssessmentItemRef assessmentItemRef) {
        // Drop out immediately if there is nothing to assemble.
        if (assessmentItemRef == null) {
            return null;
        }

        AssessmentItemRefDto assessmentItemRefDto = new AssessmentItemRefDto();
        List<AssessmentBranchRuleDto> branchRules = new LinkedList<AssessmentBranchRuleDto>();
        for (BranchRule branchRule : assessmentItemRef.getBranchRules()) {
            AssessmentBranchRuleDto branchRuleDto = branchRuleQtiDtoAssembler.assembleDto(branchRule);
            branchRules.add(branchRuleDto);
        }
        assessmentItemRefDto.setBranchRules(branchRules);
        List<String> categoryList = new LinkedList<String>();
        if (assessmentItemRef.getCategories() != null) {
            for (String category : assessmentItemRef.getCategories()) {
                categoryList.add(category.toUpperCase());
            }
        }
        assessmentItemRefDto.setCategories(categoryList);
        assessmentItemRefDto.setItemIdentifier(assessmentItemRef.getHref().toString());
        assessmentItemRefDto.setIdentifier(assessmentItemRef.getIdentifier().toString());
        AssessmentItemSessionControlDto itemSessionControlDto = itemSessionControlQtiDtoAssembler
                .assembleDto(assessmentItemRef.getItemSessionControl());
        assessmentItemRefDto.setItemSessionControl(itemSessionControlDto);
        assessmentItemRefDto.setIsFixed(assessmentItemRef.getFixed());
        assessmentItemRefDto.setIsRequired(assessmentItemRef.getRequired());
        List<AssessmentPreConditionDto> preConditions = new LinkedList<AssessmentPreConditionDto>();
        for (PreCondition preCondition : assessmentItemRef.getPreConditions()) {
            AssessmentPreConditionDto preConditionDto = preconditionQtiDtoAssembler.assembleDto(preCondition);
            preConditions.add(preConditionDto);
        }
        assessmentItemRefDto.setPreConditions(preConditions);
        List<String> templateDefaultsList = new LinkedList<String>();
        for (TemplateDefault templateDefault : assessmentItemRef.getTemplateDefaults()) {
            // TODO: Since this is a mathematical expression template, it might
            // need more work.
            String expression = templateDefault.getExpression().toString();
            templateDefaultsList.add(expression);
        }
        assessmentItemRefDto.setTemplateDefaultss(templateDefaultsList);
        // Note we are only setting the maximum here. An new object is required
        // for implementing the whole QTI spec.
        if (assessmentItemRef.getTimeLimits() != null && assessmentItemRef.getTimeLimits().getMaximum() != null) {
            assessmentItemRefDto.setTimeLimits(assessmentItemRef.getTimeLimits().getMaximum());
        }
        // TODO: Total cop out because it's not for pilot. There is a source and
        // target for each variable we should likely handle.
        assessmentItemRefDto.setVariableMapping(assessmentItemRef.getVariableMappings().toString());
        // TODO: It's possible that this needs more analysis.
        if (assessmentItemRef.getWeights() != null && !assessmentItemRef.getWeights().isEmpty()) {
            assessmentItemRefDto.setWeight(assessmentItemRef.getWeight(assessmentItemRef.getIdentifier()).getValue());
        }

        return assessmentItemRefDto;
    }

    @Override
    public AssessmentItemRef disassembleDto(AssessmentItemRefDto assessmentItemRefDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
