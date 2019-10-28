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

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentChoiceInteractionDto extends AssessmentInteractionDto {
    private static final long serialVersionUID = 2l;

    private List<AssessmentSimpleChoiceDto> choices;
    private int maxChoices;
    private int minChoices;
    private String prompt;

    public int getMaxChoices() {
        return maxChoices;
    }

    public void setMaxChoices(int maxChoices) {
        this.maxChoices = maxChoices;
    }

    public int getMinChoices() {
        return minChoices;
    }

    public void setMinChoices(int minChoices) {
        this.minChoices = minChoices;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public List<AssessmentSimpleChoiceDto> getChoices() {
        return choices;
    }

    public void setChoices(List<AssessmentSimpleChoiceDto> choices) {
        this.choices = choices;
    }

    @Override
    boolean doValidateResponses(List<String> responses) {
        return responses.size() >= getMinChoices() && responses.size() <= getMaxChoices() &&
                //Every response has to be for one of the available choices
                responses.stream().allMatch( response ->
                        choices.stream().anyMatch(choice -> choice.getIdentifier().equals(response)
                        )
                );
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
