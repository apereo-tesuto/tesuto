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
package org.cccnext.tesuto.content.assembler.shared;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.cccnext.tesuto.content.assembler.item.AssessmentDefaultValueDtoAssemblerImplTest;
import org.cccnext.tesuto.content.dto.AssessmentBaseType;
import org.cccnext.tesuto.content.dto.item.AssessmentCardinality;
import org.cccnext.tesuto.content.dto.shared.AssessmentOutcomeDeclarationDto;
import org.cccnext.tesuto.content.model.item.AssessmentDefaultValue;
import org.cccnext.tesuto.content.model.shared.AssessmentOutcomeDeclaration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentOutcomeDeclarationDtoAssemberImplTest {

    @Autowired
    AssessmentOutcomeDeclarationDtoAssembler assessmentOutcomeDeclarationAssembler;

    public static AssessmentOutcomeDeclaration getAssessmentOutcomeDeclaration() {
        AssessmentOutcomeDeclaration assessmentOutcomeDeclaration = new AssessmentOutcomeDeclaration();
        assessmentOutcomeDeclaration.setBaseType(AssessmentBaseType.PAIR);
        assessmentOutcomeDeclaration.setCardinality(AssessmentCardinality.RECORD);

        AssessmentDefaultValue defaultValue = AssessmentDefaultValueDtoAssemblerImplTest.getAssessmentDefaultValue();
        assessmentOutcomeDeclaration.setDefaultValue(defaultValue);
        assessmentOutcomeDeclaration.setInterpretation("interpretation");
        assessmentOutcomeDeclaration.setExternalScored("externalScored");
        assessmentOutcomeDeclaration.setIdentifier("id");
        assessmentOutcomeDeclaration.setLongInterpretation("data:someURI");
        assessmentOutcomeDeclaration.setLookupTable("lookupTable");
        assessmentOutcomeDeclaration.setMasteryValue(100.0);
        assessmentOutcomeDeclaration.setNormalMaximum(100.0);
        assessmentOutcomeDeclaration.setNormalMinimum(10.0);
        assessmentOutcomeDeclaration.setVariableIdentifierRef("variableIdentifierRef");
        assessmentOutcomeDeclaration.setViews(Arrays.asList("views"));
        return assessmentOutcomeDeclaration;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentOutcomeDeclaration assessmentOutcomeDeclaration = getAssessmentOutcomeDeclaration();
        AssessmentOutcomeDeclarationDto assessmentOutcomeDeclarationDto = assessmentOutcomeDeclarationAssembler
                .assembleDto(assessmentOutcomeDeclaration);
        AssessmentOutcomeDeclaration assessmentOutcomeDeclarationDisassembled = assessmentOutcomeDeclarationAssembler
                .disassembleDto(assessmentOutcomeDeclarationDto);
        assertEquals("AssessmentOutcomeDeclaration incorrectly assembled, dissassembled", assessmentOutcomeDeclaration,
                assessmentOutcomeDeclarationDisassembled);
    }

}
