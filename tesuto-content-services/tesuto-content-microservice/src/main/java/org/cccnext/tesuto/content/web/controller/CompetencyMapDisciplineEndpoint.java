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


import org.cccnext.tesuto.content.controller.CompetencyMapDisciplineController;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by jasonbrown on 7/7/16.
 */
@Controller
@RequestMapping(value = { "/service/v1/competency-map-discipline" })
public class CompetencyMapDisciplineEndpoint extends BaseController {

    @Autowired
    CompetencyMapDisciplineController controller;

    @PreAuthorize("hasAnyAuthority('VIEW_COMPETENCY_MAP_DISCIPLINES', 'API')")
    @RequestMapping( method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getAllCompetencyMapDisciplines() {
        return controller.getAllCompetencyMapDisciplines();
    }
    
    @PreAuthorize("hasAnyAuthority('VIEW_COMPETENCY_MAP_DISCIPLINES', 'API')")
    @RequestMapping(value="/{competencyMapDiscipline}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getCompetencyMapDiscipline(@PathVariable String competencyMapDiscipline) {
        return controller.getCompetencyMapDiscipline(competencyMapDiscipline);
    }

    @PreAuthorize("hasAnyAuthority('CREATE_COMPETENCY_MAP_DISCIPLINES')")
    @RequestMapping(value="/{disciplineName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createCompetencyMapDiscipline(@PathVariable String disciplineName) {
        return controller.createCompetencyMapDiscipline(disciplineName);
    }
    
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value="oauth2/{disciplineName}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity createCompetencyMapDisciplineOauth(@PathVariable String disciplineName) {
        return controller.createCompetencyMapDiscipline(disciplineName);
    }

    @PreAuthorize("hasAuthority('DELETE_COMPETENCY_MAP_DISCIPLINES')")
    @RequestMapping(value="/{disciplineName}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity deleteCompetencyMapDiscipline(@PathVariable String disciplineName) {
        return controller.deleteCompetencyMapDiscipline(disciplineName);
    }
}
