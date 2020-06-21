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
package org.cccnext.tesuto.admin.controller;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.form.StudentSearchForm;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.user.service.StudentService;
import org.cccnext.tesuto.user.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StudentSearchControllerV2 {

	@Autowired
	StudentService service;

	private static final String FIND_ANY_STUDENT_PERMISSION = "FIND_ANY_STUDENT";

	public ResponseEntity<List<StudentViewDto>> findBySearchForm(
			UserAccountDto userAccountDto, StudentSearchForm studentSearchForm)
			throws URISyntaxException {
		boolean findAnyStudent = UserAccountService.doesUserHavePermission(
				userAccountDto, FIND_ANY_STUDENT_PERMISSION);
		if (!findAnyStudent) {
			studentSearchForm.setMisCodes(userAccountDto.getCollegeIds());
		} else {
			studentSearchForm.setMisCodes(null);
		}
		List<StudentViewDto> studentViewDtos = service.findBySearchForm(
				studentSearchForm, userAccountDto.getCollegeIds());
		return new ResponseEntity<List<StudentViewDto>>(studentViewDtos,
				HttpStatus.OK);
	}
	
	public List<StudentViewDto> findBySearchForm(
			StudentSearchForm studentSearchForm, Set<String> collegeIds)
			 {

		List<StudentViewDto> studentViewDtos = service.findBySearchForm(
				studentSearchForm, collegeIds);
		return studentViewDtos;
	}
	
	public Set<String> collegesAppliedTo(String cccid) {
		return service.getCollegesAppliedTo(cccid);
	}

}
