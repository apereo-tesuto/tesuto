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

import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.cccnext.tesuto.admin.model.College;
import org.cccnext.tesuto.admin.model.District;
import org.cccnext.tesuto.admin.repository.DistrictRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by bruce on 5/24/16.
 */
@Component("collegeDtoAssembler")
public class CollegeDtoAssemblerImpl implements CollegeDtoAssembler {
    @Autowired DistrictRepository districtRepository;
    @Autowired TestLocationDtoAssembler testLocationDtoAssembler;
    @Autowired CollegeAttributeAssembler collegeAttributeAssembler;
    
    @Override
    public CollegeDto assembleDto(College college) {
        if (college == null) {
            return null;
        }
        CollegeDto dto = new CollegeDto();
        dto.setCccId(college.getCccId());
        if (college.getDistrict() != null) {
            dto.setDistrictCCCId(college.getDistrict().getCccId());
        }
        dto.setName(college.getName());
        dto.setStreetAddress1(college.getStreetAddress1());
        dto.setStreetAddress2(college.getStreetAddress2());
        dto.setCity(college.getCity());
        dto.setPostalCode(college.getPostalCode());
        dto.setUrl(college.getUrl());
        dto.setEppnSuffix(college.getEppnSuffix());
        dto.setCreatedDate(college.getCreatedDate());
        dto.setLastUpdatedDate(college.getLastUpdatedDate());
        dto.setTestLocations(testLocationDtoAssembler.assembleDto(college.getTestLocations()));
        dto.setCollegeAttributeDto(collegeAttributeAssembler.assembleDto(college.getCollegeAttribute()));
        return dto;
    }

    @Override
    public College disassembleDto(CollegeDto dto) {
        if (dto == null) {
            return null;
        }
        College college = new College();
        college.setCccId(dto.getCccId());
        if (college.getDistrict() != null) {
            District district = districtRepository.getOne(dto.getDistrictCCCId());
            college.setDistrict(district);
        }
        college.setName(dto.getName());
        college.setStreetAddress1(dto.getStreetAddress1());
        college.setStreetAddress2(dto.getStreetAddress2());
        college.setCity(dto.getCity());
        college.setPostalCode(dto.getPostalCode());
        college.setUrl(dto.getUrl());
        college.setEppnSuffix(dto.getEppnSuffix());
        college.setCreatedDate(dto.getCreatedDate());
        college.setLastUpdatedDate(dto.getLastUpdatedDate());
        college.setTestLocations(testLocationDtoAssembler.disassembleDto(dto.getTestLocations()));
        college.setCollegeAttribute(collegeAttributeAssembler.disassembleDto(dto.getCollegeAttributeDto()));
        return college;
    }
}
