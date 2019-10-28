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
package org.cccnext.tesuto.content.dto.item.metadata;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;

import java.util.List;

/**
 * Created by jasonbrown on 1/21/16.
 */
public class CompetenciesItemMetadataDto implements AbstractAssessmentDto {
    private static final long serialVersionUID = 1l;

    @JacksonXmlElementWrapper(localName = "competencyRefs")
    @JacksonXmlProperty(localName = "competencyRef")
    List<CompetencyRefItemMetadataDto> competencyRef;

    @JacksonXmlElementWrapper(localName = "skippedCompetencies")
    @JacksonXmlProperty(localName = "skippedCompetency")
    List<String> skippedCompetency;

    // Updated Metadata as of 1/29/2016
    @JacksonXmlElementWrapper(localName = "skippedCompetencyRefs")
    @JacksonXmlProperty(localName = "skippedCompetencyRefId")
    List<String> skippedCompetencyRefId;

    /**
     * Without this Jackson Serialization will throw exception if no child nodes
     * are defined. The author has defined the competencies node but no other
     * child nodes.
     */
    public CompetenciesItemMetadataDto(String notUsed) {
        this.competencyRef = null;
        this.skippedCompetency = null;
        this.skippedCompetencyRefId = null;
    }

    public CompetenciesItemMetadataDto() {
    }

    public List<CompetencyRefItemMetadataDto> getCompetencyRef() {
        return competencyRef;
    }

    public void setCompetencyRef(List<CompetencyRefItemMetadataDto> competencyRef) {
        this.competencyRef = competencyRef;
    }

    public List<String> getSkippedCompetency() {
        return skippedCompetency;
    }

    public void setSkippedCompetency(List<String> skippedCompetency) {
        this.skippedCompetency = skippedCompetency;
    }

    public List<String> getSkippedCompetencyRefId() {
        return skippedCompetencyRefId;
    }

    public void setSkippedCompetencyRefId(List<String> skippedCompetencyRefId) {
        this.skippedCompetencyRefId = skippedCompetencyRefId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).reflectionToString(this);
    }
}
