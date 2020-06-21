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

import java.util.List;

import org.cccnext.tesuto.content.model.AbstractAssessment;
import org.cccnext.tesuto.content.model.item.interaction.AssessmentInteraction;
import org.cccnext.tesuto.content.model.item.metadata.ItemMetadata;
import org.cccnext.tesuto.content.model.shared.AssessmentOutcomeDeclaration;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Note: We will need a single field, comprised of these 3 values (namespace,
 * identifier, version) to enforce uniqueness on sharded collections.
 *
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@Document // This is for Mongo
@CompoundIndexes({
        @CompoundIndex(name = "namespace_id_version_idx", def = "{'namespace' : 1, 'identifier' : 1, 'version' : -1}", unique = true),
        @CompoundIndex(name = "namespace_id_version_published_idx_1", def = "{'namespace' : 1, 'identifier' : 1, 'version' : -1, 'published' : 1}", unique = true),
        @CompoundIndex(name = "competency_type_published_idx_1", def = "{'itemMetadata.itemBankStatusType' : 1, 'published' : 1, 'itemMetadata.competencies.competencyRef.competencyMapDiscipline' : -1, 'itemMetadata.competencies.competencyRef.competencyRefId' : 1}", unique = false) })
public class AssessmentItem implements AbstractAssessment {
    private static final long serialVersionUID = 1l;

    @Id
    private String id;
    private String namespace;
    private String identifier;
    private int version;
    private boolean published;
    private List<String> resources;
    private String stylesheets;
    private String title;
    private String label;
    private String language;
    private String toolName;
    private String toolVersion;
    // TODO: Enum adaptive flag?
    // TODO: Enum timeDependent flag?
    private List<AssessmentResponseVar> responseVars;
    private List<AssessmentOutcomeDeclaration> outcomeDeclarations;
    private List<AssessmentTemplateDeclaration> templateVars;
    private AssessmentTemplateProcessing templateProcessing;
    private AssessmentStimulusRef stimulusRef;
    private String body;
    private List<AssessmentInteraction> interactions;
    private AssessmentResponseProcessing responseProcessing;
    private ItemMetadata itemMetadata;
    // TODO: Enum modalFeedback?
    // TODO: Enum apipAccessibility flag?

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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

    public List<AssessmentResponseVar> getResponseVars() {
        return responseVars;
    }

    public void setResponseVars(List<AssessmentResponseVar> responseVars) {
        this.responseVars = responseVars;
    }

    public List<AssessmentOutcomeDeclaration> getOutcomeDeclarations() {
        return outcomeDeclarations;
    }

    public void setOutcomeDeclarations(List<AssessmentOutcomeDeclaration> outcomeDeclarations) {
        this.outcomeDeclarations = outcomeDeclarations;
    }

    public List<AssessmentTemplateDeclaration> getTemplateVars() {
        return templateVars;
    }

    public void setTemplateVars(List<AssessmentTemplateDeclaration> templateVars) {
        this.templateVars = templateVars;
    }

    public AssessmentTemplateProcessing getTemplateProcessing() {
        return templateProcessing;
    }

    public void setTemplateProcessing(AssessmentTemplateProcessing templateProcessing) {
        this.templateProcessing = templateProcessing;
    }

    public AssessmentStimulusRef getStimulusRef() {
        return stimulusRef;
    }

    public void setStimulusRef(AssessmentStimulusRef stimulusRef) {
        this.stimulusRef = stimulusRef;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public AssessmentResponseProcessing getResponseProcessing() {
        return responseProcessing;
    }

    public void setResponseProcessing(AssessmentResponseProcessing responseProcessing) {
        this.responseProcessing = responseProcessing;
    }

    public List<AssessmentInteraction> getInteractions() {
        return interactions;
    }

    public void setInteractions(List<AssessmentInteraction> interactions) {
        this.interactions = interactions;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((body == null) ? 0 : body.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((interactions == null) ? 0 : interactions.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((language == null) ? 0 : language.hashCode());
        result = prime * result + ((outcomeDeclarations == null) ? 0 : outcomeDeclarations.hashCode());
        result = prime * result + ((responseProcessing == null) ? 0 : responseProcessing.hashCode());
        result = prime * result + ((responseVars == null) ? 0 : responseVars.hashCode());
        result = prime * result + ((stimulusRef == null) ? 0 : stimulusRef.hashCode());
        result = prime * result + ((templateProcessing == null) ? 0 : templateProcessing.hashCode());
        result = prime * result + ((templateVars == null) ? 0 : templateVars.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((toolName == null) ? 0 : toolName.hashCode());
        result = prime * result + ((toolVersion == null) ? 0 : toolVersion.hashCode());
        result = prime * result + ((stylesheets == null) ? 0 : stylesheets.hashCode());
        result = prime * result + ((itemMetadata == null) ? 0 : itemMetadata.hashCode());
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
        AssessmentItem other = (AssessmentItem) obj;
        if (body == null) {
            if (other.body != null)
                return false;
        } else if (!body.equals(other.body))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (identifier == null) {
            if (other.identifier != null)
                return false;
        } else if (!identifier.equals(other.identifier))
            return false;
        if (interactions == null) {
            if (other.interactions != null)
                return false;
        } else if (!interactions.equals(other.interactions))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (language == null) {
            if (other.language != null)
                return false;
        } else if (!language.equals(other.language))
            return false;
        if (outcomeDeclarations == null) {
            if (other.outcomeDeclarations != null)
                return false;
        } else if (!outcomeDeclarations.equals(other.outcomeDeclarations))
            return false;
        if (responseProcessing == null) {
            if (other.responseProcessing != null)
                return false;
        } else if (!responseProcessing.equals(other.responseProcessing))
            return false;
        if (responseVars == null) {
            if (other.responseVars != null)
                return false;
        } else if (!responseVars.equals(other.responseVars))
            return false;
        if (stimulusRef == null) {
            if (other.stimulusRef != null)
                return false;
        } else if (!stimulusRef.equals(other.stimulusRef))
            return false;
        if (templateProcessing == null) {
            if (other.templateProcessing != null)
                return false;
        } else if (!templateProcessing.equals(other.templateProcessing))
            return false;
        if (templateVars == null) {
            if (other.templateVars != null)
                return false;
        } else if (!templateVars.equals(other.templateVars))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (toolName == null) {
            if (other.toolName != null)
                return false;
        } else if (!toolName.equals(other.toolName))
            return false;
        if (toolVersion == null) {
            if (other.toolVersion != null)
                return false;
        } else if (!toolVersion.equals(other.toolVersion))
            return false;
        if (stylesheets == null) {
            if (other.stylesheets != null)
                return false;
        } else if (!stylesheets.equals(other.stylesheets))
            return false;
        if (itemMetadata == null) {
            if (other.itemMetadata != null)
                return false;
        } else if (!itemMetadata.equals(other.itemMetadata))
            return false;
        return true;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStylesheets() {
        return stylesheets;
    }

    public void setStylesheets(String stylesheets) {
        this.stylesheets = stylesheets;
    }

    public ItemMetadata getItemMetadata() {
        return itemMetadata;
    }

    public void setItemMetadata(ItemMetadata itemMetadata) {
        this.itemMetadata = itemMetadata;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
