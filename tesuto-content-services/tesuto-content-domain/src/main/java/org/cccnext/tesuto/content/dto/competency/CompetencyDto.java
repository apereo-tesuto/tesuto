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
package org.cccnext.tesuto.content.dto.competency;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;

import java.util.List;

/**
 * Although we will be moving towards a schema like the IMS Global RDCEO
 * http://www.imsglobal.org/competencies/index.html
 *
 * This is currently a custom schema.
 *
 * Created by jasonbrown on 6/17/16.
 */
public class CompetencyDto implements AbstractAssessmentDto {
    private static final long serialVersionUID = 2l;

    private String id;
    private String identifier;
    private String discipline;
    private int version;
    private boolean published;
    private String description;
    private String sampleItem;
    private String studentDescription;

    @JacksonXmlElementWrapper(localName = "competencyRefs")
    @JacksonXmlProperty(localName = "competencyRef")
    private List<CompetencyRefDto> childCompetencyDtoRefs; // list of child competencies references.

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

    public List<CompetencyRefDto> getChildCompetencyDtoRefs() {
        return childCompetencyDtoRefs;
    }

    public void setChildCompetencyDtoRefs(List<CompetencyRefDto> childCompetencyDtoRefs) {
        this.childCompetencyDtoRefs = childCompetencyDtoRefs;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
    
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
