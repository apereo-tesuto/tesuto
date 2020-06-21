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

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentExtendedTextInteractionDto extends AssessmentInteractionDto {
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
    boolean doValidateResponses(List<String> responses) {
        Integer strings = null;

        if (minStrings != null || maxStrings != null) {
            strings = responses.stream().mapToInt( response -> response.split("\\s+").length).sum();
        }
        return (minStrings == null || strings >= minStrings)
                && (maxStrings == null || strings <= maxStrings);
    }
}
