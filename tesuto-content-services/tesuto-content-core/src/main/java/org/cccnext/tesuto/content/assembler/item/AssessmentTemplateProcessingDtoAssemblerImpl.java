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

import org.cccnext.tesuto.content.dto.item.AssessmentDefaultValueDto;
import org.cccnext.tesuto.content.dto.item.AssessmentTemplateProcessingDto;
import org.cccnext.tesuto.content.model.item.AssessmentDefaultValue;
import org.cccnext.tesuto.content.model.item.AssessmentTemplateProcessing;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "templateProcessingDtoAssembler")
public class AssessmentTemplateProcessingDtoAssemblerImpl implements AssessmentTemplateProcessingDtoAssembler {

    @Autowired
    private AssessmentDefaultValueDtoAssembler defaultValueDtoAssembler;

    @Override
    public AssessmentTemplateProcessingDto assembleDto(AssessmentTemplateProcessing templateProcessing) {
        // Drop out immediately if there is nothing to assemble.
        if (templateProcessing == null) {
            return null;
        }

        AssessmentTemplateProcessingDto templateProcessingDto = new AssessmentTemplateProcessingDto();

        templateProcessingDto.setCorrectResponse(templateProcessing.getCorrectResponse());
        AssessmentDefaultValueDto defaultValueDto = defaultValueDtoAssembler
                .assembleDto(templateProcessing.getDefaultValue());
        templateProcessingDto.setDefaultValueDto(defaultValueDto);
        templateProcessingDto.setExitTemplate(templateProcessing.getExitTemplate());
        templateProcessingDto.setSetDefaultValue(templateProcessing.getSetDefaultValue());
        templateProcessingDto.setSetValue(templateProcessing.getSetValue());
        templateProcessingDto.setTemplateCondition(templateProcessing.getTemplateCondition());
        templateProcessingDto.setTemplateConstraint(templateProcessing.getTemplateConstraint());

        return templateProcessingDto;
    }

    @Override
    public AssessmentTemplateProcessing disassembleDto(AssessmentTemplateProcessingDto templateProcessingDto) {
        // Drop out immediately if there is nothing to assemble.
        if (templateProcessingDto == null) {
            return null;
        }

        AssessmentTemplateProcessing templateProcessing = new AssessmentTemplateProcessing();

        templateProcessing.setCorrectResponse(templateProcessingDto.getCorrectResponse());
        AssessmentDefaultValue defaultValue = defaultValueDtoAssembler
                .disassembleDto(templateProcessingDto.getDefaultValueDto());
        templateProcessing.setDefaultValue(defaultValue);
        templateProcessing.setExitTemplate(templateProcessingDto.getExitTemplate());
        templateProcessing.setSetDefaultValue(templateProcessingDto.getSetDefaultValue());
        templateProcessing.setSetValue(templateProcessingDto.getSetValue());
        templateProcessing.setTemplateCondition(templateProcessingDto.getTemplateCondition());
        templateProcessing.setTemplateConstraint(templateProcessingDto.getTemplateConstraint());

        return templateProcessing;
    }
}
