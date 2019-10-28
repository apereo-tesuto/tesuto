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
package org.cccnext.tesuto.content.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.service.AssessmentItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AssessmentItemReaderRestClient extends BaseRestClient<AssessmentItemDto> implements AssessmentItemReader {

	@Value("${content.http.port}")
	private Integer httpPort;

	@Value("${content.server.port}")
	private Integer serverPort;

	@Value("${content.server.context}")
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
		return "assessment-items";
	}

	@Override
	protected String context() {
		return context;
	}
	
	@Override
	public AssessmentItemDto read(String id) {
		return retrieve(endpointBuilder(id));
	}


	@Override
	public AssessmentItemDto readLatestPublishedVersion(String namespace,
			String identifier) {
		return retrieve(readLatestPublishedVersionBuilder(namespace, identifier));
	}

	@Override
	public List<AssessmentItemDto> getItemsByCompetency(String mapDiscipline,
			String competencyIdentifier) {
		AssessmentItemDto[] items = (AssessmentItemDto[])retrieveObject(getItemsByCompetencyBuilder(mapDiscipline, competencyIdentifier), AssessmentItemDto[].class);
		if(items == null || items.length == 0) {
			return new ArrayList<>();
		}
		return Arrays.asList(items);
	}

	@Override
	public List<AssessmentItemDto> getItemsByCompetencyMapDiscipline(
			String competencyMapDiscipline, List<String> fields) {
		AssessmentItemDto[] items = (AssessmentItemDto[])retrieveObject(itemsByCompetencyMapDisciplineBuilder(competencyMapDiscipline), HttpMethod.GET, buildHttpEntity(fields), AssessmentItemDto[].class);
		if(items == null || items.length == 0) {
			return new ArrayList<>();
		}
		return Arrays.asList(items);
	}
	
	UriComponentsBuilder itemsByCompetencyMapDisciplineBuilder(String competencyMapDiscipline) {
		return endpointBuilder("competency-map-discipline",competencyMapDiscipline);
	}
	
	UriComponentsBuilder getItemsByCompetencyBuilder(String mapDiscipline,
			String competencyIdentifier) {
		return endpointBuilder("competency-map-discipline",mapDiscipline, competencyIdentifier);
	}
	
	
	UriComponentsBuilder readLatestPublishedVersionBuilder(String namespace, String identifier) {
		return endpointBuilder(namespace, identifier, "published");
	}


	
	@Override
	public List<RestClientTestResult> validateEndpoints() {
		String itemId = "itemId";
		String namespace = "developer";
		String identifier = "math_test";
		String mapDiscipline = "ENGLISH";
		String compentencyIdentifier = UUID.randomUUID().toString();
		List<String> fields = Arrays.asList(
				UUID.randomUUID().toString(),
				UUID.randomUUID().toString());
		List<RestClientTestResult> results = super.validateEndpoints();
		results.add(testRetrieve("read",endpointBuilder(itemId)));
		results.add(testRetrieve("readLatestPublishedVersion",readLatestPublishedVersionBuilder(namespace, identifier), HttpMethod.GET, buildHttpEntity(null)));
		results.add(testRetrieveObject("getItemsByCompetency",getItemsByCompetencyBuilder(mapDiscipline, compentencyIdentifier), HttpMethod.GET, buildHttpEntity(null), AssessmentItemDto[].class));
		results.add(testRetrieveObject("getItemsByCompetencyMapDiscipline", itemsByCompetencyMapDisciplineBuilder(mapDiscipline), HttpMethod.GET, buildHttpEntity(null), AssessmentItemDto[].class));
		results.add(testRetrieveObject("getItemsByCompetencyMapDisciplineNoFields", itemsByCompetencyMapDisciplineBuilder(mapDiscipline), HttpMethod.GET, buildHttpEntity(null), AssessmentItemDto[].class));

		return results;
	}

	@Override
	public List<AssessmentItemDto> getAllVersions(String namespace, String identifier) {
		AssessmentItemDto[] items = (AssessmentItemDto[])retrieveObject(endpointBuilder("all", namespace, identifier), AssessmentItemDto[].class);
		return Arrays.asList(items);
	}

}
