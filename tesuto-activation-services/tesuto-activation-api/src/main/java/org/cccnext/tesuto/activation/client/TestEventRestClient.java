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
package org.cccnext.tesuto.activation.client;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.cccnext.tesuto.activation.model.TestEvent;
import org.cccnext.tesuto.activation.service.TestEventService;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.client.BaseRestClient;
import org.springframework.beans.factory.annotation.Value;

public class TestEventRestClient  extends BaseRestClient<TestEvent> implements TestEventService {

	@Value("activation.http.port")
	private Integer httpPort;

	@Value("activation.server.port")
	private Integer serverPort;

	@Value("activation.server.context")
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
		return "assessment-post-completion";
	}

	@Override
	protected String context() {
		return context;
	}
	
	@Override
	public UserAccountDto createRemoteProctorFromTestEvent(String uid, TestEvent testEvent) {
		throw new NotImplementedException();
	}

	@Override
	public TestEvent findByUuid(String uuuid) {
		throw new NotImplementedException();
	}

	

	@Override
	public List validateEndpoints() {
		//Auto-generated method stub
		return null;
	}

}
