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

import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentItemSessionControlDto implements AbstractAssessmentDto {
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
}
