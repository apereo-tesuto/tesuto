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
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.content.service.AssessmentReader;
import org.cccnext.tesuto.content.viewdto.AssessmentViewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AssessmentReaderRestClient extends BaseRestClient<AssessmentDto> implements AssessmentReader {

	
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
		return "assessments";
	}

	@Override
	protected String context() {
		return context;
	}
	
    
	@Override
	public AssessmentViewDto readViewDto(String namespace, String identifier) {
		return (AssessmentViewDto)retrieveObject(readLatestPublishedVersionBuilder(namespace, identifier), AssessmentViewDto.class);
	}
	
	@Override
	public AssessmentDto read(String id) {
		return retrieve(endpointBuilder(id), HttpMethod.GET, null);
	}

	@Override
	public AssessmentDto readLatestPublishedVersion(String namespace,
			String identifier) {
		return retrieve(readLatestPublishedVersionBuilder(namespace, identifier));

	}

	@Override
	public AssessmentDto readVersion(ScopedIdentifier scopedIdentifier,
			int version) {
		return retrieve(readVersionBuilder(scopedIdentifier.getNamespace(),scopedIdentifier.getIdentifier(),version), HttpMethod.GET, null);
	}
	

	@Override
	public AssessmentDto readLatestPublishedVersion(
			ScopedIdentifier scopedIdentifier) {
		return readLatestPublishedVersion(scopedIdentifier.getNamespace(),scopedIdentifier.getIdentifier());
	}
	
	@Override
	public List<AssessmentDto> read(ScopedIdentifier scopedIdentifier) {
		AssessmentDto[] assessmentDtos= (AssessmentDto[])retrieveObject(readListBuilder(scopedIdentifier), HttpMethod.GET, null,AssessmentDto[].class);
		if(assessmentDtos == null) {
			return new ArrayList<>();
		}
		return Arrays.asList(assessmentDtos);
	}
	
	UriComponentsBuilder readViewDtoBuilder(String namespace, String identifier) {
		return endpointBuilder("viewdto", namespace, identifier, "published");
	}

	
	UriComponentsBuilder readLatestPublishedVersionBuilder(String namespace, String identifier) {
		return endpointBuilder("view","latest", namespace, identifier);
	}

	
	UriComponentsBuilder readVersionBuilder(String namespace, String identifier, Integer version) {
		return endpointBuilder(namespace, identifier, version.toString());
	}
	
	UriComponentsBuilder readListBuilder(ScopedIdentifier scopedIdentifier) {
		return readListBuilder(scopedIdentifier.getNamespace(),scopedIdentifier.getIdentifier());
	}
	
	UriComponentsBuilder readListBuilder(String namespace, String identifier) {
		return endpointBuilder(namespace, identifier);
	}
	
	@Override
	public List<RestClientTestResult> validateEndpoints() {
		String id = UUID.randomUUID().toString();
		String namespace = "developer";
		String identifier = "math_test";
		Integer version = 1;
		ScopedIdentifier scopedIdentifier = new ScopedIdentifier(namespace, identifier);

		List<RestClientTestResult> results = super.validateEndpoints();
		results.add(testRetrieve("read",endpointBuilder(id)));
		results.add(testRetrieveObject("read(scopedIdentifier)",readListBuilder(scopedIdentifier), AssessmentViewDto[].class));
		results.add(testRetrieveObject("readViewDto",readViewDtoBuilder(namespace, identifier), AssessmentViewDto.class));
		results.add(testRetrieve("readLatestPublishedVersion",readLatestPublishedVersionBuilder(namespace, identifier)));
		results.add(testRetrieve("readVersion",readVersionBuilder(namespace, identifier, version)));
		return results;
	}

	@Override
	public List<AssessmentDto> readByCompetencyMapDisicpline(String competencyMapDiscipline) {
		AssessmentDto[] assessments = (AssessmentDto[])retrieveObject(endpointBuilder("competency-map-discipline",competencyMapDiscipline), AssessmentDto[].class);
		return Arrays.asList(assessments);
	}

	@Override
	public List<AssessmentDto> readByCompetencyMapDisicplineOrPartialIdentifier(String competencyMapDiscipline,
			String partialIdentifier) {
		AssessmentDto[] assessments = (AssessmentDto[])retrieveObject(endpointBuilder("competency-map-discipline",competencyMapDiscipline, "partial-identifier", partialIdentifier), AssessmentDto[].class);
		return Arrays.asList(assessments);
	}

}
