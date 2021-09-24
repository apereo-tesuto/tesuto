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
package org.cccnext.tesuto.content.dto.item;

import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentResponseProcessingDto implements AbstractAssessmentDto {
    private static final long serialVersionUID = 1l;

    private String template;
    private String templateLocation; // Not for pilot
    private String include; // Not for pilot
    private String responseCondition;
    private String responseProcessFragment; // Not for pilot
    private String setValue;
    private String exitReponse;
    private String lookupOutcomeValue; // Not for pilot

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getTemplateLocation() {
        return templateLocation;
    }

    public void setTemplateLocation(String templateLocation) {
        this.templateLocation = templateLocation;
    }

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    public String getResponseCondition() {
        return responseCondition;
    }

    public void setResponseCondition(String responseCondition) {
        this.responseCondition = responseCondition;
    }

    public String getResponseProcessFragment() {
        return responseProcessFragment;
    }

    public void setResponseProcessFragment(String responseProcessFragment) {
        this.responseProcessFragment = responseProcessFragment;
    }

    public String getSetValue() {
        return setValue;
    }

    public void setSetValue(String setValue) {
        this.setValue = setValue;
    }

    public String getExitReponse() {
        return exitReponse;
    }

    public void setExitReponse(String exitReponse) {
        this.exitReponse = exitReponse;
    }

    public String getLookupOutcomeValue() {
        return lookupOutcomeValue;
    }

    public void setLookupOutcomeValue(String lookupOutcomeValue) {
        this.lookupOutcomeValue = lookupOutcomeValue;
    }
}
