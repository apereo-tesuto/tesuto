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
package org.cccnext.tesuto.content.dto.metadata;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;

import java.util.List;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class CompetencyCategoryMetadataDto implements AbstractAssessmentDto {
    private static final long serialVersionUID = 1l;

    String title;
    String competencyMapDiscipline;
    String competencyRefId;
    @JacksonXmlElementWrapper(localName = "performanceRanges")
    @JacksonXmlProperty(localName = "performanceRange")
    private List<PerformanceRangeMetadataDto> performanceRanges;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompetencyMapDiscipline() {
        return competencyMapDiscipline;
    }

    public void setCompetencyMapDiscipline(String competencyMapDiscipline) {
        this.competencyMapDiscipline = competencyMapDiscipline.toUpperCase();
    }

    public String getCompetencyRefId() {
        return competencyRefId;
    }

    public void setCompetencyRefId(String competencyRefId) {
        this.competencyRefId = competencyRefId;
    }

    public List<PerformanceRangeMetadataDto> getPerformanceRanges() {
        return performanceRanges;
    }

    public void setPerformanceRanges(List<PerformanceRangeMetadataDto> performanceRanges) {
        this.performanceRanges = performanceRanges;
    }

    @Override
    public boolean equals(Object o) {
        return new EqualsBuilder().reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).reflectionToString(this);
    }
}
