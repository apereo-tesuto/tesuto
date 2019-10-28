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
package org.ccctc.common.droolsengine.engine.service;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.TestingConfiguration;
import org.ccctc.common.droolsengine.engine_actions.service.IActionService;
import org.ccctc.common.droolsengine.engine_actions.service.NoOpActionService;
import org.ccctc.common.droolsengine.facts.StudentProfileFacade;
import org.ccctc.common.droolsengine.facts.StudentProfileFactsPreProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { TestingConfiguration.class, StudentProfileFacade.class,
                NoOpActionService.class, StudentProfileFactsPreProcessor.class })
@TestPropertySource("classpath:application-test.properties")
public class DroolsRulesServiceUnitTest {
    private DroolsEngineEnvironmentConfiguration config;
    
    @Autowired
    private IContainerServiceFactory containerServiceFactory;

    private DroolsRulesService service;

    @Test
    public void initStatusDisabled() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("DROOLS_ENGINE_STATUS", "DISABLED");
        config.setEnvironment(env);
        service.setConfiguration(config);
        service.init();
        assertEquals(DroolsEngineStatus.DISABLED, service.getStatus());
    }

    @Test
    public void initStatusEnabled() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty("DROOLS_ENGINE_STATUS", "ENABLED");
        env.setProperty("DROOLS_RULES_MISCODE_COUNT", "1");
        env.setProperty("DROOLS_RULES_MISCODE_1", "ZZ1");
        env.setProperty("DROOLS_RULES_GROUPID_ZZ1", "org.jasig.portlet.rules");
        env.setProperty("DROOLS_RULES_ARTIFACTID_ZZ1", "drools-rules-survey-portlet");
        env.setProperty("DROOLS_RULES_VERSIONID_ZZ1", "0.0.1");
        config.setEnvironment(env);
        IContainerService containerService = containerServiceFactory.getContainerService(config.getDroolsRulesApplication());
        service.setContainerService(containerService);
        service.setConfiguration(config);
        service.init();
        assertEquals(DroolsEngineStatus.ENABLED, service.getStatus());
    }

    @Before
    public void setUp() {
        service = new DroolsRulesService();
        service.setName("sns-listener");
        service.setAvailableActions(new HashMap<String, IActionService>());
        config = new DroolsEngineEnvironmentConfiguration();
        service.setConfiguration(config);
    }
}
