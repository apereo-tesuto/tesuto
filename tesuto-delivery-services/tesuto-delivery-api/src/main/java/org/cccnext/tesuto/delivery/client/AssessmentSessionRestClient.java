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
package org.cccnext.tesuto.delivery.client;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.NotImplementedException;
import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.content.model.DeliveryType;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.delivery.service.AssessmentSessionNotFoundException;
import org.cccnext.tesuto.delivery.service.AssessmentSessionReader;
import org.cccnext.tesuto.delivery.view.AssessmentSessionRestrictedViewDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AssessmentSessionRestClient extends BaseRestClient<AssessmentSessionRestrictedViewDto> implements AssessmentSessionReader {

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
		return "assessmentsessions";
	}

	@Override
	protected String context() {
		return context;
	}
	
	@Override
	public Object findAssessmentSession(String assessmentSessionId)
			throws AssessmentSessionNotFoundException {
		return (Object)retrieve(endpointBuilder(assessmentSessionId));
	}

	@Override
	public Object createContentAssessmentSession(
			String namespace, String identifier) {
		return retrieve(endpointBuilder("oauth2", namespace, identifier), HttpMethod.POST,null);
	}

	@Override
	public String createUserAssessmentSession(String userId, ScopedIdentifier scopedIdentifer,
			DeliveryType deliveryType, Map<String, String> settings) {		
		return retrieveString(createUserAssessmentSessionBuilder(userId,scopedIdentifer.getNamespace(),
				scopedIdentifer.getIdentifier(), deliveryType.name()), HttpMethod.POST,buildHttpEntity(settings));
	}
	
	@Override
	public Integer getAssessmentVersionForSession(String assessmentSessionId) {
		return Integer.parseInt(retrieveString(endpointBuilder("oauth2", "assessment", "version", assessmentSessionId), HttpMethod.GET, null));
	}
	
	public UriComponentsBuilder createUserAssessmentSessionBuilder(String userId, String  namespace, String identifier,
			String deliveryType) {
		return endpointBuilder("oauth2", userId,namespace,
				identifier,deliveryType);
	}
	
	
	
	
	@Override
	public List<RestClientTestResult> validateEndpoints() {
		String identifier =  "elasamplefullv1";
		String assessmentSessionId = UUID.randomUUID().toString();
		String namespace = "TEST";
		String userId = "A123999";
		String deliveryType = DeliveryType.ONLINE.name();
		List<RestClientTestResult> results = super.validateEndpoints();
		results.add(testRetrieve("findAssessmentSession (404 Not Found is Expected, Needs to Be Updated)",endpointBuilder(assessmentSessionId)));
		results.add(testRetrieve("createContentAssessmentSession (404 Not Found is Expected)", endpointBuilder("oauth2", namespace, identifier), HttpMethod.POST,null));
		results.add(testRetrieveObject("createUserAssessmentSession (404 Not Found is Expected)", createUserAssessmentSessionBuilder(userId, namespace, identifier,
				 deliveryType), HttpMethod.POST,null, String.class));
		return results;
	}
	

	@Override
	public String createUserAssessmentSession(String userId, ScopedIdentifier assessmentId, int assessmentVersion,
			DeliveryType deliveryType, Map<String, String> settings) {
		throw new NotImplementedException("createUserAssessmentSession has not been implemented in AssessmentSessionRestClient");
	}





}
