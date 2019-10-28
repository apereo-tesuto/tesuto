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

import static org.junit.Assert.assertEquals;

import org.cccnext.tesuto.content.dto.item.AssessmentResponseProcessingDto;
import org.cccnext.tesuto.content.model.item.AssessmentResponseProcessing;
import org.junit.Test;

public class AssessmentResponseProcessingDtoAssemblerImplTest {

    public static AssessmentResponseProcessing getAssessmentResponseProcessing() {
        AssessmentResponseProcessing assessmentResponseProcessing = new AssessmentResponseProcessing();
        assessmentResponseProcessing.setExitReponse("exitReponse");
        assessmentResponseProcessing.setInclude("include");
        assessmentResponseProcessing.setLookupOutcomeValue("lookupOutcomeValue");
        assessmentResponseProcessing.setResponseCondition("responseCondition");
        assessmentResponseProcessing.setSetValue("setValue");
        assessmentResponseProcessing.setTemplate("template");
        assessmentResponseProcessing.setTemplateLocation("templateLocation");
        return assessmentResponseProcessing;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentResponseProcessingDtoAssembler assessmentResponseProcessingAssembler = new AssessmentResponseProcessingDtoAssemblerImpl();
        AssessmentResponseProcessing assessmentResponseProcessing = getAssessmentResponseProcessing();
        AssessmentResponseProcessingDto assessmentResponseProcessingDto = assessmentResponseProcessingAssembler
                .assembleDto(assessmentResponseProcessing);
        AssessmentResponseProcessing assessmentResponseProcessingDisassembled = assessmentResponseProcessingAssembler
                .disassembleDto(assessmentResponseProcessingDto);
        assertEquals("AssessmentResponseProcessing incorrectly assembled, dissassembled", assessmentResponseProcessing,
                assessmentResponseProcessingDisassembled);
    }

}
