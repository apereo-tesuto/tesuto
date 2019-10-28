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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.cccnext.tesuto.activation.SearchParameters;
import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.activation.model.Activation.Status;
import org.cccnext.tesuto.activation.service.ActivationReader;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.delivery.service.DeliveryServiceListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.mchange.v1.util.SetUtils;

@Service
public class ActivationServiceRestClient extends BaseRestClient<Activation>
		implements ActivationReader, DeliveryServiceListener {

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
		return null;
	}

	@Override
	protected String context() {
		return context;
	}

	@Override
	public Set<Activation> find(Set<String> activationIds) {
		UriComponentsBuilder builder = builderForFind(activationIds);
		Activation[] activations = (Activation[])retrieveObject(builder, Activation[].class);
		if(activations == null) {
			return new HashSet<>();
		}
		return SetUtils.setFromArray(activations);
	}
	
	public List<Activation> search(SearchParameters parameters) {
		Activation[] activations = (Activation[])retrieveObject(endpointBuilder("activation-search","search", "oauth2"), HttpMethod.POST, buildHttpEntity(parameters), Activation[].class);
		if(activations == null) {
			return new ArrayList<>();
		}
		return Arrays.asList(activations);
	}

	@Override
	//TODO potentially add current user to retain security, should not be necessary 
	public Activation find(String activationId) {
		UriComponentsBuilder builder = endpointBuilder("activations", "api", activationId);
		return retrieve(builder);

	}

	@Override
	public void completeAssessment(String assessmentSessionId, UserAccountDto requestor) {
		update(endpointBuilder("activations","complete", assessmentSessionId, "oauth2"), HttpMethod.POST, buildHttpEntity(requestor));

	}

	@Override
	public void pendingAssessment(String assessmentSessionId, UserAccountDto requestor) {
		update(endpointBuilder("activations","pending", assessmentSessionId, "oauth2"), HttpMethod.POST, buildHttpEntity(requestor));
	}

	@Override
	public void assessedAssessment(String assessmentSessionId, UserAccountDto requestor, Date statusChangeDate) {
		
		update(endpointBuilder("activations","assessed", assessmentSessionId, Long.toString(statusChangeDate.getTime()),"oauth2"), HttpMethod.POST, buildHttpEntity(requestor));
	}
	
	public UriComponentsBuilder builderForFind(Set<String> activationIds) {
		UriComponentsBuilder builder = endpointBuilder();
		builder.path("activations").queryParam("activationIds", String.join(",", activationIds));
		return builder;
	}


	@Override
	public List<RestClientTestResult> validateEndpoints() {
		String activationId = UUID.randomUUID().toString();
		String assessmentSessionId = UUID.randomUUID().toString();
		Set<String> activationIds = SetUtils.setFromArray(ArrayUtils.toArray(activationId,
				UUID.randomUUID().toString(),
				UUID.randomUUID().toString()));
		Set<String> userIds = SetUtils.setFromArray(ArrayUtils.toArray("A123999",
				UUID.randomUUID().toString(),
				UUID.randomUUID().toString()));
		List<RestClientTestResult> results = super.validateEndpoints();
		UserAccountDto requestor = new UserAccountDto();
		requestor.setUsername("A123999");
		requestor.setUserAccountId("user-account-id-A123999");
		
		SearchParameters parameters = new SearchParameters();
	    Date currentDate = new Date();

        // convert date to calendar
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, -1);
		parameters.setCurrentStatus(Status.COMPLETE);
		parameters.setMinStartDate(c.getTime());
		parameters.setUserIds(userIds);
		
		results.add(testRetrieveObject("findSet",  builderForFind(activationIds), HttpMethod.PUT, null, Activation[].class));
		results.add(testRetrieveObject("search",endpointBuilder("activation-search","search", "oauth2"), HttpMethod.POST, buildHttpEntity(parameters), Activation[].class));
		results.add(testRetrieveObject("find (403 forbidden expected)",  endpointBuilder("activations", activationId), Activation[].class));
		results.add(testUpdate("assessedAssessmentSession", endpointBuilder("activations","assessed", assessmentSessionId,Long.toString(new Date().getTime()), "oauth2"), HttpMethod.POST, buildHttpEntity(requestor)));
		results.add(testUpdate("pendingAssessmentSession", endpointBuilder("activations","pending", assessmentSessionId, "oauth2"), HttpMethod.POST, buildHttpEntity(requestor)));
		results.add(testUpdate("completeAssessmentSession", endpointBuilder("activations","complete", assessmentSessionId, "oauth2"), HttpMethod.POST, buildHttpEntity(requestor)));
		return results;
	}

}
