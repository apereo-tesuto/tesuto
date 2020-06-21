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

import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.family.AbstractFamilyConfigurator;
import org.ccctc.common.droolsengine.config.family.IFamilyConfiguratorFactory;
import org.ccctc.common.droolsengine.config.family.impl.FamilyConfiguratorFactoryImpl;
import org.ccctc.common.droolsengine.config.family.impl.FamilyConfiguratorFromEditor;
import org.ccctc.common.droolsengine.config.family.impl.FamilyConfiguratorFromEnvironment;
import org.ccctc.common.droolsengine.config.family.impl.FamilyConfiguratorFromFile;
import org.ccctc.common.droolsengine.utils.DroolsResponseErrorHandler;
import org.ccctc.common.droolsengine.utils.FactsUtils;
import org.ccctc.common.droolsengine.utils.SecurityUtils;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class TestConfigurationEngineDisabled {
    @Bean
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
    
    @Bean
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DroolsResponseErrorHandler());
        return restTemplate;
    }
    
    @Bean
    PropertyPlaceholderConfigurer propConfig() {
        PropertyPlaceholderConfigurer placeholderConfigurer = new PropertyPlaceholderConfigurer();
        placeholderConfigurer.setLocation(new ClassPathResource("application-test-enginedisabled.properties"));
        return placeholderConfigurer;
    }
    
    @Bean
    public IFamilyConfiguratorFactory getConfigurationReaderFactory() {
        return new FamilyConfiguratorFactoryImpl();
    }
    
    @Bean
    public DroolsEngineEnvironmentConfiguration getDroolsEngineEnvironmentConfiguration() {
        DroolsEngineEnvironmentConfiguration config = new DroolsEngineEnvironmentConfiguration();
        return config;
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
}
