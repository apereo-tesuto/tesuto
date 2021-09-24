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

import java.util.Arrays;
import java.util.List;

import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDisciplineDto;
import org.cccnext.tesuto.content.service.CompetencyMapDisciplineReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CompetencyMapDisciplineRestClient extends BaseRestClient<CompetencyMapDisciplineDto> implements CompetencyMapDisciplineReader {


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
		return "competency-map-discipline";
	}

	@Override
	protected String context() {
		return context;
	}

    @Override
    public CompetencyMapDisciplineDto read(String competencyMapDiscipline) {
       return retrieve(endpointBuilder(competencyMapDiscipline));
    }
    
    @Override
    public List<CompetencyMapDisciplineDto> read() {
    	CompetencyMapDisciplineDto[] dtos = (CompetencyMapDisciplineDto[])retrieveObject(endpointBuilder(), CompetencyMapDisciplineDto[].class);
    	return Arrays.asList(dtos);
    }
    
    @Override
	public List<RestClientTestResult> validateEndpoints() {
		String competencyMapDiscipline = "MATH";

		List<RestClientTestResult> results = super.validateEndpoints();
		results.add(testRetrieveObject("readList",endpointBuilder(), CompetencyMapDisciplineDto[].class));
		results.add(testRetrieve("read",endpointBuilder(competencyMapDiscipline)));
		return results;
	}

}
