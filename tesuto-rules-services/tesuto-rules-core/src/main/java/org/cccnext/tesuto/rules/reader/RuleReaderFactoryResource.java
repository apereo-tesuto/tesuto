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


import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.engine.IEngineFactory;
import org.ccctc.common.droolsengine.config.engine.impl.ContainerConfiguratorFactoryImpl;
import org.ccctc.common.droolsengine.config.engine.impl.EngineFactoryRulesFromMaven;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
@Primary
public class RuleReaderFactoryResource extends ContainerConfiguratorFactoryImpl {

	
	@Autowired
	EngineFactoryRulesFromMaven repositoryRuleReader;
	
	@Value("${ruleset.source:}")
	String ruleSetSource;
	
	public RuleReaderFactoryResource(RestTemplate restTemplate, DroolsEngineEnvironmentConfiguration config) {
		super(restTemplate, config);
	}
	
	@Override
	public IEngineFactory getEngineFactory(FamilyDTO collegeDTO, String application) {
        log.debug("Retrieving ruleReader for collegeDTO:[" + collegeDTO.getFamilyCode() + "], application:[" + application + "]");
        EngineDTO applicationDTO = collegeDTO.getEngineDTO(application);
        
        String dataSource = "";
        if (applicationDTO != null) {
            dataSource = applicationDTO.getDataSource();
        } else {
        	dataSource = ruleSetSource;
        }
        
        if ("repository".equals(dataSource)) {
            return repositoryRuleReader;
        } else {
            return super.getEngineFactory(collegeDTO, application);    
        }
    }

}
