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

import java.util.Set;

import org.cccnext.tesuto.activation.TestEventController;
import org.cccnext.tesuto.activation.model.TestEvent;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Created by bruce on 11/18/16.
 */
@Controller
@RequestMapping(value = "service/v1/test-event")
public class TestEventEndPoint extends BaseController {

	@Autowired
	TestEventController controller;


    @RequestMapping(value="{testEventId}", method= RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAuthority('VIEW_TEST_EVENT')")
    public ResponseEntity<?> get(@PathVariable("testEventId") int testEventId) {
        return controller.get(getUser(), testEventId);
    }


    @RequestMapping(value="/college/{collegeId}", method=RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAuthority('VIEW_TEST_EVENT_BY_COLLEGE')")
    public ResponseEntity<?> getByCollege(@PathVariable("collegeId") String collegeId) {
        return controller.getByCollege(getUser(), collegeId);
    }

    @RequestMapping(value="{testEventId}/activations", method=RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAuthority('VIEW_TEST_EVENT')")
    public ResponseEntity<?> getActivations(@PathVariable("testEventId") int testEventId) {
        return controller.getActivations(getUser(), testEventId);
    }

    @RequestMapping(value="", method= RequestMethod.POST, produces = "application/json")
    @PreAuthorize("hasAuthority('CREATE_TEST_EVENT')")
    public ResponseEntity<?> post(@RequestBody TestEvent testEvent, UriComponentsBuilder uriBuilder) {
        return controller.post(getUser(), getCurrentUserId(), testEvent, uriBuilder);
    }

    @RequestMapping(value="{testEventId}", method=RequestMethod.PUT, produces = "application/json")
    @PreAuthorize("hasAuthority('UPDATE_TEST_EVENT')")
    public ResponseEntity<?> put(@PathVariable int testEventId, @RequestBody TestEvent testEvent) {
        return controller.put(getUser(), testEventId, testEvent);
    }

    @RequestMapping(value="{testEventId}/students", method=RequestMethod.PUT, produces = "application/json")
    @PreAuthorize("hasAuthority('UPDATE_TEST_EVENT')")
    public ResponseEntity<?> addUsers(@PathVariable int testEventId, @RequestBody Set<String> studentIds) {
        return controller.addUsers(getUser(), testEventId, studentIds);
    }

    @RequestMapping(value="{testEventId}/cancel", method=RequestMethod.PUT, produces = "application/json")
    @PreAuthorize("hasAuthority('CANCEL_TEST_EVENT')")
    public ResponseEntity<?> cancel(@PathVariable int testEventId) {
        return controller.cancel(getUser(), testEventId);
    }

    @RequestMapping(value = "{testEventId}/passcode", method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAuthority('VIEW_REMOTE_PASSCODE')")
    public ResponseEntity<?> getRemotePasscodeForTestEvent(@PathVariable Integer testEventId) {
      return controller.getRemotePasscodeForTestEvent(testEventId);
    }

    @RequestMapping(value = "{testEventId}/passcode", method = RequestMethod.POST, produces = "application/json")
    @PreAuthorize("hasAuthority('GENERATE_REMOTE_PASSCODE_FOR_TEST_EVENT')")
    public ResponseEntity<String> createRemotePasscode(@PathVariable Integer testEventId) {
        return controller.createRemotePasscode(testEventId);
    }

    @PreAuthorize("hasAuthority('CREATE_TEST_EVENT')")
    @RequestMapping(value = "{testEventId}/creation-email", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<?> remoteProctorEvent(@PathVariable Integer testEventId) {
       return controller.remoteProctorEvent(testEventId);
    }

    @PreAuthorize("hasAuthority('CANCEL_TEST_EVENT')")
    @RequestMapping(value = "{testEventId}/cancellation-email", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<?> remoteProctorCancelEmail(@PathVariable Integer testEventId) {
       return controller.remoteProctorCancelEmail(testEventId);
    }
}
