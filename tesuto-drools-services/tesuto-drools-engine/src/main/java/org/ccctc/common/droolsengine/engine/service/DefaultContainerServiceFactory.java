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

import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.engine.IContainerConfiguratorFactory;
import org.ccctc.common.droolsengine.config.family.IFamilyConfigurator;
import org.ccctc.common.droolsengine.config.family.IFamilyConfiguratorFactory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class DefaultContainerServiceFactory implements IContainerServiceFactory {

    @Autowired
    private IFamilyConfiguratorFactory familyConfiguratorFactory;

    @Autowired
    private IContainerConfiguratorFactory containerConfiguratorFactory;

    @Autowired
    private DroolsEngineEnvironmentConfiguration config;

    @Override
    public IContainerService getContainerService(String name) {
        IFamilyConfigurator familyConfigurator = familyConfiguratorFactory.getConfigurator();
        IContainerService containerService = null;
        if (DroolsEngineEnvironmentConfiguration.CONTAINER_SERVICE_TYPE_POLLING.equals(config.getContainerServiceType())) {
            containerService = getPollingContainerService(familyConfigurator, containerConfiguratorFactory, config, name);
        } else {
            containerService = new DefaultContainerService(familyConfigurator, containerConfiguratorFactory, config, name);
        }
        log.debug("returning containerService:[" + containerService.getClass() + "] for application[" + name + "]");
        return containerService;
    }

    protected IContainerService getPollingContainerService(IFamilyConfigurator familyConfigurator, IContainerConfiguratorFactory containerConfiguratorFactory,
                                                           DroolsEngineEnvironmentConfiguration envConfig, String name) {
        return new PollingContainerService(familyConfigurator, containerConfiguratorFactory, config, name);
    }
    
}
