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
package org.cccnext.tesuto.content.model.item;

import org.cccnext.tesuto.content.model.AbstractAssessment;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentResponseProcessing implements AbstractAssessment {
    private static final long serialVersionUID = 1l;

    private String template; // Not for pilot
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((exitReponse == null) ? 0 : exitReponse.hashCode());
        result = prime * result + ((include == null) ? 0 : include.hashCode());
        result = prime * result + ((lookupOutcomeValue == null) ? 0 : lookupOutcomeValue.hashCode());
        result = prime * result + ((responseCondition == null) ? 0 : responseCondition.hashCode());
        result = prime * result + ((responseProcessFragment == null) ? 0 : responseProcessFragment.hashCode());
        result = prime * result + ((setValue == null) ? 0 : setValue.hashCode());
        result = prime * result + ((template == null) ? 0 : template.hashCode());
        result = prime * result + ((templateLocation == null) ? 0 : templateLocation.hashCode());
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
        AssessmentResponseProcessing other = (AssessmentResponseProcessing) obj;
        if (exitReponse == null) {
            if (other.exitReponse != null)
                return false;
        } else if (!exitReponse.equals(other.exitReponse))
            return false;
        if (include == null) {
            if (other.include != null)
                return false;
        } else if (!include.equals(other.include))
            return false;
        if (lookupOutcomeValue == null) {
            if (other.lookupOutcomeValue != null)
                return false;
        } else if (!lookupOutcomeValue.equals(other.lookupOutcomeValue))
            return false;
        if (responseCondition == null) {
            if (other.responseCondition != null)
                return false;
        } else if (!responseCondition.equals(other.responseCondition))
            return false;
        if (responseProcessFragment == null) {
            if (other.responseProcessFragment != null)
                return false;
        } else if (!responseProcessFragment.equals(other.responseProcessFragment))
            return false;
        if (setValue == null) {
            if (other.setValue != null)
                return false;
        } else if (!setValue.equals(other.setValue))
            return false;
        if (template == null) {
            if (other.template != null)
                return false;
        } else if (!template.equals(other.template))
            return false;
        if (templateLocation == null) {
            if (other.templateLocation != null)
                return false;
        } else if (!templateLocation.equals(other.templateLocation))
            return false;
        return true;
    }
}
