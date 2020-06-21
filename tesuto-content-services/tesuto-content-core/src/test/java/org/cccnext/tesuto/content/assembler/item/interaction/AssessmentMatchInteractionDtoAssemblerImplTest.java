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

import javax.annotation.Resource;

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionType;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentMatchInteractionDto;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentMatchInteraction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentMatchInteractionDtoAssemblerImplTest {

    @Resource(name = "assessmentMatchInteractionDtoAssembler")
    AssessmentMatchInteractionDtoAssembler assessmentMatchInteractionDtoAssembler;

    public static AssessmentMatchInteraction getMatchInteraction() {
        AssessmentMatchInteraction assessmentMatchInteraction = new AssessmentMatchInteraction();
        assessmentMatchInteraction.setId("id");
        assessmentMatchInteraction.setMaxAssociations(10);
        assessmentMatchInteraction.setMinAssociations(3);
        assessmentMatchInteraction.setPrompt("prompt");
        assessmentMatchInteraction.setResponseIdentifier("responseId");
        assessmentMatchInteraction.setShuffle(true);
        assessmentMatchInteraction.setType(AssessmentInteractionType.MATCH_INTERACTION);
        assessmentMatchInteraction.setUiid("uiid");
        assessmentMatchInteraction
                .setMatchSets(AssessmentSimpleMatchSetDtoAssemblerImplTest.getAssessmentSimpleMatchSets());
        return assessmentMatchInteraction;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentMatchInteraction assessmentMatchInteraction = getMatchInteraction();
        AssessmentMatchInteractionDto assessmentMatchInteractionDto = assessmentMatchInteractionDtoAssembler
                .assembleDto(assessmentMatchInteraction);
        AssessmentMatchInteraction assessmentMatchInteractionDisassembled = assessmentMatchInteractionDtoAssembler
                .disassembleDto(assessmentMatchInteractionDto);
        assertEquals("AssessmentMatchInteraction incorrectly assembled, dissassembled", assessmentMatchInteraction,
                assessmentMatchInteractionDisassembled);
    }
}
