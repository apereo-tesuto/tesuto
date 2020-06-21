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


import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class CompetencyGroupMappingId implements Serializable{
    static final long serialVersionUID = 1l;

    private int competencyGroupId;
    private String competencyId;
    private Integer auditId;
    
    public CompetencyGroupMappingId() {
        super();
    }
    
    public CompetencyGroupMappingId(int competencyGroupId, String competencyId, Integer auditId) {
        super();
        this.competencyGroupId = competencyGroupId;
        this.competencyId = competencyId;
        this.auditId = auditId;
    }

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

    public Integer getAuditId() {
        return auditId;
    }

    public void setAuditId(Integer auditId) {
        this.auditId = auditId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CompetencyGroupMappingId that = (CompetencyGroupMappingId) o;

        if (competencyGroupId != that.competencyGroupId)
            return false;
        
        if (auditId != that.auditId)
            return false;
        return competencyId != null ? competencyId.equals(that.competencyId) : that.competencyId == null;

    }

    @Override
    public int hashCode() {
        int result = competencyGroupId;
        result = 31 * result + (competencyId != null ? competencyId.hashCode() : 0);
        result = 31 * result + (auditId != null ? auditId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("competencyGroupId", competencyGroupId).append("competencyId",
                competencyId).append("auditId", auditId).toString();
    }
}
