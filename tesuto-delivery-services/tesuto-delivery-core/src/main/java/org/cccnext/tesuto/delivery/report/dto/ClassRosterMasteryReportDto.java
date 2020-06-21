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
package org.cccnext.tesuto.delivery.report.dto;

import org.cccnext.tesuto.content.dto.metadata.CompetencyCategoryMetadataDto;
import org.cccnext.tesuto.content.dto.metadata.PerformanceRangeMetadataDto;
import org.cccnext.tesuto.content.dto.shared.SelectedOrderedRestrictedViewCompetencies;
import org.cccnext.tesuto.domain.dto.Dto;

import java.util.List;
import java.util.Map;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class ClassRosterMasteryReportDto implements Dto {
    private List<PerformanceRangeMetadataDto> performanceRanges;
    private List<CompetencyCategoryMetadataDto> competencyCategories;
    private Map<String, PerformanceCountDto> overallPerformanceCountMap;
    private Map<String, Map<String, PerformanceCountDto>> competencyCategoryCountMap;
    SelectedOrderedRestrictedViewCompetencies selectedOrderedRestrictedViewCompetencies;
    private int totalStudentCount;
    private int studentsAssessedCount;
    private int medianDaysSinceAssessed;

    public List<PerformanceRangeMetadataDto> getPerformanceRanges() {
        return performanceRanges;
    }

    public void setPerformanceRanges(List<PerformanceRangeMetadataDto> performanceRanges) {
        this.performanceRanges = performanceRanges;
    }

    public List<CompetencyCategoryMetadataDto> getCompetencyCategories() {
        return competencyCategories;
    }

    public void setCompetencyCategories(List<CompetencyCategoryMetadataDto> competencyCategories) {
        this.competencyCategories = competencyCategories;
    }

    public Map<String, PerformanceCountDto> getOverallPerformanceCountMap() {
        return overallPerformanceCountMap;
    }

    public void setOverallPerformanceCountMap(Map<String, PerformanceCountDto> overallPerformanceCountMap) {
        this.overallPerformanceCountMap = overallPerformanceCountMap;
    }

    public Map<String, Map<String, PerformanceCountDto>> getCompetencyCategoryCountMap() {
        return competencyCategoryCountMap;
    }

    public void setCompetencyCategoryCountMap(Map<String, Map<String, PerformanceCountDto>> competencyCategoryCountMap) {
        this.competencyCategoryCountMap = competencyCategoryCountMap;
    }

    public SelectedOrderedRestrictedViewCompetencies getSelectedOrderedRestrictedViewCompetencies() {
        return selectedOrderedRestrictedViewCompetencies;
    }

    public void setSelectedOrderedRestrictedViewCompetencies(SelectedOrderedRestrictedViewCompetencies selectedOrderedRestrictedViewCompetencies) {
        this.selectedOrderedRestrictedViewCompetencies = selectedOrderedRestrictedViewCompetencies;
    }

    public int getTotalStudentCount() {
        return totalStudentCount;
    }

    public void setTotalStudentCount(int totalStudentCount) {
        this.totalStudentCount = totalStudentCount;
    }

    public int getStudentsAssessedCount() {
        return studentsAssessedCount;
    }

    public void setStudentsAssessedCount(int studentsAssessedCount) {
        this.studentsAssessedCount = studentsAssessedCount;
    }

    public int getMedianDaysSinceAssessed() {
        return medianDaysSinceAssessed;
    }

    public void setMedianDaysSinceAssessed(int medianDaysSinceAssessed) {
        this.medianDaysSinceAssessed = medianDaysSinceAssessed;
    }

    @Override
    public String toString() {
        return "ClassRosterMasteryReportDto{" +
                "\nperformanceRanges=" + performanceRanges +
                "\ncompetencyCategories=" + competencyCategories +
                "\noverallPerformanceCountMap=" + overallPerformanceCountMap +
                "\ncompetencyCategoryCountMap=" + competencyCategoryCountMap +
                "\nselectedOrderedRestrictedViewCompetencies=" + selectedOrderedRestrictedViewCompetencies +
                "\ntotalStudentCount=" + totalStudentCount +
                "\nstudentsAssessedCount=" + studentsAssessedCount +
                "\nmedianDaysSinceAssessed=" + medianDaysSinceAssessed +
                '}';
    }
}
