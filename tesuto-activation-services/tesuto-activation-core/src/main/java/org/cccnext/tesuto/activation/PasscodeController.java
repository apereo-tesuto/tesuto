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

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Created by bruce on 2/1/16.
 */

@Service
public class PasscodeController {

    @Autowired(required = true)
    PasscodeService passcodeService;

    public ResponseEntity<Passcode> createPublicPasscode(String currentUserId) {
        return new ResponseEntity<>(passcodeService.generatePublicPasscode(currentUserId), HttpStatus.CREATED);
    }

    public ResponseEntity<Passcode> createPrivatePasscode(String currentUserId) {
        return new ResponseEntity<>(passcodeService.generatePrivatePasscode(currentUserId), HttpStatus.CREATED);
    }

    public ResponseEntity<Set<Passcode>> getUserPasscodes(String currentUserId) {
        return new ResponseEntity<>(passcodeService.findAllPasscodesByUser(currentUserId), HttpStatus.OK);
    }
}
