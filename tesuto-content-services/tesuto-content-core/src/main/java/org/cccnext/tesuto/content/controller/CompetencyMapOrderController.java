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

import java.util.List;

import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto;
import org.cccnext.tesuto.content.dto.shared.OrderedCompetencySet;
import org.cccnext.tesuto.content.service.CompetencyMapOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class CompetencyMapOrderController {

    @Autowired
    @Qualifier("competencyMapOrderService")
    CompetencyMapOrderService competencyMapOrderedService;

    public 
    List<CompetencyDifficultyDto> getOrderedCompetencies(String id) {
        List<CompetencyDifficultyDto> competencies = competencyMapOrderedService.getOrderedCompetencies(id);
        return competencies;
    }
    
    public 
    String getCompetencyMapOrderId(String id) {
        return competencyMapOrderedService.getCompetencyMapOrderId(id);
    }


    public 
    List<CompetencyDifficultyDto> getOrderedCompetencies(String orderedCompetencyMapId, int versionId) {
        List<CompetencyDifficultyDto> competencies = competencyMapOrderedService.getOrderedCompetencies(orderedCompetencyMapId, versionId);
        return competencies;
    }

    public  String getOrderedCompetenciesByDiscipline( String discipline) {
        return competencyMapOrderedService.findLatestPublishedIdByCompetencyMapDiscipline(discipline);
    }

    public 
    Integer getStudentAbilityClosestCompetency(String orderedCompetencyMapId, Double studentAbility) {
        Integer position = competencyMapOrderedService.findPositionByAbility(orderedCompetencyMapId, studentAbility);
        return position;
    }

    public 
    OrderedCompetencySet getSelectedOrderedCompetencies( String orderedCompetencyMapId,
             Double studentAbility,
             Integer parentLevel,
             Integer competencyRange) {
        return competencyMapOrderedService.selectOrganizeByAbility(orderedCompetencyMapId, studentAbility, parentLevel, competencyRange);
    }

    public 
    OrderedCompetencySet getSelectedOrderedCompetenciesDefault( String orderedCompetencyMapId,
            @PathVariable(value="studentAbility") Double studentAbility) {
        return competencyMapOrderedService.selectOrganizeByAbility(orderedCompetencyMapId, studentAbility, 0, 4);
    }

}
