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
package org.cccnext.tesuto.content.assembler.shared;

import java.util.LinkedList;
import java.util.List;

import org.cccnext.tesuto.content.assembler.item.AssessmentDefaultValueDtoAssembler;
import org.cccnext.tesuto.content.dto.item.AssessmentDefaultValueDto;
import org.cccnext.tesuto.content.dto.shared.AssessmentOutcomeDeclarationDto;
import org.cccnext.tesuto.content.model.item.AssessmentDefaultValue;
import org.cccnext.tesuto.content.model.shared.AssessmentOutcomeDeclaration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "outcomeDeclarationDtoAssembler")
public class AssessmentOutcomeDeclarationDtoAssemblerImpl implements AssessmentOutcomeDeclarationDtoAssembler {

    @Autowired
    private AssessmentDefaultValueDtoAssembler defaultValueDtoAssembler;

    @Override
    public AssessmentOutcomeDeclarationDto assembleDto(AssessmentOutcomeDeclaration outcomeDeclaration) {
        // Drop out immediately if there is nothing to assemble.
        if (outcomeDeclaration == null) {
            return null;
        }

        AssessmentOutcomeDeclarationDto outcomeDeclarationDto = new AssessmentOutcomeDeclarationDto();
        outcomeDeclarationDto.setBaseType(outcomeDeclaration.getBaseType());
        outcomeDeclarationDto.setCardinality(outcomeDeclaration.getCardinality());
        AssessmentDefaultValueDto defaultValueDto = defaultValueDtoAssembler
                .assembleDto(outcomeDeclaration.getDefaultValue());
        outcomeDeclarationDto.setDefaultValue(defaultValueDto);
        outcomeDeclarationDto.setInterpretation(outcomeDeclaration.getInterpretation());
        outcomeDeclarationDto.setExternalScored(outcomeDeclaration.getExternalScored());
        outcomeDeclarationDto.setIdentifier(outcomeDeclaration.getIdentifier());
        outcomeDeclarationDto.setLongInterpretation(outcomeDeclaration.getLongInterpretation());
        outcomeDeclarationDto.setLookupTable(outcomeDeclaration.getLookupTable());
        outcomeDeclarationDto.setMasteryValue(outcomeDeclaration.getMasteryValue());
        outcomeDeclarationDto.setNormalMaximum(outcomeDeclaration.getNormalMaximum());
        outcomeDeclarationDto.setNormalMinimum(outcomeDeclaration.getNormalMinimum());
        outcomeDeclarationDto.setVariableIdentifierRef(outcomeDeclaration.getVariableIdentifierRef());
        if (outcomeDeclaration.getViews() != null) {
            List<String> viewList = new LinkedList<String>(outcomeDeclaration.getViews());
            outcomeDeclarationDto.setViews(viewList);
        }

        return outcomeDeclarationDto;
    }

    @Override
    public AssessmentOutcomeDeclaration disassembleDto(AssessmentOutcomeDeclarationDto outcomeDeclarationDto) {
        // Drop out outcomeDeclarationDto if there is nothing to assemble.
        if (outcomeDeclarationDto == null) {
            return null;
        }

        AssessmentOutcomeDeclaration outcomeDeclaration = new AssessmentOutcomeDeclaration();
        outcomeDeclaration.setBaseType(outcomeDeclarationDto.getBaseType());
        outcomeDeclaration.setCardinality(outcomeDeclarationDto.getCardinality());
        AssessmentDefaultValue defaultValue = defaultValueDtoAssembler.disassembleDto(outcomeDeclarationDto.getDefaultValue());
        outcomeDeclaration.setDefaultValue(defaultValue);
        outcomeDeclaration.setInterpretation(outcomeDeclarationDto.getInterpretation());
        outcomeDeclaration.setExternalScored(outcomeDeclarationDto.getExternalScored());
        outcomeDeclaration.setIdentifier(outcomeDeclarationDto.getIdentifier());
        outcomeDeclaration.setLongInterpretation(outcomeDeclarationDto.getLongInterpretation());
        outcomeDeclaration.setLookupTable(outcomeDeclarationDto.getLookupTable());
        outcomeDeclaration.setMasteryValue(outcomeDeclarationDto.getMasteryValue());
        outcomeDeclaration.setNormalMaximum(outcomeDeclarationDto.getNormalMaximum());
        outcomeDeclaration.setNormalMinimum(outcomeDeclarationDto.getNormalMinimum());
        outcomeDeclaration.setVariableIdentifierRef(outcomeDeclarationDto.getVariableIdentifierRef());
        if (outcomeDeclarationDto.getViews() != null) {
            List<String> viewList = new LinkedList<String>(outcomeDeclarationDto.getViews());
            outcomeDeclaration.setViews(viewList);
        }
        return outcomeDeclaration;
    }
}
