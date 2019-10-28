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

import org.cccnext.tesuto.content.model.section.AssessmentSection;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentRootNode implements AbstractAssessment {
    private static final long serialVersionUID = 1l;

    private List<AssessmentSection> assessmentSections;

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
        result = prime * result + ((assessmentSections == null) ? 0 : assessmentSections.hashCode());
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
        AssessmentRootNode other = (AssessmentRootNode) obj;
        if (assessmentSections == null) {
            if (other.assessmentSections != null)
                return false;
        } else if (!assessmentSections.equals(other.assessmentSections))
            return false;
        return true;
    }
}
