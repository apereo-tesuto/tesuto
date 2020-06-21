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

import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;

import org.cccnext.tesuto.importer.qti.assembler.AssessmentBaseTypeQtiDtoAssembler;
import org.cccnext.tesuto.content.dto.AssessmentBaseType;
import org.cccnext.tesuto.content.dto.item.AssessmentCardinality;
import org.cccnext.tesuto.content.dto.item.AssessmentCorrectResponseDto;
import org.cccnext.tesuto.content.dto.item.AssessmentDefaultValueDto;
import org.cccnext.tesuto.content.dto.item.AssessmentResponseVarDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "responseVarQtiDtoAssembler")
public class AssessmentResponseVarQtiDtoAssemblerImpl implements AssessmentResponseVarQtiDtoAssembler {

    @Autowired
    private AssessmentBaseTypeQtiDtoAssembler baseTypeQtiDtoAssembler;
    @Autowired
    private AssessmentCardinalityQtiDtoAssembler cardinalityQtiDtoAssembler;
    @Autowired
    private AssessmentCorrectResponseQtiDtoAssembler correctResponseQtiDtoAssembler;
    @Autowired
    private AssessmentDefaultValueQtiDtoAssembler defaultValueQtiDtoAssembler;
    @Autowired
    private AssessmentItemResponseMappingQtiDtoAssembler assessmentItemResponseMappingQtiDtoAssembler;

    @Override
    public AssessmentResponseVarDto assembleDto(ResponseDeclaration responseDeclaration) {
        // Drop out immediately if there is nothing to assemble.
        if (responseDeclaration == null) {
            return null;
        }

        AssessmentResponseVarDto responseVarDto = new AssessmentResponseVarDto();
        AssessmentBaseType baseTypeDto = baseTypeQtiDtoAssembler.assembleDto(responseDeclaration.getBaseType());
        responseVarDto.setBaseType(baseTypeDto);
        AssessmentCardinality cardinalityDto = cardinalityQtiDtoAssembler
                .assembleDto(responseDeclaration.getCardinality());
        responseVarDto.setCardinality(cardinalityDto);
        AssessmentCorrectResponseDto correctResponseDto = correctResponseQtiDtoAssembler
                .assembleDto(responseDeclaration.getCorrectResponse());
        responseVarDto.setCorrectResponse(correctResponseDto);
        AssessmentDefaultValueDto defaultValueDto = defaultValueQtiDtoAssembler
                .assembleDto(responseDeclaration.getDefaultValue());
        responseVarDto.setDefaultValue(defaultValueDto);
        responseVarDto.setIdentifier(responseDeclaration.getIdentifier().toString());
        responseVarDto
                .setMapping(assessmentItemResponseMappingQtiDtoAssembler.assembleDto(responseDeclaration.getMapping()));

        return responseVarDto;
    }

    @Override
    public ResponseDeclaration disassembleDto(AssessmentResponseVarDto responseVarDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
