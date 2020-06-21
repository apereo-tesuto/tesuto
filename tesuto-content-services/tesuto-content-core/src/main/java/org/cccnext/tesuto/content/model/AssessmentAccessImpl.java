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
package org.cccnext.tesuto.content.model;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(schema="public",name = "assessment_access")
@IdClass(AssessmentAccessId.class)
public class AssessmentAccessImpl implements AssessmentAccess {

	private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Id
    @Column(name = "location_id", nullable = false)
    private Integer locationId;

    @Id
    @Column(name = "assessment_identifier", nullable = false, length = 100)
    private String assessmentIdentifier;

    @Id
    @Column(name = "assessment_namespace", nullable = false, length = 100)
    private String assessmentNamespace;

    @Column(name = "active", nullable = false, length = 100)
    private Boolean active;

    @Column(name = "start_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar startDate;

    @Column(name = "end_date", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar endDate;

    public AssessmentAccessImpl() {
    }

    public AssessmentAccessImpl(AssessmentAccess access) {
        setUserId(access.getUserId());
        setLocationId(access.getLocationId());
        setAssessmentIdentifier(access.getAssessmentIdentifier());
        setAssessmentNamespace(access.getAssessmentIdentifier());
        setActive(access.getActive());
        setStartDate(access.getStartDate());
        setEndDate(access.getEndDate());
    }

    @Override
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    @Override
    public String getAssessmentIdentifier() {
        return assessmentIdentifier;
    }

    public void setAssessmentIdentifier(String assessmentIdentifier) {
        this.assessmentIdentifier = assessmentIdentifier;
    }

    @Override
    public String getAssessmentNamespace() {
        return assessmentNamespace;
    }

    public void setAssessmentNamespace(String assessmentNamespace) {
        this.assessmentNamespace = assessmentNamespace;
    }

    @Override
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @Override
    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    @Override
    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    @Override
    public ScopedIdentifier getScopedIdentifier() {
        return new ScopedIdentifier(assessmentNamespace, assessmentIdentifier);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, "expirationDate");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "expirationDate");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
