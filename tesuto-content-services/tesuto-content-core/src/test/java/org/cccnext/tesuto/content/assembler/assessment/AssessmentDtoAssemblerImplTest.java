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

import org.cccnext.tesuto.content.assembler.assessment.metadata.AssessmentMetadataDtoAssemblerImplTest;
import org.cccnext.tesuto.content.assembler.shared.AssessmentOutcomeDeclarationDtoAssemberImplTest;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.model.Assessment;
import org.cccnext.tesuto.content.model.AssessmentPart;
import org.cccnext.tesuto.content.model.shared.AssessmentOutcomeDeclaration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentDtoAssemblerImplTest {

    @Autowired
    AssessmentDtoAssembler assessmentAssembler;

    public static Assessment getAssessment() {
        Assessment assessment = new Assessment();
        AssessmentPart assessmentPart = AssessmentPartDtoAssemblerImplTest.getAssessmentPart();
        assessment.setAssessmentParts(Arrays.asList(assessmentPart));
        assessment.setId("id");
        assessment.setLanguage("language");
        assessment
                .setOutcomeProcessing(AssessmentOutcomeProcessingDtoAssemblerImplTest.getAssessmentOutcomeProcessing());
        AssessmentOutcomeDeclaration outcomeDeclaration = AssessmentOutcomeDeclarationDtoAssemberImplTest.getAssessmentOutcomeDeclaration();
        assessment.setOutcomeDeclarations(Arrays.asList(outcomeDeclaration));
        assessment.setTitle("title");
        assessment.setToolName("toolName");
        assessment.setToolVersion("toolVersion");
        assessment.setNamespace("namespace");
        assessment.setVersion(0);
        assessment.setIdentifier("identifier");
        assessment.setDuration(100.0);
        assessment.setAssessmentMetadata(AssessmentMetadataDtoAssemblerImplTest.getMetadata());
        return assessment;
    }

    @Test
    public void testAssembleDisassemble() {

        Assessment assessment = getAssessment();
        AssessmentDto assessmentDto = assessmentAssembler.assembleDto(assessment);
        Assessment assessmentDisassembled = assessmentAssembler.disassembleDto(assessmentDto);
        assertEquals("Assessment incorrectly assembled, dissassembled", assessment, assessmentDisassembled);
    }

}
