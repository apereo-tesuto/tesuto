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
package org.cccnext.tesuto.placement.stub;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.form.StudentSearchForm;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.user.service.StudentService;

public class StudentServiceStub implements StudentService {

	Map<String,StudentViewDto> studentAccounts = new HashMap<>();
	
	public void addStudentWithColleges(String id, Set<String>  collegeMisCodes) {
		StudentViewDto student = new StudentViewDto();
		student.setCccid(id);
		Map<String,Integer> collegeStatuses = new HashMap<>();
		collegeMisCodes.forEach(miscode -> collegeStatuses.put(miscode, 1));
		student.setCollegeStatuses(collegeStatuses);
		studentAccounts.put(id,student);
	}
	
	@Override
	public StudentViewDto findByCccid(String cccid, Set<String> collegeFilter) {
		return studentAccounts.get(cccid);
	}
	
	@Override
	public StudentViewDto findByCccid(String cccid) {
		return studentAccounts.get(cccid);
	}

	@Override
	public List<StudentViewDto> findBySearchForm(StudentSearchForm studentSearchForm, Set<String> collegeFilter) {
		return null;
	}
	
	@Override
	public StudentViewDto getStudentById(String cccid) {
		return studentAccounts.get(cccid);
	}
	
	
	@Override
	public Set<String> getCollegesAppliedTo(String cccid) {
		StudentViewDto student = studentAccounts.get(cccid);
		if(student != null && student.getCollegeStatuses() != null && !student.getCollegeStatuses().isEmpty()) {
			return student.getCollegeStatuses().keySet();
		}
		return null;
	}

	public StudentViewDto buildStudentFromUserAccount(UserAccountDto userAccount) {
		return null;
	}

	public Collection<String> getAllCccids() {
		return studentAccounts.keySet();
	}
}
