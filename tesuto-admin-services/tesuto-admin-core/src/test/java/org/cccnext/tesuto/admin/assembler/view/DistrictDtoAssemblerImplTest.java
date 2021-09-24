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

import org.cccnext.tesuto.admin.assembler.CollegeDtoAssembler;
import org.cccnext.tesuto.admin.assembler.DistrictDtoAssembler;
import org.cccnext.tesuto.admin.assembler.DistrictDtoAssemblerImpl;
import org.cccnext.tesuto.admin.dto.DistrictDto;
import org.cccnext.tesuto.admin.model.College;
import org.cccnext.tesuto.admin.model.District;
import org.cccnext.tesuto.util.TesutoUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/userTestApplicationContext.xml"})
public class DistrictDtoAssemblerImplTest {

    @Autowired
    DistrictDtoAssembler districtDtoAssembler;

    District getDistrict() {
        District district = new District();
        district.setCccId(TesutoUtils.newId());
        district.setCity("Here");
        district.setName("Some District");
        district.setPostalCode("12345");
        district.setStreetAddress("123 Street");
        district.setUrl("http://some.url");
        district.setCreatedDate(new Date());
        district.setLastUpdatedDate(new Date());
        district.setCollegeSet(new HashSet<>());
        return district;
    }

    DistrictDto getDistrictDto() {
        DistrictDto districtDto = new DistrictDto();
        districtDto.setCccId(TesutoUtils.newId());
        districtDto.setCity("Here");
        districtDto.setName("Some District");
        districtDto.setPostalCode("12345");
        districtDto.setStreetAddress("123 Street");
        districtDto.setUrl("http://some.url");
        districtDto.setCreatedDate(new Date());
        districtDto.setLastUpdatedDate(new Date());
        districtDto.setColleges(new HashSet<>());
        return districtDto;
    }

    @Test
    public void testAssembler() throws Exception {
        District district = getDistrict();

        DistrictDto districtDto = districtDtoAssembler.assembleDto(district);

        assertEquals(district.getCccId(), districtDto.getCccId());
        assertEquals(district.getCity(), districtDto.getCity());
        assertEquals(district.getName(), districtDto.getName());
        assertEquals(district.getPostalCode(), districtDto.getPostalCode());
        assertEquals(district.getStreetAddress(), districtDto.getStreetAddress());
        assertEquals(district.getUrl(), districtDto.getUrl());
        assertEquals(district.getCreatedDate(), districtDto.getCreatedDate());
        assertEquals(district.getLastUpdatedDate(), districtDto.getLastUpdatedDate());
    }

    @Test
    public void testDisassembler() throws Exception {
        DistrictDto districtDto = getDistrictDto();

        District district = districtDtoAssembler.disassembleDto(districtDto);

        assertEquals(district.getCccId(), districtDto.getCccId());
        assertEquals(district.getCity(), districtDto.getCity());
        assertEquals(district.getName(), districtDto.getName());
        assertEquals(district.getPostalCode(), districtDto.getPostalCode());
        assertEquals(district.getStreetAddress(), districtDto.getStreetAddress());
        assertEquals(district.getUrl(), districtDto.getUrl());
        assertEquals(district.getCreatedDate(), districtDto.getCreatedDate());
        assertEquals(district.getLastUpdatedDate(), districtDto.getLastUpdatedDate());
    }
}
