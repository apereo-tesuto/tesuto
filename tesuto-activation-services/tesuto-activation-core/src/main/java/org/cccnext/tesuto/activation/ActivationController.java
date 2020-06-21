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
package org.cccnext.tesuto.activation;

import static org.cccnext.tesuto.activation.model.Activation.Status.IN_PROGRESS;
import static org.cccnext.tesuto.activation.model.Activation.Status.READY;

import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.cccnext.tesuto.activation.PasscodeValidationAttempt.ValidationResult;
import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.content.model.DeliveryType;
import org.cccnext.tesuto.delivery.service.util.TesutoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j 
@Service
public class ActivationController {

	
    private final static String ACTIVATION_PERMISSION_KEY = "tesuto-activation-permissions";
    private final static String APPROVING_PROCTOR_KEY = "tesuto-approving-proctor";

    @Autowired
    private ActivationService service;

    @Autowired
    private ActivationSearchService searchService;

    @Autowired
    private PasscodeService passcodeService;

    @Autowired
    private RemotePasscodeService remotePasscodeService;


    private boolean locationAuthorized(UserAccountDto user, String locationId) {
        if (user.getTestLocations() == null) {
            return false;
        } else {
            return locationId != null && user.getTestLocations().stream().anyMatch(location ->
                    locationId.equals(location.getId())
            );
        }
    }

    private boolean isStudentAuthorizedFor(UserAccountDto user, Activation activation) {
        //Note: this is secure as long a students can not be created through client with out adding a suffix
        //Currently this is impossible and not expected to change.
        return activation.getDeliveryType().equals(DeliveryType.ONLINE) && activation.getUserId().equals(user.getUsername());
    }


    private Activation findProctorAuthorizedActivation(UserAccountDto user, String activationId) throws AccessDeniedException {
        Activation activation = service.find(activationId);
        if (!locationAuthorized(user, activation.getLocationId())) {
            throw new AccessDeniedException("forbidden");
        }
        return activation;
    }

    private Activation findStudentAuthorizedActivation(UserAccountDto user,String activationId) throws AccessDeniedException {
        Activation activation = service.find(activationId);
        if (!isStudentAuthorizedFor(user, activation)) {
            throw new AccessDeniedException("forbidden");
        }
        return activation;
    }
    
    public  ResponseEntity<?> get(String activationId) throws AccessDeniedException {
    	return new ResponseEntity<>(service.find(activationId), HttpStatus.OK);
    }
    
    //Wrap an error message in something that Spring will serialize into the correct format
    public static Set<String> error(String message) {
        return Collections.singleton(message);
    }


    public ResponseEntity<?> getAsProctor(UserAccountDto user, String activationId) throws AccessDeniedException {
        Activation activation = findProctorAuthorizedActivation(user, activationId);
        return new ResponseEntity<>(activation, HttpStatus.OK);
    }

    public ResponseEntity<?> get(UserAccountDto user, String activationId) throws AccessDeniedException {
        Activation activation = findStudentAuthorizedActivation(user, activationId);
        return new ResponseEntity<>(activation, HttpStatus.OK);
    }

    public ResponseEntity<?> post(UserAccountDto user, ProtoActivation activation, UriComponentsBuilder uriBuilder) {
        if (!locationAuthorized(user, activation.getLocationId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        String errors = "";
        if (activation.getAssessmentScopedIdentifier() == null) {
            errors += "AssessmentScopedIdentifier must be specified.  ";
        }
        if (activation.getUserId() == null) {
            errors += "UserId must be specified.  ";
        }
        if (errors.length() > 0) {
            return new ResponseEntity<>(error(errors), HttpStatus.BAD_REQUEST);
        }
        if(activation.getStartDate() == null){
            activation.setStartDate(new Date());
        }

        if(activation.getDeliveryType() == null){
            activation.setDeliveryType(DeliveryType.ONLINE);
        }
        String id = service.create(user.getUserAccountId(), activation);
        HttpHeaders headers = new HttpHeaders();
        URI locationUri = uriBuilder.path("/service/v1/activations/" + id).build().toUri();
        headers.setLocation(locationUri);
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    public ResponseEntity<?> update(UserAccountDto user, ProtoActivation activation, UriComponentsBuilder uriBuilder) {
        if (!locationAuthorized(user, activation.getLocationId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        service.update(user, activation);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public ResponseEntity<?> cancel(UserAccountDto user, String activationId,
            @RequestParam(name="reason", required=false) String reason
    ) {
        service.cancel(activationId, user, reason);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<?> pause(UserAccountDto user, String activationId,
                                      HttpSession session) {
        Activation activation = service.find(activationId);
        if (!IN_PROGRESS.equals(activation.getStatus())
                || !TesutoUtil.hasAssessmentSessionPermission(session, activation.getCurrentAssessmentSessionId())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        service.pause(activation, user);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<?> reactivation(UserAccountDto user, String activationId) {
        Activation activation = findProctorAuthorizedActivation(user, activationId);
        if (service.isTestEventActivation(activation)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            service.reactivate(activation, user);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    public ResponseEntity<?> pauseByAssessmentSessionId(UserAccountDto user, String assessmentSessionId,
                                      HttpSession session) {
        //The code is written as if there could be a collection of activations but, in fact, there should at most one
        Collection<Activation> activations = service.findActivationsByAssessmentSessionId(assessmentSessionId);

        if (activations.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        for (Activation activation : activations) {
            if (!IN_PROGRESS.equals(activation.getStatus())
                    || !TesutoUtil.hasAssessmentSessionPermission(session, activation.getCurrentAssessmentSessionId())) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        for (Activation activation : activations) {
            service.pause(activation,user);
        }
        TesutoUtil.resetAssessmentSessionPermissions(session);;
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<?> launchPaperActivation(UserAccountDto user, HttpServletRequest request,
            String activationId,
            int assessmentVersion,
            HttpSession session, UriComponentsBuilder uriBuilder)
            throws AccessDeniedException {
        if (StringUtils.isBlank(activationId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Activation activation = findProctorAuthorizedActivation(user, activationId);

        if (!activation.getDeliveryType().equals(DeliveryType.PAPER)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        String assessmentSessionId = service.launch(activation, assessmentVersion, user, user.getUserAccountId());
        TesutoUtil.resetAssessmentSessionPermissions(session); //Only access one assessment session at a time
        TesutoUtil.addAssessmentSessionPermission(session, assessmentSessionId, activation.getDeliveryType());

        return new ResponseEntity<>(assessmentSessionId, HttpStatus.CREATED);
    }


    public Activation launchActivation(UserAccountDto user, HttpServletRequest request,
            @PathVariable("activationId") String activationId, HttpSession session, UriComponentsBuilder uriBuilder)
            throws Exception {
    	
        Activation activation = findStudentAuthorizedActivation(user, activationId);

        if (service.requiresPasscode(activation)) {
            Set<String> permissions = (Set<String>) session.getAttribute(ACTIVATION_PERMISSION_KEY);
            //Any permission is good enough to start a READY assessment session, but if resuming an assessment
            //the activation must be specifically authorized.
            if (permissions == null) {
            	throw new Exception("No permissions");
            } else if (!activation.getStatus().equals(READY) && !permissions.contains(activationId)) {
            	throw new Exception("Must enter a passcode to restart");
            }
        }
        return activation;
    }
    
    public String launchAssessmentSession(UserAccountDto user, Activation activation, String proctorId) {
    	return service.launch(activation, user, proctorId);	
    }

    
    public ResponseEntity<?> getMyCurrentActivations(String currentUsername) {
        SearchParameters parameters = new SearchParameters();
        parameters.addUserId(currentUsername);
        Date now = new Date();
        parameters.setMaxStartDate(now);
        parameters.setMinEndDate(now);
        Set<Activation> activations = searchService.search(parameters);
        return new ResponseEntity<Set<Activation>>(activations, HttpStatus.OK);
    }
    
    public ResponseEntity<?> getAllMyActivations(String currentUsername) {
        SearchParameters parameters = new SearchParameters();
        parameters.addUserId(currentUsername);
        parameters.setIncludeCanceled(true);
        Set<Activation> activations = searchService.search(parameters);
        return new ResponseEntity<>(activations, HttpStatus.OK);
    }

    public ResponseEntity<?> validatePasscode(UserAccountDto currentUser, String activationId,
                                              @RequestBody String passcode, HttpSession session) throws AccessDeniedException {

        Activation activation = null;
        try {
            activation = findStudentAuthorizedActivation(currentUser, activationId);
        } catch (ActivationNotFoundException e) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        return checkPasscodeValidation(activation, passcode, activationId, session);
    }

    private ResponseEntity<Boolean> checkPasscodeValidation(Activation activation, String passcode, String activationId, HttpSession session) {

        ValidationResult result = null;
        String proctorId = null;
        String eventId = null;

        switch (activation.getStatus()) {
        case READY:
            if (session.getAttribute(ACTIVATION_PERMISSION_KEY) != null) {
                //a passcode has already been entered in this http session, no need to enter another
                result = ValidationResult.VALID;
            } else {
                Pair<ValidationResult, String> pair = new ImmutablePair<>(null, null);
                if (remotePasscodeService.isRemotePasscode(passcode) && activation instanceof TestEventActivation) {
                    pair = remotePasscodeService.attemptValidation((TestEventActivation) activation, passcode);
                    eventId = pair.getRight();
                } else {
                    pair = passcodeService.attemptValidation(activation, passcode, false);
                    proctorId = pair.getRight();
                }
                result = pair.getLeft();
            }
            break;
        case IN_PROGRESS:
        case PAUSED:
            Pair<ValidationResult, String> pair = new ImmutablePair<>(null, null);
            if (remotePasscodeService.isRemotePasscode(passcode) && activation instanceof TestEventActivation) {
                pair = remotePasscodeService.attemptValidation((TestEventActivation) activation, passcode);
                eventId = pair.getRight();
            } else {
                //Restarts are subject to a more stringent check
                pair = passcodeService.attemptValidation(activation, passcode, true);
                proctorId = pair.getRight();
            }
            result = pair.getLeft();
            break;
        default:
            result = ValidationResult.INVALID;
        }

        if (result == ValidationResult.INVALID || result == ValidationResult.PRIVATE_PASSCODE_REQUIRED) {
            return new ResponseEntity<Boolean>(false, HttpStatus.FORBIDDEN);
        } else if (result == ValidationResult.LOCKED) {
            // We might be abusing this http code, but it's convenient to have a
            // special code for this condition
            return new ResponseEntity<Boolean>(false, HttpStatus.TOO_MANY_REQUESTS);
        } else if (result == ValidationResult.VALID) {
            // Overwrites any existing permissions
            Set<String> permissions = new HashSet<>(1);
            permissions.add(activationId);
            session.setAttribute(ACTIVATION_PERMISSION_KEY, permissions);
            session.setAttribute(APPROVING_PROCTOR_KEY, proctorId);
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
        } else {
            log.error("Unrecognized validation result code " + result);
            return new ResponseEntity<Boolean>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    public void report(HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = "year", required = true) int year,
            @RequestParam(value = "month", required = true) int month,
            @RequestParam(value = "day", required = true) int day,
            @RequestParam(value = "timeZone", required = false, defaultValue = "0") int timeZone,
            @RequestParam(value = "duration", required = false, defaultValue = "24") int duration) throws IOException {

        month = month - 1; // Gregorian calendar months are 0 based
        Calendar from = new GregorianCalendar(year, month, day);
        from.add(Calendar.HOUR, timeZone);
        Calendar to = new GregorianCalendar(year, month, day);
        to.add(Calendar.HOUR, timeZone + duration);
        String report = String.join("\n", service.reportAsCsv(from, to)) + "\n";

        response.setContentType("text/csv");
        response.setContentLength(report.length());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=\"locations-" + year + month + day + ".csv\"";
        response.setHeader(headerKey, headerValue);

        // get output stream of the response
        response.getOutputStream().print(report);
        response.getOutputStream().close();
    }


    public ResponseEntity<?> reportStatus(HttpServletResponse response,
            @RequestParam(value = "userIds", required = false) Set<String> userIds,
            @RequestParam(value = "locationIds", required = false) Set<String> locationIds,
            @RequestParam(value = "creatorIds", required = false) Set<String> creatorIds) throws IOException {

        SearchParameters parameters = new SearchParameters();
        parameters.setUserIds(userIds);
        parameters.setLocationIds(locationIds);
        parameters.setCreatorIds(creatorIds);

        if (!parameters.isValid()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // If no activations well generate empty report
        Set<Activation> activations = searchService.search(parameters);

        String report = String.join("\n", service.activationStatusChangeReport(activations)) + "\n";

        response.setContentType("text/csv");
        response.setContentLength(report.length());

        Date date = new Date();

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=\"status-change " + date.toString() + ".csv\"";

        response.setHeader(headerKey, headerValue);

        response.getOutputStream().print(report);
        response.getOutputStream().close();

        return new ResponseEntity<>(true, HttpStatus.OK);
    }
    
    public Set<Activation> getActivations(Set<String> activationIds) {
    	return service.find(activationIds);
    }
    
    public void completeAssessment(String assessmentSessionId,  UserAccountDto requestor) {
    	service.completeAssessment(assessmentSessionId, requestor);
    }
    
    public void pendingAssessment(String assessmentSessionId, UserAccountDto requestor) {
    	service.pendingAssessment(assessmentSessionId, requestor);
    }
    
    public void assessedAssessment(String assessmentSessionId, UserAccountDto requestor, Date statusChangeDate) {
    	service.assessedAssessment(assessmentSessionId, requestor, statusChangeDate);
    }
    
    
    
}
