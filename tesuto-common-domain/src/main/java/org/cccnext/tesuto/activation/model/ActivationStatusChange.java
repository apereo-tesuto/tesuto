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
package org.cccnext.tesuto.activation.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Comparator;
import java.util.Date;

/**M
 * Created by bruce on 10/12/15.
 */

public class ActivationStatusChange {

    public final static Comparator<ActivationStatusChange> comparator = new Comparator<ActivationStatusChange>() {
        // sorts in descending order
        public int compare(ActivationStatusChange ch1, ActivationStatusChange ch2) {
            return Long.signum(ch2.getChangeDate().getTime() - ch1.getChangeDate().getTime());
        }
    };

    private Integer id; // Just to make the persistence layer happy
    private String userId; // user who requested this change
    private String userName; // display name for above userId;
    private String proctorId;
    private Activation.Status newStatus;
    private Date changeDate;
    private String reason;
    public ActivationStatusChange() {
    	
    }
    public ActivationStatusChange(String userId, String userName, String proctorId, Activation.Status newStatus, Date changeDate, String reason) {
        this.userId = userId;
        this.userName = userName;
        this.proctorId = proctorId;
        this.newStatus = newStatus;
        this.changeDate = changeDate;
        this.reason = reason;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProctorId() {
        return proctorId;
    }

    public void setProctorId(String proctorId) {
        this.proctorId = proctorId;
    }

    public Activation.Status getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(Activation.Status newStatus) {
        this.newStatus = newStatus;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
