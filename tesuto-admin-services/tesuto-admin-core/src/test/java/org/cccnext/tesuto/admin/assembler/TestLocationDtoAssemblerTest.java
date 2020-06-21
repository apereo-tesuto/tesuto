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

import static org.junit.Assert.assertEquals;

import java.util.Date;

import javax.annotation.Resource;

import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.model.TestLocation;
import org.cccnext.tesuto.admin.service.CollegeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/userTestApplicationContext.xml" })
@Transactional
public class TestLocationDtoAssemblerTest {
    @Resource (name = "testLocationDtoAssembler")
    TestLocationDtoAssembler testLocationDtoAssembler;
    
    @Autowired CollegeService collegeService;
    @Autowired CollegeDtoAssembler  collegeAssembler;

    @Test
    public void testAssembleDisassemble() {
        TestLocation testLocation = getTestLocation();
        CollegeDto collegeDto = collegeService.read("ZZ1");
        testLocation.setCollege(collegeAssembler.disassembleDto(collegeDto));
        testLocation.setCollegeId(collegeDto.getCccId());
        TestLocationDto testLocationDto = testLocationDtoAssembler.assembleDto(testLocation);
        TestLocation testLocationAssembled = testLocationDtoAssembler.disassembleDto(testLocationDto);
        assertEquals("Test Location Assembler is not recreating Testlocation", testLocation, testLocationAssembled);
    }

    public static TestLocation getTestLocation() {
        TestLocation testLocation = new TestLocation();
        testLocation.setCapacity(100);
        testLocation.setCity("city");
        testLocation.setCreatedOnDate(new Date());
        testLocation.setId("1");
        testLocation.setLastUpdatedDate(new Date());
        testLocation.setLocationStatus("ACTIVE");
        testLocation.setLocationType("ON_SITE");
        testLocation.setName("name");
        testLocation.setPostalCode("postalCode");
        testLocation.setStreetAddress1("streetAddress1");
        testLocation.setStreetAddress1("setStreetAddress2");
       
        testLocation.setCollegeId("ZZ1");
        return testLocation;
    }
}
