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
package org.cccnext.tesuto.placement.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(schema="public", name="placement_event_log")

public class PlacementEventLog implements Serializable {

    public enum EventType { 
        PLACEMENT_ASSESS_REQUEST_RECEIVED,
        PLACEMENT_MM_REQUEST_RECEIVED,
        PLACEMENT_REQUEST_NO_COLLEGES,
        PLACEMENT_REQUEST_NO_COLLEGE_DISCIPLINES,
        PLACEMENT_ASSESS_REQUEST_FAIL,
        PLACEMENT_MM_REQUEST_FAIL,
        PLACEMENT_COMPONENT_ASSESS_PROCESSING_START,
        PLACEMENT_COMPONENT_ASSESS_PROCESSING_FAIL,
        PLACEMENT_COMPONENT_ASSESS_PROCESSING_COMPLETE,
        PLACEMENT_COMPONENT_ASSESS_DUPLICATE,
        PLACEMENT_COMPONENT_ASSESS_NULL_DISCIPLINE_SEQUENCE,
        PLACEMENT_COMPONENT_MM_PROCESSING_START,
        PLACEMENT_COMPONENT_MM_PROCESSING_FAIL,
        PLACEMENT_COMPONENT_MM_PROCESSING_COMPLETE,
        PLACEMENT_COMPONENT_MM_PROCESSING_COMPLETE_NO_PLACEMENT_GENERATED,
        PLACEMENT_COMPONENT_MM_ADD_START,
        PLACEMENT_COMPONENT_MM_ADD_FAIL,
        PLACEMENT_COMPONENT_MM_ADD_COMPLETE,
        PLACEMENT_COMPONENT_MM_FAILED_ON_VALIDATE,
        PLACEMENT_COMPONENT_MM_INSUFFICIENT_DATA,
        PLACEMENT_COMPONENT_MM_DUPLICATE,
        PLACEMENT_COMPONENT_MM_NULL_DISCIPLINE_SEQUENCE,
        PLACEMENT_COMPONENT_SAVE_START, 
        PLACEMENT_COMPONENT_SAVE_COMPLETE, 
        PLACEMENT_COMPONENT_SAVE_FAILURE,
        PLACEMENT_COMPONENT_MM_OPT_OUT,
        PLACEMENT_PROCESSING_START,
        PLACEMENT_PROCESSING_FAIL,
        PLACEMENT_PROCESSING_COMPLETE,
        PLACEMENT_SAVE_START, 
        PLACEMENT_SAVE_COMPLETE, 
        PLACEMENT_DUPLICATE,
        PLACEMENT_SAVE_FAILURE,
        ASSIGN_PLACEMENT_DUPLICATE,
        ASSIGN_PLACEMENT_PROCESSING_START,
        ASSIGN_PLACEMENT_PROCESSING_FAIL,
        ASSIGN_PLACEMENT_PROCESSING_COMPLETE,
        ASSIGN_PLACEMENT_SAVE_START, 
        ASSIGN_PLACEMENT_SAVE_COMPLETE, 
        ASSIGN_PLACEMENT_SAVE_FAILURE
    };

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="placement_event_log_id")
    private Integer placementEventLogId;

    @Column(name="tracking_id")
    private String trackingId;

    @Column(name="cccid")
    private String cccId;

    @Column(name="subject_area_id")
    private Integer subjectAreaId;

    @Column(name="subject_area_version_id")
    private Integer subjectAreaVersionId;

    @Column(name="miscode")
    private String misCode;

    @Column(name="event")
    @Enumerated(EnumType.STRING)
    private EventType event;

    @Column(name="create_date")
    private Date createDate;

    @Column(name="message")
    private String message;

    public Integer getPlacementEventLogId() {
        return placementEventLogId;
    }

    public void setPlacementEventLogId(Integer placementEventLogId) {
        this.placementEventLogId = placementEventLogId;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getCccId() {
        return cccId;
    }

    public void setCccId(String cccId) {
        this.cccId = cccId;
    }

    public Integer getSubjectAreaId() {
        return subjectAreaId;
    }

    public void setSubjectAreaId(Integer subjectAreaId) {
        this.subjectAreaId = subjectAreaId;
    }

    public Integer getSubjectAreaVersionId() {
        return subjectAreaVersionId;
    }

    public void setSubjectAreaVersionId(Integer subjectAreaVersionId) {
        this.subjectAreaVersionId = subjectAreaVersionId;
    }

    public String getMisCode() {
        return misCode;
    }

    public void setMisCode(String misCode) {
        this.misCode = misCode;
    }

    public EventType getEvent() {
        return event;
    }

    public void setEvent(EventType event) {
        this.event = event;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
