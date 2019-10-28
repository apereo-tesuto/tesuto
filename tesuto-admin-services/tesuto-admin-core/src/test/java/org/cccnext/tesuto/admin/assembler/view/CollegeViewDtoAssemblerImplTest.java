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

import static org.junit.Assert.assertEquals;

import org.cccnext.tesuto.admin.assembler.view.CollegeViewDtoAssembler;
import org.cccnext.tesuto.admin.assembler.view.CollegeViewDtoAssemblerImpl;
import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.cccnext.tesuto.admin.viewdto.CollegeViewDto;
import org.cccnext.tesuto.util.TesutoUtils;
import org.junit.Test;

public class CollegeViewDtoAssemblerImplTest {

    public CollegeDto getCollegeDto(){
        CollegeDto collegeDto = new CollegeDto();
        collegeDto.setCccId(TesutoUtils.newId());
        collegeDto.setName(TesutoUtils.newId()); 
        return collegeDto;
    }
    
    @Test
    public void testAssembler() throws Exception{
        CollegeViewDtoAssembler collegeViewDtoAssembler = new CollegeViewDtoAssemblerImpl();

        CollegeDto collegeDto = getCollegeDto();

        CollegeViewDto collegeViewDto = collegeViewDtoAssembler.assembleViewDto(collegeDto);

        assertEquals("CccId is not equal", collegeDto.getCccId(), collegeViewDto.getCccId());
        assertEquals("Name is not equal", collegeDto.getName(), collegeViewDto.getName());
    }
}