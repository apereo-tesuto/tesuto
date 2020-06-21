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

import javax.servlet.http.HttpSession;

import org.cccnext.tesuto.activation.RemoteProctorController;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
@RequestMapping(value = "/service/v1/remote-proctor")
public class RemoteProctorEndpoint extends BaseController {
	
	@Autowired
	RemoteProctorController controller;

    @RequestMapping(value = "authorize", method=RequestMethod.GET, produces = "application/json")
    public String authorizeRemoteProctor(@RequestParam(name="uuid") String uuid,
                                         @RequestParam(name="acknowledge", defaultValue="false") Boolean acknowledge,
                                         Model model,
                                         HttpSession session) throws JsonProcessingException {
    	return controller.authorizeRemoteProctor(uuid, acknowledge, model, session);
    }

    @RequestMapping(value="{testEventId}", method= RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> get(HttpSession session, @PathVariable("testEventId") int testEventId) {
        return controller.get(session, testEventId);
    }

    @RequestMapping(value="{testEventId}/activations", method=RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getActivations(HttpSession session, @PathVariable("testEventId") int testEventId) {
        return controller.getActivations(session, testEventId);
    }

    @RequestMapping(value = "{testEventId}/passcode", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getRemotePasscodeForTestEvent(HttpSession session, @PathVariable Integer testEventId) {
       return controller.getRemotePasscodeForTestEvent(session, testEventId);
    }
}
