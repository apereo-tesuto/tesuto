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
package org.cccnext.tesuto.delivery.web.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.cccnext.tesuto.content.model.DeliveryType;
import org.cccnext.tesuto.delivery.controller.AssessmentSessionController;
import org.cccnext.tesuto.delivery.model.internal.Activity;
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.model.view.AssessmentSessionViewDto;
import org.cccnext.tesuto.delivery.view.TaskResponseViewDto;
import org.cccnext.tesuto.exception.ValidationException;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("service/v1/assessmentsessions")
public class AssessmentSessionEndpoint extends BaseController {

	@Autowired
	AssessmentSessionController controller;

	@PreAuthorize("hasAuthority('VIEW_PROCESSOR_DELIVERY_ASSESSMENT_SESSION')")
	@RequestMapping(value = "{assessmentSessionId}/processor", method = RequestMethod.GET)
	public ResponseEntity<AssessmentSessionViewDto> getAsProcessor(HttpSession httpSession,
			@PathVariable("assessmentSessionId") String assessmentSessionId,
			@RequestParam("assessedDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date assessedDate) {
		return controller.getAsProcessor(getUser(), httpSession, assessmentSessionId, assessedDate);
	}

	@PreAuthorize("hasAnyAuthority('VIEW_DELIVERY_ASSESSMENT_SESSION', 'API')")
	@RequestMapping(value = "{assessmentSessionId}", method = RequestMethod.GET)
	public ResponseEntity<AssessmentSessionViewDto> get(HttpSession httpSession,
			@PathVariable("assessmentSessionId") String assessmentSessionId) {
		return controller.get(assessmentSessionId);
	}

	@PreAuthorize("hasAuthority('VIEW_ALL_ASSESSMENT_SESSIONS')")
	@RequestMapping(value = "view", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody List<AssessmentSession> assessmentSessionView(@RequestParam(required = false) String userId,
			@RequestParam(required = false) String assessmentSessionId) {
		return controller.assessmentSessionView(userId, assessmentSessionId);
	}

	@PreAuthorize("hasAuthority('UPDATE_ASSESSMENT_SESSION_TASK_SET_RESPONSE')")
	@RequestMapping(value = "{assessmentSessionId}/{taskSetId}/response", method = RequestMethod.PUT)
	public ResponseEntity<Void> putResponses(HttpSession httpSession,
			@PathVariable("assessmentSessionId") String assessmentSessionId,
			@PathVariable("taskSetId") String taskSetId, @RequestBody Map<String, TaskResponseViewDto> responses) {
		return controller.putResponses(httpSession, assessmentSessionId, taskSetId, responses);
	}

	// The main purpose of this endpoint, really, is to show the structure of a
	// response in Swagger :)
	@PreAuthorize("hasAuthority('UPDATE_ASSESSMENT_SESSION_TASK_SET_RESPONSE')")
	@RequestMapping(value = "{assessmentSessionId}/{taskSetId}/response/{taskId}", method = RequestMethod.PUT)
	public ResponseEntity<Void> putResponse(HttpSession httpSession,
			@PathVariable("assessmentSessionId") String assessmentSessionId,
			@PathVariable("taskSetId") String taskSetId, @PathVariable("taskId") String taskId,
			@RequestBody TaskResponseViewDto response) {
		return controller.putResponse(httpSession, assessmentSessionId, taskSetId, taskId, response);
	}

	@PreAuthorize("hasAuthority('CREATE_ASSESSMENT_SESSION_ACTIVITY_ENTRY')")
	@RequestMapping(value = "{assessmentSessionId}/activity", method = RequestMethod.POST)
	public ResponseEntity<Void> postActivity(HttpSession httpSession,
			@PathVariable("assessmentSessionId") String assessmentSessionId, @RequestBody List<Activity> activities) {
		return controller.postActivity(assessmentSessionId, activities);
	}

	@PreAuthorize("hasAuthority('COMPLETE_ASSESSMENT_SESSION_TASK_SET')")
	@RequestMapping(value = "{assessmentSessionId}/{taskSetId}/completion", method = RequestMethod.POST)
	public ResponseEntity<?> complete(HttpSession httpSession,
			@PathVariable("assessmentSessionId") String assessmentSessionId,
			@PathVariable("taskSetId") String taskSetId, @RequestBody Map<String, TaskResponseViewDto> responses) {
		return controller.complete(getUser(), httpSession, assessmentSessionId, taskSetId, responses);
	}

	@PreAuthorize("hasAuthority('COMPLETE_PAPER_ASSESSMENT_SESSION_TASK_SET')")
	@RequestMapping(value = "{assessmentSessionId}/{taskSetId}/papercompletion", method = RequestMethod.POST)
	public ResponseEntity<?> completePaper(HttpSession httpSession,
			@PathVariable("assessmentSessionId") String assessmentSessionId,
			@PathVariable("taskSetId") String taskSetId, @RequestBody Map<String, TaskResponseViewDto> responses) {
		return controller.complete(getUser(), httpSession, assessmentSessionId, taskSetId, responses);
	}

	@PreAuthorize("hasAuthority('PREVIOUS_ASSESSMENT_SESSION_TASK_SET')")
	@RequestMapping(value = "{assessmentSessionId}/{taskSetId}/previous", method = RequestMethod.POST)
	public ResponseEntity<?> previous(HttpSession httpSession,
			@PathVariable("assessmentSessionId") String assessmentSessionId,
			@PathVariable("taskSetId") String taskSetId, @RequestBody Map<String, TaskResponseViewDto> responses) {
		return controller.getPrevious(assessmentSessionId, taskSetId, responses, null);
	}

	@PreAuthorize("hasAuthority('VIEW_COMPETENCY_MAP_DISCIPLINE_FROM_ENTRY_TESTLET')")
	@RequestMapping(value = "{assessmentSessionId}/competency-map-discipline-from-entry-testlet", method = RequestMethod.GET)
	public ResponseEntity<?> getEntryTestlet(@PathVariable("assessmentSessionId") String assessmentSessionId) {
		return controller.getEntryTestlet(assessmentSessionId);
	}

	@PreAuthorize("hasAuthority('REQUEST_PLACEMENTS_FOR_STUDENT')")
	@RequestMapping(value = "{cccid}/user", method = RequestMethod.GET)
	public ResponseEntity<?> sendRequestPlacementNotifications(@PathVariable("cccid") String cccid,
			@RequestParam(required = false, defaultValue = "true") boolean newPlacementsOnly) {
		return controller.sendRequestPlacementNotifications(cccid, newPlacementsOnly);
	}

	@PreAuthorize("hasAnyAuthority('API', 'CREATE_ACTIVATION')")
	@RequestMapping(value = "oauth2/{cccid}/{namespace}/{identifier}/{delivery-type}", method = RequestMethod.POST, produces = {
			"text/plain", "application/*" })
	public @ResponseBody String createUserAssessmentSession(@PathVariable("cccid") String cccid,
			@PathVariable("namespace") String namespace, @PathVariable("identifier") String identifier,
			@PathVariable("delivery-type") String deliveryType,
			@RequestBody(required = false) Map<String, String> settings) {
		try {
			return controller.createUserAssessementSession(cccid, namespace, identifier,
					DeliveryType.valueOf(deliveryType.toUpperCase()), settings);
		} catch (Exception exception) {
			throw new ValidationException("Unable to process due to " + exception.getMessage());
		}
	}

	@PreAuthorize("hasAnyAuthority('API','CREATE_ACTIVATION')")
	@RequestMapping(value = "oauth2/{namespace}/{identifier}", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> createContentAssessmentSession(@PathVariable("namespace") String namespace,
			@PathVariable("identifier") String identifier) {
		try {
			return controller.createContentAssessementSession(namespace, identifier);
		} catch (Exception exception) {
			throw new ValidationException("Unable to process due to " + exception.getMessage());
		}
	}
	
	@PreAuthorize("hasAnyAuthority('API','VIEW_DELIVERY_ASSESSMENT_SESSION')")
	@RequestMapping(value = "oauth2/assessment/version/{assessmentSessionId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> getAssessmentVersionForSession(@PathVariable("assessmentSessionId") String assessmentSessionId) {
		try {
			return controller.getAssessmentVersionForSession(assessmentSessionId);
		} catch (Exception exception) {
			throw new ValidationException("Unable to process due to " + exception.getMessage());
		}
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
