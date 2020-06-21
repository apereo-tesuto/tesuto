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

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.model.College;
import org.cccnext.tesuto.admin.model.TestLocation;
import org.cccnext.tesuto.admin.repository.CollegeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "testLocationDtoAssembler")
public class TestLocationDtoAssemblerImpl implements TestLocationDtoAssembler {

    @Autowired CollegeRepository collegeRepository;
    @Override
    public TestLocationDto assembleDto(TestLocation testLocation) {
        if (testLocation == null) {
            return null;
        }
        TestLocationDto testLocationDto = new TestLocationDto();
        testLocationDto.setCapacity(testLocation.getCapacity());
        testLocationDto.setCity(testLocation.getCity());
        testLocationDto.setCreatedOnDate(testLocation.getCreatedOnDate());
        testLocationDto.setId(testLocation.getId());
        testLocationDto.setLastUpdatedDate(testLocation.getLastUpdatedDate());
        testLocationDto.setLocationStatus(testLocation.getLocationStatus());
        testLocationDto.setLocationType(testLocation.getLocationType());
        testLocationDto.setName(testLocation.getName());
        testLocationDto.setPostalCode(testLocation.getPostalCode());
        testLocationDto.setStreetAddress1(testLocation.getStreetAddress1());
        testLocationDto.setStreetAddress2(testLocation.getStreetAddress2());
        testLocationDto.setCollegeId(testLocation.getCollegeId());
        testLocationDto.setCollegeName(testLocation.getCollege().getName());
        testLocationDto.setEnabled(testLocation.isEnabled());
        return testLocationDto;
    }


    @Override
    public TestLocation disassembleDto(TestLocationDto testLocationDto) {
        if (testLocationDto == null) {
            return null;
        }
        TestLocation testLocation = new TestLocation();
        testLocation.setCollegeId(testLocationDto.getCollegeId());
        if(StringUtils.isNotBlank(testLocationDto.getCollegeId())) {
            College college = collegeRepository.getOne(testLocationDto.getCollegeId());
            testLocation.setCollege(college);
        }
        testLocation.setCapacity(testLocationDto.getCapacity());
        testLocation.setCity(testLocationDto.getCity());
        testLocation.setCreatedOnDate(testLocationDto.getCreatedOnDate());
        testLocation.setId(testLocationDto.getId());
        testLocation.setLastUpdatedDate(testLocationDto.getLastUpdatedDate());
        testLocation.setLocationStatus(testLocationDto.getLocationStatus());
        testLocation.setLocationType(testLocationDto.getLocationType());
        testLocation.setName(testLocationDto.getName());
        testLocation.setPostalCode(testLocationDto.getPostalCode());
        testLocation.setStreetAddress1(testLocationDto.getStreetAddress1());
        testLocation.setStreetAddress2(testLocationDto.getStreetAddress2());
        testLocation.setEnabled(testLocationDto.isEnabled());
        return testLocation;
    }
}
