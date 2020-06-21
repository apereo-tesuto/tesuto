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
package org.cccnext.tesuto.importer.qti.assembler.shared;

import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.test.View;

import java.util.LinkedList;
import java.util.List;

import org.cccnext.tesuto.importer.qti.assembler.AssessmentBaseTypeQtiDtoAssembler;
import org.cccnext.tesuto.importer.qti.assembler.item.AssessmentCardinalityQtiDtoAssembler;
import org.cccnext.tesuto.importer.qti.assembler.item.AssessmentDefaultValueQtiDtoAssembler;
import org.cccnext.tesuto.content.dto.AssessmentBaseType;
import org.cccnext.tesuto.content.dto.item.AssessmentCardinality;
import org.cccnext.tesuto.content.dto.item.AssessmentDefaultValueDto;
import org.cccnext.tesuto.content.dto.shared.AssessmentOutcomeDeclarationDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "assessmentOutcomeDeclarationQtiDtoAssembler")
public class AssessmentOutcomeDeclarationQtiDtoAssemblerImpl implements AssessmentOutcomeDeclarationQtiDtoAssembler {

    @Autowired
    private AssessmentBaseTypeQtiDtoAssembler baseTypeQtiDtoAssembler;
    @Autowired
    private AssessmentCardinalityQtiDtoAssembler cardinalityQtiDtoAssembler;
    @Autowired
    private AssessmentDefaultValueQtiDtoAssembler defaultValueQtiDtoAssembler;

    @Override
    public AssessmentOutcomeDeclarationDto assembleDto(OutcomeDeclaration outcomeDeclaration) {
        // Drop out immediately if there is nothing to assemble.
        if (outcomeDeclaration == null) {
            return null;
        }

        AssessmentOutcomeDeclarationDto outcomeDeclarationDto = new AssessmentOutcomeDeclarationDto();
        AssessmentBaseType baseTypeDto = baseTypeQtiDtoAssembler.assembleDto(outcomeDeclaration.getBaseType());
        outcomeDeclarationDto.setBaseType(baseTypeDto);
        AssessmentCardinality cardinalityDto = cardinalityQtiDtoAssembler
                .assembleDto(outcomeDeclaration.getCardinality());
        outcomeDeclarationDto.setCardinality(cardinalityDto);
        AssessmentDefaultValueDto defaultValueDto = defaultValueQtiDtoAssembler
                .assembleDto(outcomeDeclaration.getDefaultValue());
        outcomeDeclarationDto.setDefaultValue(defaultValueDto);
        outcomeDeclarationDto.setInterpretation(outcomeDeclaration.getInterpretation());
        // FIXME: QTI WORKS does not support must update Pilbender
        //        outcomeDeclarationDto.setExternalScored();
        // FIXME: QTI WORKS does not support must update Pilbender
        //        outcomeDeclarationDto.setVariableIdentifierRef();

        // TODO: Figure out what should go here.
        outcomeDeclarationDto.setIdentifier(outcomeDeclaration.getIdentifier().toString()); // TODO:
                                                                            // Verify
                                                                            // whether
                                                                            // we
                                                                            // need
                                                                            // to
                                                                            // make
                                                                            // a
                                                                            // null
                                                                            // pointer
                                                                            // check
        if (outcomeDeclaration.getLongInterpretation() != null) {
            outcomeDeclarationDto.setLongInterpretation(outcomeDeclaration.getLongInterpretation().toString());
        }
        // outcomeDeclarationDto.setLookupTable(outcomeDeclaration.getLookupTable());
        // // Not for pilot.
        outcomeDeclarationDto.setMasteryValue(outcomeDeclaration.getMasteryValue());
        outcomeDeclarationDto.setNormalMaximum(outcomeDeclaration.getNormalMaximum());
        outcomeDeclarationDto.setNormalMinimum(outcomeDeclaration.getNormalMinimum());
        // outcomeDeclarationDto.setVariableIdentifierRef(outcomeDeclaration.get);
        // // Not for pilot
        if (outcomeDeclaration.getViews() != null) {
            List<String> viewList = new LinkedList<String>();
            for (View view : outcomeDeclaration.getViews()) {
                viewList.add(view.toQtiString()); // TODO: Is there a
                                                  // possibility of null here?
            }
            outcomeDeclarationDto.setViews(viewList);
        }

        return outcomeDeclarationDto;
    }

    @Override
    public OutcomeDeclaration disassembleDto(AssessmentOutcomeDeclarationDto outcomeDeclarationDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
