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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.activation.model.TestEvent;
import org.cccnext.tesuto.content.model.DeliveryType;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.util.Date;
import java.util.Set;

/**
 * Created by bruce on 11/15/16.
 */

@Entity
@Table(schema="public", name="test_event")
public class JpaTestEvent {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="test_event_id")
    private int testEventId;

    @Column(name="name")
    private String name;

    @Column(name="start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name="end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Column(name="college_id")
    private String collegeId;

    @Column(name="test_location_id")
    private String testLocationId;

    @Column(name="delivery_type")
    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;

    @Column(name="proctor_first_name")
    private String proctorFirstName;

    @Column(name="proctor_last_name")
    private String proctorLastName;

    @Column(name="proctor_email")
    private String proctorEmail;

    @Column(name="proctor_phone")
    private String proctorPhone;
    
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(schema="public", name="test_event_assessment", joinColumns= { @JoinColumn(name="test_event_id") })
    @Column(name="assessment_identifier")
    private Set<JpaScopedIdentifier> assessmentScopedIdentifiers;

    @Column(name="create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name="created_by")
    private String createdBy;

    @Column(name="update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @Column(name="updated_by")
    private String updatedBy;

    @Column(name="remote_passcode")
    private String remotePasscode;

    @Column(name="canceled")
    private boolean canceled;

    @Column(name="uuid")
    private String uuid;

    public boolean isCanceled() {
        return canceled;
    }
    
    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public int getTestEventId() {
        return testEventId;
    }

    public void setTestEventId(int testEventId) {
        this.testEventId = testEventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }

    
    public String getTestLocationId() {
        return testLocationId;
    }

    public void setTestLocationId(String testLocationId) {
        this.testLocationId = testLocationId;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getProctorFirstName() {
        return proctorFirstName;
    }

    public void setProctorFirstName(String proctorFirstName) {
        this.proctorFirstName = proctorFirstName;
    }

    public String getProctorLastName() {
        return proctorLastName;
    }

    public void setProctorLastName(String proctorLastName) {
        this.proctorLastName = proctorLastName;
    }

    public String getProctorEmail() {
        return proctorEmail;
    }

    public void setProctorEmail(String proctorEmail) {
        this.proctorEmail = proctorEmail;
    }

    public String getProctorPhone() {
        return proctorPhone;
    }

    public void setProctorPhone(String proctorPhone) {
        this.proctorPhone = proctorPhone;
    }

    public Set<JpaScopedIdentifier> getAssessmentScopedIdentifiers() {
        return assessmentScopedIdentifiers;
    }

    public void setAssessmentScopedIdentifiers(Set<JpaScopedIdentifier> assessmentScopedIdentifiers) {
        this.assessmentScopedIdentifiers = assessmentScopedIdentifiers;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }
    
    public String getRemotePasscode() {
        return remotePasscode;
    }

    public void setRemotePasscode(String remotePasscode) {
        this.remotePasscode = remotePasscode;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}


    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
