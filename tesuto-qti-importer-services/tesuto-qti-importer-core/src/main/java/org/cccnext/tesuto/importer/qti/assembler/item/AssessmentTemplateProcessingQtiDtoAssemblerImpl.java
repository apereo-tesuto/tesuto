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
package org.cccnext.tesuto.importer.qti.assembler.item;

import uk.ac.ed.ph.jqtiplus.node.item.template.processing.ExitTemplate;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.SetCorrectResponse;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.SetDefaultValue;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.SetTemplateValue;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.TemplateCondition;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.TemplateConstraint;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.TemplateProcessing;
import uk.ac.ed.ph.jqtiplus.node.item.template.processing.TemplateProcessingRule;

import org.cccnext.tesuto.content.dto.item.AssessmentTemplateProcessingDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "templateProcessingQtiDtoAssembler")
public class AssessmentTemplateProcessingQtiDtoAssemblerImpl implements AssessmentTemplateProcessingQtiDtoAssembler {

    @Autowired
    private AssessmentCorrectResponseQtiDtoAssembler correctResponseQtiDtoAssembler;
    @Autowired
    private AssessmentDefaultValueQtiDtoAssembler defaultValueQtiDtoAssembler;

    @Override
    public AssessmentTemplateProcessingDto assembleDto(TemplateProcessing templateProcessing) {
        // Drop out immediately if there is nothing to assemble.
        if (templateProcessing == null) {
            return null;
        }

        AssessmentTemplateProcessingDto templateProcessingDto = new AssessmentTemplateProcessingDto();
        // TODO: Verify there is only one rule for each simplification, if not
        // the last one wins and that may not be okay.
        for (TemplateProcessingRule templateProcessingRule : templateProcessing.getTemplateProcessingRules()) {
            if (templateProcessingRule instanceof SetDefaultValue) {
                // TODO: Determine whether this should be DefaultValue or
                // SetDefaultValue
                // If it's DefaultValue, then we need to wire in the already
                // crewated assembler for it.
                // If it's SetDefaultValue, then we need to implement one or
                // decide to simply make a String out of it.
                templateProcessingDto.setSetDefaultValue(((SetDefaultValue) templateProcessingRule).toString());
                /*
                 * DefaultValueDto defaultValueDto =
                 * defaultValueQtiDtoAssembler.assembleDto(null);
                 * templateProcessingDto.setDefaultValueDto(defaultValueDto);
                 */
            } else if (templateProcessingRule instanceof ExitTemplate) {
                templateProcessingDto.setExitTemplate(((ExitTemplate) templateProcessingRule).toString());
            } else if (templateProcessingRule instanceof TemplateCondition) {
                // TODO: I'm not sure we can make this simplification. We may
                // need to model these flow conditions in
                // the same way as OutcomeProcessing
                TemplateCondition templateCondition = (TemplateCondition) templateProcessingRule;
                if (templateCondition.getTemplateIf() != null) {
                    templateProcessingDto.setTemplateCondition(templateCondition.getTemplateIf().toString());
                } else if (templateCondition.getTemplateElseIfs() != null) {
                    templateProcessingDto.setTemplateCondition(templateCondition.getTemplateElseIfs().toString());
                } else if (templateCondition.getTemplateElse() != null) {
                    templateProcessingDto.setTemplateCondition(templateCondition.getTemplateElse().toString());
                }
            } else if (templateProcessingRule instanceof TemplateConstraint) {
                // TODO: Verify this is correct. More will be needed to add
                // meaing to this String representing an Expression.
                templateProcessingDto.setTemplateConstraint(
                        ((TemplateConstraint) templateProcessingRule).getExpressions().toString());
            } else if (templateProcessingRule instanceof SetTemplateValue) {
                // TODO: Getting tired of saying this, but we should verify this
                // is correct.
                templateProcessingDto.setSetValue(((SetTemplateValue) templateProcessingRule).toString());
            } else if (templateProcessingRule instanceof SetCorrectResponse) {
                // TODO: Getting tired of saying this, but we should verify this
                // is correct.
                templateProcessingDto.setCorrectResponse(((SetCorrectResponse) templateProcessingRule).toString());
                /*
                 * We may need something like this if we need to keep both the
                 * value and the description CorrectResponseDto
                 * correctResponseDto =
                 * correctResponseQtiDtoAssembler.assembleDto(null);
                 * templateProcessingDto.setCorrectResponse(correctResponseDto);
                 */
            }
        }

        return templateProcessingDto;
    }

    @Override
    public TemplateProcessing disassembleDto(AssessmentTemplateProcessingDto templateProcessingDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
