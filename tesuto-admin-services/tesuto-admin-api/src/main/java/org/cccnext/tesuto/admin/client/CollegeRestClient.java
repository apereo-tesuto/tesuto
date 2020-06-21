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

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.cccnext.tesuto.admin.service.CollegeReader;
import org.cccnext.tesuto.admin.viewdto.CollegeViewDto;
import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.mchange.v1.util.SetUtils;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Service
public class CollegeRestClient  extends BaseRestClient<CollegeViewDto> implements CollegeReader {

	@Value("${admin.http.port}")
	private Integer httpPort;
	
	@Value("${admin.server.port}")
	private Integer serverPort;
	
	@Value("${admin.server.context}")
	private String context;

	
    @Override
    public CollegeViewDto getCollegeByMisCode(String misCode) {
        return retrieve(endpointBuilder(misCode));
    }

	@Override
	public CollegeDto read(String misCode) {
        return (CollegeDto)retrieveObject(endpointBuilder("dto",misCode), CollegeDto.class);
	}
	
	@Override
	public Set<CollegeViewDto> read(Set<String> misCodes) {
		return retrieveSet(readSetBuilder(misCodes));
	}
	
	public UriComponentsBuilder readSetBuilder(Set<String> misCodes) {
		return endpointBuilder("view", "miscodes", String.join(",", misCodes));
	}
	
	protected String controller() {
		return "colleges";
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
	protected String context() {
		return context;
	}
	
	@Override
	public List<RestClientTestResult> validateEndpoints() {
		String miscode ="ZZ1";
		Set<String> miscodes = SetUtils.setFromArray(ArrayUtils.toArray("ZZ1",
				"ZZ2",
				"ZZ3"));
		List<RestClientTestResult> results = super.validateEndpoints();
		results.add(testRetrieveObject("read", endpointBuilder("dto", miscode), CollegeDto.class));
		results.add(testRetrieve("getCollegeByMisCode", endpointBuilder(miscode)));
		results.add(testRetrieveSet("readSet", readSetBuilder(miscodes)));
		return results;
	}

}
