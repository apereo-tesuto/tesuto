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
package org.cccnext.tesuto.importer.qti.assembler.assessment;

import uk.ac.ed.ph.jqtiplus.node.test.outcome.processing.ExitTest;
import uk.ac.ed.ph.jqtiplus.node.test.outcome.processing.LookupOutcomeValue;
import uk.ac.ed.ph.jqtiplus.node.test.outcome.processing.OutcomeCondition;
import uk.ac.ed.ph.jqtiplus.node.test.outcome.processing.OutcomeProcessing;
import uk.ac.ed.ph.jqtiplus.node.test.outcome.processing.OutcomeRule;
import uk.ac.ed.ph.jqtiplus.node.test.outcome.processing.ProcessOutcomeValue;

import org.cccnext.tesuto.content.dto.AssessmentOutcomeProcessingDto;


import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "outcomeProcessingQtiDtoAssembler")
public class AssessmentOutcomeProcessingQtiDtoAssemblerImpl implements AssessmentOutcomeProcessingQtiDtoAssembler {

    @Override
    public AssessmentOutcomeProcessingDto assembleDto(OutcomeProcessing outcomeProcessing) {
        // Drop out immediately if there is nothing to assemble.
        if (outcomeProcessing == null) {
            return null;
        }

        AssessmentOutcomeProcessingDto outcomeProcessingDto = new AssessmentOutcomeProcessingDto();
        // TODO: Verify, the assumption made here is the there are not duplicate
        // outcomeIf's for example.
        // So the last one wins and this might not be right?!
        for (OutcomeRule outcomeRule : outcomeProcessing.getOutcomeRules()) {
            if (outcomeRule instanceof ExitTest) {
                outcomeProcessingDto.setExitTest(((ExitTest) outcomeRule).toString());
            } else if (outcomeRule instanceof OutcomeCondition) {
                OutcomeCondition condition = (OutcomeCondition) outcomeRule;
                outcomeProcessingDto.setOutcomeIf(condition.getOutcomeIf().toString());
                outcomeProcessingDto.setOutcomeElse(
                        condition.getOutcomeElse() != null ? condition.getOutcomeElse().toString() : null);
                outcomeProcessingDto.setOutcomeElseIf(
                        condition.getOutcomeElseIfs() != null ? condition.getOutcomeElseIfs().toString() : null);
            } else if (outcomeRule instanceof LookupOutcomeValue) {
                // TODO: A bit of a punt here, verify this is what we really
                // need.
                outcomeProcessingDto.setLookupOutcomeValue(((LookupOutcomeValue) outcomeRule).toString());
            } else if (outcomeRule instanceof ProcessOutcomeValue) {
                // TODO: Again verify this handles the math the way we need.
                if (((ProcessOutcomeValue) outcomeRule).getExpression() != null) {
                    outcomeProcessingDto
                            .setSetOutcomeValue(((ProcessOutcomeValue) outcomeRule).getExpression().toString());
                }
            }

        }

        return outcomeProcessingDto;
    }

    @Override
    public OutcomeProcessing disassembleDto(AssessmentOutcomeProcessingDto outcomeProcessingDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
