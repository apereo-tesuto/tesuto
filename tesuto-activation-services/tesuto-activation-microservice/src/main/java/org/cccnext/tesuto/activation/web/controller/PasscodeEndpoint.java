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

import org.cccnext.tesuto.activation.Passcode;
import org.cccnext.tesuto.activation.PasscodeController;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by bruce on 2/1/16.
 */
@Controller
@RequestMapping(value = "service/v1/passcodes")
public class PasscodeEndpoint extends BaseController {

    @Autowired(required = true)
    PasscodeController controller;

    @RequestMapping(value = "public", method = RequestMethod.POST, produces = "application/json")
    @PreAuthorize("hasAuthority('GENERATE_PUBLIC_PASSCODE_FOR_CURRENT_USER')")
    public ResponseEntity<Passcode> createPublicPasscode() {
    	return controller.createPublicPasscode(getCurrentUserId());
    }

    @RequestMapping(value = "private", method = RequestMethod.POST, produces = "application/json")
    @PreAuthorize("hasAuthority('GENERATE_PRIVATE_PASSCODE_FOR_CURRENT_USER')")
    public ResponseEntity<Passcode> createPrivatePasscode() {
        return controller.createPrivatePasscode(getCurrentUserId());
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    @PreAuthorize("hasAuthority('VIEW_PASSCODES_FOR_CURRENT_USER')")
    public ResponseEntity<Set<Passcode>> getUserPasscodes() {
    	return controller.getUserPasscodes(getCurrentUserId());
    }
}
