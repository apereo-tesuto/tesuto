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

import org.cccnext.tesuto.admin.dto.DistrictDto;
import org.cccnext.tesuto.admin.model.District;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@Component("districtDtoAssembler")
public class DistrictDtoAssemblerImpl implements DistrictDtoAssembler {

	@Autowired
	CollegeDtoAssembler collegeDtoAssembler;

	@Override
	public DistrictDto assembleDto(District district) {
		if (district == null) {
			return null;
		}
		DistrictDto districtDto = new DistrictDto();
		districtDto.setCccId(district.getCccId());
		districtDto.setName(district.getName());
		districtDto.setStreetAddress(district.getStreetAddress());
		districtDto.setCity(district.getCity());
		districtDto.setPostalCode(district.getPostalCode());
		districtDto.setUrl(district.getUrl());
		districtDto.setCreatedDate(district.getCreatedDate());
		districtDto.setLastUpdatedDate(district.getLastUpdatedDate());
		districtDto.setColleges(collegeDtoAssembler.assembleDto(district.getCollegeSet()));
		return districtDto;
	}

	@Override
	public District disassembleDto(DistrictDto districtDto) {
		if (districtDto == null) {
			return null;
		}
		District district = new District();
		district.setCccId(districtDto.getCccId());
		district.setName(districtDto.getName());
		district.setStreetAddress(districtDto.getStreetAddress());
		district.setCity(districtDto.getCity());
		district.setPostalCode(districtDto.getPostalCode());
		district.setUrl(districtDto.getUrl());
		district.setCreatedDate(districtDto.getCreatedDate());
		district.setLastUpdatedDate(districtDto.getLastUpdatedDate());
		district.setCollegeSet(collegeDtoAssembler.disassembleDto(districtDto.getColleges()));
		return district;
	}
}
