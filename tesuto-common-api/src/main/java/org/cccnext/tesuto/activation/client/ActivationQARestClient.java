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

import java.util.Date;

import org.cccnext.tesuto.activation.ProtoActivation;
import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.client.BaseRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
public class ActivationQARestClient extends BaseRestClient<Activation> {

	@Value("${activation.http.port}")
	private Integer httpPort;

	@Value("${activation.server.port}")
	private Integer serverPort;

	@Value("${activation.server.context}")
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
		return "qa";
	}

	@Override
	protected String context() {
		return context;
	}
	
	public String launchAssessmentSession(ProtoActivation proto, String proctorId, String requestorId) {
		return retrieveString(this.endpointBuilder("oauth2","launch","proctor",proctorId,"requestor",requestorId), HttpMethod.POST, buildHttpEntity(proto));
	}
	
	public void updateActivationStartDate(String assessmentSessionId, Date startDate) {
		 retrieve(endpointBuilder("oauth2","start-date","assessment-session-id", assessmentSessionId,
				 Long.toString(startDate.getTime())), HttpMethod.PUT, null);
	}
	
	public  void updateActivationEndDate(String assessmentSessionId, Date endDate) {
		 retrieve(endpointBuilder("oauth2","end-date","assessment-session-id", assessmentSessionId), HttpMethod.PUT, null);
	}
	
	
}
