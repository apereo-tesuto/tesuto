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
import org.cccnext.tesuto.content.dto.item.AssessmentTemplateDeclarationDto;
import org.cccnext.tesuto.content.model.item.AssessmentDefaultValue;
import org.cccnext.tesuto.content.model.item.AssessmentTemplateDeclaration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "templateDeclarationDtoAssembler")
public class AssessmentTemplateDeclarationDtoAssemblerImpl implements AssessmentTemplateDeclarationDtoAssembler {

    @Autowired
    private AssessmentDefaultValueDtoAssembler defaultValueDtoAssembler;

    @Override
    public AssessmentTemplateDeclarationDto assembleDto(AssessmentTemplateDeclaration templateDeclaration) {
        // Drop out immediately if there is nothing to assemble.
        if (templateDeclaration == null) {
            return null;
        }

        AssessmentTemplateDeclarationDto templateDeclarationDto = new AssessmentTemplateDeclarationDto();
        templateDeclarationDto.setBaseType(templateDeclaration.getBaseType());
        templateDeclarationDto.setCardinality(templateDeclaration.getCardinality());
        AssessmentDefaultValueDto defaultValueDto = defaultValueDtoAssembler
                .assembleDto(templateDeclaration.getDefaultValue());
        templateDeclarationDto.setDefaultValue(defaultValueDto);
        templateDeclarationDto.setId(templateDeclaration.getId());
        templateDeclarationDto.setIsMathVar(templateDeclaration.isMathVar());
        templateDeclarationDto.setParamVariable(templateDeclaration.isParamVariable());

        return templateDeclarationDto;
    }

    @Override
    public AssessmentTemplateDeclaration disassembleDto(AssessmentTemplateDeclarationDto templateDeclarationDto) {
        // Drop out immediately if there is nothing to assemble.
        if (templateDeclarationDto == null) {
            return null;
        }

        AssessmentTemplateDeclaration templateDeclaration = new AssessmentTemplateDeclaration();
        templateDeclaration.setBaseType(templateDeclarationDto.getBaseType());
        templateDeclaration.setCardinality(templateDeclarationDto.getCardinality());
        AssessmentDefaultValue defaultValue = defaultValueDtoAssembler
                .disassembleDto(templateDeclarationDto.getDefaultValue());
        templateDeclaration.setDefaultValue(defaultValue);
        templateDeclaration.setId(templateDeclarationDto.getId());
        templateDeclaration.setIsMathVar(templateDeclarationDto.isMathVar());
        templateDeclaration.setParamVariable(templateDeclarationDto.isParamVariable());

        return templateDeclaration;
    }
}
