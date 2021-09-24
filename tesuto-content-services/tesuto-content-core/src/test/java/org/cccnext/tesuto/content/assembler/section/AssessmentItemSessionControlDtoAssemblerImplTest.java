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
package org.cccnext.tesuto.content.assembler.section;

import static org.junit.Assert.assertEquals;

import org.cccnext.tesuto.content.dto.section.AssessmentItemSessionControlDto;
import org.cccnext.tesuto.content.model.section.AssessmentItemSessionControl;
import org.junit.Test;

public class AssessmentItemSessionControlDtoAssemblerImplTest {

    public static AssessmentItemSessionControl getAssessmentItemSessionControl() {
        AssessmentItemSessionControl assessmentItemSessionControl = new AssessmentItemSessionControl();
        assessmentItemSessionControl.setAllowComment(true);
        assessmentItemSessionControl.setAllowReview(false);
        assessmentItemSessionControl.setAllowSkipping(true);
        assessmentItemSessionControl.setMaxAttempts(100);
        assessmentItemSessionControl.setShowFeedback(false);
        assessmentItemSessionControl.setShowSolution(true);
        assessmentItemSessionControl.setValidateResponses(true);
        return assessmentItemSessionControl;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentItemSessionControlDtoAssembler assessmentItemSessionControlAssembler = new AssessmentItemSessionControlDtoAssemblerImpl();
        AssessmentItemSessionControl assessmentItemSessionControl = getAssessmentItemSessionControl();
        AssessmentItemSessionControlDto assessmentItemSessionControlDto = assessmentItemSessionControlAssembler
                .assembleDto(assessmentItemSessionControl);
        AssessmentItemSessionControl assessmentItemSessionControlDisassembled = assessmentItemSessionControlAssembler
                .disassembleDto(assessmentItemSessionControlDto);
        assertEquals("AssessmentItemSessionControl incorrectly assembled, dissassembled", assessmentItemSessionControl,
                assessmentItemSessionControlDisassembled);
    }

}
