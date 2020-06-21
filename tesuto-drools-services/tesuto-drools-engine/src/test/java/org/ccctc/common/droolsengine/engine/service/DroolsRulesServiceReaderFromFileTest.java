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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.TestingConfiguration;
import org.ccctc.common.droolsengine.config.service.impl.DroolsRulesServiceFactoryFromFile;
import org.ccctc.common.droolsengine.dto.EngineConfigDTO;
import org.ccctc.common.droolsengine.engine_actions.service.IActionService;
import org.ccctc.common.droolsengine.engine_actions.service.NoOpActionService;
import org.ccctc.common.droolsengine.engine_actions.service.messaging.EmailFacade;
import org.ccctc.common.droolsengine.facts.IFactsPreProcessor;
import org.ccctc.common.droolsengine.facts.NoOpPreProcessor;
import org.ccctc.common.droolsengine.facts.StudentProfileFacade;
import org.ccctc.common.droolsengine.facts.StudentProfileFactsPreProcessor;
import org.ccctc.common.droolsengine.utils.TestValidatorFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { TestingConfiguration.class, StudentProfileFacade.class,
                NoOpActionService.class, StudentProfileFactsPreProcessor.class })
@TestPropertySource("classpath:application-test.properties")
@Configuration
public class DroolsRulesServiceReaderFromFileTest {
    private static final IActionService DEFAULT_IACTION_SERVICE_1 = new EmailFacade();

    private static final IFactsPreProcessor DEFAULT_IFACTS_PREPROCESSOR1 = new NoOpPreProcessor();

    @Autowired
    private DroolsEngineEnvironmentConfiguration config;

    @Autowired
    private IContainerServiceFactory containerServiceFactory;

    private MockEnvironment env;

    @Autowired
    private DroolsRulesServiceFactoryFromFile reader;

    private Map<String, IActionService> buildDefaultActionServices() {
        Map<String, IActionService> availableActions = new HashMap<>();
        availableActions.put(DEFAULT_IACTION_SERVICE_1.getName(), DEFAULT_IACTION_SERVICE_1);
        return availableActions;
    }

    private DroolsRulesServiceFactoryFromFile buildDefaultReader() {
        DroolsRulesServiceFactoryFromFile reader = new DroolsRulesServiceFactoryFromFile();
        reader.setPreProcessorFactory(new TestValidatorFactory());
        reader.setContainerServiceFactory(containerServiceFactory);
        env.setProperty(DroolsEngineEnvironmentConfiguration.SERVICE_SUPPORT_SOURCE_FILEPATH_KEY, "./src/main/resources");
        reader.setConfiguration(config);
        reader.setAvailableActions(buildDefaultActionServices());
        return reader;
    }

    @Test
    public void getServiceFoundInEngineSourceFile() {
        env.setProperty(DroolsEngineEnvironmentConfiguration.SERVICE_SUPPORT_SOURCE_FILEPATH_KEY, "./src/main/resources");
        IDroolsRulesService service = reader.getDroolsRulesService("applySubmit");
        assertTrue(service != null);
        assertEquals("applySubmit", service.getName());
    }

    @Test
    public void getServiceIfMissingSourceFile() {
        String SERVICE_NAME = "missingName";
        env.setProperty(DroolsEngineEnvironmentConfiguration.SERVICE_SUPPORT_SOURCE_FILEPATH_KEY, "./src/main/resources");
        env.setProperty(DroolsEngineEnvironmentConfiguration.SERVICE_SUPPORT_SOURCE_FILENAME_KEY, "notFound.json");
        try {
            reader.init(new ArrayList<String>());
        }
        catch (Exception e) {
            fail("should not throw exception [" + e + "]");
        }

        IDroolsRulesService service = reader.getDroolsRulesService(SERVICE_NAME);
        assertTrue(service != null);
        assertEquals(SERVICE_NAME, service.getName());
    }

    @Test
    public void getServiceNotFoundInSourceFile() {
        String SERVICE_NAME = "missingName";
        reader.setAvailableActions(new HashMap<>());

        env.setProperty(DroolsEngineEnvironmentConfiguration.SERVICE_SUPPORT_SOURCE_FILEPATH_KEY, "./src/main/resources");
        reader.setConfiguration(config);

        reader.setContainerServiceFactory(containerServiceFactory);
        try {
            reader.init(new ArrayList<String>());
        }
        catch (Exception e) {
            fail("should not throw exception [" + e + "]");
        }

        IDroolsRulesService service = reader.getDroolsRulesService(SERVICE_NAME);
        assertTrue(service != null);
        assertEquals(SERVICE_NAME, service.getName());
    }

    @Before
    public void setup() {
        env = new MockEnvironment();
        config.setEnvironment(env);
    }

    @Test
    public void subsetActionsEmptyLists() {
        // In this test, the factory has a list of one available action.
        // In addition, the engine config has nothing in the enabled lists.
        // Therefore, it should return the list of availableActions.
        DroolsRulesServiceFactoryFromFile reader = buildDefaultReader();

        EngineConfigDTO engine = new EngineConfigDTO();
        Map<String, IActionService> services = reader.configureActions(engine);

        assertEquals(1, services.size());
        String serviceName = DEFAULT_IACTION_SERVICE_1.getName();
        IActionService service = services.get(serviceName);
        assertTrue(service != null);
    }

    @Test
    public void subsetActionsHasMatchingEnabledListEntry() {
        // In this test, the Factory has a list of one available action.
        // In addition, the engine config says that this action is available.
        // Therefore, the list of available actions will include the available action
        DroolsRulesServiceFactoryFromFile reader = buildDefaultReader();

        EngineConfigDTO engine = new EngineConfigDTO();
        List<String> enabledActionServices = new ArrayList<>();
        enabledActionServices.add(DEFAULT_IACTION_SERVICE_1.getName());
        engine.setEnabledActionServices(enabledActionServices);
        Map<String, IActionService> services = reader.configureActions(engine);

        assertEquals(1, services.size());
        IActionService service = services.get(DEFAULT_IACTION_SERVICE_1.getName());
        assertTrue(service != null);
    }

    @Test
    public void subsetActionsHasUnmatchedEnabledListEntry() {
        // In this test, the Factory has a list of one available action
        // However, the engine config says that only "MISSING_ACTION" is allowed.
        // This means that not all actions will be returned, only those that match
        // will be returned. Since there are no matches, the returned list is empty.
        DroolsRulesServiceFactoryFromFile reader = buildDefaultReader();

        List<String> enabledActionServices = new ArrayList<>();
        enabledActionServices.add("MISSING_ACTION");

        EngineConfigDTO engine = new EngineConfigDTO();
        engine.setEnabledActionServices(enabledActionServices);
        Map<String, IActionService> services = reader.configureActions(engine);

        assertEquals(0, services.size());
    }

    @Test
    public void subsetValidatorsHasMatchingEnabledListEntry() {
        // In this test, the Factory has a list of one available preProcessor.
        // In addition, the engine config says that this preProcessor is available.
        // Therefore, the list of available preProcessor will include the available preProcessor
        DroolsRulesServiceFactoryFromFile reader = buildDefaultReader();

        EngineConfigDTO engine = new EngineConfigDTO();
        List<String> enabledFactsValidators = new ArrayList<>();
        enabledFactsValidators.add(DEFAULT_IFACTS_PREPROCESSOR1.getName());
        engine.setEnabledPreProcessors(enabledFactsValidators);
        List<IFactsPreProcessor> validators = reader.configurePreProcessors(engine, new ArrayList<String>());

        assertEquals(1, validators.size());
        IFactsPreProcessor validator = validators.get(0);
        assertEquals(validator.getName(), DEFAULT_IFACTS_PREPROCESSOR1.getName());
    }

    @Test
    public void testFactsPreProcessorHasUnmatchedEnabledListEntry() {
        // In this test, the Factory has a list of one available preProcessor.
        // However, the engine config says that only "MISSING_PREPROCESSOR" is allowed.
        // This means that no IFactsPreProcessor will be returned, only those that match
        // should be returned. Since there are no matches, the returned list is empty.
        DroolsRulesServiceFactoryFromFile reader = buildDefaultReader();

        EngineConfigDTO engine = new EngineConfigDTO();
        List<String> enabledPreProcessors = new ArrayList<>();
        enabledPreProcessors.add("MISSING_PREPROCESSOR");
        engine.setEnabledPreProcessors(enabledPreProcessors);

        List<IFactsPreProcessor> preProcessors = reader.configurePreProcessors(engine, new ArrayList<String>());
        assertEquals(0, preProcessors.size());
    }

    @Test
    public void testFactsPreProcessorNoConfiguration() {
        // In this test, the factory has a list of one available PreProcessor.
        // The engine config has nothing in the enabled lists.
        // Therefore, it should return no list of availableActions.
        DroolsRulesServiceFactoryFromFile reader = buildDefaultReader();

        EngineConfigDTO engine = new EngineConfigDTO();
        List<IFactsPreProcessor> preProcessors = reader.configurePreProcessors(engine, new ArrayList<String>());

        assertEquals(0, preProcessors.size());
    }
}
