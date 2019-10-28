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

import org.cccnext.tesuto.content.model.AbstractAssessment;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentItemSessionControl implements AbstractAssessment {
    private static final long serialVersionUID = 1l;

    private int maxAttempts;
    private Boolean showFeedback; // Not for pilot
    private Boolean allowReview; // Not for pilot
    private Boolean showSolution; // Not for pilot
    private Boolean allowComment; // Not for pilot
    private Boolean allowSkipping; // Not for pilot
    private Boolean validateResponses; // Not for pilot

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public Boolean getShowFeedback() {
        return showFeedback;
    }

    public void setShowFeedback(Boolean showFeedback) {
        this.showFeedback = showFeedback;
    }

    public Boolean getAllowReview() {
        return allowReview;
    }

    public void setAllowReview(Boolean allowReview) {
        this.allowReview = allowReview;
    }

    public Boolean getShowSolution() {
        return showSolution;
    }

    public void setShowSolution(Boolean showSolution) {
        this.showSolution = showSolution;
    }

    public Boolean getAllowComment() {
        return allowComment;
    }

    public void setAllowComment(Boolean allowComment) {
        this.allowComment = allowComment;
    }

    public Boolean getAllowSkipping() {
        return allowSkipping;
    }

    public void setAllowSkipping(Boolean allowSkipping) {
        this.allowSkipping = allowSkipping;
    }

    public Boolean getValidateResponses() {
        return validateResponses;
    }

    public void setValidateResponses(Boolean validateResponses) {
        this.validateResponses = validateResponses;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((allowComment == null) ? 0 : allowComment.hashCode());
        result = prime * result + ((allowReview == null) ? 0 : allowReview.hashCode());
        result = prime * result + ((allowSkipping == null) ? 0 : allowSkipping.hashCode());
        result = prime * result + maxAttempts;
        result = prime * result + ((showFeedback == null) ? 0 : showFeedback.hashCode());
        result = prime * result + ((showSolution == null) ? 0 : showSolution.hashCode());
        result = prime * result + ((validateResponses == null) ? 0 : validateResponses.hashCode());
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
        AssessmentItemSessionControl other = (AssessmentItemSessionControl) obj;
        if (allowComment == null) {
            if (other.allowComment != null)
                return false;
        } else if (!allowComment.equals(other.allowComment))
            return false;
        if (allowReview == null) {
            if (other.allowReview != null)
                return false;
        } else if (!allowReview.equals(other.allowReview))
            return false;
        if (allowSkipping == null) {
            if (other.allowSkipping != null)
                return false;
        } else if (!allowSkipping.equals(other.allowSkipping))
            return false;
        if (maxAttempts != other.maxAttempts)
            return false;
        if (showFeedback == null) {
            if (other.showFeedback != null)
                return false;
        } else if (!showFeedback.equals(other.showFeedback))
            return false;
        if (showSolution == null) {
            if (other.showSolution != null)
                return false;
        } else if (!showSolution.equals(other.showSolution))
            return false;
        if (validateResponses == null) {
            if (other.validateResponses != null)
                return false;
        } else if (!validateResponses.equals(other.validateResponses))
            return false;
        return true;
    }
}
