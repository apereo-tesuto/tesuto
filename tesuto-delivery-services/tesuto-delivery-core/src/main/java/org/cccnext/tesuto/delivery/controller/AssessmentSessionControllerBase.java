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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.content.dto.AssessmentPartNavigationMode;
import org.cccnext.tesuto.delivery.model.internal.Activity;
import org.cccnext.tesuto.delivery.model.view.AssessmentSessionViewDto;
import org.cccnext.tesuto.delivery.service.AssessmentNotFoundException;
import org.cccnext.tesuto.delivery.service.AssessmentSessionNotFoundException;
import org.cccnext.tesuto.delivery.service.DeliveryServiceAdapter;
import org.cccnext.tesuto.delivery.service.TaskSetCompletionValidationException;
import org.cccnext.tesuto.delivery.view.TaskResponseViewDto;
import org.cccnext.tesuto.delivery.view.TaskSetViewDto;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public abstract class AssessmentSessionControllerBase  {

    abstract public DeliveryServiceAdapter getService();

    // Thrown by the DeliveryService if it can't find the assessment specified
    // for a new assessment session
    @ExceptionHandler(AssessmentNotFoundException.class)
    public ResponseEntity<?> assessmentNotFoundHandler(AssessmentNotFoundException ex) {
        return new ResponseEntity<>(error(ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    // Thrown by the DeliveryService when attempting to load a non-existent
    // assessment session
    @ExceptionHandler(AssessmentSessionNotFoundException.class)
    public ResponseEntity<?> assessmentSessionNotFoundHandler(AssessmentSessionNotFoundException ex) {
        return new ResponseEntity<>(error(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TaskSetCompletionValidationException.class)
    public ResponseEntity<?> invalidTaskSetCompletion(TaskSetCompletionValidationException ex) {
        return new ResponseEntity<>(error(ex.toString()), HttpStatus.NOT_ACCEPTABLE);
    }

    protected AssessmentSessionViewDto findAssessmentSession(String assessmentSessionId) {
        AssessmentSessionViewDto session = getService().find(assessmentSessionId);
        if (session == null) {
            throw new AssessmentSessionNotFoundException(assessmentSessionId);
        }
        return session;
    }

    public ResponseEntity<AssessmentSessionViewDto> get(String assessmentSessionId) {
        return new ResponseEntity<AssessmentSessionViewDto>(findAssessmentSession(assessmentSessionId), HttpStatus.OK);
    }

    public ResponseEntity<Void> putResponses(String assessmentSessionId, String taskSetId,
            Map<String, TaskResponseViewDto> responses) {
        AssessmentSessionViewDto session = findAssessmentSession(assessmentSessionId);
        if (session.getCurrentTaskSet() == null || !session.getCurrentTaskSet().getTaskSetId().equals(taskSetId)) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        } else {
            getService().mergeResponses(session, responses);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
    }

    public ResponseEntity<Void> putResponse(String assessmentSessionId, String taskSetId, String taskId,
            TaskResponseViewDto response) {
        AssessmentSessionViewDto session = findAssessmentSession(assessmentSessionId);
        if (session.getCurrentTaskSet() == null || session.getCurrentTaskSet().getTaskSetId() != taskSetId) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        } else {
            Map<String, TaskResponseViewDto> responses = new HashMap<>(1);
            responses.put(taskId, response);
            getService().mergeResponses(session, responses);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
    }

    public ResponseEntity<Void> postActivity(String assessmentSessionId, List<Activity> activities) {
        AssessmentSessionViewDto session = findAssessmentSession(assessmentSessionId);
        getService().addActivities(session, activities);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    public ResponseEntity<?> complete(String assessmentSessionId, String taskSetId,
            Map<String, TaskResponseViewDto> responses, UserAccountDto requestor) {
        AssessmentSessionViewDto session = findAssessmentSession(assessmentSessionId);
        if (!isValidSession(session,assessmentSessionId, taskSetId)) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        TaskSetViewDto next = getService().complete(session, responses, requestor);
        return new ResponseEntity<TaskSetViewDto>(next, HttpStatus.CREATED);
    }

    public ResponseEntity<?> getPrevious(String assessmentSessionId, String taskSetId,
            Map<String, TaskResponseViewDto> responses, UserAccountDto requestor) {
        AssessmentSessionViewDto session = findAssessmentSession(assessmentSessionId);
        if(session.getInternalSession().getNavigationMode() != AssessmentPartNavigationMode.NONLINEAR){
            log.debug("Attempting to get previous taskset on a linear assessment.  assessmentSessionId: {}" + assessmentSessionId);
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
        if (!isValidSession(session,assessmentSessionId, taskSetId)) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        TaskSetViewDto next = getService().getPrevious(session, responses, requestor);
        return new ResponseEntity<TaskSetViewDto>(next, HttpStatus.OK);
    }

    private boolean isValidSession(AssessmentSessionViewDto session, String assessmentSessionId, String taskSetId){
        if (session.getCurrentTaskSet() == null) {
            log.debug("Attempting to complete or computePreviousTaskSet taskSet for assessment session " + assessmentSessionId
                    + " but current task set is null");
            return false;
        }
        if (!session.getCurrentTaskSet().getTaskSetId().equals(taskSetId)) {
            log.debug("Attempting to complete or computePreviousTaskSet for taskSetId " + taskSetId + " from assessment session "
                    + assessmentSessionId + " but current taskSetId is " + session.getCurrentTaskSet().getTaskSetId());
            return false;
        }

        return true;
    }
    
    
    //Wrap an error message in something that Spring will serialize into the correct format
    public static Set<String> error(String message) {
        return Collections.singleton(message);
    }

}
