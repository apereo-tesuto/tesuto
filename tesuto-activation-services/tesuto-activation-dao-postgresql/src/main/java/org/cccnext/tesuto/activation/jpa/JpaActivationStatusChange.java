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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.activation.model.ActivationStatusChange;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.util.Date;

@Entity
@Table(schema="public",name = "activation_status_change")
public class JpaActivationStatusChange {

    // We don't really use this for anything, it just makes hibernate happy
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activation_status_change_id")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "activation_id", nullable = false, updatable = false)
    private JpaActivation activation;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId; // user who requested this change

    @Column(name = "user_name", nullable = true, length = 1024)
    private String userName; // user who requested this change

    @Column(name = "proctor_id", nullable = true, length = 100)
    private String proctorId; // proctor_id who approved this change, if any

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "new_status", nullable = false)
    private Activation.Status newStatus;

    @Column(name = "change_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date changeDate;

    @Column(name="reason", nullable=true)
    private String reason;

    public JpaActivationStatusChange() {
    }

    public JpaActivationStatusChange(JpaActivation activation, ActivationStatusChange change) {
        setId(change.getId());
        setActivation(activation);
        setUserId(change.getUserId());
        setUserName(change.getUserName());
        setProctorId(change.getProctorId());
        setNewStatus(change.getNewStatus());
        setChangeDate(change.getChangeDate());
        setReason(change.getReason());
    }

    public ActivationStatusChange getActivationStatusChange() {
        ActivationStatusChange change = new ActivationStatusChange();
        change.setUserId(getUserId());
        change.setUserName(getUserName());
        change.setProctorId(getProctorId());
        change.setNewStatus(getNewStatus());
        change.setChangeDate(getChangeDate());
        change.setReason(getReason());
        change.setId(getId());
        return change;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JpaActivation getActivation() {
        return activation;
    }

    public void setActivation(JpaActivation activation) {
        this.activation = activation;
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
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
