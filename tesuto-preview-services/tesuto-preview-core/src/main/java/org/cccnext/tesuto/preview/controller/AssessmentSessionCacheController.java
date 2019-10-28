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
package org.cccnext.tesuto.preview.controller;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.delivery.controller.AssessmentSessionControllerBase;
import org.cccnext.tesuto.delivery.model.view.AssessmentSessionViewDto;
import org.cccnext.tesuto.delivery.service.DeliveryServiceAdapter;
import org.cccnext.tesuto.delivery.service.scoring.AssessmentItemScoringService;
import org.cccnext.tesuto.delivery.view.ItemOutcomeViewDto;
import org.cccnext.tesuto.delivery.view.ItemScoreViewDto;
import org.cccnext.tesuto.delivery.view.PreviewScoringViewDto;
import org.cccnext.tesuto.delivery.view.TaskResponseViewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AssessmentSessionCacheController extends AssessmentSessionControllerBase {

    @Autowired
    @Qualifier("deliveryCacheServiceAdapter")
    DeliveryServiceAdapter service;

    @Autowired
    @Qualifier("assessmentItemScoringService")
    AssessmentItemScoringService scoringService;

    @Override
    public DeliveryServiceAdapter getService() {
        return service;
    }

    @Override
    public ResponseEntity<AssessmentSessionViewDto> get(
            @PathVariable("assessmentSessionId") String assessmentSessionId) {
        return super.get(assessmentSessionId);
    }

    @Override
    public ResponseEntity<Void> putResponses(@PathVariable("assessmentSessionId") String assessmentSessionId,
            @PathVariable("taskSetId") String taskSetId, @RequestBody Map<String, TaskResponseViewDto> responses) {
        return super.putResponses(assessmentSessionId, taskSetId, responses);
    }

    // The main purpose of this endpoint, really, is to show the structure of a
    // response in Swagger :)
    @Override    public ResponseEntity<Void> putResponse(@PathVariable("assessmentSessionId") String assessmentSessionId,
            @PathVariable("taskSetId") String taskSetId, @PathVariable("taskId") String taskId,
            @RequestBody TaskResponseViewDto response) {
        return super.putResponse(assessmentSessionId, taskSetId, taskId, response);
    }

    public ResponseEntity<?> complete(UserAccountDto userAccount, @PathVariable("assessmentSessionId") String assessmentSessionId,
            @PathVariable("taskSetId") String taskSetId, @RequestBody Map<String, TaskResponseViewDto> responses) {
        log.debug("POST completion");
        return super.complete(assessmentSessionId, taskSetId, responses, userAccount);
    }

    public ResponseEntity<?> getPrevious(UserAccountDto userAccount, String assessmentSessionId,
            @PathVariable("taskSetId") String taskSetId, @RequestBody Map<String, TaskResponseViewDto> responses) {
        log.debug("POST previous");
        return super.getPrevious(assessmentSessionId, taskSetId, responses, userAccount);
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
