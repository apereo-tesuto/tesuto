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
package org.cccnext.tesuto.content.dto.item.metadata;

import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;

public class ToolsItemMetadataDto implements AbstractAssessmentDto {
    private static final long serialVersionUID = 1l;

    private String allowCalculator;
    private String allowDictionary;
    private String allowThesaurus;

    /**
     * Without this Jackson Serialization will throw exception if no child nodes
     * are defined. The author has defined the tools node but no other child
     * nodes.
     */
    public ToolsItemMetadataDto(String notUsed) {
        this.allowCalculator = null;
        this.allowDictionary = null;
        this.allowThesaurus = null;
    }

    public ToolsItemMetadataDto() {
    }

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
}
