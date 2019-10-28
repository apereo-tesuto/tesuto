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
package org.cccnext.tesuto.activation.jpa;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.cccnext.tesuto.activation.PasscodeValidationAttempt;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Calendar;

/**
 * Created by bruce on 2/5/16.
 */
@Entity
@Table(schema="public",name = "passcode_validation_attempt")
public class JpaPasscodeValidationAttempt {

    @Id
    @GeneratedValue
    @Column(name = "passcode_validation_attempt_id")
    private long passcodeValidationAttemptId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "activation_id", nullable = false, updatable = false)
    private JpaActivation jpaActivation;

    @Column(name = "result", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private PasscodeValidationAttempt.ValidationResult result;;

    @Column(name = "passcode", nullable = false)
    private String passcode;

    @Column(name = "user_id", nullable = true)
    private String userId; // creator of the passcode

    @Column(name = "event_id", nullable = true)
    private Integer eventId; // id of test event that matched the attempt

    @Column(name = "validation_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar attemptedDate;

    public long getPasscodeValidationAttemptId() {
        return passcodeValidationAttemptId;
    }

    public void setPasscodeValidationAttemptId(long passcodeValidationAttemptId) {
        this.passcodeValidationAttemptId = passcodeValidationAttemptId;
    }

    public JpaActivation getJpaActivation() {
        return jpaActivation;
    }

    public PasscodeValidationAttempt.ValidationResult getResult() {
        return result;
    }

    public void setResult(PasscodeValidationAttempt.ValidationResult result) {
        this.result = result;
    }

    public void setJpaActivation(JpaActivation jpaActivation) {
        this.jpaActivation = jpaActivation;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Calendar getAttemptedDate() {
        return attemptedDate;
    }

    public void setAttemptedDate(Calendar attemptedDate) {
        this.attemptedDate = attemptedDate;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
    }
}
