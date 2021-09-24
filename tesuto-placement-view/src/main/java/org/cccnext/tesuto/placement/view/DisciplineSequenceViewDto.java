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


public class DisciplineSequenceViewDto implements Serializable {

    static final long serialVersionUID=3l;

    private int disciplineId;
    private char cb21Code;
    private int courseGroup;
    private int level;
    private String explanation;
    private boolean showStudent;
    private String mappingLevel;
    private Set<CourseViewDto> courses;

    public int getDisciplineId() {
        return disciplineId;
    }

    public void setDisciplineId(int disciplineId) {
        this.disciplineId = disciplineId;
    }

    public char getCb21Code() {
        return cb21Code;
    }

    public void setCb21Code(char cb21Code) {
        this.cb21Code = cb21Code;
    }

    public int getCourseGroup() {
        return courseGroup;
    }

    public void setCourseGroup(int courseGroup) {
        this.courseGroup = courseGroup;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public boolean isShowStudent() {
        return showStudent;
    }

    public void setShowStudent(boolean showStudent) {
        this.showStudent = showStudent;
    }

    public String getMappingLevel() {
        return mappingLevel;
    }

    public void setMappingLevel(String mappingLevel) {
        this.mappingLevel = mappingLevel;
    }

    public Set<CourseViewDto> getCourses() {
        return courses;
    }

    public void setCourses(Set<CourseViewDto> courses) {
        this.courses = courses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DisciplineSequenceViewDto that = (DisciplineSequenceViewDto) o;

        if (disciplineId != that.disciplineId) return false;
        if (cb21Code != that.cb21Code) return false;
        if (courseGroup != that.courseGroup) return false;
        if (level != that.level) return false;
        if (showStudent != that.showStudent) return false;
        if (explanation != null ? !explanation.equals(that.explanation) : that.explanation != null) return false;
        if (mappingLevel != null ? !mappingLevel.equals(that.mappingLevel) : that.mappingLevel != null) return false;
        if (coursesIsEmpty(courses) && coursesIsEmpty(that.courses)) return true;
        return courses != null ? courses.equals(that.courses) : that.courses == null;

    }
    
    private Boolean coursesIsEmpty(Set<CourseViewDto> courses) {
    	return courses == null || courses.isEmpty() ? true : false;
    }

    @Override
    public int hashCode() {
        int result = disciplineId;
        result = 31 * result + (int) cb21Code;
        result = 31 * result + courseGroup;
        result = 31 * result + level;
        result = 31 * result + (explanation != null ? explanation.hashCode() : 0);
        result = 31 * result + (showStudent ? 1 : 0);
        result = 31 * result + (mappingLevel != null ? mappingLevel.hashCode() : 0);
        result = 31 * result + (courses != null ? courses.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DisciplineSequenceViewDto{" +
                "disciplineId=" + disciplineId +
                ", cb21Code=" + cb21Code +
                ", courseGroup=" + courseGroup +
                ", level=" + level +
                ", explanation='" + explanation + '\'' +
                ", showStudent=" + showStudent +
                ", mappingLevel='" + mappingLevel + '\'' +
                ", courses=" + courses +
                '}';
    }
}
