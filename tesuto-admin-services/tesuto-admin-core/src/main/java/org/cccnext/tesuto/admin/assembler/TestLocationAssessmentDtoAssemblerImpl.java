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
package org.cccnext.tesuto.admin.assembler;

import org.cccnext.tesuto.admin.dto.TestLocationAssessmentDto;
import org.cccnext.tesuto.admin.model.TestLocationAssessment;
import org.cccnext.tesuto.admin.repository.CollegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "testLocationAssessmentDtoAssembler")
public class TestLocationAssessmentDtoAssemblerImpl implements TestLocationAssessmentDtoAssembler {

    @Autowired CollegeRepository collegeRepository;
    @Override
    public TestLocationAssessmentDto assembleDto(TestLocationAssessment testLocationAssessment) {
        if (testLocationAssessment == null) {
            return null;
        }
        TestLocationAssessmentDto testLocationAssessmentDto = new TestLocationAssessmentDto();
        testLocationAssessmentDto.setAssessmentIdentifier(testLocationAssessment.getAssessmentIdentifier());
        testLocationAssessmentDto.setAssessmentNamespace(testLocationAssessment.getAssessmentNamespace());
        testLocationAssessmentDto.setTestLocationId(testLocationAssessment.getTestLocationId());
        
        return testLocationAssessmentDto;
    }


    @Override
    public TestLocationAssessment disassembleDto(TestLocationAssessmentDto testLocationAssessmentDto) {
        if (testLocationAssessmentDto == null) {
            return null;
        }
        TestLocationAssessment testLocationAssessment = new TestLocationAssessment();
        testLocationAssessment.setAssessmentIdentifier(testLocationAssessmentDto.getAssessmentIdentifier());
        testLocationAssessment.setAssessmentNamespace(testLocationAssessmentDto.getAssessmentNamespace());
        testLocationAssessment.setTestLocationId(testLocationAssessmentDto	.getTestLocationId());
        return testLocationAssessment;
    }
}
