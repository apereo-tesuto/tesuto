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
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;

import java.util.List;

/**
 * Although we will be moving towards a schema like the IMS Global RDCEO
 * http://www.imsglobal.org/competencies/index.html
 *
 * Currently this is a custom schema
 * CompetencyMap as the parent object to wrap a list of Competency Objects
 *
 * Created by jasonbrown on 6/17/16.
 */
public class CompetencyMapDto implements AbstractAssessmentDto {
    private static final long serialVersionUID = 2l;

    private String title;
    private String id; //needed from mongo
    private String identifier;
    private int version;
    private String discipline;
    @JacksonXmlElementWrapper(localName = "competencyRefs")
    @JacksonXmlProperty(localName = "competencyRef")
    private List<CompetencyRefDto> competencyRefs;

    private boolean published;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public List<CompetencyRefDto> getCompetencyRefs() {
        return competencyRefs;
    }

    public void setCompetencyRefs(List<CompetencyRefDto> competencyRefs) {
        this.competencyRefs = competencyRefs;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).reflectionToString(this);
    }
}
