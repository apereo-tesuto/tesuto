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

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.admin.dto.CollegeAssociatedUser;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.placement.service.PlacementComponentService;
import org.cccnext.tesuto.placement.service.PlacementService;
import org.cccnext.tesuto.placement.view.TesutoPlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.cccnext.tesuto.placement.view.student.PlacementStudentViewDto;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
// NOTE there is a potential conflict with the PlacementComponentController and CollegeController as it has the same root
@RequestMapping(value = "service/v1/colleges/{college-miscode}")
public class PlacementViewController extends BaseController {

    @Autowired
    private PlacementService placementService;

    @Autowired
    private PlacementComponentService placementComponentService;

    /**
     * return the all placements for the given college MIS Code
     * @param collegeMisCode
     * @return List of PlacementViewDtos
     */
    @PreAuthorize("hasAuthority('VIEW_PLACEMENT_DECISION')")
    @RequestMapping(value = "cccid/{cccid}/placements", method = RequestMethod.GET)
    public ResponseEntity<?> viewPlacementDecision(@PathVariable("college-miscode") String collegeMisCode, @PathVariable("cccid") String cccid) {
        if (!userIsAffiliated(collegeMisCode)) {
            return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        }

        Collection<PlacementViewDto> placements = placementService.getPlacementsForStudent(collegeMisCode, cccid);
        if (CollectionUtils.isEmpty(placements)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(placements, HttpStatus.OK);
    }

    /**
     * return the all placements for the given college MIS Code for the logged in user
     * @param collegeMisCode
     * @return List of PlacementViewDtos
     */
    @PreAuthorize("hasAuthority('STUDENT_VIEW_PLACEMENT_DECISION')")
    @RequestMapping(value = "student-placements", method = RequestMethod.GET)
    public ResponseEntity<?> studentViewPlacementDecision(@PathVariable("college-miscode") String collegeMisCode) {
        UserAccountDto user = getUser();
        if (!getUser().getCollegeIds().contains(collegeMisCode)) {
            return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        }

        Collection<PlacementStudentViewDto> placements = placementService.getStudentViewPlacementsForStudent(collegeMisCode, user.getUsername());

        if (CollectionUtils.isEmpty(placements)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(placements, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('STUDENT_VIEW_PLACEMENT_DECISION')")
    @RequestMapping(value = "placements/{placementId}")
    public ResponseEntity<?> assessmentComponentMetaData(@PathVariable("college-miscode") String collegeMisCode, @PathVariable("placementId") String placementId) {
        if(!userIsAffiliated(collegeMisCode)) {
            return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        }
        TesutoPlacementComponentViewDto tesutoPlacementComponentViewDto;
        tesutoPlacementComponentViewDto = placementComponentService.getTesutoPlacementComponent(collegeMisCode, placementId);
        ResponseEntity<?> responseEntity;
        if (tesutoPlacementComponentViewDto == null) {
            responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            responseEntity = new ResponseEntity<>(tesutoPlacementComponentViewDto, HttpStatus.OK);
        }
        return responseEntity;
    }
}
