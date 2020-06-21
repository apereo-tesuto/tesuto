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

import java.net.URI;
import java.util.Collection;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.placement.service.SubjectAreaServiceAdapter;
import org.cccnext.tesuto.placement.view.CourseViewDto;
import org.cccnext.tesuto.placement.view.DisciplineSequenceViewDto;
import org.cccnext.tesuto.placement.view.DisciplineViewDto;
import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Controller
@RequestMapping(value = "service/v1/subject-areas")
public class SubjectAreaController extends BaseController {

    @Autowired
    private SubjectAreaServiceAdapter service;

    @Autowired
    private PlacementCollegeAuthorizerService authorizationService;

    @PreAuthorize("hasAnyAuthority('CREATE_DISCIPLINE', 'API')")
    @RequestMapping(value="", method=RequestMethod.POST)
    public ResponseEntity<?> createSubjectArea(@RequestBody DisciplineViewDto subjectArea, HttpServletRequest request)
            throws Exception {
        authorizationService.authorize(subjectArea.getCollegeId(), getUser());
        service.validateDiscipline(subjectArea);

        if (StringUtils.isBlank(subjectArea.getCompetencyAttributes().getCompetencyCode())) {
            subjectArea.getCompetencyAttributes().setCompetencyCode( subjectArea.getCompetencyMapDiscipline());
        }

        Integer disciplineId = service.createDiscipline(subjectArea);
        HttpHeaders headers = new HttpHeaders();
        URI locationUri = new URI(request.getRequestURL()+"/"+disciplineId);
        headers.setLocation(locationUri);
        return new ResponseEntity<>(disciplineId, headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('GET_DISCIPLINE', 'API')")
    @RequestMapping(value="", method=RequestMethod.GET)
    public ResponseEntity<?> getAllSubjectAreas(HttpServletRequest request) {
        UserAccountDto user = getUser();
        Set<DisciplineViewDto> disciplines = service.getDisciplinesByCollegeId(user.getCollegeIds());
        return new ResponseEntity<>(disciplines, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('GET_DISCIPLINE', 'API')")
    @RequestMapping(value="{subjectAreaId}", method=RequestMethod.GET)
    public ResponseEntity<?> getSubjectArea(@PathVariable int subjectAreaId) {
        VersionedSubjectAreaViewDto versionedSubjectAreaViewDto = service.getVersionedDiscipline(subjectAreaId);
        if (versionedSubjectAreaViewDto == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        authorizationService.authorize(versionedSubjectAreaViewDto.getCollegeId(), getUser());
        return new ResponseEntity<>(versionedSubjectAreaViewDto, HttpStatus.OK);
    }


    @PreAuthorize("hasAnyAuthority('UPDATE_DISCIPLINE', 'API')")
    @RequestMapping(value="{subjectAreaId}", method=RequestMethod.PUT)
    public ResponseEntity<?> updateSubjectArea(@PathVariable int subjectAreaId,
                                               @RequestBody DisciplineViewDto discipline) {
        service.validateDiscipline(discipline);
        DisciplineViewDto old = service.getDiscipline(subjectAreaId);
        if (old == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        authorizationService.authorize(old.getCollegeId(), getUser());
        discipline.setCollegeId(old.getCollegeId()); //collegeId should be immutable
        discipline.setDisciplineId(subjectAreaId);
        discipline.setCompetencyMapVersion(old.getCompetencyMapVersion());
        discipline.setCompetencyMapDiscipline(old.getCompetencyMapDiscipline());

        service.updateDiscipline(discipline);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('UPDATE_DISCIPLINE_COMPETENCY_MAP_VERSION')")
    @RequestMapping(value="{subjectAreaId}/{competencyMapVersion}", method=RequestMethod.PUT)
    public ResponseEntity<?> updateCompetencyMapVersion(@PathVariable int subjectAreaId, @PathVariable int competencyMapVersion) {
        DisciplineViewDto discipline = service.getDiscipline(subjectAreaId);
        if (discipline == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        authorizationService.authorize(discipline.getCollegeId(), getUser());
        discipline.setCompetencyMapVersion(competencyMapVersion);
        service.updateDiscipline(discipline);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PreAuthorize("hasAuthority('DELETE_DISCIPLINE')")
    @RequestMapping(value="{subjectAreaId}", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteSubjectArea(@PathVariable int subjectAreaId) {
        DisciplineViewDto discipline = service.getDiscipline(subjectAreaId);
        if (discipline == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        authorizationService.authorize(discipline.getCollegeId(), getUser());
        service.deleteDiscipline(subjectAreaId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('GET_SEQUENCE')")
    @RequestMapping(value="{subjectAreaId}/sequences", method=RequestMethod.GET)
    public ResponseEntity<?> getSequences(@PathVariable int subjectAreaId) {
        authorizationService.authorizeForDiscipline(subjectAreaId, getUser());
        Set<DisciplineSequenceViewDto> sequences = service.getSequencesByDisciplineId(subjectAreaId);
        return new ResponseEntity<>(sequences, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('UPDATE_DISCIPLINE')")
    @RequestMapping(value="{subjectAreaId}/publish", method=RequestMethod.PUT)
    public ResponseEntity<VersionedSubjectAreaViewDto> publishSubjectArea(@PathVariable int subjectAreaId) {
        authorizationService.authorizeForDiscipline(subjectAreaId, getUser());
        VersionedSubjectAreaViewDto versionedSubjectAreaViewDto = service.createVersionedSubjectArea(subjectAreaId);
        return new ResponseEntity<>(versionedSubjectAreaViewDto, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('UPDATE_DISCIPLINE')")
    @RequestMapping(value="{subjectAreaId}/unpublish", method=RequestMethod.PUT)
    public ResponseEntity<Void> unpublishSubjectArea(@PathVariable int subjectAreaId) {

        authorizationService.authorizeForDiscipline(subjectAreaId, getUser());
        service.unpublishSubjectArea(subjectAreaId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('GET_DISCIPLINE', 'GET_DISCIPLINE_FOR_STUDENT')")
    @RequestMapping(value="{subjectAreaId}/version/{version}", method=RequestMethod.GET)
    public ResponseEntity<?> getVersionedSubjectAreas(@PathVariable int subjectAreaId, @PathVariable int version) {
        authorizationService.authorizeForDiscipline(subjectAreaId, getUser());
        VersionedSubjectAreaViewDto versionedSubjectAreaViewDto = service.getVersionedSubjectAreaDto(subjectAreaId, version);
        if (versionedSubjectAreaViewDto == null) {
            return new ResponseEntity<VersionedSubjectAreaViewDto>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(versionedSubjectAreaViewDto, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('GET_DISCIPLINE')")
    public ResponseEntity<Collection<VersionedSubjectAreaViewDto>> getAllVersionedSubjectAres(@PathVariable int subjectAreaId) {
        authorizationService.authorizeForDiscipline(subjectAreaId, getUser());
        Collection<VersionedSubjectAreaViewDto> viewDtos = service.getVersionedSubjectAreaDtos(subjectAreaId);
        return new ResponseEntity<Collection<VersionedSubjectAreaViewDto>>(viewDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('UPDATE_SEQUENCE')")
    @RequestMapping(value="{subjectAreaId}/sequences/{cb21Code}/{courseGroup}", method=RequestMethod.PUT)
    public ResponseEntity<?> updateSequence(@PathVariable int subjectAreaId,
                                            @PathVariable char cb21Code,
                                            @PathVariable int courseGroup,
                                            @RequestBody DisciplineSequenceViewDto sequence) {
        authorizationService.authorizeForDiscipline(subjectAreaId, getUser());
        if (!service.disciplineSequenceExists(subjectAreaId, cb21Code, courseGroup)) {
            return new ResponseEntity<>(error("No such sequence"), HttpStatus.NOT_FOUND);
        }
        sequence.setCb21Code(cb21Code);
        sequence.setDisciplineId(subjectAreaId);
        sequence.setCourseGroup(courseGroup);
        service.validateDisciplineSequence(sequence);
        service.upsert(sequence);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('CREATE_COURSE')")
    @RequestMapping(value="{subjectAreaId}/courses", method=RequestMethod.POST, consumes={"application/json; charset=UTF-8"})
    public ResponseEntity<?> createCourse(@PathVariable int subjectAreaId,
                                          @RequestBody CourseViewDto course,
                                          HttpServletRequest request)
            throws Exception {
        authorizationService.authorizeForDiscipline(subjectAreaId, getUser());
        service.validateCourse(course);
        int courseId = service.createCourse(subjectAreaId, course);
        URI locationUri = new URI(request.getRequestURL() + "/" + courseId);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(locationUri);
        return new ResponseEntity<>(courseId, headers, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('GET_ALL_COURSES_FOR_SEQUENCE')")
    @RequestMapping(value="{subjectAreaId}/sequences/{cb21Code}/{courseGroup}/courses", method=RequestMethod.GET)
    public ResponseEntity<?> getAllCoursesForSequence(@PathVariable int subjectAreaId,
                                                      @PathVariable char cb21Code, @PathVariable int courseGroup) {
        authorizationService.authorizeForDiscipline(subjectAreaId, getUser());
        if (!service.disciplineSequenceExists(subjectAreaId, cb21Code, courseGroup)) {
            return new ResponseEntity<>(error("No such sequence"), HttpStatus.NOT_FOUND);
        }
        Set<CourseViewDto> courses = service.getCourses(subjectAreaId, cb21Code, courseGroup);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('DELETE_COURSE')")
    @RequestMapping(value="courses/{courseId}", method=RequestMethod.DELETE)
    public ResponseEntity<?> deleteCourse(@PathVariable int courseId) {
        authorizationService.authorizeForCourse(courseId, getUser());
        CourseViewDto course = service.getCourse(courseId);
        if (course == null) {
            return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
        }
        Set<DisciplineSequenceViewDto> sequences = service.deleteCourse(courseId);
        service.removeEmptySequences(sequences);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('UPDATE_COURSE')")
    @RequestMapping(value="{subjectAreaId}/courses/{courseId}", method=RequestMethod.PUT, consumes={"application/json; charset=UTF-8"})
    public ResponseEntity<?> updateCourse(
            @PathVariable int subjectAreaId,
            @PathVariable int courseId,
            @RequestBody CourseViewDto course) throws Exception {
        course.setDisciplineId(subjectAreaId);
        authorizationService.authorizeForCourse(courseId, getUser());
        course.setCourseId(courseId);
        service.validateCourse(course);
        service.updateCourse(course);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAuthority('GET_ALL_COURSES_FOR_DISCIPLINE')")
    @RequestMapping(value="{subjectAreaId}/courses", method=RequestMethod.GET)
    public ResponseEntity<?> getCoursesForDiscipline(@PathVariable int subjectAreaId) {
        authorizationService.authorizeForDiscipline(subjectAreaId, getUser());
        Set<CourseViewDto> courses = service.getCourses(subjectAreaId);
        return new ResponseEntity<>(courses, HttpStatus.OK);
    }
}
