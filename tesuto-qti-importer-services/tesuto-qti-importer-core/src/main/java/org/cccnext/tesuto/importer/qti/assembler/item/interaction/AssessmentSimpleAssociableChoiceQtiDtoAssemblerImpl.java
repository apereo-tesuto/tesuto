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
package org.cccnext.tesuto.importer.qti.assembler.item.interaction;

import uk.ac.ed.ph.jqtiplus.node.content.basic.FlowStatic;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleAssociableChoice;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;

import java.util.UUID;

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleAssociableChoiceDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "assessmentSimpleAssociableChoiceQtiDtoAssembler")
public class AssessmentSimpleAssociableChoiceQtiDtoAssemblerImpl
        implements AssessmentSimpleAssociableChoiceQtiDtoAssembler {

    @Autowired
    QtiSerializer qtiSerializer;

    @Override
    public AssessmentSimpleAssociableChoiceDto assembleDto(SimpleAssociableChoice simpleAssociableChoice) {
        // Drop out immediately if there is nothing to assemble.
        if (simpleAssociableChoice == null) {
            return null;
        }

        AssessmentSimpleAssociableChoiceDto assessmentSimpleAssociableChoiceDto = new AssessmentSimpleAssociableChoiceDto();
        StringBuilder content = new StringBuilder();
        for (FlowStatic flowStatic : simpleAssociableChoice.getFlowStatics()) {
            content.append(qtiSerializer.serializeJqtiObject(flowStatic));
        }
        assessmentSimpleAssociableChoiceDto.setContent(content.toString());
        if (simpleAssociableChoice.getId() != null) {
            assessmentSimpleAssociableChoiceDto.setId(simpleAssociableChoice.getId().toString());
        }
        assessmentSimpleAssociableChoiceDto.setIdentifier(simpleAssociableChoice.getIdentifier().toString());
        assessmentSimpleAssociableChoiceDto.setUiid(UUID.randomUUID().toString());
        assessmentSimpleAssociableChoiceDto.setMatchMax(simpleAssociableChoice.getMatchMax());
        assessmentSimpleAssociableChoiceDto.setMatchMin(simpleAssociableChoice.getMatchMin());

        return assessmentSimpleAssociableChoiceDto;
    }

    @Override
    public SimpleAssociableChoice disassembleDto(
            AssessmentSimpleAssociableChoiceDto assessmentSimpleAssociableChoiceDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
