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
import java.util.UUID;

import org.cccnext.tesuto.client.BaseRestClient;
import org.cccnext.tesuto.client.dto.RestClientTestResult;
import org.cccnext.tesuto.content.service.UseItemCategoryReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class UseItemCategoryRestClient extends BaseRestClient<Boolean> implements UseItemCategoryReader {

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
		return "use-item-category";
	}

	@Override
	protected String context() {
		return context;
	}
	
	@Override
	public boolean isCategoryUsedInPlacementModelEvaluation(
			List<String> categoryNames, String namespace) {
		//throw new NotImplementedException();
		return retrieveBoolean(isCategoryUsedInPlacementModelEvaluationBuilder(namespace), HttpMethod.GET, buildHttpEntity(categoryNames),UseItemCategoryReader.BOOLEAN_TRUE);
	}
	
	UriComponentsBuilder isCategoryUsedInPlacementModelEvaluationBuilder(String namespace) {
		return endpointBuilder("placement-model", namespace);
	}

	@Override
	public boolean isCategoryUsedInBranchRuleEvaluation(
			List<String> categoryNames, String namespace) {
		//throw new NotImplementedException();
		return retrieveBoolean(isCategoryUsedInBranchRuleEvaluationBuilder(namespace), HttpMethod.GET, buildHttpEntity(categoryNames),UseItemCategoryReader.BOOLEAN_TRUE);
	}
	
	UriComponentsBuilder isCategoryUsedInBranchRuleEvaluationBuilder(String namespace) {
		return endpointBuilder("branch-rule",namespace);
	}

	@Override
	public List<RestClientTestResult> validateEndpoints() {
		String namespace = "developer";
		List<String> categoryNames = Arrays.asList(
				UUID.randomUUID().toString(),
				UUID.randomUUID().toString());

		List<RestClientTestResult> results = super.validateEndpoints();
		results.add(testRetrieveBoolean("isCategoryUsedInPlacementModelEvaluation",isCategoryUsedInPlacementModelEvaluationBuilder(namespace), HttpMethod.GET, buildHttpEntity(categoryNames),UseItemCategoryReader.BOOLEAN_TRUE));
		results.add(testRetrieveBoolean("isCategoryUsedInBranchRuleEvaluation",isCategoryUsedInBranchRuleEvaluationBuilder(namespace), HttpMethod.GET, buildHttpEntity(categoryNames),UseItemCategoryReader.BOOLEAN_TRUE));
		return results;
	}

}
