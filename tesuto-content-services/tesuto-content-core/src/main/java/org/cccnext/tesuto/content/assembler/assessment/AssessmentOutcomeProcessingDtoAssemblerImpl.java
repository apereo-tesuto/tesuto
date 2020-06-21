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
package org.cccnext.tesuto.content.assembler.assessment;

import org.cccnext.tesuto.content.dto.AssessmentOutcomeProcessingDto;
import org.cccnext.tesuto.content.model.AssessmentOutcomeProcessing;


import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "outcomeProcessingDtoAssembler")
public class AssessmentOutcomeProcessingDtoAssemblerImpl implements AssessmentOutcomeProcessingDtoAssembler {

    @Override
    public AssessmentOutcomeProcessingDto assembleDto(AssessmentOutcomeProcessing outcomeProcessing) {
        // Drop out immediately if there is nothing to assemble.
        if (outcomeProcessing == null) {
            return null;
        }

        AssessmentOutcomeProcessingDto outcomeProcessingDto = new AssessmentOutcomeProcessingDto();
        // TODO: Verify, the assumption made here is the there are not duplicate
        // outcomeIf's for example.
        // So the last one wins and this might not be right?!

        outcomeProcessingDto.setExitTest(outcomeProcessing.getExitTest());
        outcomeProcessingDto.setLookupOutcomeValue(outcomeProcessing.getLookupOutcomeValue());
        outcomeProcessingDto.setOutcomeElse(outcomeProcessing.getOutcomeElse());
        outcomeProcessingDto.setOutcomeElseIf(outcomeProcessing.getOutcomeElseIf());
        outcomeProcessingDto.setOutcomeIf(outcomeProcessing.getOutcomeIf());
        outcomeProcessingDto.setSetOutcomeValue(outcomeProcessing.getSetOutcomeValue());
        return outcomeProcessingDto;
    }

    @Override
    public AssessmentOutcomeProcessing disassembleDto(AssessmentOutcomeProcessingDto outcomeProcessingDto) {
        if (outcomeProcessingDto == null) {
            return null;
        }

        AssessmentOutcomeProcessing outcomeProcessing = new AssessmentOutcomeProcessing();
        // TODO: Verify, the assumption made here is the there are not duplicate
        // outcomeIf's for example.
        // So the last one wins and this might not be right?!

        outcomeProcessing.setExitTest(outcomeProcessingDto.getExitTest());
        outcomeProcessing.setLookupOutcomeValue(outcomeProcessingDto.getLookupOutcomeValue());
        outcomeProcessing.setOutcomeElse(outcomeProcessingDto.getOutcomeElse());
        outcomeProcessing.setOutcomeElseIf(outcomeProcessingDto.getOutcomeElseIf());
        outcomeProcessing.setOutcomeIf(outcomeProcessingDto.getOutcomeIf());
        outcomeProcessing.setSetOutcomeValue(outcomeProcessingDto.getSetOutcomeValue());
        return outcomeProcessing;
    }
}
