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
package org.cccnext.tesuto.web.service;

import org.cccnext.tesuto.client.BaseRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
public class QARestClient extends BaseRestClient<Void> {

	@Value("${admin.http.port}")
	Integer adminHttpPort;
	@Value("${admin.server.port}")
	Integer adminServerPort;
	@Value("${admin.server.context}")
	String adminServerContext;

	@Value("${qti.importer.http.port}")
	Integer qtiImporterHttpPort;
	@Value("${qti.importer.server.port}")
	Integer qtiImporterServerPort;
	@Value("${qti.importer.server.context}")
	String qtiImporterServerContext;

	@Value("${placement.http.port}")
	Integer placementHttpPort;
	@Value("${placement.server.port}")
	Integer placementServerPort;
	@Value("${placement.server.context}")
	String placementServerContext;

	@Value("${delivery.http.port}")
	Integer deliveryHttpPort;
	@Value("${delivery.server.port}")
	Integer deliveryServerPort;
	@Value("${delivery.server.context}")
	String deliveryServerContext;
	
	@Value("${disable.assessments}")
	Boolean assesmentsDisabled;
	
	Integer httpPort;

	Integer serverPort;

	String context;

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

	public void insertSeedData() {
		insertSeedDataServer(adminServerPort, adminHttpPort, adminServerContext);
		
		if(!assesmentsDisabled) {
			this.insertSeedDataServer(qtiImporterServerPort, qtiImporterHttpPort, qtiImporterServerContext);
		}
	
		insertSeedDataServer(placementServerPort, placementHttpPort, placementServerContext);
		
		if(!assesmentsDisabled) {
			insertSeedDataServer(deliveryServerPort, deliveryHttpPort, deliveryServerContext);
		}
	}
	
	private void insertSeedDataServer(Integer serverPort, Integer httpPort, String context) {
		this.serverPort = serverPort;
		this.httpPort = httpPort;
		this.context = context;
		update(this.endpointBuilder("oauth2"), HttpMethod.POST, null);
	}

}
