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
package org.cccnext.tesuto.admin.stub;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.cccnext.tesuto.admin.dto.StudentCollegeAffiliationDto;
import org.cccnext.tesuto.service.StudentCollegeAffiliationReader;
import org.springframework.stereotype.Service;

@Service
public class StudentCollegeAffiliationReaderStub implements StudentCollegeAffiliationReader {

	HashMap<String, HashMap<String, StudentCollegeAffiliationDto>> dtos = new HashMap<>();
	@Override
	public StudentCollegeAffiliationDto findByCccIdAndMisCode(String studentCccId, String misCode) {
		//Auto-generated method stub
		HashMap<String, StudentCollegeAffiliationDto> map = dtos.get(misCode);
		if(map == null) return null;
		return map.get(studentCccId);
	}

	@Override
	public List<StudentCollegeAffiliationDto> findByStudentCccId(String studentCccId) {
		return dtos.values().stream()
		.filter(v -> v.containsKey(studentCccId)).map(c -> c.get(studentCccId))
		.collect(Collectors.toList());
	}
	
	public void createIfNotExists(String eppn,String cccId,String misCode,String authSource) {
		
		StudentCollegeAffiliationDto dto = new StudentCollegeAffiliationDto();
		dto.setEppn(eppn);
		dto.setLoggedDate(new Date());
		dto.setMisCode(misCode);
		dto.setStudentCccId(cccId);
		if(dtos.containsKey(misCode)) {
			dtos.get(misCode).put(cccId, dto);
			
		} else {
			HashMap<String, StudentCollegeAffiliationDto> map = new HashMap<>();
			map.put(cccId, dto);
			dtos.put(misCode, map);
		}
	}

	@Override
	public List<StudentCollegeAffiliationDto> findTenMostRecent() {
		//Auto-generated method stub
		return null;
	}

}
