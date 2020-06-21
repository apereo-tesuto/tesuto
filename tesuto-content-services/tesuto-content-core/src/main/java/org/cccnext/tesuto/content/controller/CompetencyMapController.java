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
package org.cccnext.tesuto.content.controller;

import org.cccnext.tesuto.content.assembler.competency.CompetencyAssemblyService;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.service.CompetencyMapService;
import org.cccnext.tesuto.content.viewdto.CompetencyMapViewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompetencyMapController {
    
    @Autowired
    CompetencyAssemblyService competencyAssemblyService;
    
    @Autowired
    CompetencyMapService competencyMapService;

    public 
    CompetencyMapViewDto getLatestPublishedCompetencyMap(String discipline) {
        CompetencyMapDto competencyMapDto = competencyMapService.readLatestPublishedVersion(discipline);
        return competencyAssemblyService.assembleCompetencyMapViewDto(competencyMapDto);
    }
    
    
    public 
    CompetencyMapViewDto getCompetencyMapByVersion(String discipline, Integer version) {
        CompetencyMapDto competencyMapDto = competencyMapService.readByVersion(discipline, version);
        return competencyAssemblyService.assembleCompetencyMapViewDto(competencyMapDto);
    }
}
