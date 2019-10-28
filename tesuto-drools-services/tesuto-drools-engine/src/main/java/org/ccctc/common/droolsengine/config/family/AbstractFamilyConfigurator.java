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
package org.ccctc.common.droolsengine.config.family;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsengine.config.DroolsEngineEnvironmentConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractFamilyConfigurator implements IFamilyConfigurator {
    @Autowired
    protected DroolsEngineEnvironmentConfiguration config;

    @Override
    public FamilyDTO getFamily(String familyId) {
        if (StringUtils.isBlank(familyId)) {
            return null;
        }
        List<FamilyDTO> familyDTOs = getFamilies(false);
        if (familyDTOs == null) {
            return null;
        }
        for (FamilyDTO familyDTO : familyDTOs) {
            if (familyId.equals(familyDTO.getFamilyCode())) {
                return familyDTO;
            }
        }
        return null;
    }
}
