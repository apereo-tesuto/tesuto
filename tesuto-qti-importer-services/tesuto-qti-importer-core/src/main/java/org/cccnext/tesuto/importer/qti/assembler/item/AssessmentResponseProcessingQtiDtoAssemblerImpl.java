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

import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ExitResponse;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.LookupOutcomeValue;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseCondition;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessingFragment;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseRule;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.SetOutcomeValue;

import org.cccnext.tesuto.content.dto.item.AssessmentResponseProcessingDto;


import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Component(value = "responseProcessingQtiDtoAssembler")
public class AssessmentResponseProcessingQtiDtoAssemblerImpl implements AssessmentResponseProcessingQtiDtoAssembler {

    @Override
    public AssessmentResponseProcessingDto assembleDto(ResponseProcessing responseProcessing) {
        // Drop out immediately if there is nothing to assemble.
        if (responseProcessing == null) {
            return null;
        }

        AssessmentResponseProcessingDto responseProcessingDto = new AssessmentResponseProcessingDto();
        for (ResponseRule responseRule : responseProcessing.getResponseRules()) {
            log.debug("********************************************************************************");
            log.debug(responseRule.toString());
            log.debug("********************************************************************************");
        }

        // TODO: Verify there is only one rule for each simplification, if not
        // the last one wins and that may not be okay.
        for (ResponseRule responseRule : responseProcessing.getResponseRules()) {
            if (responseRule instanceof ExitResponse) {
                responseProcessingDto.setExitReponse(((ExitResponse) responseRule).toString());
            } else if (responseRule instanceof ResponseCondition) {
                // TODO: I'm not sure we can make this simplification. We may
                // need to model these flow conditions in
                // the same way as OutcomeProcessing
                ResponseCondition responseCondition = (ResponseCondition) responseRule;
                if (responseCondition.getResponseIf() != null) {
                    responseProcessingDto.setResponseCondition(responseCondition.getResponseIf().toString());
                } else if (responseCondition.getResponseElseIfs() != null) {
                    responseProcessingDto.setResponseCondition(responseCondition.getResponseElseIfs().toString());
                } else if (responseCondition.getResponseElse() != null) {
                    responseProcessingDto.setResponseCondition(responseCondition.getResponseElse().toString());
                }
            } else if (responseRule instanceof LookupOutcomeValue) {
                // TODO: Verify this is correct. More will be needed to add
                // meaing to this String representing an Expression.
                responseProcessingDto
                        .setLookupOutcomeValue(((LookupOutcomeValue) responseRule).getExpressions().toString());
            } else if (responseRule instanceof SetOutcomeValue) {
                // TODO: Getting tired of saying this, but we should verify this
                // is correct.
                responseProcessingDto.setSetValue(((SetOutcomeValue) responseRule).toString());
            } else if (responseRule instanceof ResponseProcessingFragment) {
                // TODO: May need to break this if-else into a method and make
                // it recursive for the fragment portion!
                responseProcessingDto
                        .setResponseProcessFragment(((ResponseProcessingFragment) responseRule).toString());
            }
        }

        responseProcessingDto.setInclude(null); // Not for pilot, punting
                                                // because I don't know where to
                                                // get this value.
        if (responseProcessing.getTemplate() != null) {
            responseProcessingDto.setTemplate(responseProcessing.getTemplate().toString());
        }
        if (responseProcessing.getTemplateLocation() != null) {
            responseProcessingDto.setTemplateLocation(responseProcessing.getTemplateLocation().toString());
        }

        return responseProcessingDto;
    }

    @Override
    public ResponseProcessing disassembleDto(AssessmentResponseProcessingDto responseProcessingDto) {
        // This is not currently used because we are not reexporting, so we may
        // never need it.
        throw new UnsupportedOperationException("If you're calling this method, rethink your work.");
    }
}
