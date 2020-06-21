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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleAssociableChoiceDto;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentSimpleAssociableChoice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentSimpleAssociableChoiceDtoAssemblerImplTest {

    @Resource(name = "assessmentSimpleAssociableChoiceDtoAssembler")
    AssessmentSimpleAssociableChoiceDtoAssembler assessmentSimpleAssociableChoiceDtoAssember;

    public static AssessmentSimpleAssociableChoice getAssessmentSimpleAssociableChoice() {
        AssessmentSimpleAssociableChoice assessmentSimpleAssociableChoice = new AssessmentSimpleAssociableChoice();
        assessmentSimpleAssociableChoice.setContent("content");
        assessmentSimpleAssociableChoice.setId("id");
        assessmentSimpleAssociableChoice.setIdentifier("identifier");
        assessmentSimpleAssociableChoice.setMatchMax(10);
        assessmentSimpleAssociableChoice.setMatchMin(3);
        assessmentSimpleAssociableChoice.setUiid("uiid");
        return assessmentSimpleAssociableChoice;
    }

    public static List<AssessmentSimpleAssociableChoice> getAssessmentSimpleAssociableChoices() {
        List<AssessmentSimpleAssociableChoice> list = new ArrayList<>(1);
        list.add(getAssessmentSimpleAssociableChoice());
        return list;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentSimpleAssociableChoice assessmentInteraction = getAssessmentSimpleAssociableChoice();
        AssessmentSimpleAssociableChoiceDto assessmentInteractionDto = assessmentSimpleAssociableChoiceDtoAssember
                .assembleDto(assessmentInteraction);
        AssessmentSimpleAssociableChoice assessmentInteractionDisassembled = assessmentSimpleAssociableChoiceDtoAssember
                .disassembleDto(assessmentInteractionDto);
        assertEquals("AssessmentSimpleAssociableChoice incorrectly assembled, dissassembled", assessmentInteraction,
                assessmentInteractionDisassembled);
    }
}
