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
package org.cccnext.tesuto.content.assembler.item;

import static org.junit.Assert.assertEquals;

import org.cccnext.tesuto.content.dto.item.AssessmentTemplateProcessingDto;
import org.cccnext.tesuto.content.model.item.AssessmentDefaultValue;
import org.cccnext.tesuto.content.model.item.AssessmentTemplateProcessing;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentTemplateProcessingDtoAssemblerImplTest {

    @Autowired
    AssessmentTemplateProcessingDtoAssembler assessmentTemplateProcessingAssembler;

    public static AssessmentTemplateProcessing getAssessmentTemplateProcessing() {
        AssessmentTemplateProcessing assessmentTemplateProcessing = new AssessmentTemplateProcessing();
        assessmentTemplateProcessing.setCorrectResponse("correctResponse");
        AssessmentDefaultValue defaultValue = AssessmentDefaultValueDtoAssemblerImplTest.getAssessmentDefaultValue();
        assessmentTemplateProcessing.setDefaultValue(defaultValue);
        assessmentTemplateProcessing.setExitTemplate("exitTemplate");
        assessmentTemplateProcessing.setSetDefaultValue("setDefaultValue");
        assessmentTemplateProcessing.setSetValue("setValue");
        assessmentTemplateProcessing.setTemplateCondition("templateCondition");
        assessmentTemplateProcessing.setTemplateConstraint("templateConstraint");
        return assessmentTemplateProcessing;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentTemplateProcessing assessmentTemplateProcessing = getAssessmentTemplateProcessing();
        AssessmentTemplateProcessingDto assessmentTemplateProcessingDto = assessmentTemplateProcessingAssembler
                .assembleDto(assessmentTemplateProcessing);
        AssessmentTemplateProcessing assessmentTemplateProcessingDisassembled = assessmentTemplateProcessingAssembler
                .disassembleDto(assessmentTemplateProcessingDto);
        assertEquals("AssessmentTemplateProcessing incorrectly assembled, dissassembled", assessmentTemplateProcessing,
                assessmentTemplateProcessingDisassembled);
    }

}
