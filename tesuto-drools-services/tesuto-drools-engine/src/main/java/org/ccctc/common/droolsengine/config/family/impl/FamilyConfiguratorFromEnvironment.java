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

import java.util.ArrayList;
import java.util.List;

import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.ccctc.common.droolsengine.config.family.AbstractFamilyConfigurator;
import org.springframework.stereotype.Service;

/**
 * Build a family reader that gets config from the application environment. This includes Spring properties files and system
 * environment variables. This should only be used when the application stack only has a single drools engine, as the
 * environment is poor choice to try to support configuring multiple drools engines.
 * Logging for this reader prefixed with "+--+"
 */
@Service
public class FamilyConfiguratorFromEnvironment extends AbstractFamilyConfigurator {
    @Override
    public List<FamilyDTO> getFamilies(boolean ignoreHere) {
        List<FamilyDTO> familyDTOs = new ArrayList<FamilyDTO>();
        List<String> familyCodes = config.getMisCodes();
        for (String cccMisCodefamilyCode : familyCodes) {
            FamilyDTO familyDTO = new FamilyDTO();
            familyDTO.setFamilyCode(cccMisCodefamilyCode);
            EngineDTO engineDTO = new EngineDTO();
            engineDTO.setDataSource(config.getDroolsRulesSource(cccMisCodefamilyCode));
            if (DroolsEngineEnvironmentConfiguration.RULES_SOURCE_MAVEN.equals(engineDTO.getDataSource())) {
                engineDTO.setGroupId(config.getDroolsRulesGroupId(cccMisCodefamilyCode));
                engineDTO.setArtifactId(config.getDroolsRulesArtifactId(cccMisCodefamilyCode));
                engineDTO.setVersion(config.getDroolsRulesVersionId(cccMisCodefamilyCode));
            }
            familyDTO.getEngineDTOs().put(config.getDroolsRulesApplication(), engineDTO);
            familyDTOs.add(familyDTO);
        }
        return familyDTOs;
    }

    @Override
    public String getName() {
        return "environment";
    }
}
