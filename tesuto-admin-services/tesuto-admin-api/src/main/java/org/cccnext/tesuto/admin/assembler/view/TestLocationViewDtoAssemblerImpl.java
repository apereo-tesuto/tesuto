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
package org.cccnext.tesuto.admin.assembler.view;

import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.viewdto.TestLocationViewDto;
import org.springframework.stereotype.Component;

@Component(value = "testLocationViewDtoAssemblerImpl")
public class TestLocationViewDtoAssemblerImpl implements TestLocationViewDtoAssembler {

    public TestLocationViewDto assembleViewDto(TestLocationDto testLocationDto) {

        if (testLocationDto == null) {
            return null;
        }
        TestLocationViewDto testLocationViewDto = new TestLocationViewDto();
        testLocationViewDto.setId(testLocationDto.getId());
        testLocationViewDto.setName(testLocationDto.getName());
        testLocationViewDto.setCollegeId(testLocationDto.getCollegeId());
        testLocationViewDto.setCollegeName(testLocationDto.getCollegeName());
        return testLocationViewDto;
    }

    public TestLocationDto disassembleViewDto(TestLocationViewDto dto) {
        throw new UnsupportedOperationException("If you're calling this method, you may need to complete this method.");
    }

}
