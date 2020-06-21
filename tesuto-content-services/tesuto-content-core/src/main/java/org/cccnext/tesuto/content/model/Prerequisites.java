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

import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bruce on 1/6/16.
 */
public class Prerequisites implements AbstractAssessment {
    private static final long serialVersionUID = 1l;

    @Id
    private ScopedIdentifier assessmentScopedIdentifier;
    private Set<ScopedIdentifier> prerequisites;

    public Prerequisites(ScopedIdentifier assessmentScopedIdentifier, Collection<ScopedIdentifier> prerequisites) {
        this.assessmentScopedIdentifier = assessmentScopedIdentifier;
        this.prerequisites = new HashSet<>(prerequisites.size());
        this.prerequisites.addAll(prerequisites);
    }

    public ScopedIdentifier getAssessmentScopedIdentifier() {
        return assessmentScopedIdentifier;
    }

    public void setAssessmentScopedIdentifier(ScopedIdentifier assessmentScopedIdentifier) {
        this.assessmentScopedIdentifier = assessmentScopedIdentifier;
    }

    public Set<ScopedIdentifier> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(Set<ScopedIdentifier> prerequisites) {
        this.prerequisites = prerequisites;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Prerequisites))
            return false;

        Prerequisites that = (Prerequisites) o;

        if (getAssessmentScopedIdentifier() != null
                ? !getAssessmentScopedIdentifier().equals(that.getAssessmentScopedIdentifier())
                : that.getAssessmentScopedIdentifier() != null)
            return false;
        return getPrerequisites() != null ? getPrerequisites().equals(that.getPrerequisites())
                : that.getPrerequisites() == null;

    }

    @Override
    public int hashCode() {
        int result = getAssessmentScopedIdentifier() != null ? getAssessmentScopedIdentifier().hashCode() : 0;
        result = 31 * result + (getPrerequisites() != null ? getPrerequisites().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Prerequisites{" + "assessmentScopedIdentifier=" + assessmentScopedIdentifier + ", prerequisites="
                + prerequisites + '}';
    }
}
