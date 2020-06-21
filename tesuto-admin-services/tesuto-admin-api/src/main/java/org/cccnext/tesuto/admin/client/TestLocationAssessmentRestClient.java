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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.TestLocationAssessmentDto;
import org.cccnext.tesuto.admin.service.TestLocationAssessmentReader;
import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TestLocationAssessmentRestClient extends BaseRestClient<TestLocationAssessmentDto> implements
		TestLocationAssessmentReader {

	@Value("${admin.http.port}")
	private Integer httpPort;
	
	@Value("${admin.server.port}")
	private Integer serverPort;
	
	@Value("${admin.server.context}")
	private String context;
	
	@Override
	public Set<TestLocationAssessmentDto> getByTestLocation(
			String testLocationId) {
		TestLocationAssessmentDto[] values =  (TestLocationAssessmentDto[])retrieveObject(endpointBuilder(testLocationId), TestLocationAssessmentDto[].class);
		
		return new HashSet<TestLocationAssessmentDto>(Arrays.asList(values));
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
		return "test-location-assessments";
	}

	@Override
	protected String context() {
		return context;
	}
	
	@Override
	public List<RestClientTestResult> validateEndpoints() {
		String testLocationId = "1000002";

		List<RestClientTestResult> results = super.validateEndpoints();
		results.add(testRetrieveSet("getByTestLocation",endpointBuilder(testLocationId)));
		
		return results;
	}

}
