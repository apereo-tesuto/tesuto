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

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto;
import org.cccnext.tesuto.content.dto.shared.OrderedCompetencySet;
import org.cccnext.tesuto.content.service.CompetencyMapOrderReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CompetencyMapOrderRestClient extends BaseRestClient<CompetencyDifficultyDto>
		implements CompetencyMapOrderReader {

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
		return "competency-map-order";
	}

	@Override
	protected String context() {
		return context;
	}

	@Override
	public List<CompetencyDifficultyDto> getOrderedCompetencies(String id) {
		CompetencyDifficultyDto[] dtos = (CompetencyDifficultyDto[]) retrieveObject(getOrderedCompetenciesBuilder(id),
				CompetencyDifficultyDto[].class);
		if (dtos == null) {
			return new ArrayList<>();
		}
		return Arrays.asList(dtos);
	}

	@Override
	public List<CompetencyDifficultyDto> getOrderedCompetencies(String competencyOrderMapId, int comptencyMapVersion) {
		CompetencyDifficultyDto[] dtos = (CompetencyDifficultyDto[]) retrieveObject(
				getOrderedCompetenciesBuilder(competencyOrderMapId, comptencyMapVersion),
				CompetencyDifficultyDto[].class);
		if (dtos == null) {
			return new ArrayList<>();
		}
		return Arrays.asList(dtos);
	}

	@Override
	public String findLatestPublishedIdByCompetencyMapDiscipline(String competencyDisciplineName) {
		return (String) retrieveObject(findIdByCompetencyMapDisciplineBuilder(competencyDisciplineName), String.class);
	}

	@Override
	public OrderedCompetencySet selectOrganizeByAbility(String id, Double studentDificulty, Integer parentLevel,
			Integer competencyRange) {
		return (OrderedCompetencySet) retrieveObject(
				selectOrganizeByAbilityBuilder(id, studentDificulty, parentLevel, competencyRange),
				OrderedCompetencySet.class);
	}

	@Override
	public String getCompetencyMapOrderId(String id) {
		return (String) retrieveObject(findCompetencyMapOrderIdById(id), String.class);
	}

	UriComponentsBuilder getOrderedCompetenciesBuilder(String competenecyMapDiscipline, Integer comptencyMapVersion) {
		return endpointBuilder("competencies", competenecyMapDiscipline, comptencyMapVersion.toString());
	}

	UriComponentsBuilder getOrderedCompetenciesBuilder(String competencyOrderMapId) {
		return endpointBuilder("competencies", competencyOrderMapId);
	}

	UriComponentsBuilder findIdByCompetencyMapDisciplineBuilder(String competencyDisciplineName) {
		return endpointBuilder("discipline", competencyDisciplineName);
	}

	UriComponentsBuilder findCompetencyMapOrderIdById(String id) {
		return endpointBuilder("competency-map-id", id);
	}

	UriComponentsBuilder selectOrganizeByAbilityBuilder(String id, Double studentDificulty, Integer parentLevel,
			Integer competencyRange) {
		return endpointBuilder("competencies", "selected", id, studentDificulty.toString(), parentLevel.toString(),
				competencyRange.toString());
	}

	@Override
	public List<RestClientTestResult> validateEndpoints() {
		String id = findLatestPublishedIdByCompetencyMapDiscipline("ENGLISH");
		id = StringUtils.isBlank(id) ? UUID.randomUUID().toString() : id;
		String competenecyMapDiscipline = "ENGLISH";
		String competencyOrderMapId = getCompetencyMapOrderId(id);
		int competencyMapVersion = 1;
		int parentLevel = 2;
		Double studentDificulty = 1.222;
		int competencyRange = 12;

		List<RestClientTestResult> results = super.validateEndpoints();
		results.add(testRetrieveObject("getOrderedCompetencies", getOrderedCompetenciesBuilder(id),
				CompetencyDifficultyDto[].class));
		results.add(testRetrieveObject("getOrderedCompetenciesVersion",
				getOrderedCompetenciesBuilder(competencyOrderMapId, competencyMapVersion),
				CompetencyDifficultyDto[].class));
		results.add(testRetrieveObject("findLatestPublishedIdByCompetencyMapDiscipline",
				findIdByCompetencyMapDisciplineBuilder(competenecyMapDiscipline), String.class));
		results.add(testRetrieveObject("getCompetencyMapOrderId",
				findCompetencyMapOrderIdById(id), String.class));
		results.add(testRetrieveObject("selectOrganizeByAbility",
				selectOrganizeByAbilityBuilder(id, studentDificulty, parentLevel, competencyRange), String.class));
		return results;
	}

}
