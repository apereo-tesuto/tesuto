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

import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.user.service.SecurityGroupReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SecurityGroupClient extends BaseRestClient<SecurityGroupDto> implements SecurityGroupReader {

	@Value("${admin.http.port}")
	private Integer httpPort;

	@Value("${admin.server.port}")
	private Integer serverPort;

	@Value("${admin.server.context}")
	private String serverContext;

	@Override
	public SecurityGroupDto getSecurityGroupByGroupName(String groupName) {
		// throw new NotImplementedException();
		return retrieve(securityGroupByGroupNameBuilder(groupName));
	}
	
	UriComponentsBuilder securityGroupByGroupNameBuilder(String groupName) {
		return endpointBuilder(groupName);
	}

	@Override
	protected String controller() {
		return "security-group";
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
		return serverContext;
	}

	@Override
	public List<RestClientTestResult> validateEndpoints() {
		String groupName = "LOCAL_ADMIN";
		List<RestClientTestResult> results = super.validateEndpoints();
		results.add(testRetrieve("getSecurityGroupByGroupName", securityGroupByGroupNameBuilder(groupName)));
		return results;
	}

}
