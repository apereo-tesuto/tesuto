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

import org.apache.commons.lang3.NotImplementedException;
import org.cccnext.tesuto.admin.dto.SecurityGroupApiDto;
import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.user.service.SecurityGroupApiReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SecurityGroupApiRestClient extends BaseRestClient<SecurityGroupApiDto> implements SecurityGroupApiReader {


	@Value("${admin.http.port}")
	private Integer httpPort;
	
	@Value("${admin.server.port}")
	private Integer serverPort;
	
	@Value("${admin.server.context}")
	private String context;
	
	@Override
	public List<SecurityGroupApiDto> getAllSecurityGroupApis() {
		throw new NotImplementedException("getAllSecurityGroupApis");
	}

	@Override
	public SecurityGroupApiDto getSecurityGroupApi(Integer id) {
		throw new NotImplementedException("getSecurityGroupApi");
	}

	@Override
	public SecurityGroupApiDto getSecurityGroupApiByGroupName(String groupName) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public Set<SecurityGroupApiDto> getSecurityGroupApisByUserAccountApiId(String userAccountApiId) {
		throw new NotImplementedException("getSecurityGroupApisByUserAccountApiId");
	}
	
	UriComponentsBuilder securityGroupByGroupNameBuilder(String groupName) {
		return endpointBuilder("name", groupName);
	}
	
	
	protected String controller() {
		return "security-group-api";
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
		String groupName = "LOCAL_ADMIN";
		List<RestClientTestResult> results = super.validateEndpoints();
		results.add(testRetrieve("getSecurityGroupByGroupName", securityGroupByGroupNameBuilder(groupName)));
		return results;
	}


}
