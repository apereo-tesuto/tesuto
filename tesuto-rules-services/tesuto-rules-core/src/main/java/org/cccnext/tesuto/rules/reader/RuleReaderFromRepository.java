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
package org.cccnext.tesuto.rules.reader;

import java.util.List;

import org.ccctc.common.droolscommon.model.RuleSetDTO;
import org.ccctc.common.droolsdb.services.DrlService;
import org.ccctc.common.droolsdb.services.RuleSetService;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.engine.impl.EngineFactoryRulesFromEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class RuleReaderFromRepository extends EngineFactoryRulesFromEditor {

	@Autowired
	private RuleSetService ruleSetService;

	@Value("${default.engine.application:}")
	private String application;

	@Autowired
	private DrlService dirService;

	public RuleReaderFromRepository(DroolsEngineEnvironmentConfiguration config, RestTemplate restTemplate) {
		super(config, restTemplate, "");
	}

	public List<RuleSetDTO> getRuleSetDTOs(String cccMisCode) {
		return ruleSetService.getRuleSetsByEngineAndFamily(application, cccMisCode);
	}

	public List<String> getDrlsFromRuleSetId(String ruleSetId) {
		return dirService.generateDrls(ruleSetId);
	}

}
