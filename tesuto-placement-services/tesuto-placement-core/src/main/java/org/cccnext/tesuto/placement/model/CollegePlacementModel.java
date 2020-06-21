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
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(schema="public",name = "college_placement_model")
@Deprecated // TODO: Drop the associated database table and triggers.
public class CollegePlacementModel implements Serializable {
    

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;
    

    @Column(name = "is_dirty")
    private  boolean isDirty = false;
    
    @Column(name = "college_id")
    private String collegeId;
    
    @Column(name = "college_discipline_id")
    private Integer disciplineId;
    
    @Column(name = "competency_map_discipline")
    private String competencyMapDiscipline;
    
    @Column(name = "competency_map_order_id")
    private String competencyMapOrderId;

    @Column(name="discipline_snapshot")
    @Convert(converter = JsonBDisciplineConverter.class)
    private Discipline placementModel;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }

    public String getCompetencyMapDiscipline() {
        return competencyMapDiscipline;
    }

    public void setCompetencyMapDiscipline(String competencyMapDiscipline) {
        this.competencyMapDiscipline = competencyMapDiscipline;
    }
    
    public Integer getDisciplineId() {
        return disciplineId;
    }

    public void setDisciplineId(Integer disciplineId) {
        this.disciplineId = disciplineId;
    }

    public String getCompetencyMapOrderId() {
        return competencyMapOrderId;
    }

    public void setCompetencyMapOrderId(String competencyMapOrderId) {
        this.competencyMapOrderId = competencyMapOrderId;
    }

    public Discipline getPlacementModel() {
        return placementModel;
    }

    public void setPlacementModel(Discipline placementModel) {
        this.placementModel = placementModel;
    }
    
    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
