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
import java.util.List;

import javax.annotation.Resource;

import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionType;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentSimpleMatchSetDto;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentSimpleMatchSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentSimpleMatchSetDtoAssemblerImplTest {

    @Resource(name = "assessmentSimpleMatchSetDtoAssembler")
    AssessmentSimpleMatchSetDtoAssembler AssessmentSimpleMatchSetDtoAssembler;

    public static AssessmentSimpleMatchSet getAssessmentSimpleMatchSet() {
        AssessmentSimpleMatchSet assessmentSimpleMatchSet = new AssessmentSimpleMatchSet();
        assessmentSimpleMatchSet.setId("id");
        assessmentSimpleMatchSet.setType(AssessmentInteractionType.MATCH_INTERACTION);
        assessmentSimpleMatchSet.setUiid("uiid");
        assessmentSimpleMatchSet.setMatchSet(
                AssessmentSimpleAssociableChoiceDtoAssemblerImplTest.getAssessmentSimpleAssociableChoices());
        return assessmentSimpleMatchSet;
    }

    public static List<AssessmentSimpleMatchSet> getAssessmentSimpleMatchSets() {
        return Arrays.asList(getAssessmentSimpleMatchSet());
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentSimpleMatchSet assessmentSimpleMatchSet = getAssessmentSimpleMatchSet();
        AssessmentSimpleMatchSetDto assessmentSimpleMatchSetDto = AssessmentSimpleMatchSetDtoAssembler
                .assembleDto(assessmentSimpleMatchSet);
        AssessmentSimpleMatchSet assessmentSimpleMatchSetDisassembled = AssessmentSimpleMatchSetDtoAssembler
                .disassembleDto(assessmentSimpleMatchSetDto);
        assertEquals("AssessmentSimpleMatchSet incorrectly assembled, dissassembled", assessmentSimpleMatchSet,
                assessmentSimpleMatchSetDisassembled);
    }
}
