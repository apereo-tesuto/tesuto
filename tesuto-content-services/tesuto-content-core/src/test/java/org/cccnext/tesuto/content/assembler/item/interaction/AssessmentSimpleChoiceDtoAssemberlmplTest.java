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

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionType;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleChoiceDto;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentSimpleChoice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentSimpleChoiceDtoAssemberlmplTest {

    @Autowired
    AssessmentSimpleChoiceDtoAssembler assessmentSimpleChoiceAssembler;

    public static AssessmentSimpleChoice getAssessmentSimpleChoice() {
        AssessmentSimpleChoice assessmentSimpleChoice = new AssessmentSimpleChoice();
        assessmentSimpleChoice.setContent("content");
        assessmentSimpleChoice.setId("id");
        assessmentSimpleChoice.setIdentifier("identifier");
        assessmentSimpleChoice.setType(AssessmentInteractionType.CHOICE_INTERACTION);
        assessmentSimpleChoice.setUiid("uiid");
        return assessmentSimpleChoice;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentSimpleChoice assessmentSimpleChoice = getAssessmentSimpleChoice();
        AssessmentSimpleChoiceDto assessmentSimpleChoiceDto = assessmentSimpleChoiceAssembler
                .assembleDto(assessmentSimpleChoice);
        AssessmentSimpleChoice assessmentSimpleChoiceDisassembled = assessmentSimpleChoiceAssembler
                .disassembleDto(assessmentSimpleChoiceDto);
        assertEquals("AssessmentSimpleChoice incorrectly assembled, dissassembled", assessmentSimpleChoice,
                assessmentSimpleChoiceDisassembled);
    }

}
