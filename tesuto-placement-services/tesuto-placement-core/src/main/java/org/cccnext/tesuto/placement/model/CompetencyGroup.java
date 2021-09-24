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

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.io.Serializable;
import java.util.Set;

@Entity
@Audited
@AuditTable(schema="public",value = "history_competency_group")
@Table(schema="public",name = "competency_group")
public class CompetencyGroup implements Serializable {


    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "competency_group_id")
    private Integer competencyGroupId;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "probability_success")
    private Integer probabilitySuccess;

    @Column(name = "title")
    private String title;

    @OneToMany(mappedBy = "competencyGroup", fetch=FetchType.EAGER)
    private Set<CompetencyGroupMapping> competencyGroupMappings;

    public Integer getCompetencyGroupId() {
        return competencyGroupId;
    }

    public void setCompetencyGroupId(Integer competencyGroupId) {
        this.competencyGroupId = competencyGroupId;
    }

    @JsonIgnore
    public Course getCourse() {
        return course;
    }

    @JsonIgnore
    public void setCourse(Course course) {
        this.course = course;
    }

    public Integer getProbabilitySuccess() {
        return probabilitySuccess;
    }

    public void setProbabilitySuccess(Integer probabilitySuccess) {
        this.probabilitySuccess = probabilitySuccess;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<CompetencyGroupMapping> getCompetencyGroupMappings() {
        return competencyGroupMappings;
    }

    public void setCompetencyGroupMappings(Set<CompetencyGroupMapping> competencyGroupMappings) {
        this.competencyGroupMappings = competencyGroupMappings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof CompetencyGroup))
            return false;

        CompetencyGroup that = (CompetencyGroup) o;

        if (getCompetencyGroupId() != null ? !getCompetencyGroupId().equals(that.getCompetencyGroupId())
                : that.getCompetencyGroupId() != null)
            return false;
        if (getProbabilitySuccess() != null ? !getProbabilitySuccess().equals(that.getProbabilitySuccess())
                : that.getProbabilitySuccess() != null)
            return false;
        return getTitle() != null ? getTitle().equals(that.getTitle()) : that.getTitle() == null;

    }

    @Override
    public int hashCode() {
        int result = getCompetencyGroupId() != null ? getCompetencyGroupId().hashCode() : 0;
        result = 31 * result + (getProbabilitySuccess() != null ? getProbabilitySuccess().hashCode() : 0);
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CompetencyGroup{" + "competencyGroupId=" + competencyGroupId + ", course=" + course
                + ", probabilitySuccess=" + probabilitySuccess + ", title='" + title + '\''
                + '}';
    }
}
