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

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.engine.IEngineFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.client.RestTemplate;

public class ContainerConfiguratorFactoryImplTest {
    private static final String DEFAULT_APPLICATION_NAME = "sns-listener";
    private static final String DEFAULT_CCC_MIS_CODE = "ZZ1";

    private ContainerConfiguratorFactoryImpl factory;

    @Test
    public void editorDatasource() {
        EngineDTO engineDTO = new EngineDTO().setDataSource(DroolsEngineEnvironmentConfiguration.RULES_SOURCE_EDITOR)
                                             .setName(DEFAULT_APPLICATION_NAME);
        Map<String, EngineDTO> engines = new HashMap<String, EngineDTO>();
        engines.put(DEFAULT_APPLICATION_NAME, engineDTO);
        FamilyDTO collegeDTO = new FamilyDTO().setEngines(engines).setFamilyCode(DEFAULT_CCC_MIS_CODE).setStatus("versioned");

        IEngineFactory reader = factory.getEngineFactory(collegeDTO);
        assertTrue(reader instanceof EngineFactoryRulesFromEditor);
    }

    @Test
    public void mavenDatasource() {
        EngineDTO engineDTO = new EngineDTO().setDataSource("maven").setName(DEFAULT_APPLICATION_NAME);
        Map<String, EngineDTO> engines = new HashMap<String, EngineDTO>();
        engines.put(DEFAULT_APPLICATION_NAME, engineDTO);
        FamilyDTO collegeDTO = new FamilyDTO().setEngines(engines).setFamilyCode(DEFAULT_CCC_MIS_CODE).setStatus("versioned");

        IEngineFactory reader = factory.getEngineFactory(collegeDTO);
        assertTrue(reader instanceof EngineFactoryRulesFromMaven);
    }

    @Before
    public void setup() {
        MockEnvironment env = new MockEnvironment();
        DroolsEngineEnvironmentConfiguration config = new DroolsEngineEnvironmentConfiguration();
        config.setEnvironment(env);
        RestTemplate restTemplate = new RestTemplate();
        factory = new ContainerConfiguratorFactoryImpl(restTemplate, config);
    }
}
