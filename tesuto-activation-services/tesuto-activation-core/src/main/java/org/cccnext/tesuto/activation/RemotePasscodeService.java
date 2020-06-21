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

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.cccnext.tesuto.activation.model.TestEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.GregorianCalendar;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Service("remotePasscodeService")
public class RemotePasscodeService {

    @Autowired
    private PasscodeDao dao;

    @Autowired
    private TestEventServiceImpl testEventService;

    @Autowired
    private TestEventDao testEventDao;

    @Autowired(required = false)
    @Qualifier("remotePasscodeConfiguration")
    private PasscodeServiceConfiguration remoteConfig;

    @Autowired
    private PasscodeServiceUtil passcodeServiceUtil;

    public String createRemotePasscode() {
        return passcodeServiceUtil.createPasscode(remoteConfig);
    }

    public String createRemotePasscode(Integer eventId) {
        TestEvent testEvent = testEventService.find(eventId);
        String newPasscode = createRemotePasscode();
        testEvent.setRemotePasscode(createRemotePasscode());
        testEventDao.update(testEvent);
        return newPasscode;
    }

    public Pair<PasscodeValidationAttempt.ValidationResult,String> attemptValidation(TestEventActivation activation, String passcodeValue) {
        TestEvent testEvent = activation.getTestEvent();
        String testEventPasscode = testEvent.getRemotePasscode();
        PasscodeValidationAttempt attempt = new PasscodeValidationAttempt();
        attempt.setActivation(activation);
        attempt.setPasscode(passcodeValue);
        attempt.setAttemptedDate(new GregorianCalendar());
        Integer eventId = null;
        if (passcodeServiceUtil.isActivationLocked(activation)) {
            attempt.setResult(PasscodeValidationAttempt.ValidationResult.LOCKED);
        } else {
            if (!testEvent.isCanceled() && passcodeValue.equalsIgnoreCase(testEventPasscode)) {
                attempt.setResult(PasscodeValidationAttempt.ValidationResult.VALID);
                eventId = activation.getTestEvent().getTestEventId();
                attempt.setEventId(eventId);
            } else {
                attempt.setResult(PasscodeValidationAttempt.ValidationResult.INVALID);
            }
        }
        dao.saveLatestValidationAttempt(attempt);
        return new ImmutablePair<PasscodeValidationAttempt.ValidationResult,String>(attempt.getResult(), String.valueOf(eventId));
    }

    public String getPasscodeForTestEvent(Integer eventId) {
        TestEvent testEvent = testEventService.find(eventId);
        return testEvent.getRemotePasscode();
    }

    public boolean isRemotePasscode(String passcode) {
        boolean isRemotePasscode = false;
        if ((passcode.length() == remoteConfig.getPasscodeLength() + remoteConfig.getPrefix().length())
                && passcode.startsWith(remoteConfig.getPrefix())) {
            isRemotePasscode = true;
        }
        return isRemotePasscode;
    }
}
