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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.engine.IContainerConfiguratorFactory;
import org.ccctc.common.droolsengine.config.engine.IEngineFactory;
import org.ccctc.common.droolsengine.config.family.IFamilyConfigurator;
import org.kie.api.runtime.KieContainer;


import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class DefaultContainerService implements IContainerService {
    private IFamilyConfigurator collegeReader;

    private DroolsEngineEnvironmentConfiguration config;
    
    private final Map<String, KieContainer> kContainers = new HashMap<String, KieContainer>();
        
    private String name;

    private IContainerConfiguratorFactory ruleReaderFactory;

    public DefaultContainerService(IFamilyConfigurator collegeReader, IContainerConfiguratorFactory ruleReaderFactory,
                    DroolsEngineEnvironmentConfiguration config, String name) {
        this.collegeReader = collegeReader;
        this.ruleReaderFactory = ruleReaderFactory;
        this.config = config;
        this.name = name;
        loadAllRules();
    };

    @Override
    public void createContainers() {
        // TODO No default behavior?
    }

    @Override
    public KieContainer getContainer(String familyCode) {
        KieContainer kContainer = kContainers.get(familyCode);
        if (kContainer == null) {
            log.debug("No rules found for " + familyCode + ", checking for default rules for " + config.getDefaultFamilyCode());
            if (config == null) {
                log.error("Config is null, unable to retrieve a default container");
                return null;
            }
            kContainer = kContainers.get(config.getDefaultFamilyCode());
        }
        return kContainer;
    }

    @Override
    public Map<String, KieContainer> getContainers() {
        return kContainers;
    }

    @Override
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new HashMap<String, Object>();
        info.put("class", this.getClass().getName());
        info.put("name", getName());
        info.put("containers", this.kContainers.keySet());
        return info;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public List<String> getRuleSetDrls(String cccMisCode) {
        List<FamilyDTO> collegeDTOs = collegeReader.getFamilies(true);
        if (collegeDTOs.size() < 1) {
            log.error("DROOLS_RULES_MISCODE_COUNT is 0 or not found, no rules loaded.");
            return Arrays.asList("DROOLS_RULES_MISCODE_COUNT is 0 or not found, no rules loaded.");
        }

        for (FamilyDTO collegeDTO : collegeDTOs) {
            if (cccMisCode.equals(collegeDTO.getFamilyCode())) {
                if (StringUtils.isBlank(this.getName())) {
                    IEngineFactory ruleReader = ruleReaderFactory.getEngineFactory(collegeDTO);
                    return ruleReader.getDrls(collegeDTO);
                } else {
                    IEngineFactory ruleReader = ruleReaderFactory.getEngineFactory(collegeDTO, this.getName());
                    return ruleReader.getDrls(collegeDTO);
                }
            }
        }
        return Arrays.asList(String.format("No drools rules found for  cccMisCode %s", cccMisCode, collegeDTOs.size()));
    }

    public void loadAllRules() {
        kContainers.clear();
        List<FamilyDTO> collegeDTOs = collegeReader.getFamilies(true);
        if (collegeDTOs.size() < 1) {
            log.warn("Container Service [" + this.getName() + "] found no colleges in [" + collegeReader.getClass().getName()
                            + "] college reader.\n" + "No rules to load.  Engine will be disabled.");
            return;
        }

        if (ruleReaderFactory == null) {
            log.error("RuleReaderFactory is null, unable to load containers");
            return;
        }
        for (FamilyDTO collegeDTO : collegeDTOs) {
            IEngineFactory ruleReader = ruleReaderFactory.getEngineFactory(collegeDTO, this.getName());

            KieContainer kieContainer = ruleReader.getContainer(collegeDTO);
            if (kieContainer != null) {
                String cccMisCode = collegeDTO.getFamilyCode();
                kContainers.put(cccMisCode, kieContainer);
            }
        }

        log.info("Container Service [" + this.getName() + "] created [" + kContainers.size() + "] containers for colleges "
                        + kContainers.keySet());
    }
}
