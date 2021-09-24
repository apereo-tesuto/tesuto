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
public class AssessmentTemplateProcessingDto implements AbstractAssessmentDto {
    private static final long serialVersionUID = 1l;

    private String setValue;
    private String exitTemplate;
    private String templateCondition;
    // TODO: Not used right now but it likely should. I'm skipping it in the
    // adapter right now.
    private AssessmentDefaultValueDto defaultValueDto;
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

    public AssessmentDefaultValueDto getDefaultValueDto() {
        return defaultValueDto;
    }

    public void setDefaultValueDto(AssessmentDefaultValueDto defaultValueDto) {
        this.defaultValueDto = defaultValueDto;
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
}
