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
package org.cccnext.tesuto.content.dto;

import org.cccnext.tesuto.content.dto.expression.AssessmentBranchRuleDto;
import org.cccnext.tesuto.content.dto.expression.AssessmentPreConditionDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemSessionControlDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Josh Corbin <jcorbin@unicon.net>
 */
public class AssessmentPartDto implements AbstractAssessmentDto, AssessmentComponentDto {
    private static final long serialVersionUID = 1l;

    private String id;
    private AssessmentPartNavigationMode assessmentPartNavigationMode;
    private AssessmentPartSubmissionMode assessmentPartSubmissionMode;
    private AssessmentItemSessionControlDto itemSessionControl;
    private Double duration;

    private List<AssessmentPreConditionDto> preConditions;
    private List<AssessmentBranchRuleDto> branchRules;
    private List<AssessmentSectionDto> assessmentSections;
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

    public void setAssessmentPartNavigationMode(AssessmentPartNavigationMode assessmentPartNavigation) {
        this.assessmentPartNavigationMode = assessmentPartNavigation;
    }

    public AssessmentPartSubmissionMode getAssessmentPartSubmission() {
        return assessmentPartSubmissionMode;
    }

    public void setAssessmentPartSubmissionMode(AssessmentPartSubmissionMode assessmentPartSubmissionMode) {
        this.assessmentPartSubmissionMode = assessmentPartSubmissionMode;
    }

    public AssessmentItemSessionControlDto getItemSessionControl() {
        return itemSessionControl;
    }

    public void setItemSessionControl(AssessmentItemSessionControlDto itemSessionControl) {
        this.itemSessionControl = itemSessionControl;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public List<AssessmentPreConditionDto> getPreConditions() {
        return preConditions;
    }

    public void setPreConditions(List<AssessmentPreConditionDto> preConditions) {
        this.preConditions = preConditions;
    }

    public List<AssessmentBranchRuleDto> getBranchRules() {
        return branchRules;
    }

    public void setBranchRules(List<AssessmentBranchRuleDto> branchRules) {
        this.branchRules = branchRules;
    }

    public List<AssessmentSectionDto> getAssessmentSections() {
        return assessmentSections;
    }

    public void setAssessmentSections(List<AssessmentSectionDto> assessmentSections) {
        this.assessmentSections = assessmentSections;
    }

    public Stream<? extends AssessmentComponentDto> getChildren() {
        return assessmentSections.stream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AssessmentPartDto))
            return false;

        AssessmentPartDto that = (AssessmentPartDto) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null)
            return false;
        if (getAssessmentPartNavigationMode() != that.getAssessmentPartNavigationMode())
            return false;
        if (assessmentPartSubmissionMode != that.assessmentPartSubmissionMode)
            return false;
        if (getItemSessionControl() != null ? !getItemSessionControl().equals(that.getItemSessionControl())
                : that.getItemSessionControl() != null)
            return false;
        if (getDuration() != null ? !getDuration().equals(that.getDuration()) : that.getDuration() != null)
            return false;
        if (getPreConditions() != null ? !getPreConditions().equals(that.getPreConditions())
                : that.getPreConditions() != null)
            return false;
        if (getBranchRules() != null ? !getBranchRules().equals(that.getBranchRules()) : that.getBranchRules() != null)
            return false;
        return getAssessmentSections() != null ? getAssessmentSections().equals(that.getAssessmentSections())
                : that.getAssessmentSections() == null;

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result
                + (getAssessmentPartNavigationMode() != null ? getAssessmentPartNavigationMode().hashCode() : 0);
        result = 31 * result + (assessmentPartSubmissionMode != null ? assessmentPartSubmissionMode.hashCode() : 0);
        result = 31 * result + (getItemSessionControl() != null ? getItemSessionControl().hashCode() : 0);
        result = 31 * result + (getDuration() != null ? getDuration().hashCode() : 0);
        result = 31 * result + (getPreConditions() != null ? getPreConditions().hashCode() : 0);
        result = 31 * result + (getBranchRules() != null ? getBranchRules().hashCode() : 0);
        result = 31 * result + (getAssessmentSections() != null ? getAssessmentSections().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AssessmentPartDto{" + "id='" + id + '\'' + ", assessmentPartNavigationMode="
                + assessmentPartNavigationMode + ", assessmentPartSubmissionMode=" + assessmentPartSubmissionMode
                + ", itemSessionControl=" + itemSessionControl + ", duration=" + duration + ", preConditions="
                + preConditions + ", branchRules=" + branchRules + ", assessmentSections=" + assessmentSections + '}';
    }
}
