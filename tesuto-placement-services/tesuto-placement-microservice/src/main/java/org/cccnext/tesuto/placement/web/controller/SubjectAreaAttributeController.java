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

import org.cccnext.tesuto.placement.service.SubjectAreaServiceAdapter;
import org.cccnext.tesuto.placement.view.CompetencyAttributesViewDto;
import org.cccnext.tesuto.placement.view.DisciplineViewDto;
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
@RequestMapping(value = "service/v1/subject-areas/")
public class SubjectAreaAttributeController extends BaseController {
    @Autowired
    private SubjectAreaServiceAdapter service;

    @Autowired
    private PlacementCollegeAuthorizerService authorizationService;

    @PreAuthorize("hasAuthority('GET_DISCIPLINE')")
    @RequestMapping(value="{subjectAreaId}/attributes", method=RequestMethod.GET)
    public ResponseEntity<?> getAttributes(@PathVariable int subjectAreaId) {
        DisciplineViewDto subjectArea = service.getDiscipline(subjectAreaId);
        if (subjectArea == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        authorizationService.authorize(subjectArea.getCollegeId(), getUser());
        return new ResponseEntity<>(subjectArea.getCompetencyAttributes(), HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('UPDATE_DISCIPLINE')")
    @RequestMapping(value="{subjectAreaId}/attributes", method=RequestMethod.PUT)
    public ResponseEntity<?> updateDiscipline(@PathVariable int subjectAreaId, @RequestBody CompetencyAttributesViewDto competencyAttributes) {

    	DisciplineViewDto old = service.getDiscipline(subjectAreaId);
        if (old == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        authorizationService.authorize(old.getCollegeId(), getUser());
        old.setCompetencyAttributes(competencyAttributes);
        service.updateDiscipline(old);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
