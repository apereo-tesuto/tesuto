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

import org.apache.commons.lang3.StringUtils;

import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;

import org.cccnext.tesuto.placement.dto.CollegeCompetencyAverageViewDto;
import org.cccnext.tesuto.placement.service.CompetencyAveragesService;
import org.cccnext.tesuto.placement.service.SubjectAreaServiceAdapter;
import org.cccnext.tesuto.placement.view.DisciplineViewDto;
import org.cccnext.tesuto.placement.view.student.VersionedSubjectAreaStudentViewDto;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Controller
//NOTE there is a potential conflict with the PlacementComponentController and PlacementViewController as it has the same root
@RequestMapping(value = "service/v1/colleges/")
public class CollegeController extends BaseController {

    @Autowired
    private SubjectAreaServiceAdapter service;

    @Autowired
    private CompetencyAveragesService competencyAveragesService;

    @PreAuthorize("hasAnyAuthority('GET_DISCIPLINE_BY_COLLEGE_ID', 'API')")
    @RequestMapping(value = "{collegeMiscode}/subject-areas", method = RequestMethod.GET)
    public ResponseEntity<?> getSubjectAreas(@PathVariable String collegeMiscode) {
        if (!userIsAffiliated(collegeMiscode) && !userHasRole("API")) {
            throw new AccessDeniedException("Cannot access college "+ collegeMiscode);
        }
        Set<VersionedSubjectAreaViewDto> versionedSubjectAreaViewDtos = service.getVersionedSubjectAreaByCollegeId(Collections.singleton(collegeMiscode));
        return new ResponseEntity<>(versionedSubjectAreaViewDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('GET_DISCIPLINE_FOR_STUDENT')")
    @RequestMapping(value = "{collegeMiscode}/student-subject-areas", method = RequestMethod.GET)
    public ResponseEntity<?> getSubjectAreasForStudent(@PathVariable String collegeMiscode) {
        if (!userIsAffiliated(collegeMiscode)) {
            throw new AccessDeniedException("Cannot access college "+ collegeMiscode);
        }
        Set<VersionedSubjectAreaStudentViewDto> versionedSubjectAreaStudentViewDtos = service.getVersionedSubjectAreaStudentViewByCollegeId(Collections.singleton(collegeMiscode));
        return new ResponseEntity<>(versionedSubjectAreaStudentViewDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('GET_COLLEGE_COMPETENCY_AVERAGES')")
    @RequestMapping(value = "{collegeMiscode}/averages", method = RequestMethod.GET)
    public ResponseEntity<?> getCompetencyAverages(@PathVariable String collegeMiscode,
                                                   @RequestParam(value = "subjectAreaId", required = false) Integer subjectAreaId,
                                                   @RequestParam(value = "courseId", required = false) Integer courseId) {
        CollegeCompetencyAverageViewDto averages = competencyAveragesService.calculateCollegeCompetencyAverages(collegeMiscode, subjectAreaId, courseId);
        return new ResponseEntity<>(averages, HttpStatus.OK);
    }

    @RequestMapping(value = "{collegeMiscode}/subject-areas-rules", method = RequestMethod.GET)
    public ResponseEntity<?> getSubjectAreasRules(@PathVariable String collegeMiscode) {
        if (!userIsAffiliated(collegeMiscode)) {
            throw new AccessDeniedException("Cannot access college "+ collegeMiscode);
        }
        Set<DisciplineViewDto> disciplines = service.getDisciplinesByCollegeId(Collections.singleton(collegeMiscode));
        Set<String> ruleIds  = new HashSet<String>();
        for (DisciplineViewDto disciplineViewDto : disciplines) {
			if (disciplineViewDto.getCompetencyAttributes() != null &&
					StringUtils.isNotBlank(disciplineViewDto.getCompetencyAttributes().getMmDecisionLogic())) {
				ruleIds.add(disciplineViewDto.getCompetencyAttributes().getMmDecisionLogic());
			}
		}
        return new ResponseEntity<>(ruleIds, HttpStatus.OK);
    }

}

