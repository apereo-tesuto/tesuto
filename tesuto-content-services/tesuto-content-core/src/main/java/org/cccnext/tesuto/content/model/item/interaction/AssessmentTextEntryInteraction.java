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
package org.cccnext.tesuto.content.model.item.interaction;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentTextEntryInteraction extends AssessmentInteraction {
    private static final long serialVersionUID = 1l;

    private String placeholderText;
    private Integer expectedLength;
    private String patternMask;

    public String getPlaceholderText() {
        return placeholderText;
    }

    public void setPlaceholderText(String placeholderText) {
        this.placeholderText = placeholderText;
    }

    public Integer getExpectedLength() {
        return expectedLength;
    }

    public void setExpectedLength(Integer expectedLength) {
        this.expectedLength = expectedLength;
    }

    public String getPatternMask() {
        return patternMask;
    }

    public void setPatternMask(String patternMask) {
        this.patternMask = patternMask;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((expectedLength == null) ? 0 : expectedLength.hashCode());
        result = prime * result + ((patternMask == null) ? 0 : patternMask.hashCode());
        result = prime * result + ((placeholderText == null) ? 0 : placeholderText.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        AssessmentTextEntryInteraction other = (AssessmentTextEntryInteraction) obj;
        if (expectedLength == null) {
            if (other.expectedLength != null)
                return false;
        } else if (!expectedLength.equals(other.expectedLength))
            return false;
        if (patternMask == null) {
            if (other.patternMask != null)
                return false;
        } else if (!patternMask.equals(other.patternMask))
            return false;
        if (placeholderText == null) {
            if (other.placeholderText != null)
                return false;
        } else if (!placeholderText.equals(other.placeholderText))
            return false;
        return true;
    }
}
