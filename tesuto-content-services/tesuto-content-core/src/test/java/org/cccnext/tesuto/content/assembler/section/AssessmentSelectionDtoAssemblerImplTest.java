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

import org.cccnext.tesuto.content.dto.section.AssessmentSelectionDto;
import org.cccnext.tesuto.content.model.section.AssessmentSelection;
import org.junit.Test;

public class AssessmentSelectionDtoAssemblerImplTest {

    public static AssessmentSelection getAssessmentSelection() {
        AssessmentSelection assessmentSelection = new AssessmentSelection();

        assessmentSelection.setExtensions("extensions");
        assessmentSelection.setSelect(3);
        assessmentSelection.setWithReplacement(true);
        return assessmentSelection;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentSelectionDtoAssembler assessmentSelectionAssembler = new AssessmentSelectionDtoAssemblerImpl();
        AssessmentSelection assessmentSelection = getAssessmentSelection();
        AssessmentSelectionDto assessmentSelectionDto = assessmentSelectionAssembler.assembleDto(assessmentSelection);
        AssessmentSelection assessmentSelectionDisassembled = assessmentSelectionAssembler
                .disassembleDto(assessmentSelectionDto);
        assertEquals("AssessmentSelection incorrectly assembled, dissassembled", assessmentSelection,
                assessmentSelectionDisassembled);
    }

}
