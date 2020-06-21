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
package org.cccnext.tesuto.placement.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Date;

public class AssessmentCompletePlacementInputDto extends  PlacementEventInputDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Double studentAbility;
    private String assessmentSessionId;
    private String assessmentTitle;
    private String elaIndicator;
    private Date completionDate;

    public Double getStudentAbility() {
        return studentAbility;
    }
    public void setStudentAbility(Double studentAbility) {
        this.studentAbility = studentAbility;
    }

    public String getAssessmentSessionId() {
        return assessmentSessionId;
    }
    public void setAssessmentSessionId(String assessmentSessionId) {
        this.assessmentSessionId = assessmentSessionId;
    }

    public String getAssessmentTitle() {
        return assessmentTitle;
    }

    public void setAssessmentTitle(String assessmentTitle) {
        this.assessmentTitle = assessmentTitle;
    }

    public String getElaIndicator() {
        return elaIndicator;
    }

    public void setElaIndicator(String elaIndicator) {
        this.elaIndicator = elaIndicator;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
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
