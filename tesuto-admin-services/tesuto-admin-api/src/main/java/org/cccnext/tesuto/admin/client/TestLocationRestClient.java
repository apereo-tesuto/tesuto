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

import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.service.TestLocationReader;
import org.cccnext.tesuto.client.BaseRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TestLocationRestClient extends BaseRestClient<TestLocationDto> implements TestLocationReader {

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
		return "test-locations";
	}

	@Override
	protected String context() {
		return context;
	}

	@Override
	public List<TestLocationDto> read() {
		return retrieveList(this.endpointBuilder("all"));
	}
	
	@Override
	public TestLocationDto read(String id) {
		return retrieve(this.endpointBuilder(id));
	}

}
