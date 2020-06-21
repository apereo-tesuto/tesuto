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
package org.ccctc.common.droolsengine.config.engine.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.kie.api.runtime.KieContainer;



/**
 * Configure kcontainers based on rules that are compiled into maven artifacts
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class EngineFactoryRulesFromMaven extends AbstractEngineFactory {

    public EngineFactoryRulesFromMaven(DroolsEngineEnvironmentConfiguration config, String engine) {
        this.config = config;
        this.engineName = engine;
    }

    /**
     * This method will call out to create a new container. As this is an expensive operation, callers should first check if the
     * container is already being created by another thread (by checking with the iskContainerBeingBuilt(...) method).
     * 
     * @see org.ccctc.common.droolsengine.config.engine.IEngineFactory#getContainer(org.ccctc.common.droolscommon.model.FamilyDTO)
     */
    @Override
    public KieContainer getContainer(FamilyDTO familyDTO) {
        log.debug("getContainer(): for familyDTO [" + familyDTO + "], engine:[" + engineName + "]");
        KieContainer kContainer = null;
        String familyCode = familyDTO.getFamilyCode();
        EngineDTO EngineDTO = familyDTO.getEngineDTO(engineName);
        if (EngineDTO != null) {
            String groupId = EngineDTO.getGroupId();
            String artifactId = EngineDTO.getArtifactId();
            String version = EngineDTO.getVersion();
            log.debug("!~~~! START getting container from maven [" + EngineDTO + "]");
            kContainer = kContainerFactory.createKieContainerFromMavenRepository(groupId, artifactId, version);
            log.debug("!~~~! END getting container from maven [" + EngineDTO + "]");
        } else {
            Map<String, EngineDTO> EngineDTOs = familyDTO.getEngineDTOs();
            log.warn("Family [" + familyCode + "] does not have configuration for " + "engine[" + engineName
                            + "]; no maven settings to load.  " + "Valid engines are [" + EngineDTOs.keySet() + "]");
        }
        if (kContainer == null) {
            log.warn("Unable to generate kieContainer for family [" + familyCode + "] and engine:[" + engineName + "]");
        }
        return kContainer;
    }

    /**
     * Check to see if the container we desire is in the process of being built by another thread, if so the caller should simply
     * wait instead of using this class' getContainer(...) method to create a new one.
     * 
     * @see org.ccctc.common.droolsengine.config.engine.IEngineFactory#iskContainerBeingBuilt(org.ccctc.common.droolscommon.model.FamilyDTO)
     */
    public boolean iskContainerBeingBuilt(FamilyDTO familyDTO) {
        String familyCode = familyDTO.getFamilyCode();
        log.debug("Checking to see if we are already building for family[" + familyCode + "] and engine:[" + engineName + "]");
        EngineDTO EngineDTO = familyDTO.getEngineDTO(engineName);
        if (EngineDTO != null) {
            String groupId = EngineDTO.getGroupId();
            String artifactId = EngineDTO.getArtifactId();
            String version = EngineDTO.getVersion();
            return kContainerFactory.isKieContainerBeingBuilt(groupId, artifactId, version);
        } else {
            Map<String, EngineDTO> EngineDTOs = familyDTO.getEngineDTOs();
            log.debug("Family [" + familyCode + "] does not have configuration for " + "engine[" + engineName
                            + "]; no maven settings to load.  " + "Valid engines are [" + EngineDTOs.keySet() + "]");
            return false;
        }
    }

    @Override
    public List<String> getDrls(FamilyDTO familyDTO) {
        EngineDTO EngineDTO = familyDTO.getEngineDTO(engineName);
        String groupId = EngineDTO.getGroupId();
        String artifactId = EngineDTO.getArtifactId();
        String version = EngineDTO.getVersion();
        return Arrays.asList(String.format("Rules are generated from maven repository groupId:%s, artifactId:%s, version:%s",
                        groupId, artifactId, version));
    }
}
