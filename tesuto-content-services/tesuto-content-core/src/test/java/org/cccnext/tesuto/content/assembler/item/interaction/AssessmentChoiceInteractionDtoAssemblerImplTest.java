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

import java.util.Arrays;

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentChoiceInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionType;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentChoiceInteraction;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentSimpleChoice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentChoiceInteractionDtoAssemblerImplTest {

    @Autowired
    AssessmentChoiceInteractionDtoAssembler assessmentChoiceInteractionAssembler;

    public static AssessmentChoiceInteraction getAssessmentChoiceInteraction() {
        AssessmentChoiceInteraction assessmentChoiceInteraction = new AssessmentChoiceInteraction();
        assessmentChoiceInteraction.setId("id");
        assessmentChoiceInteraction.setMaxChoices(10);
        assessmentChoiceInteraction.setPrompt("prompt");
        assessmentChoiceInteraction.setResponseIdentifier("responseId");
        AssessmentSimpleChoice simpleChoice = AssessmentSimpleChoiceDtoAssemberlmplTest.getAssessmentSimpleChoice();
        assessmentChoiceInteraction.setChoices(Arrays.asList(simpleChoice));
        assessmentChoiceInteraction.setType(AssessmentInteractionType.CHOICE_INTERACTION);
        assessmentChoiceInteraction.setUiid("uiid");
        return assessmentChoiceInteraction;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentChoiceInteraction assessmentChoiceInteraction = getAssessmentChoiceInteraction();
        AssessmentChoiceInteractionDto assessmentChoiceInteractionDto = assessmentChoiceInteractionAssembler
                .assembleDto(assessmentChoiceInteraction);
        AssessmentChoiceInteraction assessmentChoiceInteractionDisassembled = assessmentChoiceInteractionAssembler
                .disassembleDto(assessmentChoiceInteractionDto);
        assertEquals("AssessmentChoiceInteraction incorrectly assembled, dissassembled", assessmentChoiceInteraction,
                assessmentChoiceInteractionDisassembled);
    }

}
