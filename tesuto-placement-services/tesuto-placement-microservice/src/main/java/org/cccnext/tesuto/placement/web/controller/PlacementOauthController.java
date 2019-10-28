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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;

import org.cccnext.tesuto.placement.service.PlacementComponentService;
import org.cccnext.tesuto.placement.service.PlacementService;
import org.cccnext.tesuto.placement.service.SubjectAreaServiceAdapter;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.cccnext.tesuto.placement.view.student.VersionedSubjectAreaStudentViewDto;
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
@RequestMapping(value = "service/v1/oauth2")
public class PlacementOauthController extends BaseController {

    @Autowired
    private PlacementService placementService;

    @Autowired
    private SubjectAreaServiceAdapter subjectAreaService;

    @Autowired
    private PlacementComponentService placementComponentService;

    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value = "placements/college/{college-miscode}/cccid/{cccid}", method = RequestMethod.GET)
    public ResponseEntity<?> oauthPlacementsForCollegeAndStudent(@PathVariable("college-miscode") String collegeMisCode, @PathVariable("cccid") String cccid) {

        Collection<PlacementViewDto> placements = placementService.getPlacementsForStudent(collegeMisCode, cccid);
        if (CollectionUtils.isEmpty(placements)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(placements, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('API')")
    @RequestMapping(value="subject-area/{subjectAreaId}/version/{version}", method=RequestMethod.GET)
    public ResponseEntity<?> getOauthVersionedSubjectAreas(@PathVariable int subjectAreaId, @PathVariable int version) {

        VersionedSubjectAreaViewDto versionedSubjectAreaViewDto = subjectAreaService.getVersionedSubjectAreaDto(subjectAreaId, version);
        if (versionedSubjectAreaViewDto == null) {
            return new ResponseEntity<VersionedSubjectAreaViewDto>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(versionedSubjectAreaViewDto, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value = "placement-component/{college-miscode}/cccid/{cccid}", method = RequestMethod.GET)
    public ResponseEntity<?> oauthPlacementComponents(@PathVariable("college-miscode") String collegeMisCode, @PathVariable(value="cccid") String cccid) {
        Collection<PlacementComponentViewDto> placementComponents;
        placementComponents = placementComponentService.getPlacementComponents(collegeMisCode, cccid);

        if (CollectionUtils.isEmpty(placementComponents)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(placementComponents, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value = "subject-areas/placements/college/{college-miscode}/cccid/{cccid}", method = RequestMethod.GET)
    public ResponseEntity<?> oauthSubjectAreasAndPlacementsForStudentByCollege(@PathVariable("college-miscode") String collegeMisCode,
                                                                                @PathVariable("cccid") String cccid) {
        Map<String, Collection> results = new HashMap<>();
        Set<VersionedSubjectAreaStudentViewDto> versionedSubjectAreaStudentViewDtos = subjectAreaService.getVersionedSubjectAreaStudentViewByCollegeId(Collections.singleton(collegeMisCode));
        results.put("subjectAreas", versionedSubjectAreaStudentViewDtos);

        Collection<PlacementViewDto> placements = placementService.getPlacementsForStudent(collegeMisCode, cccid);
        results.put("placements", placements);

        return new ResponseEntity<>(results, HttpStatus.OK);
    }
}
