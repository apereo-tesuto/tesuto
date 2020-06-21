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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.activation.model.ActivationStatusChange;
import org.cccnext.tesuto.content.model.DeliveryType;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by bruce on 12/19/16.
 */
@Entity
@Table(schema="public", name="activation_view")
public class JpaActivationView {
    @Id
    @Column(name = "activation_id")
    private String activationId; // primary key

    @Column(name = "user_id")
    private String userId; // user to whom this activation applies

    @Column(name = "namespace")
    private String namespace;

    @Column(name = "assessment_identifier")
    private String assessmentIdentifier; // activated assessment

    @Column(name = "assessment_title")
    private String assessmentTitle;

    @Column(name = "status")
    @Enumerated(EnumType.ORDINAL)
    private Activation.Status statusForSearch;

    @Column(name = "creator_id")
    private String creatorId;

    @Column(name = "creator_name")
    private String creatorName;

    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name = "current_assessment_session_id")
    private String currentAssessmentSessionId;

    @Column(name = "status_update_date")
    private Date statusUpdateDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema="public", name = "activation_attribute", joinColumns = { @JoinColumn(name = "activation_id") })
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    private Map<String, String> attributes = new HashMap<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "activation", orphanRemoval = true)
    private Set<JpaActivationStatusChange> statusChangeHistory;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(schema="public", name = "activation_assessment_session", joinColumns = { @JoinColumn(name = "activation_id") })
    @Column(name = "assessment_session_id")
    private Set<String> assessmentSessionIds;

    @Column(name = "location_id")
    private String locationId;

    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate; // Activation cannot be used before this time

    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate; // Activation cannot be used after this time

    @Column(name = "delivery_type")
    @Enumerated(EnumType.STRING)
    private DeliveryType deliveryType;

    @Column(name="test_event_id")
    private Integer testEventId;

    @Column(name="test_event_name")
    private String testEventName;

    @Column(name="proctor_first_name")
    private String proctorFirstName;

    @Column(name="proctor_last_name")
    private String proctorLastName;

    @Column(name="proctor_email")
    private String proctorEmail;

    @Column(name="proctor_phone")
    private String proctorPhone;

    public String getActivationId() {
        return activationId;
    }

    public void setActivationId(String activationId) {
        this.activationId = activationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getAssessmentIdentifier() {
        return assessmentIdentifier;
    }

    public void setAssessmentIdentifier(String assessmentIdentifier) {
        this.assessmentIdentifier = assessmentIdentifier;
    }

    public String getAssessmentTitle() {
        return assessmentTitle;
    }

    public void setAssessmentTitle(String assessmentTitle) {
        this.assessmentTitle = assessmentTitle;
    }

    public Activation.Status getStatusForSearch() {
        return statusForSearch;
    }

    public void setStatusForSearch(Activation.Status status) {
        this.statusForSearch = status;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCurrentAssessmentSessionId() {
        return currentAssessmentSessionId;
    }

    public void setCurrentAssessmentSessionId(String currentAssessmentSessionId) {
        this.currentAssessmentSessionId = currentAssessmentSessionId;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public Set<ActivationStatusChange> getActivationStatusChangeHistory() {
        return statusChangeHistory.stream().map(jpaChange -> jpaChange.getActivationStatusChange())
                .collect(Collectors.toSet());
    }

    public Set<JpaActivationStatusChange> getStatusChangeHistory() {
        return statusChangeHistory;
    }

    public void setStatusChangeHistory(Set<JpaActivationStatusChange> statusChangeHistory) {
        this.statusChangeHistory = statusChangeHistory;
    }

    public Set<String> getAssessmentSessionIds() {
        return assessmentSessionIds;
    }

    public void setAssessmentSessionIds(Set<String> assessmentSessionIds) {
        this.assessmentSessionIds = assessmentSessionIds;
    }

    public Date getStatusUpdateDate() {
        return statusUpdateDate;
    }

    public void setStatusUpdateDate(Date statusUpdateDate) {
        this.statusUpdateDate = statusUpdateDate;
    }


    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
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

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public Integer getTestEventId() {
        return testEventId;
    }

    public void setTestEventId(Integer testEventId) {
        this.testEventId = testEventId;
    }

    public String getTestEventName() {
        return testEventName;
    }

    public void setTestEventName(String testEventName) {
        this.testEventName = testEventName;
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
