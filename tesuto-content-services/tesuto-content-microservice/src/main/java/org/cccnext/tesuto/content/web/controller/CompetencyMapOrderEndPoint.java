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
package org.cccnext.tesuto.content.web.controller;

import java.util.List;

import org.cccnext.tesuto.content.controller.CompetencyMapOrderController;
import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto;
import org.cccnext.tesuto.content.dto.shared.OrderedCompetencySet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/service/v1/competency-map-order")
public class CompetencyMapOrderEndPoint {

	@Autowired
    CompetencyMapOrderController controller;

    @PreAuthorize("hasAnyAuthority('VIEW_COMPETENCY_MAP', 'API')")
    @RequestMapping(value = "/competencies/{id}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    List<CompetencyDifficultyDto> getOrderedCompetencies(@PathVariable(value="id") String id) {
        return controller.getOrderedCompetencies(id);
    }
    
    @PreAuthorize("hasAnyAuthority('VIEW_COMPETENCY_MAP', 'API')")
    @RequestMapping(value = "/competency-map-id/{id}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String getCompetencyMapIdFromId(@PathVariable(value="id") String id) {
        return controller.getCompetencyMapOrderId(id);
    }

    @PreAuthorize("hasAnyAuthority('VIEW_COMPETENCY_MAP', 'API')")
    @RequestMapping(value = "/competencies/{orderedCompetencyMapId}/{versionId}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    List<CompetencyDifficultyDto> getOrderedCompetencies(@PathVariable String orderedCompetencyMapId, @PathVariable int versionId) {
       return controller.getOrderedCompetencies(orderedCompetencyMapId, versionId);
    }

    @PreAuthorize("hasAnyAuthority('VIEW_COMPETENCY_MAP', 'API')")
    @RequestMapping(value = "/discipline/{discipline}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody String getIdByDiscipline(@PathVariable String discipline) {
        return controller.getOrderedCompetenciesByDiscipline(discipline);
    }

    @PreAuthorize("hasAuthority('VIEW_COMPETENCY_MAP')")
    @RequestMapping(value = "/position/{id}/{studentAbility}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    Integer getStudentAbilityClosestCompetency(@PathVariable(value="id") String id, @PathVariable(value="studentAbility") Double studentAbility) {
       return controller.getStudentAbilityClosestCompetency(id, studentAbility);
    }

    @PreAuthorize("hasAnyAuthority('VIEW_COMPETENCY_MAP', 'API')")
    @RequestMapping(value = "/competencies/selected/{id}/{studentAbility}/{parentLevel}/{competencyRange}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    OrderedCompetencySet getSelectedOrderedCompetencies(@PathVariable(value="id") String id,
            @PathVariable(value="studentAbility") Double studentAbility,
            @PathVariable(value="parentLevel") Integer parentLevel,
            @PathVariable(value="competencyRange") Integer competencyRange) {
    	return controller.getSelectedOrderedCompetencies(id, studentAbility, parentLevel, competencyRange);
    }

    @PreAuthorize("hasAuthority('VIEW_COMPETENCY_MAP')")
    @RequestMapping(value = "/competencies/selected/{id}/{studentAbility}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    OrderedCompetencySet getSelectedOrderedCompetenciesDefault(@PathVariable(value="id") String orderedCompetencyMapId,
            @PathVariable(value="studentAbility") Double studentAbility) {
        return controller.getSelectedOrderedCompetenciesDefault(orderedCompetencyMapId, studentAbility);
    }

}
