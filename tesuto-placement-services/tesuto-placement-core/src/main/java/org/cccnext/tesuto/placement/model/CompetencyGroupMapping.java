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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Audited
@AuditTable(schema="public", value ="history_competency_group_mapping")
@Table(schema="public",name = "competency_group_mapping")
@IdClass(CompetencyGroupMappingId.class)
public class CompetencyGroupMapping implements Serializable {


    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name="competency_group_id")
    private int competencyGroupId;
    @Id
    @Column(name="competency_id")
    private String competencyId;
    
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    @Column(name = "audit_id", insertable = false)
    private Integer auditId;

    @ManyToOne
    @JoinColumn(name="competency_group_id", updatable = false, insertable = false)
    private CompetencyGroup competencyGroup;

    public int getCompetencyGroupId() {
        return competencyGroupId;
    }

    public void setCompetencyGroupId(int competencyGroupId) {
        this.competencyGroupId = competencyGroupId;
    }

    public String getCompetencyId() {
        return competencyId;
    }

    public void setCompetencyId(String competencyId) {
        this.competencyId = competencyId;
    }

    @JsonIgnore
    public CompetencyGroup getCompetencyGroup() {
        return competencyGroup;
    }

    @JsonIgnore
    public void setCompetencyGroup(CompetencyGroup competencyGroup) {
        this.competencyGroup = competencyGroup;
        this.competencyGroupId = competencyGroup.getCompetencyGroupId();
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
        if (!(o instanceof CompetencyGroupMapping)) return false;

        CompetencyGroupMapping that = (CompetencyGroupMapping) o;

        if (getCompetencyGroupId() != that.getCompetencyGroupId()) return false;
        
        if (getAuditId() != that.getAuditId()) return false;
        return getCompetencyId() != null ? getCompetencyId().equals(that.getCompetencyId()) : that.getCompetencyId() == null;

    }

    @Override
    public int hashCode() {
        int result = getCompetencyGroupId();
        result = 31 * result + (getCompetencyId() != null ? getCompetencyId().hashCode() : 0);   
        result = 31 * result + (auditId != null ? auditId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CompetencyGroupMapping{" +
                "competencyGroupId=" + competencyGroupId +
                ", competencyId='" + competencyId + '\'' +
                ", competencyGroup=" + competencyGroup +
                ", auditId=" + auditId +
                '}';
    }
}
