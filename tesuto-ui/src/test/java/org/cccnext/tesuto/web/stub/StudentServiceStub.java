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
package org.cccnext.tesuto.web.stub;

import java.util.List;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.form.StudentSearchForm;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.user.service.StudentService;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceStub implements StudentService {

	@Override
	public StudentViewDto getStudentById(String cccid) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getCollegesAppliedTo(String cccid) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public List<StudentViewDto> findBySearchForm(StudentSearchForm studentSearchForm, Set<String> collegeFilter) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public StudentViewDto findByCccid(String cccid, Set<String> collegeFilter) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public StudentViewDto findByCccid(String cccid) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public StudentViewDto buildStudentFromUserAccount(UserAccountDto userAccount) {
		//Auto-generated method stub
		return null;
	}

}
