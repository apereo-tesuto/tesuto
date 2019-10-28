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
package org.cccnext.tesuto.delivery.controller;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.content.dto.shared.OrderedCompetencySetRestrictedView;
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.repository.mongo.AssessmentSessionRepository;
import org.cccnext.tesuto.delivery.service.CompetencyMapMasteryRestrictedViewService;
import org.cccnext.tesuto.user.service.StudentReader;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class CompetencyMasteryController {
	
	    
    @Autowired
    CompetencyMapMasteryRestrictedViewService competencyMapMasteryService;

    @Autowired
    AssessmentSessionRepository assessmentSessionRepository;
    
    
    @Autowired
    private StudentReader studentService;

    /**
     *
     * VIEW_MY_COMPETENCY_MASTERY has been deleted from SECURITY_GROUP_SECURITY_PERMISSION table
     * and will be added back in at a future date.  This permission was tied to the student.
     * See flyway script V2.91
     *
     */
    public ResponseEntity<?> assessmentSessionViewCompetencies(UserAccountDto userAccount, HttpSession httpSession,
            @PathVariable("assessmentSessionId") String assessmentSessionId, @RequestParam(required = false) Integer parentLevel) {

        AssessmentSession assessmentSession = assessmentSessionRepository.findById(assessmentSessionId).get();
        if (assessmentSession == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!userAccount.hasPermission("VIEW_COMPETENCY_MASTERY") && !userAccount.hasPermission("VIEW_MY_COMPETENCY_MASTERY")) {
            if (!assessmentSession.getUserId().equalsIgnoreCase(userAccount.getUsername())) {
                return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
            }
        }     	
        
        if(!atLeastOneCommonCollegeToCurrentUser(userAccount, assessmentSession.getUserId())){
            return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        }
        
        Map<String,OrderedCompetencySetRestrictedView> competencyMapSet = competencyMapMasteryService.getMasteryRestrictedView(assessmentSession,  parentLevel);
       
        if(competencyMapSet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(competencyMapSet, HttpStatus.OK);
    }
    
    
    public ResponseEntity<?> counselorAssessmentSessionViewCompetencies(HttpSession httpSession,
            @PathVariable("assessmentSessionId") String assessmentSessionId, @RequestParam(required = false) Integer parentLevel) {

        AssessmentSession assessmentSession = assessmentSessionRepository.findById(assessmentSessionId).get();
        if (assessmentSession == null) {
            log.error("Assessment Session with id: {} not found.", assessmentSessionId );
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        Map<String,OrderedCompetencySetRestrictedView> competencyMapSet = competencyMapMasteryService.getMasteryRestrictedView(assessmentSession,  parentLevel);
       
        if(competencyMapSet == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(competencyMapSet, HttpStatus.OK);
    }
    
    protected Boolean atLeastOneCommonCollegeToCurrentUser(UserAccountDto currentUserAccount, String cccid) {
        StudentViewDto student = buildStudent(cccid);
        for(String collegeId:student.getCollegeStatuses().keySet()) {
            if(currentUserAccount.getCollegeIds().contains(collegeId)){
                return true;
            }
        }
        return false;
    }
    
    protected StudentViewDto buildStudent(String cccid) {
        StudentViewDto student = null;
        try {
             student = studentService.getStudentById(cccid);
        } catch (HttpClientErrorException ex) {
            log.error("Unable to find student " + cccid + " through service, apparently failure caused by http error. Service may be down.", ex);
        } catch (Exception ex) {
            log.error("Error caused by service. Service may be down.", ex);
        }
        return student;
       
    }

}
