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
package org.ccctc.droolseditor.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccctc.common.droolscommon.model.EngineDTO;
import org.ccctc.common.droolscommon.model.FamilyDTO;
import org.ccctc.common.droolsdb.dynamodb.utils.AbstractMapper;
import org.ccctc.common.droolsdb.services.IEngineService;
import org.ccctc.droolseditor.views.FamilyView;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.util.CollectionUtils;

public class FamilyViewMapper extends AbstractMapper<FamilyView, FamilyDTO> {
    @Autowired
    Mapper mapper;

    @Autowired
    IEngineService engineService;

    @Override
    protected FamilyView doMapTo(FamilyDTO familyDto) {
        FamilyView familyView = mapper.map(familyDto, FamilyView.class);
        if (familyDto.getEngineDTOs() != null && !familyDto.getEngineDTOs().isEmpty()) {
            familyView.setSelectedApplications(familyDto.getEngineDTOs().keySet());
        }
        return familyView;
    }

    @Override
    protected FamilyDTO doMapFrom(FamilyView engineView) {
        FamilyDTO familyDTO = mapper.map(engineView, FamilyDTO.class);
        List<EngineDTO> engines = engineService.getEnginesByNames(engineView.getSelectedApplications());
        Map<String, EngineDTO> enginesForFamily = new HashMap<>();
        if (!CollectionUtils.isNullOrEmpty(engines)) {
            engines.forEach(engine -> enginesForFamily.put(engine.getName(), engine));
        }
        familyDTO.setEngines(enginesForFamily);
        return familyDTO;
    }

}
