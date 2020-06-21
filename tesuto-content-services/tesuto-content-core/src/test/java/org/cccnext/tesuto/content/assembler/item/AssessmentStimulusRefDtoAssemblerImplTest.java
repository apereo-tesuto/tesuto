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

import org.cccnext.tesuto.content.dto.item.AssessmentStimulusRefDto;
import org.cccnext.tesuto.content.model.item.AssessmentStimulusRef;
import org.junit.Test;

public class AssessmentStimulusRefDtoAssemblerImplTest {

    public static AssessmentStimulusRef getAssessmentStimulusRef() {
        AssessmentStimulusRef assessmentStimulusRef = new AssessmentStimulusRef();
        assessmentStimulusRef.setHref("href");
        assessmentStimulusRef.setId("id");
        return assessmentStimulusRef;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentStimulusRefDtoAssembler assessmentStimulusRefAssembler = new AssessmentStimulusRefDtoAssemblerImpl();
        AssessmentStimulusRef assessmentStimulusRef = getAssessmentStimulusRef();
        AssessmentStimulusRefDto assessmentStimulusRefDto = assessmentStimulusRefAssembler
                .assembleDto(assessmentStimulusRef);
        AssessmentStimulusRef assessmentStimulusRefDisassembled = assessmentStimulusRefAssembler
                .disassembleDto(assessmentStimulusRefDto);
        assertEquals("AssessmentStimulusRef incorrectly assembled, dissassembled", assessmentStimulusRef,
                assessmentStimulusRefDisassembled);
    }

}
