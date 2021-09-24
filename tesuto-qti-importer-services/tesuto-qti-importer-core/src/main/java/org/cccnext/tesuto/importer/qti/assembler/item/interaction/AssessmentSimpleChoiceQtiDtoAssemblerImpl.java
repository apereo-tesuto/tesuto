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
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleChoice;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;

import java.util.UUID;

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleChoiceDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "simpleChoiceQtiDtoAssembler")
public class AssessmentSimpleChoiceQtiDtoAssemblerImpl implements AssessmentSimpleChoiceQtiDtoAssembler {

    @Autowired
    QtiSerializer qtiSerializer;

    @Override
    public AssessmentSimpleChoiceDto assembleDto(SimpleChoice simpleChoice) {
        // Drop out immediately if there is nothing to assemble.
        if (simpleChoice == null) {
            return null;
        }

        AssessmentSimpleChoiceDto simpleChoiceDto = new AssessmentSimpleChoiceDto();
        StringBuilder content = new StringBuilder();
        for (FlowStatic flowStatic : simpleChoice.getFlowStatics()) {
            content.append(qtiSerializer.serializeJqtiObject(flowStatic));
        }
        simpleChoiceDto.setContent(content.toString());
        if (simpleChoice.getId() != null) {
            simpleChoiceDto.setId(simpleChoice.getId().toString());
        }
        simpleChoiceDto.setIdentifier(simpleChoice.getIdentifier().toString());
        simpleChoiceDto.setUiid(UUID.randomUUID().toString());

        simpleChoiceDto.setAriaControls(simpleChoice.getAriaControls());
        simpleChoiceDto.setAriaDescribedBy(simpleChoice.getAriaDescribedBy());
        simpleChoiceDto.setAriaFlowsTo(simpleChoice.getAriaFlowsTo());
        simpleChoiceDto.setAriaLabel(simpleChoice.getAriaLabel());
        simpleChoiceDto.setAriaLabelledBy(simpleChoice.getAriaLabelledBy());
        simpleChoiceDto.setAriaLevel(simpleChoice.getAriaLevel());
        simpleChoiceDto.setAriaLive(simpleChoice.getAriaLive());
        simpleChoiceDto.setAriaOrientation(simpleChoice.getAriaOrientation());
        simpleChoiceDto.setAriaOwns(simpleChoice.getAriaOwns());

        return simpleChoiceDto;
    }

    @Override
    public SimpleChoice disassembleDto(AssessmentSimpleChoiceDto SimpleChoiceDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
