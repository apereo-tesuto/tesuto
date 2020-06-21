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
package org.cccnext.tesuto.activation.web.controller;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.activation.ActivationController;
import org.cccnext.tesuto.activation.ProtoActivation;
import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.content.client.AssessmentReaderRestClient;
import org.cccnext.tesuto.content.model.DeliveryType;
import org.cccnext.tesuto.delivery.client.AssessmentSessionRestClient;
import org.cccnext.tesuto.delivery.service.util.TesutoUtil;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.cccnext.tesuto.springboot.web.RequestForwardService;
import org.cccnext.tesuto.web.client.WebUIUriBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping(value = "service/v1/activations")
public class ActivationEndpoint extends BaseController {

    private final static String ACTIVATION_PERMISSION_KEY = "tesuto-activation-permissions";
    private final static String APPROVING_PROCTOR_KEY = "tesuto-approving-proctor";

    @Autowired
    private ActivationController controller;
    
    @Autowired
    private RequestForwardService requestForwardService;
    
    @Autowired
    private AssessmentSessionRestClient deliveryService;
    
    @Autowired
    WebUIUriBuilder webUriBuilder;
    

 

    @RequestMapping(value = "{activationId}/proctor", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAuthority('VIEW_PROCTOR_ACTIVATION')")
    public ResponseEntity<?> getAsProctor(@PathVariable("activationId") String activationId) throws AccessDeniedException {
        return controller.getAsProctor(getUser(), activationId);
    }

    @RequestMapping(value = "{activationId}", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAnyAuthority('VIEW_ACTIVATION','API')")
    public ResponseEntity<?> get(@PathVariable("activationId") String activationId) throws AccessDeniedException {
       return controller.get(getUser(), activationId);
    }
    
    @RequestMapping(value = "api/{activationId}", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAuthority('API')")
    public ResponseEntity<?> apiGet(@PathVariable("activationId") String activationId) throws AccessDeniedException {
       return controller.get(activationId);
    }
 

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    @PreAuthorize("hasAuthority('CREATE_ACTIVATION')")
    public ResponseEntity<?> post(@RequestBody ProtoActivation activation, UriComponentsBuilder uriBuilder) {
        return controller.post(getUser(), activation, uriBuilder);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT, produces = "application/json")
    @PreAuthorize("hasAuthority('UPDATE_ACTIVATION')")
    public ResponseEntity<?> update(@RequestBody ProtoActivation activation, UriComponentsBuilder uriBuilder) {
        return controller.update(getUser(), activation, uriBuilder);
    }

    @RequestMapping(value = "{activationId}/cancellation", method = RequestMethod.PUT, produces = "application/json")
    @PreAuthorize("hasAuthority('CANCEL_ACTIVATION')")
    public ResponseEntity<?> cancel(
            @PathVariable("activationId") String activationId,
            @RequestParam(name="reason", required=false) String reason
    ) {
        return controller.cancel(getUser(), activationId, reason);
    }

    @RequestMapping(value = "{activationId}/pause", method = RequestMethod.PUT, produces = "application/json")
    @PreAuthorize("hasAuthority('PAUSE_ACTIVATION')")
    public ResponseEntity<?> pause(@PathVariable("activationId") String activationId,
                                      HttpSession session) {
       return controller.pause(getUser(), activationId, session);
    }

    @RequestMapping(value = "{activationId}/reactivate", method = RequestMethod.PUT, produces = "application/json")
    @PreAuthorize("hasAuthority('REACTIVATE_ACTIVATION')")
    public ResponseEntity<?> reactivation(@PathVariable("activationId") String activationId) {
        return controller.reactivation(getUser(), activationId);
    }

    @RequestMapping(value = "pause", method = RequestMethod.PUT, produces = "application/json")
    @PreAuthorize("hasAuthority('PAUSE_ACTIVATION')")
    public ResponseEntity<?> pauseByAssessmentSessionId(@RequestParam("assessmentSessionId") String assessmentSessionId,
                                      HttpSession session) {
        //The code is written as if there could be a collection of activations but, in fact, there should at most one
        return controller.pauseByAssessmentSessionId(getUser(), assessmentSessionId, session);
    }

    @RequestMapping(value="{activationId}/launch/paper", method = RequestMethod.POST, produces = "text/plain")
    @PreAuthorize("hasAuthority('LAUNCH_PAPER_ACTIVATION')")
    public ResponseEntity<?> launchPaperActivation(HttpServletRequest request,
            @PathVariable("activationId") String activationId,
            @RequestParam(value = "version", required = true) int assessmentVersion,
            HttpSession session, UriComponentsBuilder uriBuilder)
            throws AccessDeniedException {
        return controller.launchPaperActivation(getUser(), request, activationId, assessmentVersion, session, uriBuilder);
    }

    @RequestMapping(value = "{activationId}/launch", method = RequestMethod.POST, produces = "text/plain")
    @PreAuthorize("hasAuthority('LAUNCH_ACTIVATION')")
    public ResponseEntity<?> launchActivation(HttpServletRequest request,
            @PathVariable("activationId") String activationId, HttpSession session, UriComponentsBuilder uriBuilder)
            throws AccessDeniedException {
    	if (StringUtils.isBlank(activationId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    	UserAccountDto user = getUser();
        Activation activation;
		try {
			activation = controller.launchActivation(user, request, activationId, session, uriBuilder);
		} catch (Exception exception) {
			return new ResponseEntity<>(error(exception.getMessage()),HttpStatus.FORBIDDEN);
		}
        String proctorId = (String)session.getAttribute(APPROVING_PROCTOR_KEY);
        String assessmentSessionId = controller.launchAssessmentSession(user, activation, proctorId);
        
        String jspUrl = getActivationLaunchUrl(activation,  assessmentSessionId,  session,  request, uriBuilder,  proctorId);
        return new ResponseEntity<>(jspUrl, HttpStatus.OK);
    }

    private String getActivationLaunchUrl(Activation activation, String assessmentSessionId, HttpSession session, HttpServletRequest request,
                                          UriComponentsBuilder uriBuilder, String proctorId) {
        TesutoUtil.resetAssessmentSessionPermissions(session); //Only access one assessment session at a time
        TesutoUtil.addAssessmentSessionPermission(session, assessmentSessionId, activation.getDeliveryType());

        HttpHeaders headers = new HttpHeaders();
        //String getUrl = requestForwardService.getForwardUrl(request,  "/assessmentsessions/" + assessmentSessionId);
        
        URI locationUri = deliveryService.endpointBuilder("assessmentsessions",assessmentSessionId).build().toUri();
        headers.setLocation(locationUri);
        //String jspUrl = requestForwardService.getForwardUrl(request, "/assessment/");
        
        if (activation.getDeliveryType().equals(DeliveryType.PAPER)) {
        	return webUriBuilder.buildAssessmentPrintUrl(assessmentSessionId);
        }
        return webUriBuilder.buildAssessmentUrl(assessmentSessionId);
    }
    @PreAuthorize("hasAuthority('VIEW_CURRENT_USER_ACTIVE_ACTIVATIONS')")
    @RequestMapping(value = "mine/active", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getMyCurrentActivations() {
        return controller.getMyCurrentActivations(getCurrentUsername());
    }

    @PreAuthorize("hasAuthority('VIEW_CURRENT_USER_ACTIVATIONS')")
    @RequestMapping(value = "mine/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getAllMyActivations() {
        return controller.getAllMyActivations(getCurrentUsername());
    }

    @PreAuthorize("hasAuthority('AUTHORIZE_ACTIVATION')")
    @RequestMapping(value = "{activationId}/authorize", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<?> validatePasscode(@PathVariable("activationId") String activationId,
                                              @RequestBody String passcode, HttpSession session) throws AccessDeniedException {

        return controller.validatePasscode(getUser(),activationId, passcode, session);
    }



    @RequestMapping(value = "report", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAuthority('VIEW_ACTIVATIONS_REPORT')")
    public void report(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "year", required = true) int year,
            @RequestParam(value = "month", required = true) int month,
            @RequestParam(value = "day", required = true) int day,
            @RequestParam(value = "timeZone", required = false, defaultValue = "0") int timeZone,
            @RequestParam(value = "duration", required = false, defaultValue = "24") int duration) throws IOException {

       controller.report(request, response, year, month, day, timeZone, duration);
    }

    @RequestMapping(value = "status-change", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAuthority('VIEW_ACTIVATION_STATUS_CHANGE_REPORT')")
    public ResponseEntity<?> reportStatus(HttpServletResponse response,
            @RequestParam(value = "userIds", required = false) Set<String> userIds,
            @RequestParam(value = "locationIds", required = false) Set<String> locationIds,
            @RequestParam(value = "creatorIds", required = false) Set<String> creatorIds) throws IOException {

        return  controller.reportStatus(response, userIds, locationIds, creatorIds);
    }
    
    @PreAuthorize("hasAnyAuthority('API', 'VIEW_ACTIVATIONS_REPORT')")
    @RequestMapping(value = "", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getActivations(@RequestParam("activationIds") Set<String> activationIds) {
    	return new ResponseEntity<>(controller.getActivations(activationIds), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('API')")
    @RequestMapping(value = "complete/{assessmentSessionId}/oauth2", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Void> completeAssessment(@PathVariable("assessmentSessionId") String assessmentSessionId, @RequestBody UserAccountDto requestor) {
    	controller.completeAssessment(assessmentSessionId, requestor);
    	return new ResponseEntity<Void>(HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('API')")
    @RequestMapping(value = "pending/{assessmentSessionId}/oauth2", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Void> pendingAssessments(@PathVariable("assessmentSessionId") String assessmentSessionId, @RequestBody UserAccountDto requestor) {
    	controller.pendingAssessment(assessmentSessionId, requestor);
    	return new ResponseEntity<Void>(HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyAuthority('API')")
    @RequestMapping(value = "assessed/{assessmentSessionId}/{status-change-date}/oauth2", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Void> assessedAssessments(@PathVariable("assessmentSessionId") String assessmentSessionId, @PathVariable("status-change-date") Long statusChangeDate, @RequestBody UserAccountDto requestor) {
    	controller.assessedAssessment(assessmentSessionId, requestor, new Date(statusChangeDate));
    	return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
