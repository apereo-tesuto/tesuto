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
package org.cccnext.tesuto.content.model.section;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cccnext.tesuto.content.model.AbstractAssessment;
import org.cccnext.tesuto.content.model.expression.AssessmentBranchRule;
import org.cccnext.tesuto.content.model.expression.AssessmentPreCondition;
import org.cccnext.tesuto.content.model.item.AssessmentStimulusRef;
import org.cccnext.tesuto.content.model.shared.AssessmentComponent;

/**
 * This has a reference to itself because it is recursive. At the end of the
 * tree it should be null.
 * 
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentSection implements AbstractAssessment, AssessmentComponent {
    private static final long serialVersionUID = 1l;

    private String id;
    private boolean isRequired;
    private boolean isFixed;
    private String title;
    private Boolean visible; // Not for pilot
    private Boolean keepTogether; // Not for pilot
    // TODO: This is also in AssessmentPart, a cycle is created.
    private List<AssessmentPreCondition> preConditions; // May not be needed for
                                                        // pilot
    private List<AssessmentBranchRule> branchRules;
    private AssessmentItemSessionControl itemSessionControl;
    private String timeLimits; // Not for pilot
    private AssessmentSelection selection;
    private AssessmentOrdering ordering;
    private List<AssessmentRubricBlock> rubricBlocks;
    private List<AssessmentItemRef> assessmentItemRefs;
    private List<AssessmentSection> assessmentSections; // Self referencing and
                                                        // recursive.
    private List<AssessmentComponent> assessmentComponents;
    private AssessmentStimulusRef assessmentStimulusRef; // Not for pilot

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

    public AssessmentItemSessionControl getItemSessionControl() {
        return itemSessionControl;
    }

    public void setItemSessionControl(AssessmentItemSessionControl itemSessionControl) {
        this.itemSessionControl = itemSessionControl;
    }

    public String getTimeLimits() {
        return timeLimits;
    }

    public void setTimeLimits(String timeLimits) {
        this.timeLimits = timeLimits;
    }

    public AssessmentSelection getSelection() {
        return selection;
    }

    public void setSelection(AssessmentSelection selection) {
        this.selection = selection;
    }

    public AssessmentOrdering getOrdering() {
        return ordering;
    }

    public void setOrdering(AssessmentOrdering ordering) {
        this.ordering = ordering;
    }

    public List<AssessmentRubricBlock> getRubricBlocks() {
        return rubricBlocks;
    }

    public void setRubricBlocks(List<AssessmentRubricBlock> rubricBlocks) {
        this.rubricBlocks = rubricBlocks;
    }

    public List<AssessmentSection> getAssessmentSections() {
        return assessmentSections;
    }

    public void setAssessmentSections(List<AssessmentSection> assessmentSections) {
        this.assessmentSections = assessmentSections;
    }

    public List<AssessmentComponent> getAssessmentComponents() {
        return assessmentComponents;
    }

    public void setAssessmentComponents(List<AssessmentComponent> assessmentComponents) {
        this.assessmentComponents = assessmentComponents;
    }

    public AssessmentStimulusRef getAssessmentStimulusRef() {
        return assessmentStimulusRef;
    }

    public void setAssessmentStimulusRef(AssessmentStimulusRef assessmentStimulusRef) {
        this.assessmentStimulusRef = assessmentStimulusRef;
    }

    public List<AssessmentItemRef> getAssessmentItemRefs() {
        return assessmentItemRefs;
    }

    public void setAssessmentItemRefs(List<AssessmentItemRef> assessmentItemRefs) {
        this.assessmentItemRefs = assessmentItemRefs;
    }

    @Override
    public boolean equals(Object o) {
        return new EqualsBuilder().reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().reflectionHashCode(this);
    }
}
