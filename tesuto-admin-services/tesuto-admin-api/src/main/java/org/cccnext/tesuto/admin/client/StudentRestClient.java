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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.cccnext.tesuto.admin.form.StudentSearchForm;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.user.service.StudentReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.mchange.v1.util.SetUtils;

@Service
public class StudentRestClient  extends BaseRestClient<StudentViewDto> implements StudentReader {

	@Value("${admin.http.port}")
	private Integer httpPort;
	
	@Value("${admin.server.port}")
	private Integer serverPort;
	
	@Value("${admin.server.context}")
	private String context;

    String permission = "FIND_ANY_STUDENT";
    

    @Override
    public StudentViewDto getStudentById(String userId) {
       return retrieve(studentByIdBuilder(userId));
    }
    
    UriComponentsBuilder studentByIdBuilder(String userId) {
    	return endpointBuilder("search",userId);
    }

    @Override
    public  Set<String> getCollegesAppliedTo(String cccid){           
        return retrieveStringSet(getCollegesAppliedToBuilder(cccid), HttpMethod.GET, null);
    }
    
    UriComponentsBuilder getCollegesAppliedToBuilder(String cccid) {
    	return endpointBuilder("search",cccid,"colleges");
    }


	@Override
	public List<StudentViewDto> findBySearchForm(StudentSearchForm studentSearchForm, Set<String> collegeFilter) {
		//throw new NotImplementedException();
		return retrieveList(findBySearchFormBuilder(collegeFilter), HttpMethod.POST, findBySearchFormEntity(studentSearchForm));

	}
	
	UriComponentsBuilder findBySearchFormBuilder(Set<String> collegeFilter) {
    	return endpointBuilder("search","oauth2","filtered").queryParam("miscodes", String.join(",", collegeFilter));
    }
	
	@SuppressWarnings("unchecked")
	HttpEntity<StudentSearchForm> findBySearchFormEntity(StudentSearchForm studentSearchForm) {
		return (HttpEntity<StudentSearchForm>)buildHttpEntity(studentSearchForm);
	
	}

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
		return "students";
	}

	@Override
	protected String context() {
		return context;
	}
	
	@Override
	protected String version() {
		return "v2";
	}
	
	@Override
	public List<RestClientTestResult> validateEndpoints() {
		Set<String> miscodes = SetUtils.setFromArray(ArrayUtils.toArray("ZZ1",
				"ZZ2",
				"ZZ3"));
		StudentSearchForm  studentSearchForm = new StudentSearchForm();
		studentSearchForm.setCccids(Arrays.asList("A123456",
				"A123457",
				"A123458"));
		String username = "A123456";
		List<RestClientTestResult> results = super.validateEndpoints();
		results.add(testRetrieve("studentById", studentByIdBuilder(username)));
		results.add(testRetrieveStringList("getCollegesAppliedTo", getCollegesAppliedToBuilder(username), HttpMethod.GET, null));
		results.add(testRetrieveList("findBySearchForm", findBySearchFormBuilder(miscodes), HttpMethod.POST, findBySearchFormEntity(studentSearchForm)));
		return results;
	}
}
