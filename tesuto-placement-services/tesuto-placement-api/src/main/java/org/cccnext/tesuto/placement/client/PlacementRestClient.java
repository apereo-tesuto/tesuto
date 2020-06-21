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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.placement.service.PlacementReader;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class PlacementRestClient extends BaseRestClient<PlacementComponentViewDto> implements PlacementReader {

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
		return "oauth2";
	}

	@Override
	protected String context() {
		return context;
	}

	@Override
	public VersionedSubjectAreaViewDto getVersionSubjectArea(Integer subjectAreaId, Integer subjectAreaVersion) {
		return (VersionedSubjectAreaViewDto) retrieveObject(
				getVersionSubjectAreaBuilder(subjectAreaId,subjectAreaVersion),
				VersionedSubjectAreaViewDto.class);
	}
	
	UriComponentsBuilder getVersionSubjectAreaBuilder(Integer subjectAreaId, Integer subjectAreaVersion) {
		return endpointBuilder("subject-area", subjectAreaId.toString(), "version", subjectAreaVersion.toString());
	}

	@Override
	public List<PlacementComponentViewDto> getPlacementComponents(String collegeMisCode, String cccid) {
		PlacementComponentViewDto[] components = (PlacementComponentViewDto[])retrieveObject(getPlacementComponentsBuilder(collegeMisCode, cccid), PlacementComponentViewDto[].class);
		return Arrays.asList(components);
	}
	
	UriComponentsBuilder getPlacementComponentsBuilder(String collegeMisCode, String cccid) {
		return endpointBuilder("placement-component", collegeMisCode, "cccid", cccid);
	}
	

	@Override
	public Collection<PlacementViewDto> getPlacements(String collegeMisCode, String cccid) {
		PlacementViewDto[] objects = (PlacementViewDto[])retrieveObject(getPlacementsBuilder(collegeMisCode, cccid), PlacementViewDto[].class);
		return Arrays.asList(objects);
	}

	
	UriComponentsBuilder getPlacementsBuilder(String collegeMisCode, String cccid) {
		UriComponentsBuilder builder = endpointBuilder("placements");
		if(StringUtils.isNotBlank(collegeMisCode))
			builder.pathSegment("college", collegeMisCode);
		if(StringUtils.isNotBlank(cccid))
			builder.pathSegment("cccid", cccid);
		return builder;
	}
	
	@Override
	public List<RestClientTestResult> validateEndpoints() {
		String collegeMisCode = "ZZ1";
		String cccid = "A123456";
		Integer subjectAreaId = 1;
		Integer subjectAreaVersion = 1;

		List<RestClientTestResult> results = super.validateEndpoints();
		results.add(testRetrieve("getVersionSubjectArea",getVersionSubjectAreaBuilder(subjectAreaId, subjectAreaVersion)));
		results.add(testRetrieveList("getPlacementComponents",  getPlacementComponentsBuilder(collegeMisCode, cccid)));
		results.add(testRetrieveObject("getPlacements",  getPlacementsBuilder(collegeMisCode, cccid), PlacementViewDto[].class));
		return results;
	}

}
