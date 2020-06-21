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

import uk.ac.ed.ph.jqtiplus.node.content.variable.TextOrVariable;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.InlineChoice;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;

import java.util.UUID;

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInlineChoiceDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "assessmentInlineChoiceQtiDtoAssembler")
public class AssessmentInlineChoiceQtiDtoAssemblerImpl implements AssessmentInlineChoiceQtiDtoAssembler {

    @Autowired
    QtiSerializer qtiSerializer;

    @Override
    public AssessmentInlineChoiceDto assembleDto(InlineChoice inlineChoice) {
        // Drop out immediately if there is nothing to assemble.
        if (inlineChoice == null) {
            return null;
        }

        AssessmentInlineChoiceDto assessmentInlineChoiceDto = new AssessmentInlineChoiceDto();
        StringBuilder content = new StringBuilder();
        for (TextOrVariable textOrVariable : inlineChoice.getTextOrVariables()) {
            content.append(qtiSerializer.serializeJqtiObject(textOrVariable));
        }
        assessmentInlineChoiceDto.setContent(content.toString());
        if (inlineChoice.getId() != null) {
            assessmentInlineChoiceDto.setId(inlineChoice.getId().toString());
        }
        assessmentInlineChoiceDto.setIdentifier(inlineChoice.getIdentifier().toString());
        assessmentInlineChoiceDto.setUiid(UUID.randomUUID().toString());

        return assessmentInlineChoiceDto;
    }

    @Override
    public InlineChoice disassembleDto(AssessmentInlineChoiceDto inlineChoiceDto) {
        return null;
    }
}
