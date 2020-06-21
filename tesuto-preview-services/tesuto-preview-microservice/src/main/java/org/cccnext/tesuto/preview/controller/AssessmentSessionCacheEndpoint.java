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

import java.util.Map;

import org.cccnext.tesuto.delivery.model.view.AssessmentSessionViewDto;
import org.cccnext.tesuto.delivery.view.TaskResponseViewDto;
import org.cccnext.tesuto.preview.controller.AssessmentSessionCacheController;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("service/v1/preview/assessmentsessions")
public class AssessmentSessionCacheEndpoint extends BaseController {

    @Autowired
    AssessmentSessionCacheController controller;

    @PreAuthorize("hasAuthority('VIEW_PREVIEW_ASSESSMENT_SESSION')")
    @RequestMapping(value = "{assessmentSessionId}", method = RequestMethod.GET)
    public ResponseEntity<AssessmentSessionViewDto> get(
            @PathVariable("assessmentSessionId") String assessmentSessionId) {
        return controller.get(assessmentSessionId);
    }

    @PreAuthorize("hasAuthority('UPDATE_PREVIEW_ASSESSMENT_SESSION_TASK_SET_RESPONSE')")
    @RequestMapping(value = "{assessmentSessionId}/{taskSetId}/response", method = RequestMethod.PUT)
    public ResponseEntity<Void> putResponses(@PathVariable("assessmentSessionId") String assessmentSessionId,
            @PathVariable("taskSetId") String taskSetId, @RequestBody Map<String, TaskResponseViewDto> responses) {
        return controller.putResponses(assessmentSessionId, taskSetId, responses);
    }

    // The main purpose of this endpoint, really, is to show the structure of a
    // response in Swagger :)
    @PreAuthorize("hasAuthority('UPDATE_PREVIEW_ASSESSMENT_SESSION_TASK_SET_RESPONSE')")
    @RequestMapping(value = "{assessmentSessionId}/{taskSetId}/response/{taskId}", method = RequestMethod.PUT)
    public ResponseEntity<Void> putResponse(@PathVariable("assessmentSessionId") String assessmentSessionId,
            @PathVariable("taskSetId") String taskSetId, @PathVariable("taskId") String taskId,
            @RequestBody TaskResponseViewDto response) {
        return controller.putResponse(assessmentSessionId, taskSetId, taskId, response);
    }

    @PreAuthorize("hasAuthority('COMPLETE_PREVIEW_ASSESSMENT_SESSION_TASK_SET')")
    @RequestMapping(value = "{assessmentSessionId}/{taskSetId}/completion", method = RequestMethod.POST)
    public ResponseEntity<?> complete(@PathVariable("assessmentSessionId") String assessmentSessionId,
            @PathVariable("taskSetId") String taskSetId, @RequestBody Map<String, TaskResponseViewDto> responses) {
        log.debug("POST completion");
        return controller.complete(assessmentSessionId, taskSetId, responses, getUser());
    }

    @PreAuthorize("hasAuthority('PREVIOUS_PREVIEW_ASSESSMENT_SESSION_TASK_SET')")
    @RequestMapping(value = "{assessmentSessionId}/{taskSetId}/previous", method = RequestMethod.POST)
    public ResponseEntity<?> getPrevious(@PathVariable("assessmentSessionId") String assessmentSessionId,
            @PathVariable("taskSetId") String taskSetId, @RequestBody Map<String, TaskResponseViewDto> responses) {
        log.debug("POST previous");
        return controller.getPrevious(assessmentSessionId, taskSetId, responses, getUser());
    }

    @PreAuthorize("hasAuthority('CHECK_PREVIEW_ASSESSMENT_SESSION_TASK_SET_SCORE')")
    @RequestMapping(value = "{assessmentSessionId}/{taskSetId}/score", method = RequestMethod.POST)
    public ResponseEntity<?> checkTaskSetScore(@PathVariable("assessmentSessionId") String assessmentSessionId,
            @PathVariable("taskSetId") String taskSetId, @RequestBody Map<String, TaskResponseViewDto> responses) {
        log.debug("POST score");
       return controller.checkTaskSetScore(assessmentSessionId, taskSetId, responses);
    }

    @PreAuthorize("hasAuthority('CHECK_PREVIEW_ASSESSMENT_SESSION_TASK_SCORE')")
    @RequestMapping(value = "{assessmentSessionId}/{taskSetId}/{taskId}/score", method = RequestMethod.POST)
    public ResponseEntity<?> checkTaskScore(@PathVariable("assessmentSessionId") String assessmentSessionId,
            @PathVariable("taskSetId") String taskSetId, @PathVariable("taskId") String taskId,
            @RequestBody Map<String, TaskResponseViewDto> responses) {
        log.debug("POST score");

        return controller.checkTaskScore(assessmentSessionId, taskSetId, taskId, responses);
    }
}
