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
package org.cccnext.tesuto.content.model.item.metadata;

import org.cccnext.tesuto.content.model.AbstractAssessment;

import java.util.List;

/**
 * Created by jasonbrown on 1/21/16.
 */
public class CompetenciesItemMetadata implements AbstractAssessment {
    private static final long serialVersionUID = 1l;
    List<CompetencyRefItemMetadata> competencyRef;
    List<String> skippedCompetency;

    // updated 1/29/16 to metadata
    List<String> skippedCompetencyRefId;

    public List<CompetencyRefItemMetadata> getCompetencyRef() {
        return competencyRef;
    }

    public void setCompetencyRef(List<CompetencyRefItemMetadata> competencyRef) {
        this.competencyRef = competencyRef;
    }

    public List<String> getSkippedCompetency() {
        return skippedCompetency;
    }

    public void setSkippedCompetency(List<String> skippedCompetency) {
        this.skippedCompetency = skippedCompetency;
    }

    public List<String> getSkippedCompetencyRefId() {
        return skippedCompetencyRefId;
    }

    public void setSkippedCompetencyRefId(List<String> skippedCompetencyRefId) {
        this.skippedCompetencyRefId = skippedCompetencyRefId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CompetenciesItemMetadata that = (CompetenciesItemMetadata) o;

        if (competencyRef != null ? !competencyRef.equals(that.competencyRef) : that.competencyRef != null)
            return false;
        if (skippedCompetency != null ? !skippedCompetency.equals(that.skippedCompetency)
                : that.skippedCompetency != null)
            return false;
        return skippedCompetencyRefId != null ? skippedCompetencyRefId.equals(that.skippedCompetencyRefId)
                : that.skippedCompetencyRefId == null;

    }

    @Override
    public int hashCode() {
        int result = competencyRef != null ? competencyRef.hashCode() : 0;
        result = 31 * result + (skippedCompetency != null ? skippedCompetency.hashCode() : 0);
        result = 31 * result + (skippedCompetencyRefId != null ? skippedCompetencyRefId.hashCode() : 0);
        return result;
    }
}
