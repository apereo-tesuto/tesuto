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

import org.cccnext.tesuto.content.dto.item.AssessmentResponseProcessingDto;
import org.cccnext.tesuto.content.model.item.AssessmentResponseProcessing;


import org.springframework.stereotype.Component;

/**
 * Also known as Response Declaration.
 *
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "responseProcessingDtoAssembler")
public class AssessmentResponseProcessingDtoAssemblerImpl implements AssessmentResponseProcessingDtoAssembler {

    @Override
    public AssessmentResponseProcessingDto assembleDto(AssessmentResponseProcessing responseProcessing) {
        // Drop out immediately if there is nothing to assemble.
        if (responseProcessing == null) {
            return null;
        }

        AssessmentResponseProcessingDto responseProcessingDto = new AssessmentResponseProcessingDto();
        responseProcessingDto.setExitReponse(responseProcessing.getExitReponse());
        responseProcessingDto.setInclude(responseProcessing.getInclude());
        responseProcessingDto.setLookupOutcomeValue(responseProcessing.getLookupOutcomeValue());
        responseProcessingDto.setResponseCondition(responseProcessing.getResponseCondition());
        responseProcessingDto.setResponseProcessFragment(responseProcessing.getResponseProcessFragment());
        responseProcessingDto.setSetValue(responseProcessing.getSetValue());
        responseProcessingDto.setTemplate(responseProcessing.getTemplate());
        responseProcessingDto.setTemplateLocation(responseProcessing.getTemplateLocation());

        return responseProcessingDto;
    }

    @Override
    public AssessmentResponseProcessing disassembleDto(AssessmentResponseProcessingDto responseProcessingDto) {
        // Drop out immediately if there is nothing to assemble.
        if (responseProcessingDto == null) {
            return null;
        }

        AssessmentResponseProcessing responseProcessing = new AssessmentResponseProcessing();
        responseProcessing.setExitReponse(responseProcessingDto.getExitReponse());
        responseProcessing.setInclude(responseProcessingDto.getInclude());
        responseProcessing.setLookupOutcomeValue(responseProcessingDto.getLookupOutcomeValue());
        responseProcessing.setResponseCondition(responseProcessingDto.getResponseCondition());
        responseProcessing.setResponseProcessFragment(responseProcessingDto.getResponseProcessFragment());
        responseProcessing.setSetValue(responseProcessingDto.getSetValue());
        responseProcessing.setTemplate(responseProcessingDto.getTemplate());
        responseProcessing.setTemplateLocation(responseProcessingDto.getTemplateLocation());

        return responseProcessing;
    }
}
