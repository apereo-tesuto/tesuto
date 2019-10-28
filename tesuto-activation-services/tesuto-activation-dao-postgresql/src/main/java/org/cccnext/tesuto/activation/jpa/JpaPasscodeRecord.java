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

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.cccnext.tesuto.activation.Passcode;
import org.cccnext.tesuto.activation.Passcode.PasscodeType;
import org.cccnext.tesuto.activation.model.TestEvent;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by bruce on 1/27/16.
 */
@Entity
@Table(schema="public",name = "passcode_record")
public class JpaPasscodeRecord {

    @Id
    @GeneratedValue
    @Column(name = "passcode_record_id")
    private Long passcodeRecordId;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "user_id", nullable = true, length = 100)
    private String userId;

    @Column(name="test_event_id", nullable=true)
    private Integer testEventId;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private PasscodeType type;

    @Column(name = "create_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createDate;

    public JpaPasscodeRecord() {
    }

    public JpaPasscodeRecord(Passcode passcode) {
        setValue(passcode.getValue());
        setType(passcode.getType());
        setUserId(passcode.getUserId());
        setCreateDate(passcode.getCreateDate());
    }

    public JpaPasscodeRecord(JpaTestEvent testEvent) {
        setValue(testEvent.getRemotePasscode());
        setTestEventId(testEvent.getTestEventId());
        setCreateDate(new GregorianCalendar());
        setType(PasscodeType.REMOTE); //TODO refactor validation, this was added as part of the refactor to microservices
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public PasscodeType getType() {
        return type;
    }

    public void setType(PasscodeType type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getTestEventId() {
        return testEventId;
    }

    public void setTestEventId(Integer testEventId) {
        this.testEventId = testEventId;
    }

    public Calendar getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Calendar createDate) {
        this.createDate = createDate;
    }

    public String toString() {
        return ReflectionToStringBuilder.reflectionToString(this);
    }
}
