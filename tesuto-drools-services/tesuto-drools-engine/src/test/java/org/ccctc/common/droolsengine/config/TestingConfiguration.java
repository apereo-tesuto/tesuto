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
package org.ccctc.common.droolsengine.config;

import org.ccctc.common.droolsengine.engine.service.IContainerService;
import org.ccctc.common.droolscommon.KieContainerFactory;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.engine.IContainerConfiguratorFactory;
import org.ccctc.common.droolsengine.config.engine.impl.ContainerConfiguratorFactoryImpl;
import org.ccctc.common.droolsengine.config.family.AbstractFamilyConfigurator;
import org.ccctc.common.droolsengine.config.family.IFamilyConfiguratorFactory;
import org.ccctc.common.droolsengine.config.family.impl.FamilyConfiguratorFactoryImpl;
import org.ccctc.common.droolsengine.config.family.impl.FamilyConfiguratorFromEditor;
import org.ccctc.common.droolsengine.config.family.impl.FamilyConfiguratorFromEnvironment;
import org.ccctc.common.droolsengine.config.family.impl.FamilyConfiguratorFromFile;
import org.ccctc.common.droolsengine.config.service.IServiceConfiguratorFactory;
import org.ccctc.common.droolsengine.config.service.impl.DroolsRulesServiceFactoryFromFile;
import org.ccctc.common.droolsengine.config.service.impl.ServiceConfiguratorFactoryImpl;
import org.ccctc.common.droolsengine.engine.service.DefaultContainerServiceFactory;
import org.ccctc.common.droolsengine.engine.service.DroolsRulesService;
import org.ccctc.common.droolsengine.engine_actions.service.NoOpActionService;
import org.ccctc.common.droolsengine.facts.IPreProcessorFactory;
import org.ccctc.common.droolsengine.facts.NoOpPreProcessor;
import org.ccctc.common.droolsengine.utils.DroolsResponseErrorHandler;
import org.ccctc.common.droolsengine.utils.FactsUtils;
import org.ccctc.common.droolsengine.utils.SecurityUtils;
import org.ccctc.common.droolsengine.utils.TestContainerServiceFactory;
import org.ccctc.common.droolsengine.utils.TestValidatorFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.env.MockEnvironment;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.client.RestTemplate;

@Configuration
@PropertySource("application-test.properties")
public class TestingConfiguration {
    @Bean
    public IPreProcessorFactory getValidatorFactory() {
        return new TestValidatorFactory();
    }
    
    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public PropertyPlaceholderConfigurer propConfig() {
        PropertyPlaceholderConfigurer placeholderConfigurer = new PropertyPlaceholderConfigurer();
        placeholderConfigurer.setLocation(new ClassPathResource("application-test.properties"));
        return placeholderConfigurer;
    }

    @Bean
    public RestTemplate getRestTemplate() {
        final RestTemplate rt = new RestTemplate();
        rt.setErrorHandler(new DroolsResponseErrorHandler());
        return rt;
    }

    @Bean
    public DroolsEngineEnvironmentConfiguration getDroolsEngineEnvironmentConfiguration() {
        DroolsEngineEnvironmentConfiguration config = new DroolsEngineEnvironmentConfiguration();
        return config;
    }

    @Bean
    public IContainerConfiguratorFactory getRuleReader() {
        ContainerConfiguratorFactoryImpl ruleReaderFactory = new ContainerConfiguratorFactoryImpl(getRestTemplate(), getDroolsEngineEnvironmentConfiguration());
        return ruleReaderFactory;
    }

    @Bean
    public IFamilyConfiguratorFactory getConfigurationReaderFactory() {
        return new FamilyConfiguratorFactoryImpl();
    }

    @Bean
    public IServiceConfiguratorFactory getEngineReaderFactory() {
        return new ServiceConfiguratorFactoryImpl();
    }

    @Bean
    public KieContainerFactory getKieContainerFactory() {
        return new KieContainerFactory();
    }

    @Bean
    public FactsUtils getFactsUtils() {
        return new FactsUtils();
    }

    @Bean
    public SecurityUtils getSecurityUtils() {
        return new SecurityUtils();
    }

    @Bean
    public DroolsRulesServiceFactoryFromFile getEngineReaderFromFile() {
        return new DroolsRulesServiceFactoryFromFile();
    }

    @Bean
    public AbstractFamilyConfigurator getCollegeReaderFromEditor() {
        return new FamilyConfiguratorFromEditor();
    }

    @Bean
    public FamilyConfiguratorFromFile getCollegeReaderFromFile() {
        return new FamilyConfiguratorFromFile();
    }

    @Bean
    public FamilyConfiguratorFromEnvironment getCollegeReaderFromEnvironment() {
        return new FamilyConfiguratorFromEnvironment();
    }

    @Bean
    public DefaultContainerServiceFactory getContainerServiceFactory() {
        return new TestContainerServiceFactory();
    }

    @Bean
    public DroolsRulesService droolsRulesService() {
        MockEnvironment env = new MockEnvironment();
        env.setProperty(DroolsEngineEnvironmentConfiguration.MISCODE_COUNT_KEY, "1");
        env.setProperty(DroolsEngineEnvironmentConfiguration.MISCODE_PREFIX + "1", "ZZ1");
        env.setProperty(DroolsEngineEnvironmentConfiguration.MISCODE_GROUPID_KEY + "ZZ1", "org.cccmypath.rules.default");
        env.setProperty(DroolsEngineEnvironmentConfiguration.MISCODE_ARTIFACTID_KEY + "ZZ1", "drools-rules-listener-rules");
        env.setProperty(DroolsEngineEnvironmentConfiguration.MISCODE_VERSIONID_KEY + "ZZ1", "unit-test");

        DroolsEngineEnvironmentConfiguration config = new DroolsEngineEnvironmentConfiguration();
        config.setEnvironment(env);

        IContainerService containerService = getContainerServiceFactory().getContainerService(config.getDroolsRulesApplication());
        DroolsRulesService rulesService = new DroolsRulesService();
        rulesService.setContainerService(containerService);
        rulesService.setConfiguration(config);
        rulesService.init();
        return rulesService;
    }

    @Bean
    public NoOpPreProcessor noOpFactsValidator() {
        return new NoOpPreProcessor();
    }

    @Bean
    public NoOpActionService noOpActionService() {
        return new NoOpActionService();
    }
}
