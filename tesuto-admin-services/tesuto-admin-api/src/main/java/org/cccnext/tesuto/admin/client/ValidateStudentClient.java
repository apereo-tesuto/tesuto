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
	package org.cccnext.tesuto.admin.client;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.domain.util.upload.CsvImportError;
import org.cccnext.tesuto.user.service.ValidateStudentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.mchange.v1.util.SetUtils;

@Service
public class ValidateStudentClient extends BaseRestClient<CsvImportError> implements ValidateStudentService {

	@Value("${admin.http.port}")
	private Integer httpPort;

	@Value("${admin.server.port}")
	private Integer serverPort;

	@Value("${admin.server.context}")
	private String context;

	@Override
	protected Integer httpPort() {
		return httpPort;
	}

	@Override
	protected Integer serverPort() {
		return serverPort;
	}

	@Override
	protected String controller() {
		return "student-validation";
	}

	@Override
	protected String context() {
		return context;
	}

	@Override
	public Collection<CsvImportError> validateShort(StudentViewDto student, Collection<String> collegeIds) {
		// throw new NotImplementedException();
		return retrieveSet(validateShortBuilder(collegeIds), HttpMethod.PUT, buildHttpEntity(student));

	}
	
	UriComponentsBuilder validateShortBuilder(Collection<String> collegeIds) {
		return endpointBuilder("oauth2")
				.queryParam("miscodes", String.join(",", collegeIds));
	}
	
	@Override
	public List<RestClientTestResult> validateEndpoints() {
		StudentViewDto student = new StudentViewDto();
		student.setCccid("A123456");
		Map<String,Integer> collegeStatuses = new HashMap<>();
		collegeStatuses.put("ZZ1", 1);
		student.setCollegeStatuses(collegeStatuses);
		Set<String> miscodes = SetUtils.setFromArray(ArrayUtils.toArray("ZZ1",
				"ZZ2",
				"ZZ3"));
		List<RestClientTestResult> results = super.validateEndpoints();
		results.add(testRetrieveSet("validateShort", validateShortBuilder(miscodes), HttpMethod.PUT, buildHttpEntity(student)));
		return results;
	}

}
