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

import javax.servlet.http.HttpServletRequest;

import org.cccnext.tesuto.content.controller.ContentOauth2Controller;
import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "oauth2/service/v1")
public class ContentOauth2Endpoint {
    
    @Autowired
    ContentOauth2Controller controller;
    
    @PreAuthorize("hasAnyAuthority('VIEW_COMPETENCY_MAP','API')")
    @RequestMapping(value = "competency-map-order/competencies/{orderedCompetencyMapId}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    List<CompetencyDifficultyDto> getOrderedCompetencies(HttpServletRequest request, @PathVariable String orderedCompetencyMapId) throws IllegalAccessException {
        return controller.getOrderedCompetencies(request, orderedCompetencyMapId);
    }

    @PreAuthorize("hasAnyAuthority('VIEW_COMPETENCY_MAP','API')")
    @RequestMapping(value = "competency-map-order/discipline/{competencyDisciplineName}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity
     getCurrentCompetencyMapOrderId(HttpServletRequest request, @PathVariable("competencyDisciplineName") String competencyDisciplineName) throws IllegalAccessException {
    	return controller.getCurrentCompetencyMapOrderId(request, competencyDisciplineName);
    }

    @PreAuthorize("hasAnyAuthority('VIEW_COMPETENCY_MAP','API')")
    @RequestMapping(value = "competency-maps/{discipline}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getLatestPublishedCompetencyMapOauth(HttpServletRequest request, @PathVariable("discipline") String discipline) throws IllegalAccessException {
        return controller.getLatestPublishedCompetencyMapOauth(request, discipline);
    }

    @PreAuthorize("hasAnyAuthority('VIEW_COMPETENCY_MAP_DISCIPLINES', 'API')")
    @RequestMapping(value="competency-map-discipline/{competencyMapDiscipline}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getCompetencyMapDiscipline(@PathVariable String competencyMapDiscipline) {
        return controller.getCompetencyMapDiscipline(competencyMapDiscipline);
    }
    
    @PreAuthorize("hasAnyAuthority('VIEW_COMPETENCY_MAP_DISCIPLINES', 'API')")
    @RequestMapping(value="competency-map-discipline", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getCompetencyMapDisciplines() {
        return controller.getCompetencyMapDisciplines();
    }
}
