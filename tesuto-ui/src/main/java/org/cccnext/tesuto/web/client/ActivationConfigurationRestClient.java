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
package org.cccnext.tesuto.web.client;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ActivationConfigurationRestClient extends ConfigurationClient{

	
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
	protected String context() {
		return context;
	}

	@Override
	public String getMicroserviceName() {
		return "activation";
	}

	@Override
	public String getMicroserviceDisplayName() {
		return "Activation Microservice";
	}

}
