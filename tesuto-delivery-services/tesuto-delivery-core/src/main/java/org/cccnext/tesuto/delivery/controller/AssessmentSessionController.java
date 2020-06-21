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

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.cccnext.tesuto.activation.service.ActivationStatus;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.content.model.DeliveryType;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.delivery.model.internal.Activity;
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.model.view.AssessmentSessionViewDto;
import org.cccnext.tesuto.delivery.repository.mongo.AssessmentSessionRepository;
import org.cccnext.tesuto.delivery.service.AssessmentSessionUnauthorizedException;
import org.cccnext.tesuto.delivery.service.DeliveryServiceAdapter;
import org.cccnext.tesuto.delivery.service.PostDeliveryAssessmentCompletionService;
import org.cccnext.tesuto.delivery.service.util.TesutoUtil;
import org.cccnext.tesuto.delivery.view.ItemOutcomeViewDto;
import org.cccnext.tesuto.delivery.view.ItemScoreViewDto;
import org.cccnext.tesuto.delivery.view.PreviewScoringViewDto;
import org.cccnext.tesuto.delivery.view.TaskResponseViewDto;
import org.cccnext.tesuto.domain.dto.ScopedIdentifierDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AssessmentSessionController extends AssessmentSessionControllerBase {

    @Autowired
    @Qualifier("deliveryServiceAdapter")
    DeliveryServiceAdapter service;

    @Autowired
    AssessmentSessionRepository assessmentSessionRepository;

    @Autowired
    PostDeliveryAssessmentCompletionService postDeliveryService;

    @Autowired
    ActivationStatus activationStatus;

    @Override
    public DeliveryServiceAdapter getService() {
        return service;
    }

    // Thrown by an AssessmentSessionController if unauthorized access is
    // attempted to an assessment session
    @ExceptionHandler(AssessmentSessionUnauthorizedException.class)
    public ResponseEntity<?> unauthorizedAccessHandler(AssessmentSessionUnauthorizedException ex) {
        return new ResponseEntity<>(error(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    protected void authorize(HttpSession httpSession, String assessmentSessionId)
            throws AssessmentSessionUnauthorizedException {
        if (!TesutoUtil.hasAssessmentSessionPermission(httpSession, assessmentSessionId))
            throw new AssessmentSessionUnauthorizedException();
    }


    protected boolean isPaperPencilSession(AssessmentSessionViewDto assessmentSessionViewDto) {
        return assessmentSessionViewDto.getInternalSession().getDeliveryType() == DeliveryType.PAPER;
    }

    public ResponseEntity<AssessmentSessionViewDto> getAsProcessor(UserAccountDto userAccount, HttpSession httpSession, String assessmentSessionId, Date assessedDate) {
        boolean paperPencilOnly = false;
        try {
            authorize(httpSession, assessmentSessionId);
        } catch (Exception exception) {
            paperPencilOnly = true;
        }
        ResponseEntity<AssessmentSessionViewDto> assessmentSessionViewDto = super.get(assessmentSessionId);
        if (paperPencilOnly && !isPaperPencilSession(assessmentSessionViewDto.getBody())) {
            throw new AssessmentSessionUnauthorizedException();
        }
        activationStatus.assessedAssessment(assessmentSessionViewDto.getBody().getAssessmentSessionId(), userAccount, assessedDate);
        activationStatus.pendingAssessment(assessmentSessionViewDto.getBody().getAssessmentSessionId(), userAccount);
        return assessmentSessionViewDto;
    }

    public ResponseEntity<AssessmentSessionViewDto> get(HttpSession httpSession, String assessmentSessionId) {
        boolean paperPencilOnly = false;
        authorize(httpSession, assessmentSessionId);
        ResponseEntity<AssessmentSessionViewDto> assessmentSessionViewDto = super.get(assessmentSessionId);
        if (paperPencilOnly && !isPaperPencilSession(assessmentSessionViewDto.getBody())) {
            throw new AssessmentSessionUnauthorizedException();
        }
        return assessmentSessionViewDto;
    }

    public@ResponseBody List<AssessmentSession> assessmentSessionView(String userId,String assessmentSessionId) {
        List<AssessmentSession> assessmentSessionList = new LinkedList<>(); // null object pattern
        if (userId != null) {
            assessmentSessionList = assessmentSessionRepository.findByUserIdIgnoreCase(userId);
        } else if (assessmentSessionId != null) {
            assessmentSessionList = assessmentSessionRepository.findByAssessmentSessionId(assessmentSessionId);
        }
        return assessmentSessionList;
    }

    public ResponseEntity<Void> putResponses(HttpSession httpSession, String assessmentSessionId, String taskSetId,  Map<String, TaskResponseViewDto> responses) {
        authorize(httpSession, assessmentSessionId);
        return super.putResponses(assessmentSessionId, taskSetId, responses);
    }

    public ResponseEntity<Void> putResponse(HttpSession httpSession, String assessmentSessionId, String taskSetId, String taskId, TaskResponseViewDto response) {
        authorize(httpSession, assessmentSessionId);
        return super.putResponse(assessmentSessionId, taskSetId, taskId, response);
    }

    public ResponseEntity<Void> postActivity(HttpSession httpSession,String assessmentSessionId, List<Activity> activities) {
        authorize(httpSession, assessmentSessionId);
        return super.postActivity(assessmentSessionId, activities);
    }


    public ResponseEntity<?> complete(UserAccountDto userAccount, HttpSession httpSession, String assessmentSessionId, String taskSetId, Map<String, TaskResponseViewDto> responses) {
        authorize(httpSession, assessmentSessionId);
        return super.complete(assessmentSessionId, taskSetId, responses, userAccount);
    }

    public ResponseEntity<?> completePaper(UserAccountDto userAccount, HttpSession httpSession,String assessmentSessionId,String taskSetId, Map<String, TaskResponseViewDto> responses) {
        AssessmentSessionViewDto assessmentSession = findAssessmentSession(assessmentSessionId);
        if (!isPaperPencilSession(assessmentSession)) {
            throw new AssessmentSessionUnauthorizedException();
        }
        return super.complete(assessmentSessionId, taskSetId, responses, userAccount);
    }

    public ResponseEntity<?> previous(UserAccountDto userAccount, HttpSession httpSession,String assessmentSessionId, String taskSetId, Map<String, TaskResponseViewDto> responses) {
        authorize(httpSession, assessmentSessionId);
        return super.getPrevious(assessmentSessionId, taskSetId, responses, userAccount);
    }

    public ResponseEntity<?> getEntryTestlet(String assessmentSessionId){
        List<AssessmentSession> assessmentSessions = assessmentSessionRepository.findCompetencyMapDisciplineFromEntryTestletByAssessmentSessionId(assessmentSessionId);
        if(assessmentSessions == null){
            return new ResponseEntity<>(assessmentSessions, HttpStatus.NOT_FOUND);
        }else {
            String entryTestlet = assessmentSessions.get(0).getCompetencyMapDisciplineFromEntryTestlet();
            HttpStatus status = (entryTestlet == null) ? HttpStatus.NO_CONTENT : HttpStatus.OK;  // null is expected
            return new ResponseEntity<>(entryTestlet, status);
        }
    }

    public ResponseEntity<?> sendRequestPlacementNotifications(String cccid,boolean newPlacementsOnly){
        assessmentSessionRepository.findByUserIdIgnoreCase(cccid).stream().forEach(a -> postDeliveryService.requestPlacements(a, newPlacementsOnly));
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    public String createUserAssessementSession(String cccid,
    		String namespace,
    		String identifier,
    		DeliveryType deliveryType,
            Map<String,String> settings){
        ScopedIdentifierDto scopedIdentifier = new ScopedIdentifierDto(namespace, identifier);
    	String assessmentSessionId = service.createUserAssessmentSession(cccid, scopedIdentifier, deliveryType, settings);
    	return assessmentSessionId;
    }
    
    public ResponseEntity<?> createContentAssessementSession(String namespace,
    		String identifier){
    	Object assessmentSessionView = service.createContentAssessmentSession(namespace, identifier);
    	return new ResponseEntity<>(assessmentSessionView, HttpStatus.OK);
    }
    
    public ResponseEntity<?> getAssessmentVersionForSession(String sessionId) {
    	return new ResponseEntity<>(service.getAssessmentVersionForSession(sessionId), HttpStatus.OK);
    }
    
    public ResponseEntity<?> checkTaskSetScore(@PathVariable("assessmentSessionId") String assessmentSessionId,
            @PathVariable("taskSetId") String taskSetId, @RequestBody Map<String, TaskResponseViewDto> responses) {
        log.debug("POST score");
        List<ItemScoreViewDto> scores = service.checkResponseScores(service.find(assessmentSessionId), taskSetId,
                responses);
        Collection<ItemOutcomeViewDto> outcomes = service.checkOutcomes(scores);
        PreviewScoringViewDto scoring = new PreviewScoringViewDto();
        scoring.setScores(scores);
        scoring.setOutcomes(outcomes);
        return new ResponseEntity<PreviewScoringViewDto>(scoring, HttpStatus.OK);
    }

    public ResponseEntity<?> checkTaskScore(@PathVariable("assessmentSessionId") String assessmentSessionId,
            @PathVariable("taskSetId") String taskSetId, @PathVariable("taskId") String taskId,
            @RequestBody Map<String, TaskResponseViewDto> responses) {
        log.debug("POST score");

        List<ItemScoreViewDto> scores = service.checkResponseScores(service.find(assessmentSessionId), taskSetId,
                taskId, responses);
        Collection<ItemOutcomeViewDto> outcomes = service.checkOutcomes(scores);
        PreviewScoringViewDto scoring = new PreviewScoringViewDto();
        scoring.setScores(scores);
        scoring.setOutcomes(outcomes);
        return new ResponseEntity<PreviewScoringViewDto>(scoring, HttpStatus.OK);
    }
}
