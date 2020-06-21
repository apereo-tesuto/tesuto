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
package org.cccnext.tesuto.content.dto.section;

import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;
import org.cccnext.tesuto.content.dto.AssessmentComponentDto;
import org.cccnext.tesuto.content.dto.expression.AssessmentBranchRuleDto;
import org.cccnext.tesuto.content.dto.expression.AssessmentPreConditionDto;
import org.cccnext.tesuto.content.dto.item.AssessmentStimulusRefDto;

/**
 * This has a reference to itself because it is recursive. At the end of the
 * tree it should be null.
 * 
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentSectionDto implements AbstractAssessmentDto, AssessmentComponentDto {
    private static final long serialVersionUID = 1l;

    private String id;
    private boolean isRequired;
    private boolean isFixed;
    private String title;
    private Boolean visible; // Not for pilot
    private Boolean keepTogether; // Not for pilot
    private List<AssessmentPreConditionDto> preConditions; // May not be needed
                                                           // for pilot
    private List<AssessmentBranchRuleDto> branchRules;
    private AssessmentItemSessionControlDto itemSessionControl;
    private String timeLimits; // Not for pilot
    private AssessmentSelectionDto selection;
    private AssessmentOrderingDto ordering;
    private List<AssessmentRubricBlockDto> rubricBlocks;
    private List<AssessmentItemRefDto> assessmentItemRefs;
    private List<AssessmentSectionDto> assessmentSections; // Self referencing
                                                           // and recursive.
    private List<AssessmentComponentDto> assessmentComponents;
    private AssessmentStimulusRefDto assessmentStimulusRef; // Not for pilot

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public void setIsRequired(boolean isRequired) {
        this.isRequired = isRequired;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public void setIsFixed(boolean isFixed) {
        this.isFixed = isFixed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getKeepTogether() {
        return keepTogether;
    }

    public void setKeepTogether(Boolean keepTogether) {
        this.keepTogether = keepTogether;
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

    public AssessmentItemSessionControlDto getItemSessionControl() {
        return itemSessionControl;
    }

    public void setItemSessionControlDto(AssessmentItemSessionControlDto itemSessionControl) {
        this.itemSessionControl = itemSessionControl;
    }

    public String getTimeLimits() {
        return timeLimits;
    }

    public void setTimeLimits(String timeLimits) {
        this.timeLimits = timeLimits;
    }

    public AssessmentSelectionDto getSelection() {
        return selection;
    }

    public void setSelection(AssessmentSelectionDto selection) {
        this.selection = selection;
    }

    public AssessmentOrderingDto getOrdering() {
        return ordering;
    }

    public void setOrdering(AssessmentOrderingDto ordering) {
        this.ordering = ordering;
    }

    public List<AssessmentRubricBlockDto> getRubricBlocks() {
        return rubricBlocks;
    }

    public void setRubricBlocks(List<AssessmentRubricBlockDto> rubricBlocks) {
        this.rubricBlocks = rubricBlocks;
    }

    public List<AssessmentSectionDto> getAssessmentSections() {
        return assessmentSections;
    }

    public void setAssessmentSections(List<AssessmentSectionDto> assessmentSections) {
        this.assessmentSections = assessmentSections;
    }

    public List<AssessmentComponentDto> getAssessmentComponents() {
        return assessmentComponents;
    }

    public void setAssessmentComponents(List<AssessmentComponentDto> assessmentComponents) {
        this.assessmentComponents = assessmentComponents;
    }

    public AssessmentStimulusRefDto getAssessmentStimulusRef() {
        return assessmentStimulusRef;
    }

    public void setAssessmentStimulusRef(AssessmentStimulusRefDto assessmentStimulusRef) {
        this.assessmentStimulusRef = assessmentStimulusRef;
    }

    public List<AssessmentItemRefDto> getAssessmentItemRefs() {
        return assessmentItemRefs;
    }

    public void setAssessmentItemRefs(List<AssessmentItemRefDto> assessmentItemRefs) {
        this.assessmentItemRefs = assessmentItemRefs;
    }

    public Stream<? extends AssessmentComponentDto> getChildren() {
        return Stream.concat(assessmentItemRefs.stream(), assessmentSections.stream());
    }

    @Override
    public boolean equals(Object o) {
        return new EqualsBuilder().reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).reflectionToString(this);
    }
}
