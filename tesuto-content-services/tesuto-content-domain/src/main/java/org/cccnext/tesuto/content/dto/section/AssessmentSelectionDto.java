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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentSelectionDto implements AbstractAssessmentDto {
    private static final long serialVersionUID = 1l;

    private int select;
    private Boolean withReplacement; // Not for 1.4
    private String extensions; // Not for 1.4

    public int getSelect() {
        return select;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    //assumed false for 1.4
    public Boolean getWithReplacement() {
        return withReplacement == null ? false : withReplacement;
    }

    public void setWithReplacement(Boolean withReplacement) {
        this.withReplacement = withReplacement;
    }

    public String getExtensions() {
        return extensions;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).reflectionToString(this);
    }
}
