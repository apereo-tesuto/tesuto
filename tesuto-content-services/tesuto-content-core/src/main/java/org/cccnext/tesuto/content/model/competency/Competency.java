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
package org.cccnext.tesuto.content.model.competency;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cccnext.tesuto.content.model.AbstractAssessment;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Created by jasonbrown on 6/17/16.
 */
@Document // This is for Mongo
@CompoundIndexes({
        @CompoundIndex(name = "discipline_id_version_idx", def = "{'discipline' : 1, 'identifier' : 1, 'version' : -1}", unique = true),
        @CompoundIndex(name = "discipline_id_version_published_idx_1", def = "{'discipline' : 1, 'identifier' : 1, 'version' : -1, 'published' : 1}", unique = true) })
public class Competency implements AbstractAssessment {
    private static final long serialVersionUID = 3l;
    @Id
    private String id;
    private String identifier;
    private String discipline;
    private int version;
    private boolean published;
    private String description;
    private String sampleItem;
    private String studentDescription;
    private List<CompetencyRef> childCompetencyRefs; // list of child competencies references.

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSampleItem() {
        return sampleItem;
    }

    public void setSampleItem(String sampleQuestions) {
        this.sampleItem = sampleQuestions;
    }

    public String getStudentDescription() {
        return studentDescription;
    }

    public void setStudentDescription(String studentFacingText) {
        this.studentDescription = studentFacingText;
    }

    public List<CompetencyRef> getChildCompetencyRefs() {
        return childCompetencyRefs;
    }

    public void setChildCompetencyRefs(List<CompetencyRef> CompetencyList) {
        this.childCompetencyRefs = CompetencyList;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
