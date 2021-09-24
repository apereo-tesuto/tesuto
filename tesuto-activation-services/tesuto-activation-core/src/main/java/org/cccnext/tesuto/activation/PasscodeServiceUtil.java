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
import org.cccnext.tesuto.activation.model.Activation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.GregorianCalendar;

import lombok.extern.slf4j.Slf4j;
/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Slf4j
@Service("passcodeServiceUtil")
public class PasscodeServiceUtil {

    static final char[] PASSCODE_CHARACTERS = "ABCDEFGHJKLMNPQRSTWXYZ23456789".toCharArray();

    @Autowired
    private PasscodeDao dao;

    // A validation cannot be attempted for an activation if there have been
    // maxFailedAttempts failed attempts in the past lockedFor seconds
    @Value("${passcode.maxFailedAttempts}")
    private Integer maxFailedAttempts; // ignore this if the setting is null

    @Value("${passcode.lockedForSeconds}")
    private Integer lockedFor;

    public Integer getMaxFailedAttempts() {
        return maxFailedAttempts;
    }

    public void setMaxFailedAttempts(Integer maxFailedAttempts) {
        this.maxFailedAttempts = maxFailedAttempts;
    }

    public Integer getLockedFor() {
        return lockedFor;
    }

    public void setLockedFor(Integer lockedFor) {
        this.lockedFor = lockedFor;
    }

    public String createPasscode(PasscodeServiceConfiguration config) {
        SecureRandom secureRandom = null;
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
        	log.error("Algorithm for generating secure random numbers is missing from the JVM implementation.");
        	return null;
        }
        secureRandom.setSeed(System.currentTimeMillis());

        String randomString = RandomStringUtils.random(config.getPasscodeLength(), 0, PASSCODE_CHARACTERS.length,
                false, false, PASSCODE_CHARACTERS, secureRandom);

        return config.getPrefix() +  randomString;
    }

    public boolean isActivationLocked(Activation activation) {
        if (lockedFor == null || maxFailedAttempts == null) {
            return false;
        }
        Calendar lockTime = new GregorianCalendar();
        lockTime.add(Calendar.SECOND, -1 * lockedFor);
        long failures = dao.findValidationAttempts(activation).stream()
                .filter(attempt -> !attempt.isSuccessful() && attempt.getAttemptedDate().after(lockTime)).count();
        return failures >= maxFailedAttempts;
    }
}
