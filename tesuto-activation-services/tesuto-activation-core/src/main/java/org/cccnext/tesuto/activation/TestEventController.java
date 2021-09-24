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

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

import org.cccnext.tesuto.activation.model.TestEvent;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.service.CollegeReader;
import org.cccnext.tesuto.admin.viewdto.CollegeViewDto;
import org.cccnext.tesuto.admin.viewdto.RemoteProctorDto;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.remoteproctor.service.EmailSenderService;
import org.cccnext.tesuto.remoteproctor.service.RemoteProctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Created by bruce on 11/18/16.
 */
@Service
public class TestEventController {

    @Autowired ActivationService activationService;
    @Autowired RemotePasscodeService remotePasscodeService;
    @Autowired TestEventServiceImpl testEventService;
    @Autowired protected EmailSenderService emailSenderService;
    @Autowired protected RemoteProctorService remoteProctorService;
    @Autowired protected CollegeReader collegeService;

    private boolean isLocationAuthorized(UserAccountDto user, TestEvent event) {
        return user.getTestLocations() != null &&
        		user.getTestLocations().stream().anyMatch(loc -> loc.getId().equals(event.getTestLocationId()));
    }

    private TestEvent getLocationAuthorizedTestEvent(UserAccountDto user, int testEventId) {
        TestEvent event = testEventService.find(testEventId);
        if (event == null) {
            throw new NotFoundException("cannot find test event with id " + testEventId);
        }
        if (!isLocationAuthorized(user, event)) {
            throw new AccessDeniedException("Cannot access test event at that location");
        }
        return event;
    }

    private boolean isCollegeAuthorized(UserAccountDto user,String collegeId) {
        return user.getColleges() != null &&
                user.getColleges().stream().anyMatch( college -> college.getCccId().equals(collegeId));
    }

    public ResponseEntity<?> get(UserAccountDto user, int testEventId) {
        TestEvent testEvent = getLocationAuthorizedTestEvent(user, testEventId);
        return new ResponseEntity<>(testEvent, HttpStatus.OK);
    }

    public ResponseEntity<?> getByCollege(UserAccountDto user, String collegeId) {
        if (!isCollegeAuthorized(user, collegeId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            Set<String> locationIds = user.getTestLocations().stream().map(loc -> loc.getId()).collect(Collectors.toSet());
            Set<TestEvent> events =  testEventService.findByCollegeId(collegeId).stream().filter(
                    event -> locationIds.contains(event.getTestLocationId())
            ).collect(Collectors.toSet());
            return new ResponseEntity<>(events, HttpStatus.OK);
        }
    }

    public ResponseEntity<?> getActivations(UserAccountDto user, int testEventId) {
        // TODO: Make this call from the remote proctor endpoint.
        getLocationAuthorizedTestEvent(user, testEventId);
        return new ResponseEntity<>(activationService.findActivationsByTestEventId(testEventId), HttpStatus.OK);
    }

    public ResponseEntity<?> post(UserAccountDto user,String currentUserId, TestEvent testEvent, UriComponentsBuilder uriBuilder) {
        if (!isLocationAuthorized(user, testEvent)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else {
            testEventService.validate(testEvent);
            int testEventId = testEventService.create(currentUserId, testEvent);
            HttpHeaders headers = new HttpHeaders();
            URI locationUri = uriBuilder.path("/service/v1/test-event/" + testEventId).build().toUri();
            headers.setLocation(locationUri);
            return new ResponseEntity<Integer>(testEventId, headers, HttpStatus.CREATED);
        }
    }

    public ResponseEntity<?> put(UserAccountDto user,int testEventId, TestEvent testEvent) {
        getLocationAuthorizedTestEvent(user, testEventId); //verifies that user has the right to edit existing TestEvent
        testEvent.setTestEventId(testEventId);
        if (!isLocationAuthorized(user, testEvent)) { //now check the new location
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        testEventService.validate(testEvent);
        testEventService.updateTestEvent(user, testEvent);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<?> addUsers(UserAccountDto user, int testEventId, Set<String> studentIds) {
        TestEvent testEvent = getLocationAuthorizedTestEvent(user, testEventId);
        testEventService.createActivationsFor(user, testEvent, studentIds);
        // TODO: Hook point for Remote Proctoring.
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


    public ResponseEntity<?> cancel(UserAccountDto user, int testEventId) {
        TestEvent testEvent = getLocationAuthorizedTestEvent(user, testEventId);
        testEventService.cancelTestEvent(user, testEvent);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<?> getRemotePasscodeForTestEvent(Integer testEventId) {
        TestEvent testEvent = testEventService.find(testEventId);
        if (testEvent == null || testEvent.isCanceled()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(remotePasscodeService.getPasscodeForTestEvent(testEventId), HttpStatus.OK);
    }

    public ResponseEntity<String> createRemotePasscode(Integer testEventId) {
        return new ResponseEntity<>(remotePasscodeService.createRemotePasscode(testEventId), HttpStatus.CREATED);
    }

    public ResponseEntity<?> remoteProctorEvent(Integer testEventId) {
        RemoteProctorDto remoteProctorDto = remoteProctorService.readRemoteProctorByTestEventId(testEventId);
        CollegeViewDto college = collegeService.getCollegeByMisCode(testEventService.find(testEventId).getCollegeId());

        emailSenderService.sendRemoteEventCreationEmails(remoteProctorDto, college);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<?> remoteProctorCancelEmail(Integer testEventId) {
        RemoteProctorDto remoteProctorDto = remoteProctorService.readRemoteProctorByTestEventId(testEventId);
        CollegeViewDto college = collegeService.getCollegeByMisCode(testEventService.find(testEventId).getCollegeId());

        emailSenderService.sendRemoteEventCancellationEmail(remoteProctorDto, college);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
