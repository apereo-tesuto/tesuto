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
package org.cccnext.tesuto.placement.view;


import java.io.Serializable;
import java.util.Set;

public class CompetencyGroupViewDto implements Serializable {

    static final long serialVersionUID=2l;

    private Integer competencyGroupId;
    private Integer courseId;
    private Integer percent;
    private String name;
    private Set<String> competencyIds;

    public Integer getCompetencyGroupId() {
        return competencyGroupId;
    }

    public void setCompetencyGroupId(Integer competencyGroupId) {
        this.competencyGroupId = competencyGroupId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getCompetencyIds() {
        return competencyIds;
    }

    public void setCompetencyIds(Set<String> competencyIds) {
        this.competencyIds = competencyIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompetencyGroupViewDto)) return false;

        CompetencyGroupViewDto that = (CompetencyGroupViewDto) o;

        if (getCompetencyGroupId() != null ? !getCompetencyGroupId().equals(that.getCompetencyGroupId()) : that.getCompetencyGroupId() != null)
            return false;
        if (getCourseId() != null ? !getCourseId().equals(that.getCourseId()) : that.getCourseId() != null)
            return false;
        if (getPercent() != null ? !getPercent().equals(that.getPercent()) : that.getPercent() != null)
            return false;
        if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
        return competencyIds != null ? competencyIds.equals(that.competencyIds) : that.competencyIds == null;

    }

    @Override
    public int hashCode() {
        int result = getCompetencyGroupId() != null ? getCompetencyGroupId().hashCode() : 0;
        result = 31 * result + (getCourseId() != null ? getCourseId().hashCode() : 0);
        result = 31 * result + (getPercent() != null ? getPercent().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (competencyIds != null ? competencyIds.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CompetencyGroupViewDto{" +
                "competencyGroupId=" + competencyGroupId +
                ", courseId=" + courseId +
                ", percentage=" + percent +
                ", name='" + name + '\'' +
                ", competencyIds=" + competencyIds +
                '}';
    }
}
