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
public class AssessmentExtendedTextInteraction extends AssessmentInteraction {
    private static final long serialVersionUID = 1l;

    private Integer expectedLines;
    private Integer minStrings;
    private Integer maxStrings;
    private Integer expectedLength;

    public Integer getExpectedLines() {
        return expectedLines;
    }

    public void setExpectedLines(Integer expectedLines) {
        this.expectedLines = expectedLines;
    }

    public Integer getMinStrings() {
        return minStrings;
    }

    public void setMinStrings(Integer minStrings) {
        this.minStrings = minStrings;
    }

    public Integer getMaxStrings() {
        return maxStrings;
    }

    public void setMaxStrings(Integer maxStrings) {
        this.maxStrings = maxStrings;
    }

    public Integer getExpectedLength() {
        return expectedLength;
    }

    public void setExpectedLength(Integer expectedLength) {
        this.expectedLength = expectedLength;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expectedLength == null) ? 0 : expectedLength.hashCode());
        result = prime * result + ((expectedLines == null) ? 0 : expectedLines.hashCode());
        result = prime * result + ((maxStrings == null) ? 0 : maxStrings.hashCode());
        result = prime * result + ((minStrings == null) ? 0 : minStrings.hashCode());
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
        AssessmentExtendedTextInteraction other = (AssessmentExtendedTextInteraction) obj;
        if (expectedLength == null) {
            if (other.expectedLength != null)
                return false;
        } else if (!expectedLength.equals(other.expectedLength))
            return false;
        if (expectedLines == null) {
            if (other.expectedLines != null)
                return false;
        } else if (!expectedLines.equals(other.expectedLines))
            return false;
        if (maxStrings == null) {
            if (other.maxStrings != null)
                return false;
        } else if (!maxStrings.equals(other.maxStrings))
            return false;
        if (minStrings == null) {
            if (other.minStrings != null)
                return false;
        } else if (!minStrings.equals(other.minStrings))
            return false;
        return true;
    }
}
