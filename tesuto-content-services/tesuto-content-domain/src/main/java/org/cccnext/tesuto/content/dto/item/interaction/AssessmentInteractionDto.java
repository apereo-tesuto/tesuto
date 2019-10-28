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
import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.AssessmentPartDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import java.util.Collections;
import java.util.List;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */

@JsonTypeInfo(use = Id.CLASS,
include = JsonTypeInfo.As.PROPERTY,
property = "typeClass")
@JsonSubTypes({
@Type(value = AssessmentChoiceInteractionDto.class),
@Type(value = AssessmentInlineChoiceInteractionDto.class),
@Type(value = AssessmentExtendedTextInteractionDto.class),
@Type(value = AssessmentMatchInteractionDto.class),
@Type(value = AssessmentSimpleAssociableChoiceDto.class),
@Type(value = AssessmentSimpleChoiceDto.class),
@Type(value = AssessmentSimpleMatchSetDto.class),
@Type(value = AssessmentTextEntryInteractionDto.class)
})
public class AssessmentInteractionDto implements AbstractAssessmentDto {
    private static final long serialVersionUID = 2l;

    private String id;
    private AssessmentInteractionType type;
    private String responseIdentifier;

    private String ariaControls;
    private String ariaDescribedBy;
    private String ariaFlowsTo;
    private String ariaLabel;
    private String ariaLabelledBy;
    private String ariaLevel;
    private String ariaLive;
    private String ariaOrientation;
    private String ariaOwns;

    // This is an acronym Charles Manson stole from the Beatles, we're stealing
    // it back: User Interface ID.
    private String uiid;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AssessmentInteractionType getType() {
        return type;
    }

    public void setType(AssessmentInteractionType type) {
        this.type = type;
    }

    public String getUiid() {
        return uiid;
    }

    public void setUiid(String uiid) {
        this.uiid = uiid;
    }

    public String getResponseIdentifier() {
        return responseIdentifier;
    }

    public void setResponseIdentifier(String responseIdentifier) {
        this.responseIdentifier = responseIdentifier;
    }

    public String getAriaControls() {
        return ariaControls;
    }

    public void setAriaControls(String ariaControls) {
        this.ariaControls = ariaControls;
    }

    public String getAriaDescribedBy() {
        return ariaDescribedBy;
    }

    public void setAriaDescribedBy(String ariaDescribedBy) {
        this.ariaDescribedBy = ariaDescribedBy;
    }

    public String getAriaFlowsTo() {
        return ariaFlowsTo;
    }

    public void setAriaFlowsTo(String ariaFlowsTo) {
        this.ariaFlowsTo = ariaFlowsTo;
    }

    public String getAriaLabel() {
        return ariaLabel;
    }

    public void setAriaLabel(String ariaLabel) {
        this.ariaLabel = ariaLabel;
    }

    public String getAriaLabelledBy() {
        return ariaLabelledBy;
    }

    public void setAriaLabelledBy(String ariaLabelledBy) {
        this.ariaLabelledBy = ariaLabelledBy;
    }

    public String getAriaLevel() {
        return ariaLevel;
    }

    public void setAriaLevel(String ariaLevel) {
        this.ariaLevel = ariaLevel;
    }

    public String getAriaLive() {
        return ariaLive;
    }

    public void setAriaLive(String ariaLive) {
        this.ariaLive = ariaLive;
    }

    public String getAriaOrientation() {
        return ariaOrientation;
    }

    public void setAriaOrientation(String ariaOrientation) {
        this.ariaOrientation = ariaOrientation;
    }

    public String getAriaOwns() {
        return ariaOwns;
    }

    public void setAriaOwns(String ariaOwns) {
        this.ariaOwns = ariaOwns;
    }

    public boolean validateResponses(List<String> values) {
        if (values == null) {
            values = Collections.emptyList();
        }
        return doValidateResponses(values);
    }

    //To be overridden by subclasses
    boolean doValidateResponses(List<String> values) {
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
