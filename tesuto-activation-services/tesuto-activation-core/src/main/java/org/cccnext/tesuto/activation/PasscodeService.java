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
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.cccnext.tesuto.activation.Passcode.PasscodeType;
import org.cccnext.tesuto.activation.PasscodeValidationAttempt.ValidationResult;
import org.cccnext.tesuto.activation.model.Activation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by bruce on 1/27/16.
 */
@Service("passcodeService")
public class PasscodeService {

    static final long MINUTE_MULTIPLIER = 60000l;

    @Autowired
    private PasscodeDao dao;

    @Autowired
    private ActivationService activationService;

    @Autowired(required=false)
    @Qualifier("privatePasscodeConfiguration")
    private PasscodeServiceConfiguration privateConfig;

    @Autowired(required=false)
    @Qualifier("publicPasscodeConfiguration")
    private PasscodeServiceConfiguration publicConfig;

    @Autowired
    private PasscodeServiceUtil passcodeServiceUtil;

    public void setDao(PasscodeDao dao) {
        this.dao = dao;
    }

    public PasscodeServiceConfiguration getPrivateConfig() {
        return privateConfig;
    }

    public void setPrivateConfig(PasscodeServiceConfiguration privateConfig) {
        this.privateConfig = privateConfig;
    }

    public PasscodeServiceConfiguration getPublicConfig() {
        return publicConfig;
    }

    public void setPublicConfig(PasscodeServiceConfiguration publicConfig) {
        this.publicConfig = publicConfig;
    }

    public Pair<ValidationResult,String> attemptValidation(Activation activation, String passcodeValue, boolean requirePrivate) {
        PasscodeValidationAttempt attempt = new PasscodeValidationAttempt();
        attempt.setActivation(activation);
        attempt.setPasscode(passcodeValue);
        attempt.setAttemptedDate(new GregorianCalendar());
        String proctorId = null;
        if (passcodeServiceUtil.isActivationLocked(activation)) {
            attempt.setResult(ValidationResult.LOCKED);
        } else {
            Passcode result = getValidPasscode(passcodeValue);
            attempt.setResult((result == null) ? ValidationResult.INVALID
                    : (requirePrivate && result.isPublic()) ? ValidationResult.PRIVATE_PASSCODE_REQUIRED
                            : ValidationResult.VALID);
            if (result != null) {
                proctorId = result.getUserId();
                attempt.setUserId(proctorId);
            }
        }
        dao.saveLatestValidationAttempt(attempt);
        return new ImmutablePair<ValidationResult,String>(attempt.getResult(), proctorId);
    }

    /* This is just for testing! It deletes all record of the passcode */
    public boolean delete(String value) {
        return dao.delete(value);
    };

    public Set<Passcode> findAllPasscodesByUser(String userId) {
        return filterExpired(dao.findAllPasscodesByUser(userId));
    }

    private Passcode createPasscode(String userId, PasscodeType type, PasscodeServiceConfiguration config) {
        Passcode passcode = new Passcode();
        passcode.setUserId(userId);
        passcode.setCreateDate(new GregorianCalendar());
        passcode.setType(type);
        passcode.setValue(passcodeServiceUtil.createPasscode(config));
        setPasscodeExpirationDate(passcode);
        dao.savePasscode(passcode);
        return passcode;
    }

    public Passcode generatePrivatePasscode(String userId) {
        return createPasscode(userId, PasscodeType.PRIVATE, privateConfig);
    }

    public Passcode generatePublicPasscode(String userId) {
        return createPasscode(userId, PasscodeType.PUBLIC, publicConfig);
    }

    private Passcode getValidPasscode(String value) {
        Set<Passcode> results = filterExpired(dao.findPasscodesByValue(value.toUpperCase()));
        if (results.size() == 0) {
            return null;
        } else {
            return results.iterator().next();
        }
    }

    public boolean isValidPasscode(String value) {
        return getValidPasscode(value) != null;
    }

    private Calendar expirationDate(Passcode passcode, Integer hourHand, Integer minuteHand, int minExpiration) {
        long minExpirationTime = passcode.getCreateDate().getTimeInMillis() + minExpiration * MINUTE_MULTIPLIER;
        Calendar expirationDate = new GregorianCalendar();
        expirationDate.setTimeInMillis(minExpirationTime);
        if (hourHand != null && hourHand >= 0) {
            expirationDate.add(Calendar.HOUR, 1);
            expirationDate = DateUtils.truncate(expirationDate, Calendar.HOUR_OF_DAY);
            while (expirationDate.get(Calendar.HOUR_OF_DAY) != hourHand) {
                expirationDate.add(Calendar.HOUR, 1);
            }
        }
        if (minuteHand != null && minuteHand >= 0) {
            Calendar minExpirationDate = new GregorianCalendar();
            minExpirationDate.setTimeInMillis(minExpirationTime);
            expirationDate = DateUtils.truncate(expirationDate, Calendar.HOUR_OF_DAY);
            while (expirationDate.before(minExpirationDate)) {
                expirationDate.add(Calendar.MINUTE, minuteHand);
            }
        }
        return expirationDate;
    }

    public void setPasscodeExpirationDate(Passcode passcode) {
        passcode.setExpirationDate(passcode.isPublic()
                ? expirationDate(passcode, publicConfig.getHourHand(), publicConfig.getMinuteHand(),
                        publicConfig.getMinExpiration())
                : expirationDate(passcode, privateConfig.getHourHand(), privateConfig.getMinuteHand(),
                        privateConfig.getMinExpiration()));
    }

    private boolean isExpired(Passcode passcode) {
        if (passcode.getExpirationDate() == null) {
            setPasscodeExpirationDate(passcode);
        }
        return passcode.getExpirationDate().before(new GregorianCalendar());
    }

    // TODO: there would be some performance benefit to moving this filtering
    // into the JPA query
    private Set<Passcode> filterExpired(Set<Passcode> passcodes) {
        return passcodes.stream().filter(passcode -> !isExpired(passcode)).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return "PasscodeService{" + "dao=" + dao + ", privateConfig=" + privateConfig + ", publicConfig=" + publicConfig + '}';
    }
}
