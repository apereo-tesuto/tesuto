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
package org.ccctc.common.droolsengine.config.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.service.IDroolsRulesServiceFactory;
import org.ccctc.common.droolsengine.config.service.IServiceConfiguratorFactory;
import org.ccctc.common.droolsengine.engine.service.DroolsRulesService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Build a IDroolsRulesServiceFactory from the env config.
 * @see org.ccctc.common.droolsengine.config.service.IServiceConfiguratorFactory
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class ServiceConfiguratorFactoryImpl implements IServiceConfiguratorFactory {
    private final Map<String, DroolsRulesService> droolsRulesServices = new HashMap<>();

    @Autowired
    private DroolsEngineEnvironmentConfiguration envConfig;


    private final Map<String, IDroolsRulesServiceFactory> serviceFactories = new HashMap<>();

    @Autowired
    private DroolsRulesServiceFactoryFromFile serviceFactory;

    public IDroolsRulesServiceFactory getDroolsRulesServiceFactory() {
        IDroolsRulesServiceFactory factory = serviceFactories.get(envConfig.getEngineSource());
        if (factory != null) {
            log.debug("Found service factory [" + envConfig.getEngineSource() + "], returning :[" + factory.getClass() + "]");
            return factory;
        }
        // The default (and only way to configure engines currently is from a file
        DroolsRulesServiceFactoryFromFile serviceFactory = this.serviceFactory;
        serviceFactories.put(envConfig.getEngineSource(), serviceFactory);
        factory = serviceFactory;
        log.debug("returning service factory:[" + factory.getClass() + "]");
        return factory;
    }

    public Map<String, DroolsRulesService> getDroolsRulesServices() {
        return droolsRulesServices;
    }

    public void setConfiguration(DroolsEngineEnvironmentConfiguration config) {
        this.envConfig = config;
    }

}
