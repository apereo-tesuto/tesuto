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

import java.util.List;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentInlineChoiceInteraction extends AssessmentInteraction {
    private static final long serialVersionUID = 1l;

    private List<AssessmentInlineChoice> inlineChoices;
    private int maxChoices;
    private String prompt;

    public int getMaxChoices() {
        return maxChoices;
    }

    public void setMaxChoices(int maxChoices) {
        this.maxChoices = maxChoices;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public List<AssessmentInlineChoice> getInlineChoices() {
        return inlineChoices;
    }

    public void setInlineChoices(List<AssessmentInlineChoice> inlineChoices) {
        this.inlineChoices = inlineChoices;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + maxChoices;
        result = prime * result + ((prompt == null) ? 0 : prompt.hashCode());
        result = prime * result + ((inlineChoices == null) ? 0 : inlineChoices.hashCode());
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
        AssessmentInlineChoiceInteraction other = (AssessmentInlineChoiceInteraction) obj;
        if (maxChoices != other.maxChoices)
            return false;
        if (prompt == null) {
            if (other.prompt != null)
                return false;
        } else if (!prompt.equals(other.prompt))
            return false;
        if (inlineChoices == null) {
            if (other.inlineChoices != null)
                return false;
        } else if (!inlineChoices.equals(other.inlineChoices))
            return false;
        return true;
    }
}
