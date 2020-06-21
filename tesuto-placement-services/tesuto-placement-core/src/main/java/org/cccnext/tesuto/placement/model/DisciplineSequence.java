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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Audited
@AuditTable(schema="public", value ="history_college_discipline_sequence")
@Table(schema="public",name = "college_discipline_sequence")
@IdClass(DisciplineSequenceId.class)
public class DisciplineSequence implements Serializable {


    private static final long serialVersionUID = 2L;

    @Id
    @Column(name="college_discipline_id")
    private int disciplineId;

    @Id
    @Column(name="cb21_code")
    private char cb21Code;

    @Id
    @Column(name="course_group")
    private int courseGroup;

    @ManyToOne(fetch=FetchType.EAGER) //This is expected to be cached
    @JoinColumn(name="cb21_code", updatable = false, insertable = false)
    private CB21 cb21;

    @ManyToOne(fetch=FetchType.EAGER) 
    @JoinColumn(name="college_discipline_id", updatable = false, insertable = false)
    private Discipline discipline;

    @Column(name="explanation")
    private String explanation;

    @Column(name="show_student")
    private boolean showStudent;

    @Column(name="mapping_level")
    private String mappingLevel;

    @OneToMany(mappedBy="disciplineSequence", fetch=FetchType.EAGER, cascade=CascadeType.REMOVE)
    private Set<DisciplineSequenceCourse> disciplineSequenceCourses;

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

    public CB21 getCb21() {
        return cb21;
    }

    public void setCb21(CB21 cb21) {
        this.cb21 = cb21;
        this.cb21Code = cb21.getCb21Code();
    }

    @JsonIgnore
    public Discipline getDiscipline() {
        return discipline;
    }

    @JsonIgnore
    public void setDiscipline(Discipline discipline) {
        this.discipline = discipline;
        this.disciplineId = discipline.getDisciplineId();
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

    public Set<DisciplineSequenceCourse> getDisciplineSequenceCourses() {
        if (disciplineSequenceCourses == null) {
            disciplineSequenceCourses = new HashSet<>();
        }
        return disciplineSequenceCourses;
    }

    public void setDisciplineSequenceCourses(Set<DisciplineSequenceCourse> disciplineSequenceCourses) {
        this.disciplineSequenceCourses = disciplineSequenceCourses;
    }

    public String getMappingLevel() {
        return mappingLevel;
    }

    public void setMappingLevel(String mappingLevel) {
        this.mappingLevel = mappingLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DisciplineSequence that = (DisciplineSequence) o;

        if (disciplineId != that.disciplineId) return false;
        if (cb21Code != that.cb21Code) return false;
        if (courseGroup != that.courseGroup) return false;
        if (showStudent != that.showStudent) return false;
        //if (discipline != null ? !discipline.equals(that.discipline) : that.discipline != null) return false;
        if (explanation != null ? !explanation.equals(that.explanation) : that.explanation != null) return false;
        return mappingLevel != null ? mappingLevel.equals(that.mappingLevel) : that.mappingLevel == null;
    }

    @Override
    public int hashCode() {
        int result = disciplineId;
        result = 31 * result + (int) cb21Code;
        result = 31 * result + courseGroup;
        //result = 31 * result + (discipline != null ? discipline.hashCode() : 0);
        result = 31 * result + (explanation != null ? explanation.hashCode() : 0);
        result = 31 * result + (showStudent ? 1 : 0);
        result = 31 * result + (mappingLevel != null ? mappingLevel.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DisciplineSequence{" +
                "disciplineId=" + disciplineId +
                ", cb21Code=" + cb21Code +
                ", courseGroup=" + courseGroup +
                ", cb21=" + cb21 +
                ", explanation='" + explanation + '\'' +
                ", showStudent=" + showStudent +
                ", mappingLevel='" + mappingLevel + '\'' +
                '}';
    }

}
