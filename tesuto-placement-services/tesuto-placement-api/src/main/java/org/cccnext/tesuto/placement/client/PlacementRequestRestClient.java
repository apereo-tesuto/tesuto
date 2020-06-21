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
package org.cccnext.tesuto.placement.client;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.placement.service.PlacementRequester;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class PlacementRequestRestClient extends BaseRestClient<Void> implements PlacementRequester {

	@Value("${placement.http.port}")
	private Integer httpPort;

	@Value("${placement.server.port}")
	private Integer serverPort;

	@Value("${placement.server.context}")
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
		return "placement-request";
	}

	@Override
	protected String context() {
		return context;
	}
	
	@Override
    public void requestPlacements(String misCode, String studentCccId) {
        UriComponentsBuilder builder = requestPlacementsBuilder(misCode, studentCccId);
        update(builder,HttpMethod.PUT, null);
    }
    
    UriComponentsBuilder requestPlacementsBuilder(String misCode, String studentCccId){
    	 UriComponentsBuilder builder  = endpointBuilder();
         if (StringUtils.isNotBlank(misCode)) {
             builder.pathSegment("colleges", misCode);
         }

         if (StringUtils.isNotBlank(studentCccId)) {
             builder.pathSegment("cccid", studentCccId);
         }
         return builder;
    }
    
    @Override
	public List<RestClientTestResult> validateEndpoints() {
		String misCode = "ZZ1";
		String userId = "A123456";
		List<RestClientTestResult> results = super.validateEndpoints();
		results.add(testUpdate("requestPlacements", requestPlacementsBuilder(misCode, userId),HttpMethod.PUT, null));
		results.add(testUpdate("requestPlacementsNoUserId", requestPlacementsBuilder("", userId),HttpMethod.PUT, null));
		return results;
	}

}
