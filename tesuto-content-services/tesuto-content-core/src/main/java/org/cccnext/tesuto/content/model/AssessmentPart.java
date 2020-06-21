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

import java.util.List;

import org.cccnext.tesuto.content.dto.AssessmentPartNavigationMode;
import org.cccnext.tesuto.content.dto.AssessmentPartSubmissionMode;
import org.cccnext.tesuto.content.model.expression.AssessmentBranchRule;
import org.cccnext.tesuto.content.model.section.AssessmentItemSessionControl;
import org.cccnext.tesuto.content.model.section.AssessmentSection;
import org.cccnext.tesuto.content.model.expression.AssessmentPreCondition;

/**
 * @author Josh Corbin <jcorbin@unicon.net>
 */
public class AssessmentPart implements AbstractAssessment {
    private static final long serialVersionUID = 1l;

    private String id;
    private AssessmentPartNavigationMode assessmentPartNavigationMode;
    private AssessmentPartSubmissionMode assessmentPartSubmissionMode;
    private AssessmentItemSessionControl itemSessionControl;
    private Double duration;

    // TODO: This is also in AssessmentSection, a cycle is created.
    private List<AssessmentPreCondition> preConditions;
    private List<AssessmentBranchRule> branchRules;
    private List<AssessmentSection> assessmentSections;
    // TODO: feedback

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AssessmentPartNavigationMode getAssessmentPartNavigationMode() {
        return assessmentPartNavigationMode;
    }

    public void setAssessmentPartNavigationMode(AssessmentPartNavigationMode assessmentPartNavigationMode) {
        this.assessmentPartNavigationMode = assessmentPartNavigationMode;
    }

    public AssessmentPartSubmissionMode getAssessmentPartSubmissionMode() {
        return assessmentPartSubmissionMode;
    }

    public void setAssessmentPartSubmissionMode(AssessmentPartSubmissionMode assessmentPartSubmissionMode) {
        this.assessmentPartSubmissionMode = assessmentPartSubmissionMode;
    }

    public AssessmentItemSessionControl getItemSessionControl() {
        return itemSessionControl;
    }

    public void setItemSessionControl(AssessmentItemSessionControl itemSessionControl) {
        this.itemSessionControl = itemSessionControl;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public List<AssessmentPreCondition> getPreConditions() {
        return preConditions;
    }

    public void setPreConditions(List<AssessmentPreCondition> preConditions) {
        this.preConditions = preConditions;
    }

    public List<AssessmentBranchRule> getBranchRules() {
        return branchRules;
    }

    public void setBranchRules(List<AssessmentBranchRule> branchRules) {
        this.branchRules = branchRules;
    }

    public List<AssessmentSection> getAssessmentSections() {
        return assessmentSections;
    }

    public void setAssessmentSections(List<AssessmentSection> assessmentSections) {
        this.assessmentSections = assessmentSections;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((assessmentPartNavigationMode == null) ? 0 : assessmentPartNavigationMode.hashCode());
        result = prime * result
                + ((assessmentPartSubmissionMode == null) ? 0 : assessmentPartSubmissionMode.hashCode());
        result = prime * result + ((assessmentSections == null) ? 0 : assessmentSections.hashCode());
        result = prime * result + ((branchRules == null) ? 0 : branchRules.hashCode());
        result = prime * result + ((duration == null) ? 0 : duration.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((itemSessionControl == null) ? 0 : itemSessionControl.hashCode());
        result = prime * result + ((preConditions == null) ? 0 : preConditions.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AssessmentPart other = (AssessmentPart) obj;
        if (assessmentPartNavigationMode != other.assessmentPartNavigationMode)
            return false;
        if (assessmentPartSubmissionMode != other.assessmentPartSubmissionMode)
            return false;
        if (assessmentSections == null) {
            if (other.assessmentSections != null)
                return false;
        } else if (!assessmentSections.equals(other.assessmentSections))
            return false;
        if (branchRules == null) {
            if (other.branchRules != null)
                return false;
        } else if (!branchRules.equals(other.branchRules))
            return false;
        if (duration == null) {
            if (other.duration != null)
                return false;
        } else if (!duration.equals(other.duration))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (itemSessionControl == null) {
            if (other.itemSessionControl != null)
                return false;
        } else if (!itemSessionControl.equals(other.itemSessionControl))
            return false;
        if (preConditions == null) {
            if (other.preConditions != null)
                return false;
        } else if (!preConditions.equals(other.preConditions))
            return false;
        return true;
    }

}
