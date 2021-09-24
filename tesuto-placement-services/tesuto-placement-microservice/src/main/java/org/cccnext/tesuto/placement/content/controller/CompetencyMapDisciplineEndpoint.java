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
package org.cccnext.tesuto.placement.content.controller;


import java.util.List;

import org.cccnext.tesuto.content.dto.competency.CompetencyMapDisciplineDto;
import org.cccnext.tesuto.content.service.CompetencyMapDisciplineReader;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by jstanley on 7/7/18.  TODO note this is only to be active when content microservice is not active
 */
@Controller
@RequestMapping(value = { "/service/v1/competency-map-discipline" })
public class CompetencyMapDisciplineEndpoint extends BaseController {

    @Autowired
    CompetencyMapDisciplineReader controller;

    @PreAuthorize("hasAnyAuthority('VIEW_COMPETENCY_MAP_DISCIPLINES', 'API')")
    @RequestMapping( method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<CompetencyMapDisciplineDto> getAllCompetencyMapDisciplines() {
        return controller.read();
    }
    
    @PreAuthorize("hasAnyAuthority('VIEW_COMPETENCY_MAP_DISCIPLINES', 'API')")
    @RequestMapping(value="/{competencyMapDiscipline}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody CompetencyMapDisciplineDto getCompetencyMapDiscipline(@PathVariable String competencyMapDiscipline) {
        return controller.read(competencyMapDiscipline);
    }


}
