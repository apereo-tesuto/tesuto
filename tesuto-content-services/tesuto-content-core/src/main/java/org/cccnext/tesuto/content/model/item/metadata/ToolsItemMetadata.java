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
package org.cccnext.tesuto.content.model.item.metadata;

import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;

public class ToolsItemMetadata implements AbstractAssessmentDto {
    private static final long serialVersionUID = 1l;

    private String allowCalculator;
    private String allowDictionary;
    private String allowThesaurus;

    public String getAllowCalculator() {
        return allowCalculator;
    }

    public void setAllowCalculator(String allowCalculator) {
        this.allowCalculator = allowCalculator;
    }

    public String getAllowDictionary() {
        return allowDictionary;
    }

    public void setAllowDictionary(String allowDictionary) {
        this.allowDictionary = allowDictionary;
    }

    public String getAllowThesaurus() {
        return allowThesaurus;
    }

    public void setAllowThesaurus(String allowThesaurus) {
        this.allowThesaurus = allowThesaurus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ToolsItemMetadata that = (ToolsItemMetadata) o;

        if (allowCalculator != null ? !allowCalculator.equals(that.allowCalculator) : that.allowCalculator != null)
            return false;
        if (allowDictionary != null ? !allowDictionary.equals(that.allowDictionary) : that.allowDictionary != null)
            return false;
        return allowThesaurus != null ? allowThesaurus.equals(that.allowThesaurus) : that.allowThesaurus == null;

    }

    @Override
    public int hashCode() {
        int result = allowCalculator != null ? allowCalculator.hashCode() : 0;
        result = 31 * result + (allowDictionary != null ? allowDictionary.hashCode() : 0);
        result = 31 * result + (allowThesaurus != null ? allowThesaurus.hashCode() : 0);
        return result;
    }
}
