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
package org.cccnext.tesuto.content.model.metadata;

import org.cccnext.tesuto.content.model.AbstractAssessment;

import java.util.List;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class CompetencyCategoryMetadata implements AbstractAssessment {
    private static final long serialVersionUID = 1l;

    String title;
    String competencyMapDiscipline;
    String competencyRefId;
    List<PerformanceRangeMetadata> performanceRanges;

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
        this.competencyMapDiscipline = competencyMapDiscipline;
    }

    public String getCompetencyRefId() {
        return competencyRefId;
    }

    public void setCompetencyRefId(String competencyRefId) {
        this.competencyRefId = competencyRefId;
    }

    public List<PerformanceRangeMetadata> getPerformanceRanges() {
        return performanceRanges;
    }

    public void setPerformanceRanges(List<PerformanceRangeMetadata> performanceRanges) {
        this.performanceRanges = performanceRanges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompetencyCategoryMetadata that = (CompetencyCategoryMetadata) o;

        if (!title.equals(that.title)) return false;
        if (!competencyMapDiscipline.equals(that.competencyMapDiscipline)) return false;
        if (!competencyRefId.equals(that.competencyRefId)) return false;
        return performanceRanges.equals(that.performanceRanges);

    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + competencyMapDiscipline.hashCode();
        result = 31 * result + competencyRefId.hashCode();
        result = 31 * result + performanceRanges.hashCode();
        return result;
    }
}
