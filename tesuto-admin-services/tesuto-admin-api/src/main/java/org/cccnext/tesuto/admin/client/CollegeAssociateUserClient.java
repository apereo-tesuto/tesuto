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
import java.util.UUID;

import org.cccnext.tesuto.admin.dto.CollegeAssociatedUser;
import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.user.service.CollegeAssociatedUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CollegeAssociateUserClient extends BaseRestClient<CollegeAssociatedUser>
		implements CollegeAssociatedUserService {

	@Value("${admin.http.port}")
	private Integer httpPort;

	@Value("${admin.server.port}")
	private Integer serverPort;

	@Value("${admin.server.context}")
	private String context;

	@Override
	public CollegeAssociatedUser getCollegeAssociatedUser(String username) {

		return retrieve(getCollegeAssociatedUserBuilder(username));
	}

	UriComponentsBuilder getCollegeAssociatedUserBuilder(String username) {
		return endpointBuilder().queryParam("username", username);
	}

	@Override
	public Boolean isAssociatedToCollege(String username, String cccCollegeMisCode) {
		return (Boolean) retrieveBoolean(isAssociatedToCollegeBulder(username, cccCollegeMisCode), "IS_ASSOCIATED");
	}

	UriComponentsBuilder isAssociatedToCollegeBulder(String username, String cccCollegeMisCode) {
		return endpointBuilder("colleges").queryParam("username", username).queryParam("miscode", cccCollegeMisCode);
	}

	@Override
	protected String controller() {
		return "college-associated-user";
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
		String miscode = "ZZ1";
		String username = "A123456";
		List<RestClientTestResult> results = super.validateEndpoints();
		results.add(testRetrieve("getCollegeAssociatedUser", getCollegeAssociatedUserBuilder(username)));
		results.add(testRetrieveBoolean("isAssociatedToCollege", isAssociatedToCollegeBulder(username, miscode), "IS_ASSOCIATED"));
		return results;
	}

}
