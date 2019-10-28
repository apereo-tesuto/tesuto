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
package org.cccnext.tesuto.placement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cccnext.tesuto.placement.view.CourseViewDto;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Audited
@AuditTable(schema="public", value ="history_course")
@Table(schema="public",name = "course")
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="course_id")
    private Integer courseId;

    @Column(name="name")
    private String name;

    @Column(name="cid")
    private String cid;

    @Column(name="subject")
    private String subject;

    @Column(name="number")
    private String number;

    @Column(name="description")
    private String description;

    @Column(name="mmap_equivalent_code")
    private String mmapEquivalentCode;

    @Column(name="sis_test_code")
    private String sisTestCode;

    @Column(name="competency_group_logic")
    private String competencyGroupLogic;

    @OneToMany(fetch=FetchType.EAGER, mappedBy="course", cascade=CascadeType.REMOVE)
    @NotAudited
    private Set<DisciplineSequenceCourse> disciplineSequenceCourses;

    @OneToMany(mappedBy="course", fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)
    @NotAudited
    private Set<CompetencyGroup> competencyGroups;

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMmapEquivalentCode() {
        return mmapEquivalentCode;
    }

    public void setMmapEquivalentCode(String mmapEquivalentCode) {
        this.mmapEquivalentCode = mmapEquivalentCode;
    }

    public String getSisTestCode() {
        return sisTestCode;
    }

    public void setSisTestCode(String sisTestCode) {
        this.sisTestCode = sisTestCode;
    }

    @JsonIgnore
    public Set<DisciplineSequenceCourse> getDisciplineSequenceCourses() {
        return disciplineSequenceCourses;
    }

    @JsonIgnore
    public void setDisciplineSequenceCourses(Set<DisciplineSequenceCourse> disciplineSequenceCourses) {
        this.disciplineSequenceCourses = disciplineSequenceCourses;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCompetencyGroupLogic() {
        return competencyGroupLogic;
    }

    public void setCompetencyGroupLogic(String competencyGroupLogic) {
        this.competencyGroupLogic = competencyGroupLogic;
    }

    public Set<CompetencyGroup> getCompetencyGroups() {
        return competencyGroups;
    }

    public void setCompetencyGroups(Set<CompetencyGroup> competencyGroups) {
        this.competencyGroups = competencyGroups;
    }

    @JsonIgnore
    public Set<DisciplineSequence> getDisciplineSequences() {
        return getDisciplineSequenceCourses().stream().map(cdsc -> cdsc.getDisciplineSequence() ).collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;

        Course course = (Course) o;

        if (getCourseId() != null ? !getCourseId().equals(course.getCourseId()) : course.getCourseId() != null)
            return false;
        if (getName() != null ? !getName().equals(course.getName()) : course.getName() != null) return false;
        if (getCid() != null ? !getCid().equals(course.getCid()) : course.getCid() != null) return false;
        if (getSubject() != null ? !getSubject().equals(course.getSubject()) : course.getSubject() != null) return false;
        if (getNumber() != null ? !getNumber().equals(course.getNumber()) : course.getNumber() != null) return false;
        if (getCompetencyGroupLogic() != null ? !getCompetencyGroupLogic().equals(course.getCompetencyGroupLogic()) : course.getCompetencyGroupLogic() != null) return false;

        return getDescription() != null ? getDescription().equals(course.getDescription()) : course.getDescription() == null;

    }

    public boolean equals(CourseViewDto course) {

        if (getCourseId() != null ? !getCourseId().equals(course.getCourseId()) : course.getCourseId() != null)
            return false;
        if (getName() != null ? !getName().equals(course.getName()) : course.getName() != null) return false;
        if (getCid() != null ? !getCid().equals(course.getCid()) : course.getCid() != null) return false;
        if (getSubject() != null ? !getSubject().equals(course.getSubject()) : course.getSubject() != null) return false;
        if (getNumber() != null ? !getNumber().equals(course.getNumber()) : course.getNumber() != null) return false;
        if (getCompetencyGroupLogic() != null ? !getCompetencyGroupLogic().equals(course.getCompetencyGroupLogic()) : course.getCompetencyGroupLogic() != null) return false;

        return getDescription() != null ? getDescription().equals(course.getDescription()) : course.getDescription() == null;

    }


    @Override
    public int hashCode() {
        int result = getCourseId() != null ? getCourseId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getCid() != null ? getCid().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getSubject() != null ? getSubject().hashCode() : 0);
        result = 31 * result + (getNumber() != null ? getNumber().hashCode() : 0);
        result = 31 * result + (getCompetencyGroupLogic() != null ? getCompetencyGroupLogic().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", name='" + name + '\'' +
                ", cid='" + cid + '\'' +
                ", description='" + description + '\'' +
                ", course='" + subject + '\'' +
                ", number='" + number + '\'' +
                ", competencyGroupLogic='" + competencyGroupLogic + '\'' +
                '}';
    }
}
