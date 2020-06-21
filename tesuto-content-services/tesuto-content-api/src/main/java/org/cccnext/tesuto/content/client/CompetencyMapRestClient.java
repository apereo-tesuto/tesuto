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

import java.util.List;

import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.service.CompetencyMapReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Service
public class CompetencyMapRestClient extends BaseRestClient<CompetencyMapDto> implements CompetencyMapReader {

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
		return "competency-maps";
	}

	@Override
	protected String context() {
		return context;
	}

    @Override
    public CompetencyMapDto readLatestPublishedVersion(String competencyDisciplineName) {
    	return retrieve(endpointBuilder(competencyDisciplineName), HttpMethod.GET,null);
    }
    
    @Override
	public List<RestClientTestResult> validateEndpoints() {
		String competencyMapDiscipline = "MATH";
		List<RestClientTestResult> results = super.validateEndpoints();
		results.add(testRetrieve("readLatestPublishedVersion",endpointBuilder(competencyMapDiscipline)));
		return results;
	}
}
