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

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AssessmentAccessId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;

    private Integer locationId;

    private String assessmentIdentifier;

    private String assessmentNamespace;

    public AssessmentAccessId() {
    }

    public AssessmentAccessId(AssessmentAccess activationAccess) {
        this.setAssessmentIdentifier(activationAccess.getAssessmentIdentifier());
        this.setAssessmentNamespace(activationAccess.getAssessmentNamespace());
        this.setUserId(activationAccess.getUserId());
        this.setLocationId(activationAccess.getLocationId());
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getAssessmentIdentifier() {
        return assessmentIdentifier;
    }

    public void setAssessmentIdentifier(String assessmentIdentifier) {
        this.assessmentIdentifier = assessmentIdentifier;
    }

    public String getAssessmentNamespace() {
        return assessmentNamespace;
    }

    public void setAssessmentNamespace(String assessmentNamespace) {
        this.assessmentNamespace = assessmentNamespace;
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
