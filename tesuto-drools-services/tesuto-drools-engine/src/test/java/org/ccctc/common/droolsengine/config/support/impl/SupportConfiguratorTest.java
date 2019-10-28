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
package org.ccctc.common.droolsengine.config.support.impl;

import static org.junit.Assert.assertTrue;

import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.TestingConfiguration;
import org.ccctc.common.droolsengine.config.service.IDroolsRulesServiceFactory;
import org.ccctc.common.droolsengine.config.service.IServiceConfiguratorFactory;
import org.ccctc.common.droolsengine.config.service.impl.DroolsRulesServiceFactoryFromFile;
import org.ccctc.common.droolsengine.engine.service.DroolsRulesService;
import org.ccctc.common.droolsengine.engine.service.IDroolsRulesService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class,
    classes={TestingConfiguration.class })
public class SupportConfiguratorTest {

    @Autowired
    private IServiceConfiguratorFactory readerFactory;

    @Autowired
    private DroolsEngineEnvironmentConfiguration config;

    private MockEnvironment env;

    @Before
    public void setup() {
        env = new MockEnvironment();
        config.setEnvironment(env);
    }

    @Test
    public void defaultReader() {
        env.setProperty(DroolsEngineEnvironmentConfiguration.SERVICE_SUPPORT_SOURCE_KEY,
                        DroolsEngineEnvironmentConfiguration.SERVICE_SUPPORT_SOURCE_FILE);

        IDroolsRulesServiceFactory reader = readerFactory.getDroolsRulesServiceFactory();
        assertTrue(reader instanceof DroolsRulesServiceFactoryFromFile);

        IDroolsRulesService rulesService = reader.getDroolsRulesService("sns-listener");
        assertTrue(rulesService instanceof DroolsRulesService);

        // Don't like this test, it is testing the service, not the reader.
        // DroolsRulesService service = (DroolsRulesService) rulesService;
        // List<IFactsPreProcessor> validators = service.getFactPreProcessors();
        //
        // // Should contain no validators unless configured for them
        // assertEquals(0, validators.size());
    }

    @Test
    public void readerWithFactsValidatorExclusions() {
        env.setProperty(DroolsEngineEnvironmentConfiguration.SERVICE_SUPPORT_SOURCE_KEY,
                        DroolsEngineEnvironmentConfiguration.SERVICE_SUPPORT_SOURCE_FILE);
        env.setProperty(DroolsEngineEnvironmentConfiguration.SERVICE_SUPPORT_SOURCE_FILEPATH_KEY, "src/test/resources");
        env.setProperty(DroolsEngineEnvironmentConfiguration.SERVICE_SUPPORT_SOURCE_FILENAME_KEY, "enginesDisables.json");
        String path = config.getEngineSourceFilePath() + ":" + config.getEngineSourceFileName();
        System.out.println("path:[" + path + "]");
        IDroolsRulesServiceFactory reader = readerFactory.getDroolsRulesServiceFactory();
        assertTrue(reader instanceof DroolsRulesServiceFactoryFromFile);
        IDroolsRulesService rulesService = reader.getDroolsRulesService("applySubmit");
        assertTrue(rulesService instanceof DroolsRulesService);
        
        // Don't like this test - it is testing the service not the reader.
        // DroolsRulesService service = (DroolsRulesService) rulesService;
        // List<IFactsPreProcessor> validators = service.getFactPreProcessors();
        // // NOOP IFactsValidator should be disabled
        // assertEquals(0, validators.size());
    }
}
