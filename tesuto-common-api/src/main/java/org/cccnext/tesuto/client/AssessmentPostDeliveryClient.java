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
package org.cccnext.tesuto.client;

import java.util.List;

import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AssessmentPostDeliveryClient extends BaseRestClient<Void> {

    @Value("${delivery.http.port}")
	private Integer httpPort;

	@Value("${delivery.server.port}")
	private Integer serverPort;

	@Value("${delivery.server.context}")
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
		return "delivery";
	}

	@Override
	protected String context() {
		return context;
	}   
    
    public void requestPlacements(String cccid) {
    	UriComponentsBuilder builder = endpointBuilder();
    	builder.queryParam("cccid", cccid);
    	
        update(builder,HttpMethod.PUT,null);
    }
    
    public void requestPlacements(String cccid, String collegeId) {
        UriComponentsBuilder builder = endpointBuilder();
    	builder.queryParam("cccid", cccid);
    	builder.queryParam("collegeId", collegeId);
        update(builder,HttpMethod.PUT,null);    }

    public void requestPlacements(String cccid, String collegeId, String subjectAreaName) {
       
        update(requestPlacementsBuilder(cccid,collegeId,subjectAreaName),HttpMethod.PUT,null);
    }
    
    public UriComponentsBuilder requestPlacementsBuilder(String cccid, String collegeId, String subjectAreaName) {
    	 UriComponentsBuilder builder = endpointBuilder("assessment-post-completion","oauth2");
     	builder.queryParam("cccid", cccid);
     	builder.queryParam("collegeId", collegeId);
     	builder.queryParam("subjectAreaName", subjectAreaName);
     	return builder;
    }

	@Override
	public List<RestClientTestResult> validateEndpoints() {
		List<RestClientTestResult> results = super.validateEndpoints();
		results.add(testUpdate("requestPlacements", 
				requestPlacementsBuilder("cccid","collegeId","subjectAreaName"), 
				HttpMethod.PUT, null));
		return results;
	}

}
