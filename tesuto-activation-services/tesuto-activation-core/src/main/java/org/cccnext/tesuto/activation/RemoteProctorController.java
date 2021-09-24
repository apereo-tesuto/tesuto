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

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.cccnext.tesuto.activation.model.TestEvent;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.user.service.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RemoteProctorController {

    private final static String TEST_EVENT_ID_ATTRIBUTE = "test_event_id";
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired ActivationService activationService;
    @Autowired TestEventServiceImpl testEventService;
    @Autowired RemotePasscodeService remotePasscodeService;
    @Autowired UserContextService userContextService;

    private void authorize(HttpSession session, Integer testEventId) {
        if (!testEventId.equals(session.getAttribute(TEST_EVENT_ID_ATTRIBUTE))) {
            throw new AccessDeniedException("Cannot access " + testEventId);
        }
    }
		
    public String authorizeRemoteProctor(String uuid,
                                         Boolean acknowledge,
                                         Model model,
                                         HttpSession session) throws JsonProcessingException {
        TestEvent event = testEventService.findByUuid(uuid);
        if (event == null) {
            throw new AccessDeniedException("No event found with that ui");
        }
        if (event.getEndDate().before(new Date())) {
            throw new AccessDeniedException("Test Event Expired");
        }
        UserAccountDto user = testEventService.createRemoteProctorFromTestEvent(uuid, event);
        if (user == null) {
            throw new AccessDeniedException("Something went wrong");
        }
        if (acknowledge) {
            testEventService.acknowledge(event);
            session.setAttribute(TEST_EVENT_ID_ATTRIBUTE, event.getTestEventId());
            userContextService.updateUserAuthentication(user);
            model.addAttribute("event", mapper.writeValueAsString(event));
            model.addAttribute("user", mapper.writeValueAsString(user));
            model.addAttribute("assessments", mapper.writeValueAsString(testEventService.assessmentTitles(event)));
            return("remoteProctorView");
        } else {
            model.addAttribute("uuid", uuid);
            return("acknowledgementForm");
        }
    }

    public ResponseEntity<?> get(HttpSession session, int testEventId) {
        authorize(session, testEventId);
        return new ResponseEntity<>(testEventService.find(testEventId), HttpStatus.OK);
    }

    public ResponseEntity<?> getActivations(HttpSession session,int testEventId) {
        authorize(session, testEventId);
        return new ResponseEntity<>(activationService.findActivationsByTestEventId(testEventId), HttpStatus.OK);
    }


    public ResponseEntity<?> getRemotePasscodeForTestEvent(HttpSession session, Integer testEventId) {
        authorize(session, testEventId);
        return new ResponseEntity<>(remotePasscodeService.getPasscodeForTestEvent(testEventId), HttpStatus.OK);
    }
}
