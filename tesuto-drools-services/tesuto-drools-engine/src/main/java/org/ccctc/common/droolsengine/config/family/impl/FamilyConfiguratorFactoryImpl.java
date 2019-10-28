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
package org.ccctc.common.droolsengine.config.family.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.family.IFamilyConfigurator;
import org.ccctc.common.droolsengine.config.family.IFamilyConfiguratorFactory;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Factory for creating family readers built from the configuration.
 * Set the property: family.source
 * Valid values are: editor | file | environment
 * 
 * Additional properties are needed depending on the choice of configuration
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class FamilyConfiguratorFactoryImpl implements IFamilyConfiguratorFactory {
    @Autowired
    private List<IFamilyConfigurator> familyReaders;

    @Autowired
    private DroolsEngineEnvironmentConfiguration config;

    private final Map<String, IFamilyConfigurator> familyReadersByName = new HashMap<>();

    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        for (IFamilyConfigurator reader : familyReaders) {
            familyReadersByName.put(reader.getName(), reader);
        }
        log.debug("The following FamilyReaders are available:" + familyReadersByName.keySet());
    }

    public IFamilyConfigurator getConfigurator() {
        String source = config.getFamilySource();
        log.debug("familySource:[" + source + "]");
        IFamilyConfigurator familyConfigurator = this.familyReadersByName.get(source);
        if (familyConfigurator == null) {
            throw new RuntimeException("Could not retrieve Family configurator [" + source + "]");
        }
        log.debug("Returning Family Configurator [" + familyConfigurator.getClass() + "]");
        return familyConfigurator;
    }

    @Override
    public void setDroolsEngineEnvironmentConfiguration(DroolsEngineEnvironmentConfiguration config) {
        this.config = config;
    }
}
