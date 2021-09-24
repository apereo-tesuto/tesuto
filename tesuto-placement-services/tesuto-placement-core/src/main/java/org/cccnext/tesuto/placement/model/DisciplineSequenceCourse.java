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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Audited
@AuditTable(schema="public", value ="history_college_discipline_sequence_course")
@Table(schema="public",name = "college_discipline_sequence_course")
@IdClass(DisciplineSequenceCourseId.class)
public class DisciplineSequenceCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name="college_discipline_id")
    private int disciplineId;

    @Id
    @Column(name="course_id")
    private int courseId;

    @Id
    @Column(name="cb21_code")
    private char cb21Code;

    @Id
    @Column(name="course_group")
    private int courseGroup;

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    @Column(name = "audit_id", insertable = false)
    private Integer auditId;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "college_discipline_id", referencedColumnName="college_discipline_id", updatable = false, insertable = false),
            @JoinColumn(name = "cb21_code", referencedColumnName="cb21_code", updatable = false, insertable = false),
            @JoinColumn(name = "course_group", referencedColumnName="course_group", updatable = false, insertable = false)
    })
    private DisciplineSequence disciplineSequence;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="course_id",  updatable=false, insertable=false)
    private Course course;

    public DisciplineSequenceCourse() {}

    public DisciplineSequenceCourse(int disciplineId, char cb21Code, int courseGroup, int courseId) {
        this.disciplineId = disciplineId;
        this.cb21Code = cb21Code;
        this.courseGroup = courseGroup;
        this.courseId = courseId;
    }


    public int getDisciplineId() {
        return disciplineId;
    }

    public void setDisciplineId(int disciplineId) {
        this.disciplineId = disciplineId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
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

    @JsonIgnore
    public DisciplineSequence getDisciplineSequence() {
        return disciplineSequence;
    }

    @JsonIgnore
    public void setDisciplineSequence(DisciplineSequence disciplineSequence) {
        this.disciplineSequence = disciplineSequence;
        this.disciplineId = disciplineSequence.getDisciplineId();
        this.cb21Code = disciplineSequence.getCb21Code();
        this.courseGroup = disciplineSequence.getCourseGroup();
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
        this.courseId = course.getCourseId();
    }

    public Integer getAuditId() {
        return auditId;
    }

    public void setAuditId(Integer auditId) {
        this.auditId = auditId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisciplineSequenceCourse)) return false;

        DisciplineSequenceCourse that = (DisciplineSequenceCourse) o;

        if (getDisciplineId() != that.getDisciplineId()) return false;
        if (getCourseId() != that.getCourseId()) return false;
        if (getCb21Code() != that.getCb21Code()) return false;
        if (getCourseGroup() != that.getCourseGroup()) return false;
        if (getAuditId() != null ? !getAuditId().equals(that.getAuditId()) : that.getAuditId() != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = getDisciplineId();
        result = 31 * result + getCourseId();
        result = 31 * result + (int) getCb21Code();
        result = 31 * result + getCourseGroup();
        result = 31 * result + (getAuditId() != null ? getAuditId().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DisciplineSequenceCourse{" +
                "disciplineId=" + disciplineId +
                ", courseId=" + courseId +
                ", cb21Code=" + cb21Code +
                ", courseGroup=" + courseGroup +
                ", auditId=" + auditId +
                ", course=" + course.getName() +
                '}';
    }
}
