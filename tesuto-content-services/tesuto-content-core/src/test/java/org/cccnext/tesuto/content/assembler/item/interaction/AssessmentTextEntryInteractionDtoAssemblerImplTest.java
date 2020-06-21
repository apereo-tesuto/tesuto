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

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionType;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentTextEntryInteractionDto;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentTextEntryInteraction;
import org.junit.Test;

public class AssessmentTextEntryInteractionDtoAssemblerImplTest {

    public static AssessmentTextEntryInteraction getAssessmentTextEntryInteraction() {
        AssessmentTextEntryInteraction assessmentTextEntryInteraction = new AssessmentTextEntryInteraction();
        assessmentTextEntryInteraction.setId("id");
        assessmentTextEntryInteraction.setPatternMask("patternMask");
        assessmentTextEntryInteraction.setPlaceholderText("placeholderText");
        assessmentTextEntryInteraction.setResponseIdentifier("responseId");
        assessmentTextEntryInteraction.setType(AssessmentInteractionType.TEXT_ENTRY_INTERACTION);
        assessmentTextEntryInteraction.setUiid("uiid");
        return assessmentTextEntryInteraction;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentTextEntryInteractionDtoAssembler assessmentTextEntryInteractionAssembler = new AssessmentTextEntryInteractionDtoAssemblerImpl();
        AssessmentTextEntryInteraction assessmentTextEntryInteraction = getAssessmentTextEntryInteraction();
        AssessmentTextEntryInteractionDto assessmentTextEntryInteractionDto = assessmentTextEntryInteractionAssembler
                .assembleDto(assessmentTextEntryInteraction);
        AssessmentTextEntryInteraction assessmentTextEntryInteractionDisassembled = assessmentTextEntryInteractionAssembler
                .disassembleDto(assessmentTextEntryInteractionDto);
        assertEquals("AssessmentTextEntryInteraction incorrectly assembled, dissassembled",
                assessmentTextEntryInteraction, assessmentTextEntryInteractionDisassembled);
    }

}
