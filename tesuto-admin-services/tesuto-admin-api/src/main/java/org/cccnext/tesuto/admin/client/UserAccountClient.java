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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.user.service.UserAccountReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class UserAccountClient extends BaseRestClient<UserAccountDto> implements UserAccountReader {

	@Value("${admin.http.port}")
	private Integer httpPort;

	@Value("${admin.server.port}")
	private Integer serverPort;

	@Value("${admin.server.context}")
	private String context;

	public UserAccountDto getUserAccountByUsername(String username) {
		return retrieve(userAccountByUsernameBuilder(username));
	}

	UriComponentsBuilder userAccountByUsernameBuilder(String username) {
		return endpointBuilder("username").queryParam("username", username);
	}

	@Override
	public UserAccountDto getUserAccount(String id) {
		return retrieve(endpointBuilder(id));
	}

	@Override
	public void failedLogin(String userAccountId) {
		update(failedLoginBuilder(userAccountId), HttpMethod.PUT, null);
	}

	UriComponentsBuilder failedLoginBuilder(String userAccountId) {
		return endpointBuilder(userAccountId, "login", "failed");
	}

	@Override
	public void clearFailedLogins(String userAccountId) {
		update(clearFailedLoginsBuilder(userAccountId), HttpMethod.PUT, null);
	}

	UriComponentsBuilder clearFailedLoginsBuilder(String userAccountId) {
		return endpointBuilder(userAccountId, "login", "succeeded");
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
		return "oauth2";
	}

	@Override
	protected String context() {
		return context;
	}

	@Override
	public List<RestClientTestResult> validateEndpoints() {
		String username = "multiple@democollege.edu";
		List<RestClientTestResult> results = super.validateEndpoints();
		results.add(testRetrieve("getUserAccountByUsername", userAccountByUsernameBuilder(username)));
		UserAccountDto userAccountDto = getUserAccountByUsername(username);
		String userAccountId = "superuser";
		if(userAccountDto != null) {
			userAccountId = userAccountDto.getUserAccountId();
		}
		results.add(testUpdate("failedLogin", failedLoginBuilder(userAccountId), HttpMethod.PUT, null));
		results.add(testUpdate("clearFailedLogins", clearFailedLoginsBuilder(userAccountId), HttpMethod.PUT, null));
		return results;
	}
}
