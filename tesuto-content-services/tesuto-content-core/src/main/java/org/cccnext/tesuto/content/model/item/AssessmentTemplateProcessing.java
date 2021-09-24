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
public class AssessmentTemplateProcessing implements AbstractAssessment {
    private static final long serialVersionUID = 1l;

    private String setValue;
    private String exitTemplate;
    private String templateCondition;
    // TODO: Not used right now but it likely should. I'm skipping it in the
    // adapter right now.
    private AssessmentDefaultValue defaultValue;
    private String setDefaultValue;
    private String correctResponse;
    private String templateConstraint;

    public String getSetValue() {
        return setValue;
    }

    public void setSetValue(String setValue) {
        this.setValue = setValue;
    }

    public String getExitTemplate() {
        return exitTemplate;
    }

    public void setExitTemplate(String exitTemplate) {
        this.exitTemplate = exitTemplate;
    }

    public String getTemplateCondition() {
        return templateCondition;
    }

    public void setTemplateCondition(String templateCondition) {
        this.templateCondition = templateCondition;
    }

    public AssessmentDefaultValue getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(AssessmentDefaultValue defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getCorrectResponse() {
        return correctResponse;
    }

    public void setCorrectResponse(String correctResponse) {
        this.correctResponse = correctResponse;
    }

    public String getTemplateConstraint() {
        return templateConstraint;
    }

    public void setTemplateConstraint(String templateConstraint) {
        this.templateConstraint = templateConstraint;
    }

    public String getSetDefaultValue() {
        return setDefaultValue;
    }

    public void setSetDefaultValue(String setDefaultValue) {
        this.setDefaultValue = setDefaultValue;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((correctResponse == null) ? 0 : correctResponse.hashCode());
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((exitTemplate == null) ? 0 : exitTemplate.hashCode());
        result = prime * result + ((setDefaultValue == null) ? 0 : setDefaultValue.hashCode());
        result = prime * result + ((setValue == null) ? 0 : setValue.hashCode());
        result = prime * result + ((templateCondition == null) ? 0 : templateCondition.hashCode());
        result = prime * result + ((templateConstraint == null) ? 0 : templateConstraint.hashCode());
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
        AssessmentTemplateProcessing other = (AssessmentTemplateProcessing) obj;
        if (correctResponse == null) {
            if (other.correctResponse != null)
                return false;
        } else if (!correctResponse.equals(other.correctResponse))
            return false;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (exitTemplate == null) {
            if (other.exitTemplate != null)
                return false;
        } else if (!exitTemplate.equals(other.exitTemplate))
            return false;
        if (setDefaultValue == null) {
            if (other.setDefaultValue != null)
                return false;
        } else if (!setDefaultValue.equals(other.setDefaultValue))
            return false;
        if (setValue == null) {
            if (other.setValue != null)
                return false;
        } else if (!setValue.equals(other.setValue))
            return false;
        if (templateCondition == null) {
            if (other.templateCondition != null)
                return false;
        } else if (!templateCondition.equals(other.templateCondition))
            return false;
        if (templateConstraint == null) {
            if (other.templateConstraint != null)
                return false;
        } else if (!templateConstraint.equals(other.templateConstraint))
            return false;
        return true;
    }
}
