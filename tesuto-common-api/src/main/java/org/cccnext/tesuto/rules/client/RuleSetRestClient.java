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
package org.cccnext.tesuto.rules.client;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.rules.service.RuleServiceWebServiceQueries;
import org.cccnext.tesuto.rules.service.RuleSetReader;
import org.ccctc.common.droolscommon.action.result.ActionResult;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class RuleSetRestClient extends BaseRestClient<ActionResult>
		implements RuleSetReader, RuleServiceWebServiceQueries {

	@Value("${rules.http.port}")
	private Integer httpPort;

	@Value("${rules.server.port}")
	private Integer serverPort;

	@Value("${rules.server.context}")
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
	protected String service() {
		return "service";
	}

	@Override
	protected String version() {
		return "v1";
	}

	@Override
	public List<ActionResult> executeRulesEngine(String engineId, Map<String, ?> facts) {

		ActionResult[] results = (ActionResult[])retrieveObject(executeRulesEngineBuilder(engineId), 
				HttpMethod.POST, 
				executeRulesEngineEntity(facts),
				ActionResult[].class);
		if(results == null)
			return new ArrayList<>();
		return Arrays.asList(results);
	}
	
	@Override
	public List<FamilyDTO> findColleges(String status) {
		FamilyDTO[] objects = (FamilyDTO[]) retrieveObject(requestCollegesByStatusBuilder(status),
				FamilyDTO[].class);
		if (objects == null) {
			return null;
		}
		return Arrays.asList(objects);
	}



	public List<RuleSetDTO> getLogics(String miscode, String competencyMapDiscipline) {
		RuleSetDTO[] objects = (RuleSetDTO[]) retrieveObject(getLogicsBuilder(miscode, competencyMapDiscipline),
				RuleSetDTO[].class);
		if (objects == null) {
			return null;
		}
		return Arrays.asList(objects);
	}

	public RuleSetDTO findByRuleSetId(String ruleSetId) {
		return (RuleSetDTO) retrieveObject(getRuleSetBuilder(ruleSetId), RuleSetDTO.class);

	}
	
	UriComponentsBuilder executeRulesEngineBuilder(String engineId) {
		return endpointBuilder("rules-engine","oauth2", engineId);
	}

	@SuppressWarnings("unchecked")
	HttpEntity<Map<String, ?>> executeRulesEngineEntity(Map<String, ?> facts) {
		return (HttpEntity<Map<String, ?>>) buildHttpEntity(facts);
	}

	UriComponentsBuilder getRuleSetBuilder(String ruleSetId) {
		return endpointBuilder("ruleset", ruleSetId);
	}

	UriComponentsBuilder getLogicsBuilder(String miscode, String competencyMapDiscipline) {
		return endpointBuilder("ruleset", "miscode", miscode, "competency", competencyMapDiscipline);
	}

	public void requestOnBoardCollege(String cccMisCode, String description) {
		update(requestOnBoardCollegeBuilder(cccMisCode, description), HttpMethod.PUT, null);
	}

	UriComponentsBuilder requestOnBoardCollegeBuilder(String cccMisCode, String description) {
		try {
			if (description == null)
				description = "College " + cccMisCode;
			return endpointBuilder("onboard", "oauth2", cccMisCode).queryParam("description",
					URLEncoder.encode(description, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unable to encode description: " + description.toString());
		}
	}

	UriComponentsBuilder requestCollegesByStatusBuilder(String status) {
		return endpointBuilder("colleges").queryParam("status", status);
	}
	
	@Override
	public List<RestClientTestResult> validateEndpoints() {
		String miscode = "ZZ1";
		String competencyMapDiscipline = "ENGLISH";
		String description = "College of The Valley";
		Map<String, ?> facts = new HashMap<>();
		List<RestClientTestResult> results = super.validateEndpoints();
		results.add(testRetrieveList("executeRulesEngine", executeRulesEngineBuilder("mmppPlacementRules"), HttpMethod.POST,
				executeRulesEngineEntity(facts)));
		results.add(testRetrieveObject("getLogics", getLogicsBuilder(miscode, competencyMapDiscipline),
				RuleSetDTO[].class));
		results.add(testUpdate("requestOnBoardCollege", requestOnBoardCollegeBuilder("ZZ20", description),
				HttpMethod.PUT, null));
		results.add(testRetrieveObject("findColleges", requestCollegesByStatusBuilder("active"),
				HttpMethod.GET, null, FamilyDTO[].class));
		return results;
	}

	

	
}
