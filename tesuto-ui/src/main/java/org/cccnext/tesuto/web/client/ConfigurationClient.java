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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.springframework.http.HttpMethod;

public abstract class ConfigurationClient extends BaseRestClient<String> {

	@Override
	protected String service() {
		return null;
	}
	
	@Override
	protected String version() {
		return null;
	} 
	
	public String getConfiguration() {
		return retrieveString(endpointBuilder(), HttpMethod.GET, null);
	}
	
	public List<RestClientTestResult> getRestClientStatus() {
		RestClientTestResult[] results = (RestClientTestResult[])retrieveObject(endpointBuilder("clients","status"), RestClientTestResult[].class);
		if(results == null) {
			return new ArrayList<>();
		}
		return Arrays.asList(results);
	}
	
	public List<String> getConfigurations() {
		return Arrays.asList(getConfiguration().split("\n"));
	}
	
	public Map<String,String> getProperties() {
		Map<String,String> properties = new HashMap<>();
		getConfigurations().forEach(s -> addProperty(properties, s));
		return properties;
	}
	
	private void addProperty(Map<String,String> properties, String configuration) {
		String[] property = configuration.split(" : ");
		properties.put(property[0], property[1]);
	}
	
	@Override
	protected String controller() {
		return "configuration";
	}
	
	public abstract String getMicroserviceName();
	public abstract String getMicroserviceDisplayName();
}
