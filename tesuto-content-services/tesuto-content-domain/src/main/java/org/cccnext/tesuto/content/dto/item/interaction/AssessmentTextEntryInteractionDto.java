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
package org.cccnext.tesuto.content.dto.item.interaction;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentTextEntryInteractionDto extends AssessmentInteractionDto {
    private static final long serialVersionUID = 1l;
    private String placeholderText;
    private Integer expectedLength;
    private String patternMask;
    private Predicate<String> pattern;

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
    boolean doValidateResponses(List<String> responses) {
        if (pattern == null && patternMask != null) {
            pattern = Pattern.compile(patternMask).asPredicate();
        }
        return pattern == null || responses.stream().allMatch(pattern);
    }
}
