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

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionType;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentInteraction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentInteractionDtoAssemblerImplTest {

    @Autowired
    AssessmentInteractionDtoAssembler assessmentInteractionAssembler;

    public static AssessmentInteraction getAssessmentInteraction() {
        AssessmentInteraction assessmentInteraction = new AssessmentInteraction();
        assessmentInteraction.setId("id");
        assessmentInteraction.setType(AssessmentInteractionType.NULL_INTERACTION);
        assessmentInteraction.setUiid("uiid");
        assessmentInteraction.setResponseIdentifier("responseIdentifier");
        assessmentInteraction.setAriaLabel("An aria label");
        return assessmentInteraction;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentInteraction assessmentInteraction = getAssessmentInteraction();
        AssessmentInteractionDto assessmentInteractionDto = assessmentInteractionAssembler
                .assembleDto(assessmentInteraction);
        AssessmentInteraction assessmentInteractionDisassembled = assessmentInteractionAssembler
                .disassembleDto(assessmentInteractionDto);
        assertEquals("AssessmentInteraction incorrectly assembled, dissassembled", assessmentInteraction,
                assessmentInteractionDisassembled);
    }
}
