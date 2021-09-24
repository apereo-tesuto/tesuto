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
package org.cccnext.tesuto.placement.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class CourseSubjectAreaCompetencyGroupAverageViewDto {
    private Integer courseId;
    private String courseName;
    private Integer subjectAreaId;
    private String subjectAreaTitle;
    private Set<CompetencyGroupAverageViewDto> competencyGroupAverageViewDtoSet = new HashSet<>();

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getSubjectAreaId() {
        return subjectAreaId;
    }

    public void setSubjectAreaId(Integer subjectAreaId) {
        this.subjectAreaId = subjectAreaId;
    }

    public String getSubjectAreaTitle() {
        return subjectAreaTitle;
    }

    public void setSubjectAreaTitle(String subjectAreaTitle) {
        this.subjectAreaTitle = subjectAreaTitle;
    }

    public Set<CompetencyGroupAverageViewDto> getCompetencyGroupAverageViewDtoSet() {
        return competencyGroupAverageViewDtoSet;
    }

    public void setCompetencyGroupAverageViewDtoSet(Set<CompetencyGroupAverageViewDto> competencyGroupAverageViewDtoSet) {
        this.competencyGroupAverageViewDtoSet = competencyGroupAverageViewDtoSet;
    }

    @Override
    public String toString() {
        return "CourseSubjectAreaCompetencyGroupAverageViewDto{" +
                "courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", subjectAreaId=" + subjectAreaId +
                ", subjectAreaTitle='" + subjectAreaTitle + '\'' +
                ", competencyGroupAverageViewDtoList=" + competencyGroupAverageViewDtoSet +
                '}';
    }
}
