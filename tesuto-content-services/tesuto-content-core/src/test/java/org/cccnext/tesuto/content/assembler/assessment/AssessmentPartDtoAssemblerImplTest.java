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
package org.cccnext.tesuto.content.assembler.assessment;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.cccnext.tesuto.content.assembler.section.AssessmentBranchRuleDtoAssemblerImplTest;
import org.cccnext.tesuto.content.assembler.section.AssessmentItemSessionControlDtoAssemblerImplTest;
import org.cccnext.tesuto.content.assembler.section.AssessmentPreconditionDtoAssemblerImplTest;
import org.cccnext.tesuto.content.assembler.section.AssessmentSectionDtoAssemblerImplTest;
import org.cccnext.tesuto.content.dto.AssessmentPartDto;
import org.cccnext.tesuto.content.dto.AssessmentPartNavigationMode;
import org.cccnext.tesuto.content.dto.AssessmentPartSubmissionMode;
import org.cccnext.tesuto.content.model.AssessmentPart;
import org.cccnext.tesuto.content.model.expression.AssessmentBranchRule;
import org.cccnext.tesuto.content.model.section.AssessmentItemSessionControl;
import org.cccnext.tesuto.content.model.expression.AssessmentPreCondition;
import org.cccnext.tesuto.content.model.section.AssessmentSection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentPartDtoAssemblerImplTest {

    @Autowired
    AssessmentPartDtoAssembler assessmentPartAssembler;

    public static AssessmentPart getAssessmentPart() {
        AssessmentPart assessmentPart = new AssessmentPart();
        AssessmentSection assessmentSection = AssessmentSectionDtoAssemblerImplTest.getAssessmentSection();
        assessmentPart.setAssessmentSections(Arrays.asList(assessmentSection));
        AssessmentBranchRule assessmentBranchRule = AssessmentBranchRuleDtoAssemblerImplTest.getAssessmentBranchRule();
        assessmentPart.setBranchRules(Arrays.asList(assessmentBranchRule));
        assessmentPart.setDuration(10.0);
        assessmentPart.setId("id");
        AssessmentItemSessionControl itemSessionControl = AssessmentItemSessionControlDtoAssemblerImplTest
                .getAssessmentItemSessionControl();
        assessmentPart.setItemSessionControl(itemSessionControl);
        assessmentPart.setAssessmentPartNavigationMode(AssessmentPartNavigationMode.NONLINEAR);
        AssessmentPreCondition assessmentPreCondition = AssessmentPreconditionDtoAssemblerImplTest
                .getAssessmentPrecondition();
        assessmentPart.setPreConditions(Arrays.asList(assessmentPreCondition));
        assessmentPart.setAssessmentPartSubmissionMode(AssessmentPartSubmissionMode.INDIVIDUAL);
        return assessmentPart;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentPart assessmentPart = getAssessmentPart();
        AssessmentPartDto assessmentPartDto = assessmentPartAssembler.assembleDto(assessmentPart);
        AssessmentPart assessmentPartDisassembled = assessmentPartAssembler.disassembleDto(assessmentPartDto);
        assertEquals("AssessmentPart incorrectly assembled, dissassembled", assessmentPart, assessmentPartDisassembled);
    }

}
