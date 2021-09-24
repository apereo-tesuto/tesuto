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

import org.cccnext.tesuto.activation.Passcode;
import org.cccnext.tesuto.activation.Passcode.PasscodeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Calendar;

/**
 * Created by bruce on 1/27/16.
 */
@Entity
@Table(schema="public",name = "passcode")
@IdClass(PasscodeId.class)
public class JpaPasscode {

    @Column(name = "value", nullable = false)
    private String value;

    @Id
    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Id
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private PasscodeType type;

    @Column(name = "create_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar createDate;

    public JpaPasscode() {
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

    public Calendar getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Calendar createDate) {
        this.createDate = createDate;
    }

    @Override
    public String toString() {
        return "JpaPasscode{" + "value='" + value + '\'' + ", userId='" + userId + '\'' + ", type=" + type
                + ", createDate=" + createDate + '}';
    }
}
