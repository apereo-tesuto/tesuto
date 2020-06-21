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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.service.IDroolsRulesServiceFactory;
import org.ccctc.common.droolsengine.dto.EngineConfigDTO;
import org.ccctc.common.droolsengine.engine.service.IContainerServiceFactory;
import org.ccctc.common.droolsengine.engine.service.DroolsRulesService;
import org.ccctc.common.droolsengine.engine.service.IContainerService;
import org.ccctc.common.droolsengine.engine.service.IDroolsRulesService;
import org.ccctc.common.droolsengine.engine_actions.service.IActionService;
import org.ccctc.common.droolsengine.facts.IFactsPreProcessor;
import org.ccctc.common.droolsengine.facts.IPreProcessorFactory;
import org.ccctc.common.droolsengine.utils.SecurityUtils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Reads rulesServices.json file (reads the file name and path from the DroolsEngineEnvironmentConfiguration) in order to configure the
 * rules engines, their pre-processors and the valid actions that can be taken by the rules.
 * Logging prefixed with "--"
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class DroolsRulesServiceFactoryFromFile implements IDroolsRulesServiceFactory {
    @Autowired
    private Map<String, IActionService> availableActions;

    @Autowired
    private IContainerServiceFactory containerServiceFactory;
    
    private Map<String, DroolsRulesService> droolsRulesServices;
    
    @Autowired
    private DroolsEngineEnvironmentConfiguration envConfig;
    
    @Autowired
    private IPreProcessorFactory preProcessorFactory;
    
    @Autowired
    private SecurityUtils securityUtils;

    private DroolsRulesService buildDroolsRulesService(EngineConfigDTO engineConfig, ArrayList<String> logMessages) {
        IContainerService containerService = containerServiceFactory.getContainerService(engineConfig.getId());
        logMessages.add("-- START buildDroolsRulesService(): Service for engine:[" + engineConfig.toString() + "] [" + containerService.getClass() + "]");
        DroolsRulesService service = new DroolsRulesService();
        service.setName(engineConfig.getId());
        service.setContainerService(containerService);
        service.setConfiguration(envConfig);
        service.setSecurityUtils(securityUtils);
        service.setAvailableActions(configureActions(engineConfig));
        service.setFactPreProcessors(configurePreProcessors(engineConfig, logMessages));
        service.init();
        droolsRulesServices.put(engineConfig.getId(), service);
        logMessages.add("-- END buildDroolsRulesService(): Service that has just been built:[" + service.getName() + "]");
        return service;
    }
    
    /**
     * configureActions(...) returns a subset of all available IActionServices.  The default is to make ALL actions available.
     * If the engine configuration has entries in its availableActions list, only those named services will be part of the list 
     * (again, an empty list defaults to ALL). If "ALL" is included in the list, all actions will be used.
     * @return a Map<String, IActionService> of IActionServices that are available to this rules engine.  The
     * list will never be null.
     */
    public Map<String, IActionService> configureActions(EngineConfigDTO engine) {
        List<String> configuredActions = engine.getEnabledActionServices() == null ? new ArrayList<>()
                                                                                   : engine.getEnabledActionServices();
        final Map<String, IActionService> results = new HashMap<>();

        if (configuredActions.contains("ALL") || configuredActions.isEmpty()) {
            results.putAll(availableActions);
        } else {
            configuredActions.forEach(actionName -> {
                if (availableActions.containsKey(actionName)) {
                    results.put(actionName, availableActions.get(actionName));
                }
            });
        }

        log.debug("-- DroolsRulesService [" + engine.getId() + "] has [" + results.size() + "] actions enabled");
        return results;
    }
    
    /**
     * By default, no PreProcessors are configured. If "ALL" is part of the configuration, all available preprocessors will be used.
     * @param engine
     * @return List<IFactsPreProcessor> that are available to this engine. List will never be null, but it can be empty.
     */
    public List<IFactsPreProcessor> configurePreProcessors(EngineConfigDTO engine, ArrayList<String> logMessages) {
        // Though perhaps not the most efficent way to configure, there are not expected to be a large number of
        // PreProcessors, so the code here is written for understandibility
        
        logMessages.add("---- START configurePreProcessors(): engine=" + engine.toString());
        final List<String> configuredNames = engine.getEnabledPreProcessors() == null ? new ArrayList<>()
                                                                                      : engine.getEnabledPreProcessors();
        
        List<IFactsPreProcessor> results = new ArrayList<>();
        List<IFactsPreProcessor> availablePreProcessors = preProcessorFactory.getFactPreProcessors();

        if (configuredNames.contains("ALL")) {
            results.addAll(availablePreProcessors);
        } else {
            availablePreProcessors.forEach(preProcessor -> {
                if (configuredNames.contains(preProcessor.getName())) {
                    results.add(preProcessor);
                }
            });
        }
        
        logMessages.add("------ configuration : " + configuredNames);
        logMessages.add("------ DroolsRulesService [" + engine.getId() + "] has [" + results.size() + "] PreProcessors enabled");
        logMessages.add("---- END configurePreProcessors()");
        return results;
    }
    
    @Override
    public IDroolsRulesService getDroolsRulesService(String engineId) {
        ArrayList<String> logMessages = new ArrayList<String>();
        logMessages.add("-- getDroolsRulesEngine(): Looking for engine: " + engineId);
        if (droolsRulesServices == null) {
            init(logMessages);
        }
        DroolsRulesService service = droolsRulesServices.get(engineId);
        if (service == null) {
            logMessages.add("---- Engine [" + engineId + "] not found in list, generating default engine.");
            EngineConfigDTO engine = new EngineConfigDTO();
            engine.setId(engineId);
            service = this.buildDroolsRulesService(engine, logMessages);
            logMessages.add("---- Generated new Engine:[" + engine.getId() + "] after initialization");
        }
        
        logMessages.forEach(message -> log.debug(message));
        return service;
    }
    
    @Override
    public List<EngineConfigDTO> getEngineConfigurations(ArrayList<String> logMessages) {
        ObjectMapper mapper = new ObjectMapper();
        String resourcePath = envConfig.getEngineSourceFilePath() + "/" + envConfig.getEngineSourceFileName();
        logMessages.add("-- START getEngineConfigurations(): Attempting to read Engine Configurations from file [" + resourcePath + "]");
        Resource resource = new FileSystemResource(resourcePath);
        List<EngineConfigDTO> engines = new ArrayList<>();
        try {
            engines = mapper.readValue(resource.getInputStream(), new TypeReference<List<EngineConfigDTO>>() {});
        } catch (JsonParseException e) {
            log.error("--- Engine Reader from File configuration [" + resourcePath + "] is invalid JSON at [" + e.getMessage() + "]");
            log.error("--- A default engine will be generated at first use" );
        } catch (JsonMappingException e) {
            log.error(e.getMessage(), e);
        } catch (IOException e) {
            log.error("--- Engine Reader from File - Configuration [" + resourcePath + "] not found, a default engine will be generated at first use");
        }
        engines.forEach(engineConfig -> logMessages.add("------ engineConfigDTO: " + engineConfig.toString()));
        logMessages.add("-- END getEngineConfigurations()");
        return engines;
    }

    public void init(ArrayList<String> logMessages) {
        logMessages.add("-- INIT START for DroolsRulesServiceFactoryFromFile --");
        droolsRulesServices = new HashMap<>();
        
        if (this.availableActions == null) {
            this.availableActions = new HashMap<>();
        }
        
        List<EngineConfigDTO> engines = getEngineConfigurations(logMessages);
        logMessages.add("-- INIT: Configurations read, building engines --");
        for (EngineConfigDTO engine : engines) {
            this.buildDroolsRulesService(engine, logMessages);
            logMessages.add("-- Generated new Engine:[" + engine.getId() + "]");
            logMessages.add("-- Engine config:" + engine.toString());
        }
        logMessages.add("-- INIT COMPLETE for DroolsRulesServiceFactoryFromFile --");
    }
    
    public void setAvailableActions(Map<String, IActionService> availableActions) {
        this.availableActions = availableActions;
    }
    
    public void setConfiguration(DroolsEngineEnvironmentConfiguration config) {
        this.envConfig = config;
    }
    
    public void setContainerServiceFactory(IContainerServiceFactory containerServiceFactory) {
        this.containerServiceFactory = containerServiceFactory;
    }
    
    public void setPreProcessorFactory(IPreProcessorFactory factory) {
        preProcessorFactory = factory;
    }
    
    public void setSecurityUtils(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }
}
