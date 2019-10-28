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
package org.cccnext.tesuto.content.model.metadata;

import org.cccnext.tesuto.content.model.AbstractAssessment;

public class PrerequisiteMetadata implements AbstractAssessment {
    private static final long serialVersionUID = 1l;

    private String assessmentIdRef;

    public String getAssessmentIdRef() {
        return assessmentIdRef;
    }

    public void setAssessmentIdRef(String assessmentIdRef) {
        this.assessmentIdRef = assessmentIdRef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        PrerequisiteMetadata that = (PrerequisiteMetadata) o;

        return assessmentIdRef != null ? assessmentIdRef.equals(that.assessmentIdRef) : that.assessmentIdRef == null;

    }

    @Override
    public int hashCode() {
        return assessmentIdRef != null ? assessmentIdRef.hashCode() : 0;
    }
}
