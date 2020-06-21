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

import org.cccnext.tesuto.content.controller.CompetencyMapController;
import org.cccnext.tesuto.content.viewdto.CompetencyMapViewDto;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/service/v1/competency-maps")
public class CompetencyMapEndPoint extends BaseController {
    
    @Autowired
    CompetencyMapController controller;
    
    @PreAuthorize("hasAnyAuthority('VIEW_COMPETENCY_MAP', 'API')")
    @RequestMapping(value = "{discipline}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    CompetencyMapViewDto getLatestPublishedCompetencyMap(@PathVariable("discipline") String discipline) {
       return controller.getLatestPublishedCompetencyMap(discipline);
    }
    
    
    @PreAuthorize("hasAnyAuthority('VIEW_COMPETENCY_MAP', 'API')")
    @RequestMapping(value = "{discipline}/{version}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    CompetencyMapViewDto getCompetencyMapByVersion(@PathVariable String discipline, @PathVariable Integer version) {
        return controller.getCompetencyMapByVersion(discipline, version);
    }
}
