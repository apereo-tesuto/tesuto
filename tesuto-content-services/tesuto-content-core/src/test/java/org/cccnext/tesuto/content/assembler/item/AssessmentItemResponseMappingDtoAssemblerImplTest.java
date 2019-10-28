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

import java.util.HashMap;
import java.util.Map;

import org.cccnext.tesuto.content.dto.item.AssessmentItemResponseMappingDto;
import org.cccnext.tesuto.content.model.item.AssessmentItemResponseMapping;
import org.junit.Test;

public class AssessmentItemResponseMappingDtoAssemblerImplTest {

    public static AssessmentItemResponseMapping getAssessmentItemResponseMapping() {
        AssessmentItemResponseMapping assessmentItemResponseMapping = new AssessmentItemResponseMapping();
        assessmentItemResponseMapping.setDefaultValue(0.0);
        ;
        assessmentItemResponseMapping.setLowerBound(0.0);
        assessmentItemResponseMapping.setUpperBound(3.0);
        Map<String, Double> map = new HashMap<String, Double>();
        map.put("AB", 3.0);
        assessmentItemResponseMapping.setMapping(map);
        return assessmentItemResponseMapping;
    }

    @Test
    public void testAssembleDisassemble() {
        AssessmentItemResponseMappingDtoAssembler assessmentItemResponseMappingDtoAssembler = new AssessmentItemResponseMappingDtoAssemblerImpl();
        AssessmentItemResponseMapping assessmentItemResponseMapping = getAssessmentItemResponseMapping();
        AssessmentItemResponseMappingDto assessmentItemResponseMappingDto = assessmentItemResponseMappingDtoAssembler
                .assembleDto(assessmentItemResponseMapping);
        AssessmentItemResponseMapping assessmentItemResponseMappingDisassembled = assessmentItemResponseMappingDtoAssembler
                .disassembleDto(assessmentItemResponseMappingDto);
        assertEquals("AssessmentCorrectResponse incorrectly assembled, dissassembled", assessmentItemResponseMapping,
                assessmentItemResponseMappingDisassembled);
    }

}
