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

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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

/*
  A class with the same fields as Activation, but with JPA annotations.
 */

@NamedQueries({
        @NamedQuery(name = "activation.report",
                query = "select new org.cccnext.tesuto.activation.ActivationCount(act.locationId, act.assessmentTitle, act.creatorName, act.statusForSearch, count(*)) "
                        + "from JpaActivation act where act.createDate >= :from and act.createDate < :to "
                        + "group by act.locationId, act.assessmentTitle, act.creatorName, act.statusForSearch"
        )
})

@Entity
@Table(schema="public", name = "activation")
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="activation_type")
public abstract class JpaActivation {

    @Id
    @Column(name = "activation_id", nullable = false, length = 100)
    private String activationId; // primary key

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId; // user to whom this activation applies

    @Column(name = "namespace", nullable = false, length = 100)
    private String namespace;

    @Column(name = "assessment_identifier", nullable = false, length = 100)
    private String assessmentIdentifier; // activated assessment

    @Column(name = "assessment_title", nullable = true, length = 1024)
    private String assessmentTitle;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Activation.Status statusForSearch;

    @Column(name = "creator_id", nullable = true, length = 100)
    private String creatorId; // user who created this activation

    @Column(name = "creator_name", nullable = true, length = 1024)
    private String creatorName;

    @Column(name = "create_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name = "current_assessment_session_id", nullable = true, length = 100)
    private String currentAssessmentSessionId;

    @Column(name = "is_prerequisites_met", nullable = false)
    private boolean prerequisitesMet;

    @Column(name = "status_update_date", nullable = true)
    private Date statusUpdateDate;

    @ElementCollection(fetch = FetchType.LAZY)
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

    public boolean isPrerequisitesMet() {
        return prerequisitesMet;
    }

    public void setPrerequisitesMet(boolean prerequisitesMet) {
        this.prerequisitesMet = prerequisitesMet;
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

    public void setActivationStatusChangeHistory(List<ActivationStatusChange> statusChangeHistory) {
        this.statusChangeHistory = statusChangeHistory.stream()
                .map((change -> new JpaActivationStatusChange(this, change))).collect(Collectors.toSet());
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
