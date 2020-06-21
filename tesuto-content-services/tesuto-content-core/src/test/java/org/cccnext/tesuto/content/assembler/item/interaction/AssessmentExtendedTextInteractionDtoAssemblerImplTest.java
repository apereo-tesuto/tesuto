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
package org.cccnext.tesuto.content.assembler.item.interaction;

import static org.junit.Assert.assertEquals;

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentExtendedTextInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionType;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentExtendedTextInteraction;
import org.junit.Test;

public class AssessmentExtendedTextInteractionDtoAssemblerImplTest {

    public static AssessmentExtendedTextInteraction getAssessmentExtendedTextInteraction() {
        AssessmentExtendedTextInteraction assessmentExtendedTextInteraction = new AssessmentExtendedTextInteraction();
        assessmentExtendedTextInteraction.setExpectedLength(100);
        assessmentExtendedTextInteraction.setExpectedLines(10);
        assessmentExtendedTextInteraction.setId("id");
        assessmentExtendedTextInteraction.setMaxStrings(10);
        assessmentExtendedTextInteraction.setMinStrings(1);
        assessmentExtendedTextInteraction.setResponseIdentifier("responseId");
        assessmentExtendedTextInteraction.setType(AssessmentInteractionType.EXTENDED_TEXT_INTERACTION);
        assessmentExtendedTextInteraction.setUiid("uiid");
        return assessmentExtendedTextInteraction;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentExtendedTextInteractionDtoAssembler assessmentExtendedTextInteractionAssembler = new AssessmentExtendedTextInteractionDtoAssemblerImpl();
        AssessmentExtendedTextInteraction assessmentExtendedTextInteraction = getAssessmentExtendedTextInteraction();
        AssessmentExtendedTextInteractionDto assessmentExtendedTextInteractionDto = assessmentExtendedTextInteractionAssembler
                .assembleDto(assessmentExtendedTextInteraction);
        AssessmentExtendedTextInteraction assessmentExtendedTextInteractionDisassembled = assessmentExtendedTextInteractionAssembler
                .disassembleDto(assessmentExtendedTextInteractionDto);
        assertEquals("AssessmentExtendedTextInteraction incorrectly assembled, dissassembled",
                assessmentExtendedTextInteraction, assessmentExtendedTextInteractionDisassembled);
    }

}
