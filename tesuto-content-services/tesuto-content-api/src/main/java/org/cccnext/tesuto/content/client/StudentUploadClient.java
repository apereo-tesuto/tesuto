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
package org.cccnext.tesuto.content.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.content.service.StudentUploadService;
import org.cccnext.tesuto.domain.util.upload.CsvImportLineResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class StudentUploadClient extends BaseRestClient<CsvImportLineResult<StudentViewDto>> implements StudentUploadService {

	@Value("${content.http.port}")
	private Integer httpPort;

	@Value("${content.server.port}")
	private Integer serverPort;

	@Value("${content.server.context}")
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
		return "student-upload";
	}

	@Override
	protected String context() {
		return context;
	}
    
	@Override
	public List<CsvImportLineResult<StudentViewDto>> validateStudentCsvData(
			String studentCSV, Collection<String> collegeIds)
			throws IOException {
		
		return (List<CsvImportLineResult<StudentViewDto>>)retrieveObject(validateStudentCsvDataBuilder(collegeIds), 
				HttpMethod.POST, buildHttpEntity(studentCSV), parameterizedListReference() );
		
	}
	UriComponentsBuilder validateStudentCsvDataBuilder(Collection<String> collegeIds) throws UnsupportedEncodingException {
		return endpointBuilder("oauth2").queryParam("college-ids",URLEncoder.encode(StringUtils.join(collegeIds, ","), "UTF-8"));
	}
	
	
	@Override
	public List<RestClientTestResult> validateEndpoints() {
		String studentCSV = "cccid,asdf,asdf,asdf";
		List<String> collegeIds = Arrays.asList(
				UUID.randomUUID().toString(),
				UUID.randomUUID().toString());
		List<RestClientTestResult> results = super.validateEndpoints();
		try {
			results.add(testRetrieveObject("validateStudentCsvData",validateStudentCsvDataBuilder(collegeIds), HttpMethod.POST, 
					buildHttpEntity(studentCSV), parameterizedListReference()));
		} catch (UnsupportedEncodingException e) {
			RestClientTestResult result = new RestClientTestResult();
			result.setEndpoint(endpointBuilder(StringUtils.join(collegeIds, ",")).toUriString());
			result.setClassName(getClass().getName());
			result.setMethodName("validateStudentCsvData");
			result.setMessage("Failed due to encoding exception" + e.getMessage());
		}
		return results;
	}
	
}
