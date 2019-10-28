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

import java.util.Arrays;

import org.cccnext.tesuto.content.dto.item.AssessmentDefaultValueDto;
import org.cccnext.tesuto.content.model.item.AssessmentDefaultValue;
import org.junit.Test;

public class AssessmentDefaultValueDtoAssemblerImplTest {

    public static AssessmentDefaultValue getAssessmentDefaultValue() {
        AssessmentDefaultValue assessmentDefaultValue = new AssessmentDefaultValue();
        assessmentDefaultValue.setDescription("description");
        assessmentDefaultValue.setValues(Arrays.asList("defaultValue"));
        return assessmentDefaultValue;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentDefaultValueDtoAssembler assessmentDefaultValueAssembler = new AssessmentDefaultValueDtoAssemblerImpl();
        AssessmentDefaultValue assessmentDefaultValue = getAssessmentDefaultValue();
        AssessmentDefaultValueDto assessmentDefaultValueDto = assessmentDefaultValueAssembler
                .assembleDto(assessmentDefaultValue);
        AssessmentDefaultValue assessmentDefaultValueDisassembled = assessmentDefaultValueAssembler
                .disassembleDto(assessmentDefaultValueDto);
        assertEquals("AssessmentDefaultValue incorrectly assembled, dissassembled", assessmentDefaultValue,
                assessmentDefaultValueDisassembled);
    }

}
