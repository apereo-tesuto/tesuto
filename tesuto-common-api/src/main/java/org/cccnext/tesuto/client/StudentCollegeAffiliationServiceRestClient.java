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
package org.cccnext.tesuto.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.admin.dto.StudentCollegeAffiliationDto;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.service.StudentCollegeAffiliationReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class StudentCollegeAffiliationServiceRestClient extends BaseRestClient<StudentCollegeAffiliationDto>
		implements StudentCollegeAffiliationReader {

	@Value("${ui.http.port}")
	private Integer httpPort;

	@Value("${ui.server.port}")
	private Integer serverPort;

	@Value("${ui.server.context}")
	private String context;

	@Override
	public StudentCollegeAffiliationDto findByCccIdAndMisCode(String cccid, String misCode) {
		return retrieve(endpointBuilder("cccid", cccid, "miscode", misCode), HttpMethod.GET, null);
	}

	@Override
	public List<StudentCollegeAffiliationDto> findByStudentCccId(String studentCccId) {
		StudentCollegeAffiliationDto[] dtos = (StudentCollegeAffiliationDto[])retrieveObject(endpointBuilder("cccid", studentCccId), StudentCollegeAffiliationDto[].class);
		if(dtos == null) {
			return new ArrayList<>();
		}
		return Arrays.asList(dtos);
	}
	
	@Override
	public List<StudentCollegeAffiliationDto> findTenMostRecent() {
		StudentCollegeAffiliationDto[] dtos = (StudentCollegeAffiliationDto[])retrieveObject(endpointBuilder("recent"), StudentCollegeAffiliationDto[].class);
		if(dtos == null) {
			return new ArrayList<>();
		}
		return Arrays.asList(dtos);
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
		return "student-affiliations";
	}

	@Override
	protected String context() {
		return context;
	}
	
	@Override
	public List<RestClientTestResult> validateEndpoints() {
		String cccid = "A123456";
		String misCode = "ZZ1";
		List<RestClientTestResult> results = super.validateEndpoints();
		List<StudentCollegeAffiliationDto> recent = findTenMostRecent();
		results.add(testRetrieveObject("recent", endpointBuilder("recent"), StudentCollegeAffiliationDto[].class));
		if(CollectionUtils.isEmpty(recent)) {
			results.add(getNotTestedResult("findByStudentCccId", endpointBuilder("cccid",cccid)));
			results.add(getNotTestedResult("findByCccIdAndMisCode", endpointBuilder("cccid",cccid,"miscode", misCode)));

		} else {
			results.add(testRetrieveObject("findByStudentCccId", endpointBuilder("cccid",recent.get(0).getStudentCccId()), StudentCollegeAffiliationDto[].class));
			results.add(testRetrieve("findByCccIdAndMisCode", endpointBuilder("cccid",recent.get(0).getStudentCccId(),"miscode", recent.get(0).getMisCode())));
		}
		return results;
	}
	
	RestClientTestResult getNotTestedResult(String methodName, UriComponentsBuilder builder) {
		RestClientTestResult result = new RestClientTestResult(getClass(),  methodName, builder, HttpMethod.GET);
		result.complete(HttpStatus.I_AM_A_TEAPOT.name(), "Not tested due to no available affiliations");
		return result;
		
	}
}
