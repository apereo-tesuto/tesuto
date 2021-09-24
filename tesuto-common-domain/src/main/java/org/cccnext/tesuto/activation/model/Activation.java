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

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.activation.model.ActivationStatusChange;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.content.model.DeliveryType;
import org.cccnext.tesuto.domain.dto.ScopedIdentifierDto;



import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
//TODO Determine if any value to previously abstract classes
public class Activation {

    public enum Status {
        PREREQUISITES_NOT_MET,
        ACTIVATED,          // start date of the activation is in the future.
        READY,              // activation is ready to be taken, which mean the start date has approached
        IN_PROGRESS,        // student started the activation
        COMPLETE,           // student completed the activation
        EXPIRED,            // end date of the activation was prior to today, and the status is not deactivated or canceled.
        DEACTIVATED,        // proctor/admin deactivate the activation, before any student completed
        UNKNOWN,
        PAUSED,             // proctor paused the activation
        INCOMPLETE,         // end date passed and the activation was in the state in_progess or paused
        PENDING_SCORING,    // scoring has started but not completed.
        ASSESSED            // paper+pencil only. student has completed their paper assessment.
    };

    private String activationId;
    private String userId;
    private ScopedIdentifierDto assessmentScopedIdentifier;
    private String assessmentTitle;
    private Map<String, String> attributes = new HashMap<>();
    private LinkedList<ActivationStatusChange> statusChangeHistory = new LinkedList<>(); // sorted by date descending
    private String currentAssessmentSessionId;
    private Date statusUpdateDate;
    private Set<String> assessmentSessionIds = new HashSet<>();
    private String creatorId;
    private String creatorName;
    private Date createDate;
    private StudentViewDto student; //Populated sometimes for the UI

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

    public ScopedIdentifierDto getAssessmentScopedIdentifier() {
        return assessmentScopedIdentifier;
    }

    public void setAssessmentScopedIdentifier(ScopedIdentifierDto assessmentScopedIdentifier) {
        this.assessmentScopedIdentifier = assessmentScopedIdentifier;
    }

    public String getAssessmentTitle() {
        return assessmentTitle;
    }

    public void setAssessmentTitle(String assessmentTitle) {
        this.assessmentTitle = assessmentTitle;
    }
//abstract
    public DeliveryType getDeliveryType() {return DeliveryType.ONLINE;}
//abstract
    public  String getLocationId() {return null;}
//abstract
    public  Date getStartDate(){return null;}

    public void  setStartDate(Date date) {}
//abstract
    public  Date getEndDate() {return null;}

    public void  setEndDate(Date date) {}

    @JsonIgnore
    public void setAllAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public List<ActivationStatusChange> getStatusChangeHistory() {
        return statusChangeHistory;
    }

    public void addStatusChange(String requestorId, String requestorName, String proctorId, Status newStatus, String reason, Date statusChangeDate) {
        // It must be added to the front of the list
        setStatusUpdateDate(statusChangeDate);
        statusChangeHistory.add(0, new ActivationStatusChange(requestorId, requestorName, proctorId, newStatus, statusChangeDate, reason));
    }

    public void addStatusChange(String requestorId, String requestorName, String proctorId, Status newStatus, String reason) {
        Date now = new Date();
        addStatusChange(requestorId, requestorName, proctorId, newStatus, reason, now);
    }

    public void setStatusChangeHistory(Collection<ActivationStatusChange> statusChangeHistory) {
        this.statusChangeHistory = new LinkedList<>();
        this.statusChangeHistory.addAll(statusChangeHistory);
        this.statusChangeHistory.sort(ActivationStatusChange.comparator);
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

    public Set<String> getAssessmentSessionIds() {
        return assessmentSessionIds;
    }

    public void addAssessmentSessionId(String assessmentSessionId) {
        currentAssessmentSessionId = assessmentSessionId;
        assessmentSessionIds.add(assessmentSessionId);
    }

    public void setAssessmentSessionIds(Set<String> assessmentSessionIds) {
        this.assessmentSessionIds = assessmentSessionIds;
    }

    public String getCurrentAssessmentSessionId() {
        return currentAssessmentSessionId;
    }

    public void setCurrentAssessmentSessionId(String assessmentSessionId) {
        this.currentAssessmentSessionId = assessmentSessionId;
        this.assessmentSessionIds.add(assessmentSessionId);
    }

    public Date getStatusUpdateDate() {
        return statusUpdateDate;
    }

    public void setStatusUpdateDate(Date statusUpdateDate) {
        this.statusUpdateDate = statusUpdateDate;
    }

    private boolean isExpired() {
        LocalDate localEndDate = getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localEndDate.isBefore(LocalDate.now());
    }

    private Status computeStatus(ActivationStatusChange mostRecentChange) {
        Status status = null;
        // the database stores concrete times for all the dates, so we need to blank out the time portion if we compare dates,
        // otherwise before and after could lead to incorrect results.
        LocalDate today = LocalDate.now();

        // it looks like READY and ACTIVATED status are never persisted, otherwise we need a different check here
        if (mostRecentChange != null) {
            status = mostRecentChange.getNewStatus(); //latest status change
        } else {
            LocalDate localStartDate = getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (localStartDate.isAfter(today)) {
                status = Status.ACTIVATED;
            } else {
                status = Status.READY;
            }
        }
        if (isExpired()) {
            if (status == Status.IN_PROGRESS || status == Status.PAUSED) {
                status = Status.INCOMPLETE;
            } else if (status != Status.DEACTIVATED && status != Status.COMPLETE) {
                status = Status.EXPIRED;
            }
        }
        return status;
    }


    public Status getStatus() {
        return computeStatus((statusChangeHistory.isEmpty() ? null : statusChangeHistory.get(0)));
    }

    //Get the status before the most recent deactivation
    private Status getOldStatus() {
        Deque<ActivationStatusChange> changes = new ArrayDeque<>(statusChangeHistory);
        while (changes.size() > 0 && changes.peek().getNewStatus() == Status.DEACTIVATED) {
            changes.pop();
        }
        return computeStatus(changes.peek());
    }

    public long getTimeSpentOnAssessment() {
        long total = 0;
        Long start = null;
        Iterator<ActivationStatusChange> changes = statusChangeHistory.descendingIterator();
        while (changes.hasNext()) {
            ActivationStatusChange change = changes.next();
            Status status = change.getNewStatus();
            if (status.equals(Status.IN_PROGRESS)) {
                start = change.getChangeDate().getTime();
            } else if (status.equals(Status.PAUSED) || status.equals(Status.COMPLETE)) {
                if (start == null) {
                    log.warn("Something wrong with ActivationStatusChangeSet :" + this.toString());
                } else {
                    total += change.getChangeDate().getTime() - start;
                    start = null;
                }
            }
        }
        if (getStatus() == Status.IN_PROGRESS) {
            if (start == null){
                log.warn("Something wrong with ActivationStatusChangeSet :" + this.toString());
            } else {
                total += System.currentTimeMillis() - start;
            }
        }
        return total;
    }

    public boolean isPrerequisitesMet() {
        return true;
    }

    public StudentViewDto getStudent() {
        return student;
    }

    public void setStudent(StudentViewDto student) {
        this.student = student;
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
