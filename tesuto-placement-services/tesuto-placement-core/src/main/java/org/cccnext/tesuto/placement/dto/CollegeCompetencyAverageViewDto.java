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
public class CollegeCompetencyAverageViewDto {
    private String collegeId;
    private String collegeName;
    private Set<CourseSubjectAreaCompetencyGroupAverageViewDto> courseSubjectAreaCompetencyGroupAverageViewDtoSet = new HashSet<>();

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public Set<CourseSubjectAreaCompetencyGroupAverageViewDto> getCourseSubjectAreaCompetencyGroupAverageViewDtoSet() {
        return courseSubjectAreaCompetencyGroupAverageViewDtoSet;
    }

    public void setCourseSubjectAreaCompetencyGroupAverageViewDtoSet(Set<CourseSubjectAreaCompetencyGroupAverageViewDto> courseSubjectAreaCompetencyGroupAverageViewDtoSet) {
        this.courseSubjectAreaCompetencyGroupAverageViewDtoSet = courseSubjectAreaCompetencyGroupAverageViewDtoSet;
    }

    @Override
    public String toString() {
        return "CollegeCompetencyAverageViewDto{" +
                "collegeId='" + collegeId + '\'' +
                ", collegeName='" + collegeName + '\'' +
                ", courseSubjectAreaCompetencyGroupAverageViewDtoList=" + courseSubjectAreaCompetencyGroupAverageViewDtoSet +
                '}';
    }
}
