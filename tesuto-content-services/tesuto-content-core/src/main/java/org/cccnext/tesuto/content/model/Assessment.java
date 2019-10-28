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
package org.cccnext.tesuto.content.model;

import java.util.List;

import org.cccnext.tesuto.content.model.metadata.AssessmentMetadata;
import org.cccnext.tesuto.content.model.shared.AssessmentOutcomeDeclaration;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Note: We will need a single field, comprised of these 3 values (namespace,
 * identifier, version) to enforce uniqueness on sharded collections.
 *
 * @author Josh Corbin <jcorbin@unicon.net>
 */
@Document // Mongo annotation
@CompoundIndexes({
        @CompoundIndex(name = "namespace_id_version_idx", def = "{'namespace' : 1, 'identifier' : 1, 'version' : -1}", unique = true),
        @CompoundIndex(name = "namespace_id_version_published_idx_1", def = "{'namespace' : 1, 'identifier' : 1, 'version' : -1, 'published' : 1}", unique = true) })
public class Assessment implements AbstractAssessment {

    private static final long serialVersionUID = 1l; // Not sure if this is  needed for Assessment?

    @Id
    private String id;
    private String namespace;
    private String identifier;
    private int version;
    private boolean published;
    private List<String> resources;
    private String title;
    private String language; // Not in QTI, but will need to populate this from
                             // metadata
    private String toolName;
    private String toolVersion;
    private Double duration;

    private List<AssessmentOutcomeDeclaration> outcomeDeclarations;
    private List<AssessmentPart> assessmentParts;
    private String stylesheets;
    private AssessmentOutcomeProcessing outcomeProcessing;
    private AssessmentMetadata assessmentMetadata;

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

    public List<AssessmentOutcomeDeclaration> getOutcomeDeclarations() {
        return outcomeDeclarations;
    }

    public void setOutcomeDeclarations(List<AssessmentOutcomeDeclaration> outcomeDeclarations) {
        this.outcomeDeclarations = outcomeDeclarations;
    }

    public List<AssessmentPart> getAssessmentParts() {
        return assessmentParts;
    }

    public void setAssessmentParts(List<AssessmentPart> assessmentParts) {
        this.assessmentParts = assessmentParts;
    }

    public AssessmentOutcomeProcessing getOutcomeProcessing() {
        return outcomeProcessing;
    }

    public void setOutcomeProcessing(AssessmentOutcomeProcessing outcomeProcessing) {
        this.outcomeProcessing = outcomeProcessing;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((assessmentParts == null) ? 0 : assessmentParts.hashCode());
        result = prime * result + ((duration == null) ? 0 : duration.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result + ((language == null) ? 0 : language.hashCode());
        result = prime * result + ((outcomeProcessing == null) ? 0 : outcomeProcessing.hashCode());
        result = prime * result + ((outcomeDeclarations == null) ? 0 : outcomeDeclarations.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((toolName == null) ? 0 : toolName.hashCode());
        result = prime * result + ((toolVersion == null) ? 0 : toolVersion.hashCode());
        result = prime * result + ((stylesheets == null) ? 0 : stylesheets.hashCode());
        result = prime * result + ((assessmentMetadata == null) ? 0 : assessmentMetadata.hashCode());
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
        Assessment other = (Assessment) obj;
        if (assessmentParts == null) {
            if (other.assessmentParts != null)
                return false;
        } else if (!assessmentParts.equals(other.assessmentParts))
            return false;
        if (duration == null) {
            if (other.duration != null)
                return false;
        } else if (!duration.equals(other.duration))
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
        if (language == null) {
            if (other.language != null)
                return false;
        } else if (!language.equals(other.language))
            return false;
        if (outcomeProcessing == null) {
            if (other.outcomeProcessing != null)
                return false;
        } else if (!outcomeProcessing.equals(other.outcomeProcessing))
            return false;
        if (outcomeDeclarations == null) {
            if (other.outcomeDeclarations != null)
                return false;
        } else if (!outcomeDeclarations.equals(other.outcomeDeclarations))
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
        if (assessmentMetadata == null) {
            if (other.assessmentMetadata != null)
                return false;
        } else if (!assessmentMetadata.equals(other.assessmentMetadata))
            return false;
        return true;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
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

    public AssessmentMetadata getAssessmentMetadata() {
        return assessmentMetadata;
    }

    public void setAssessmentMetadata(AssessmentMetadata assessmentMetadata) {
        this.assessmentMetadata = assessmentMetadata;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    @JsonIgnore
    public ScopedIdentifier getScopedIdentifier() {
        return new ScopedIdentifier(namespace, identifier);
    }
}
