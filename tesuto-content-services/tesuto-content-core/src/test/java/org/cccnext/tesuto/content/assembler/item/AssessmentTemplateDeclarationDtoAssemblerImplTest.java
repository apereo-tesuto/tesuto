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

import org.cccnext.tesuto.content.dto.AssessmentBaseType;
import org.cccnext.tesuto.content.dto.item.AssessmentCardinality;
import org.cccnext.tesuto.content.dto.item.AssessmentTemplateDeclarationDto;
import org.cccnext.tesuto.content.model.item.AssessmentDefaultValue;
import org.cccnext.tesuto.content.model.item.AssessmentTemplateDeclaration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentTemplateDeclarationDtoAssemblerImplTest {

    @Autowired
    AssessmentTemplateDeclarationDtoAssembler assessmentTemplateDeclarationAssembler;

    public static AssessmentTemplateDeclaration getAssessmentTemplateDeclaration() {
        AssessmentTemplateDeclaration assessmentTemplateDeclaration = new AssessmentTemplateDeclaration();

        assessmentTemplateDeclaration.setBaseType(AssessmentBaseType.FILE);
        assessmentTemplateDeclaration.setCardinality(AssessmentCardinality.SINGLE);
        AssessmentDefaultValue defaultValue = AssessmentDefaultValueDtoAssemblerImplTest.getAssessmentDefaultValue();
        assessmentTemplateDeclaration.setDefaultValue(defaultValue);
        assessmentTemplateDeclaration.setId("id");
        assessmentTemplateDeclaration.setIsMathVar(true);
        assessmentTemplateDeclaration.setParamVariable(false);
        return assessmentTemplateDeclaration;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentTemplateDeclaration assessmentTemplateDeclaration = getAssessmentTemplateDeclaration();
        AssessmentTemplateDeclarationDto assessmentTemplateDeclarationDto = assessmentTemplateDeclarationAssembler
                .assembleDto(assessmentTemplateDeclaration);
        AssessmentTemplateDeclaration assessmentTemplateDeclarationDisassembled = assessmentTemplateDeclarationAssembler
                .disassembleDto(assessmentTemplateDeclarationDto);
        assertEquals("AssessmentTemplateDeclaration incorrectly assembled, dissassembled",
                assessmentTemplateDeclaration, assessmentTemplateDeclarationDisassembled);
    }

}
