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

import org.cccnext.tesuto.content.dto.item.AssessmentCorrectResponseDto;
import org.cccnext.tesuto.content.dto.item.AssessmentDefaultValueDto;
import org.cccnext.tesuto.content.dto.item.AssessmentResponseVarDto;
import org.cccnext.tesuto.content.model.item.AssessmentCorrectResponse;
import org.cccnext.tesuto.content.model.item.AssessmentDefaultValue;
import org.cccnext.tesuto.content.model.item.AssessmentResponseVar;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "responseVarDtoAssembler")
public class AssessmentResponseVarDtoAssemblerImpl implements AssessmentResponseVarDtoAssembler {

    @Autowired
    private AssessmentCorrectResponseDtoAssembler correctResponseDtoAssembler;
    @Autowired
    private AssessmentDefaultValueDtoAssembler defaultValueDtoAssembler;

    @Autowired
    private AssessmentItemResponseMappingDtoAssembler assessmentItemResponseMappingDtoAssembler;

    @Override
    public AssessmentResponseVarDto assembleDto(AssessmentResponseVar responseDeclaration) {
        // Drop out immediately if there is nothing to assemble.
        if (responseDeclaration == null) {
            return null;
        }

        AssessmentResponseVarDto responseVarDto = new AssessmentResponseVarDto();
        responseVarDto.setBaseType(responseDeclaration.getBaseType());
        responseVarDto.setCardinality(responseDeclaration.getCardinality());
        AssessmentCorrectResponseDto correctResponseDto = correctResponseDtoAssembler
                .assembleDto(responseDeclaration.getCorrectResponse());
        responseVarDto.setCorrectResponse(correctResponseDto);
        AssessmentDefaultValueDto defaultValueDto = defaultValueDtoAssembler
                .assembleDto(responseDeclaration.getDefaultValue());
        responseVarDto.setDefaultValue(defaultValueDto);
        responseVarDto.setIdentifier(responseDeclaration.getIdentifier().toString());
        responseVarDto
                .setMapping(assessmentItemResponseMappingDtoAssembler.assembleDto(responseDeclaration.getMapping()));

        return responseVarDto;
    }

    @Override
    public AssessmentResponseVar disassembleDto(AssessmentResponseVarDto responseVarDto) {
        // Drop out immediately if there is nothing to assemble.
        if (responseVarDto == null) {
            return null;
        }

        AssessmentResponseVar responseVar = new AssessmentResponseVar();
        responseVar.setBaseType(responseVarDto.getBaseType());
        responseVar.setCardinality(responseVarDto.getCardinality());
        AssessmentCorrectResponse correctResponse = correctResponseDtoAssembler
                .disassembleDto(responseVarDto.getCorrectResponse());
        responseVar.setCorrectResponse(correctResponse);
        AssessmentDefaultValue defaultValue = defaultValueDtoAssembler.disassembleDto(responseVarDto.getDefaultValue());
        responseVar.setDefaultValue(defaultValue);
        responseVar.setIdentifier(responseVarDto.getIdentifier().toString());
        responseVar.setMapping(assessmentItemResponseMappingDtoAssembler.disassembleDto(responseVarDto.getMapping()));

        return responseVar;
    }
}
