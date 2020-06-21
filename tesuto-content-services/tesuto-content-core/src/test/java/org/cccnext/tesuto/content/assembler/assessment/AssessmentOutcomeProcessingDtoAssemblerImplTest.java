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

import static org.junit.Assert.assertEquals;

import org.cccnext.tesuto.content.dto.AssessmentOutcomeProcessingDto;
import org.cccnext.tesuto.content.model.AssessmentOutcomeProcessing;
import org.junit.Test;

public class AssessmentOutcomeProcessingDtoAssemblerImplTest {

    public static AssessmentOutcomeProcessing getAssessmentOutcomeProcessing() {
        AssessmentOutcomeProcessing assessmentOutcomeProcessing = new AssessmentOutcomeProcessing();
        assessmentOutcomeProcessing.setExitTest("exitTest");
        assessmentOutcomeProcessing.setLookupOutcomeValue("lookupOutcomeValue");
        assessmentOutcomeProcessing.setOutcomeElse("outcomeElse");
        assessmentOutcomeProcessing.setOutcomeElseIf("outcomeElseIf");
        assessmentOutcomeProcessing.setOutcomeIf("outcomeIf");
        assessmentOutcomeProcessing.setSetOutcomeValue("setOutcomeValue");

        return assessmentOutcomeProcessing;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentOutcomeProcessingDtoAssembler assessmentOutcomeProcessingAssembler = new AssessmentOutcomeProcessingDtoAssemblerImpl();
        AssessmentOutcomeProcessing assessmentOutcomeProcessing = getAssessmentOutcomeProcessing();
        AssessmentOutcomeProcessingDto assessmentOutcomeProcessingDto = assessmentOutcomeProcessingAssembler
                .assembleDto(assessmentOutcomeProcessing);
        AssessmentOutcomeProcessing assessmentOutcomeProcessingDisassembled = assessmentOutcomeProcessingAssembler
                .disassembleDto(assessmentOutcomeProcessingDto);
        assertEquals("AssessmentOutcomeProcessing incorrectly assembled, dissassembled", assessmentOutcomeProcessing,
                assessmentOutcomeProcessingDisassembled);
    }

}
