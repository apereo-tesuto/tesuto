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

import org.cccnext.tesuto.admin.assembler.view.TestLocationViewDtoAssembler;
import org.cccnext.tesuto.admin.assembler.view.TestLocationViewDtoAssemblerImpl;
import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.viewdto.TestLocationViewDto;
import org.cccnext.tesuto.util.TesutoUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by jasonbrown on 8/4/16.
 */
public class TestLocationViewDtoAssemblerImplTest {

    public TestLocationDto getTestLocationDto(){
        TestLocationDto testLocationDto = new TestLocationDto();
        testLocationDto.setCollegeId(TesutoUtils.newId());
        testLocationDto.setCollegeName(TesutoUtils.newId());
        testLocationDto.setId("1");
        testLocationDto.setName(TesutoUtils.newId());
        return testLocationDto;
    }

    @Test
    public void testAssembler() throws Exception{
        TestLocationViewDtoAssembler testLocationViewDtoAssembler = new TestLocationViewDtoAssemblerImpl();

        TestLocationDto testLocationDto = getTestLocationDto();

        TestLocationViewDto testLocationViewDto = testLocationViewDtoAssembler.assembleViewDto(testLocationDto);

        assertEquals("Id is not equal", testLocationDto.getId(), testLocationViewDto.getId());
        assertEquals("Name is not equal", testLocationDto.getName(), testLocationViewDto.getName());
        assertEquals("College Id is not equal", testLocationDto.getCollegeId(), testLocationViewDto.getCollegeId());
        assertEquals("College Name is not equal", testLocationDto.getCollegeName(), testLocationViewDto.getCollegeName());
    }
}
