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
package org.ccctc.common.droolsengine.config.engine.impl;

import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.engine.IContainerConfiguratorFactory;
import org.ccctc.common.droolsengine.config.engine.IEngineFactory;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class ContainerConfiguratorFactoryImpl implements IContainerConfiguratorFactory {
    private DroolsEngineEnvironmentConfiguration config;

    private RestTemplate restTemplate;

    public ContainerConfiguratorFactoryImpl(RestTemplate restTemplate, DroolsEngineEnvironmentConfiguration config) {
        this.config = config;
        this.restTemplate = restTemplate;
    }

    public IEngineFactory getEngineFactory(FamilyDTO familyDTO) {
        String application = config.getDroolsRulesApplication();
        return this.getEngineFactory(familyDTO, application);
    }

    @Override
    public IEngineFactory getEngineFactory(FamilyDTO familyDTO, String engineName) {
        log.debug("Retrieving EngineFactory for familyDTO:[" + familyDTO.getFamilyCode() + "], engine:[" + engineName + "]");
        EngineDTO engineDTO = familyDTO.getEngineDTO(engineName);

        String dataSource = "";
        if (engineDTO != null) {
            dataSource = engineDTO.getDataSource();
        } else {
            dataSource = DroolsEngineEnvironmentConfiguration.RULES_SOURCE_DEFAULT;
            log.warn("Application [" + engineName + "] not found for college[" + familyDTO.getFamilyCode() + "], "
                            + "Valid engines are [" + familyDTO.getEngineDTOs().keySet() + "].  "
                            + "Using default datasource:[" + dataSource + "]");
        }

        if (DroolsEngineEnvironmentConfiguration.RULES_SOURCE_EDITOR.equals(dataSource)) {
            AbstractEngineFactory reader = new EngineFactoryRulesFromEditor(config, restTemplate, engineName);
            return reader;
        } else {
            AbstractEngineFactory reader = new EngineFactoryRulesFromMaven(config, engineName);
            return reader;
        }
    }

}
