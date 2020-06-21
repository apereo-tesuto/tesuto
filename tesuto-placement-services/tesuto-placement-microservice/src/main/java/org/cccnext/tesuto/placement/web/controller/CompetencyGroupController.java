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
package org.cccnext.tesuto.placement.web.controller;

import org.cccnext.tesuto.placement.service.CompetencyGroupService;
import org.cccnext.tesuto.placement.view.CompetencyGroupViewDto;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "service/v1/competency-groups")
public class CompetencyGroupController extends BaseController {
    
    @Autowired CompetencyGroupService competencyGroupService;
    
    @Autowired
    PlacementCollegeAuthorizerService authenticationService;
    
    @PreAuthorize("hasAuthority('GET_COMPETENCY_GROUP')")
    @RequestMapping(value = "/{competency-group-id}", method = RequestMethod.GET)
    public ResponseEntity<?> updateCompetencyGroup(@PathVariable("competency-group-id")  Integer competencyGroupId) {
        CompetencyGroupViewDto competencyGroupViewDto = competencyGroupService.get(competencyGroupId);
        authenticationService.authorizeForCourse(competencyGroupViewDto.getCourseId(), getUser());
        return new ResponseEntity<>(competencyGroupViewDto, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('UPDATE_COMPETENCY_GROUP')")
    @RequestMapping(value = "/{competency-group-id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateCompetencyGroup(@PathVariable("competency-group-id") Integer competencyGroupId, @RequestBody CompetencyGroupViewDto competencyGroup) {
        authenticationService.authorizeForCourse(competencyGroup.getCourseId(), getUser());
        competencyGroup.setCompetencyGroupId(competencyGroupId);
        competencyGroupService.upsert(competencyGroup);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @PreAuthorize("hasAuthority('DELETE_COMPETENCY_GROUP')")
    @RequestMapping(value = "/{competency-group-id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCompetencyGroup(@PathVariable("competency-group-id")  Integer competencyGroupId) {
        CompetencyGroupViewDto competencyGroupViewDto = competencyGroupService.get(competencyGroupId);
        authenticationService.authorizeForCourse(competencyGroupViewDto.getCourseId(), getUser());
        competencyGroupService.delete(competencyGroupId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
