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

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.cccnext.tesuto.placement.service.CompetencyGroupService;
import org.cccnext.tesuto.placement.view.CompetencyGroupViewDto;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "service/v1/courses")
public class CourseController  extends BaseController {
    
    @Autowired CompetencyGroupService competencyGroupService;
    
    @Autowired
    PlacementCollegeAuthorizerService authenticationService;
    
    //@PreAuthorize("hasAuthority('CREATE_COMPETENCY_GROUP')")
    @RequestMapping(value = "{courseId}/competency-groups", method = RequestMethod.POST)
    public ResponseEntity<?> postCompetencyGroup(HttpServletRequest request, @RequestBody CompetencyGroupViewDto competencyGroup) {
        
        authenticationService.authorizeForCourse(competencyGroup.getCourseId(), getUser());
        Integer id = competencyGroupService.upsert(competencyGroup);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
    
    //@PreAuthorize("hasAuthority('GET_COMPETENCY_GROUPS_BY_CLASS_ID')")
    @RequestMapping(value = "{courseId}/competency-groups", method = RequestMethod.GET)
    public ResponseEntity<?> getCompetencyGroupsByClassId(@PathVariable int courseId) {
        authenticationService.authorizeForCourse(courseId, getUser());
        Set<CompetencyGroupViewDto> competencyGroups = competencyGroupService.getCompetencyGroups(courseId);
        return new ResponseEntity<>(competencyGroups, HttpStatus.OK);
    }

}
