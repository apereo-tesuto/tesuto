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

import uk.ac.ed.ph.jqtiplus.node.item.template.declaration.TemplateDeclaration;

import org.cccnext.tesuto.importer.qti.assembler.AssessmentBaseTypeQtiDtoAssembler;
import org.cccnext.tesuto.content.dto.AssessmentBaseType;
import org.cccnext.tesuto.content.dto.item.AssessmentCardinality;
import org.cccnext.tesuto.content.dto.item.AssessmentDefaultValueDto;
import org.cccnext.tesuto.content.dto.item.AssessmentTemplateDeclarationDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@Component(value = "templateDeclarationQtiDtoAssembler")
public class AssessmentTemplateDeclarationQtiDtoAssemblerImpl implements AssessmentTemplateDeclarationQtiDtoAssembler {

    @Autowired
    private AssessmentBaseTypeQtiDtoAssembler baseTypeQtiDtoAssembler;
    @Autowired
    private AssessmentCardinalityQtiDtoAssembler cardinalityQtiDtoAssembler;
    @Autowired
    private AssessmentDefaultValueQtiDtoAssembler defaultValueQtiDtoAssembler;

    @Override
    public AssessmentTemplateDeclarationDto assembleDto(TemplateDeclaration templateDeclaration) {
        // Drop out immediately if there is nothing to assemble.
        if (templateDeclaration == null) {
            return null;
        }

        AssessmentTemplateDeclarationDto templateDeclarationDto = new AssessmentTemplateDeclarationDto();
        AssessmentBaseType baseTypeDto = baseTypeQtiDtoAssembler.assembleDto(templateDeclaration.getBaseType());
        templateDeclarationDto.setBaseType(baseTypeDto);
        AssessmentCardinality cardinalityDto = cardinalityQtiDtoAssembler
                .assembleDto(templateDeclaration.getCardinality());
        templateDeclarationDto.setCardinality(cardinalityDto);
        AssessmentDefaultValueDto defaultValueDto = defaultValueQtiDtoAssembler
                .assembleDto(templateDeclaration.getDefaultValue());
        templateDeclarationDto.setDefaultValue(defaultValueDto);
        templateDeclarationDto.setId(templateDeclaration.getIdentifier().toString());
        templateDeclarationDto.setIsMathVar(templateDeclaration.getMathVariable());
        templateDeclarationDto.setParamVariable(templateDeclaration.getParamVariable());

        return templateDeclarationDto;
    }

    @Override
    public TemplateDeclaration disassembleDto(AssessmentTemplateDeclarationDto templateDeclarationDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
