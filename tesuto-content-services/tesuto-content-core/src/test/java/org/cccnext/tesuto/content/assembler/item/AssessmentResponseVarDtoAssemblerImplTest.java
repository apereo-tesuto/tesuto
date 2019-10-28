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
import org.cccnext.tesuto.content.dto.item.AssessmentResponseVarDto;
import org.cccnext.tesuto.content.model.item.AssessmentCorrectResponse;
import org.cccnext.tesuto.content.model.item.AssessmentDefaultValue;
import org.cccnext.tesuto.content.model.item.AssessmentItemResponseMapping;
import org.cccnext.tesuto.content.model.item.AssessmentResponseVar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/testApplicationContext.xml" })
public class AssessmentResponseVarDtoAssemblerImplTest {

    @Autowired
    AssessmentResponseVarDtoAssembler assessmentResponseVarAssembler;

    public static AssessmentResponseVar getAssessmentResponseVar() {
        AssessmentResponseVar assessmentResponseVar = new AssessmentResponseVar();
        assessmentResponseVar.setBaseType(AssessmentBaseType.DIRECTED_PAIR);
        assessmentResponseVar.setCardinality(AssessmentCardinality.ORDERED);
        AssessmentCorrectResponse correctResponse = AssessmentCorrectResponseDtoAssemblerImplTest
                .getAssessmentCorrectResponse();
        assessmentResponseVar.setCorrectResponse(correctResponse);
        AssessmentDefaultValue defaultValue = AssessmentDefaultValueDtoAssemblerImplTest.getAssessmentDefaultValue();
        assessmentResponseVar.setDefaultValue(defaultValue);
        AssessmentItemResponseMapping mapping = AssessmentItemResponseMappingDtoAssemblerImplTest
                .getAssessmentItemResponseMapping();
        assessmentResponseVar.setMapping(mapping);
        assessmentResponseVar.setIdentifier("id");
        return assessmentResponseVar;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentResponseVar assessmentResponseVar = getAssessmentResponseVar();
        AssessmentResponseVarDto assessmentResponseVarDto = assessmentResponseVarAssembler
                .assembleDto(assessmentResponseVar);
        AssessmentResponseVar assessmentResponseVarDisassembled = assessmentResponseVarAssembler
                .disassembleDto(assessmentResponseVarDto);
        assertEquals("AssessmentResponseVar incorrectly assembled, dissassembled", assessmentResponseVar,
                assessmentResponseVarDisassembled);
    }

}
