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
package org.cccnext.tesuto.content.dto.item.metadata;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;

public class CompetencyRefItemMetadataDto implements AbstractAssessmentDto {
    private static final long serialVersionUID = 2l;

    private String mapDiscipline; //Update 8/10/2016 to competencyMapDiscipline
    private String competencyMapDiscipline;
    private String competencyId;
    private String competencyRefId; // Update 1/29/2016 to competencyId

    /**
     * Without this Jackson Serialization will throw exception if no child nodes
     * are defined. The author has defined the competencyRef node but no other
     * child nodes.
     */
    public CompetencyRefItemMetadataDto(String notUsed) {
        this.mapDiscipline = null;
        this.competencyMapDiscipline = null;
        this.competencyId = null;
        this.competencyRefId = null;
    }

    public CompetencyRefItemMetadataDto() {
    }

    public String getCompetencyMapDiscipline() {
        return competencyMapDiscipline;
    }

    public void setCompetencyMapDiscipline(String competencyMapDiscipline) {
        this.competencyMapDiscipline = competencyMapDiscipline;
    }

    public String getMapDiscipline() {
        return mapDiscipline;
    }

    public void setMapDiscipline(String mapDiscipline) {
        this.mapDiscipline = mapDiscipline;
    }

    public String getCompetencyId() {
        return competencyId;
    }

    public void setCompetencyId(String competencyId) {
        this.competencyId = competencyId;
    }

    public String getCompetencyRefId() {
        return competencyRefId;
    }

    public void setCompetencyRefId(String competencyRefId) {
        this.competencyRefId = competencyRefId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).reflectionToString(this);
    }
}
