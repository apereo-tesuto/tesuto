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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionDto;
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto;
import org.cccnext.tesuto.content.dto.shared.AssessmentOutcomeDeclarationDto;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentItemDto implements AbstractAssessmentDto {
    private static final long serialVersionUID = 1l;

    private String id;
    private String namespace;
    private String identifier;
    private int version;
    private boolean published;
    private List<String> resources;
    private String title;
    private String label;
    private String language;
    private String toolName;
    private String toolVersion;
    // TODO: Enum adaptive flag?
    // TODO: Enum timeDependent flag?
    private List<AssessmentResponseVarDto> responseVars;
    private List<AssessmentOutcomeDeclarationDto> outcomeDeclarationDtos;
    private List<AssessmentTemplateDeclarationDto> templateVars;
    private AssessmentTemplateProcessingDto templateProcessing;
    private AssessmentStimulusRefDto stimulusRef;
    private String stylesheets;
    private String body;
    private List<AssessmentInteractionDto> interactions;
    private AssessmentResponseProcessingDto responseProcessing;
    private ItemMetadataDto itemMetadata;
    // TODO: Enum modalFeedback?
    // TODO: Enum apipAccessibility flag?

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getToolVersion() {
        return toolVersion;
    }

    public void setToolVersion(String toolVersion) {
        this.toolVersion = toolVersion;
    }

    public List<AssessmentResponseVarDto> getResponseVars() {
        return responseVars;
    }

    public void setResponseVars(List<AssessmentResponseVarDto> responseVars) {
        this.responseVars = responseVars;
    }

    public List<AssessmentOutcomeDeclarationDto> getOutcomeDeclarationDtos() {
        return outcomeDeclarationDtos;
    }

    public void setOutcomeDeclarationDtos(List<AssessmentOutcomeDeclarationDto> outcomeDeclarationDtos) {
        this.outcomeDeclarationDtos = outcomeDeclarationDtos;
    }

    public List<AssessmentTemplateDeclarationDto> getTemplateVars() {
        return templateVars;
    }

    public void setTemplateVars(List<AssessmentTemplateDeclarationDto> templateVars) {
        this.templateVars = templateVars;
    }

    public AssessmentTemplateProcessingDto getTemplateProcessing() {
        return templateProcessing;
    }

    public void setTemplateProcessing(AssessmentTemplateProcessingDto templateProcessing) {
        this.templateProcessing = templateProcessing;
    }

    public AssessmentStimulusRefDto getStimulusRef() {
        return stimulusRef;
    }

    public void setStimulusRef(AssessmentStimulusRefDto stimulusRef) {
        this.stimulusRef = stimulusRef;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public AssessmentResponseProcessingDto getResponseProcessing() {
        return responseProcessing;
    }

    public void setResponseProcessing(AssessmentResponseProcessingDto responseProcessing) {
        this.responseProcessing = responseProcessing;
    }

    public List<AssessmentInteractionDto> getInteractions() {
        return interactions;
    }

    public void setInteractions(List<AssessmentInteractionDto> interactions) {
        this.interactions = interactions;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<String> getResources() {
        return resources;
    }

    public void setResources(List<String> resources) {
        this.resources = resources;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getStylesheets() {
        return stylesheets;
    }

    public void setStylesheets(String stylesheets) {
        this.stylesheets = stylesheets;
    }

    public ItemMetadataDto getItemMetadata() {
        return itemMetadata;
    }

    public void setItemMetadata(ItemMetadataDto itemMetadata) {
        this.itemMetadata = itemMetadata;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getScopedIdTag() {
        return namespace + ":" + identifier + ":" + version;
    }
    
    public String getScopedTag() {
        return namespace + ":" + identifier;
    }

    public List<AssessmentOutcomeDeclarationDto> getOutcomeDeclarationDtosByIdentifier(String filterByIdentifier){
        if(CollectionUtils.isEmpty(outcomeDeclarationDtos)) {
            return null;
        }
        return getOutcomeDeclarationDtos().stream().filter(o -> filterByIdentifier.equals(o.getIdentifier())).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).reflectionToString(this);
    }
}
