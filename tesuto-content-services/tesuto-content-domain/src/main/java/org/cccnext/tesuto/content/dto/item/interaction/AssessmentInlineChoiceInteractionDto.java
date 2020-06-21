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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentInlineChoiceInteractionDto extends AssessmentInteractionDto {
    private static final long serialVersionUID = 1l;

    private String prompt;
    private List<AssessmentInlineChoiceDto> inlineChoices;

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public List<AssessmentInlineChoiceDto> getInlineChoices() {
        return inlineChoices;
    }

    public void setInlineChoices(List<AssessmentInlineChoiceDto> inlineChoices) {
        this.inlineChoices = inlineChoices;
    }

    @Override
    boolean doValidateResponses(List<String> responses) {
        Set<String> validResponses = inlineChoices.stream().map(choice -> choice.getIdentifier()).collect(Collectors.toSet());
        return responses.stream().allMatch(response -> validResponses.contains(response));
    }
}
