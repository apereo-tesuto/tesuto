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
package org.cccnext.tesuto.content.model.metadata;

import org.cccnext.tesuto.content.model.AbstractAssessment;

import java.util.List;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class CompetencyPerformanceMetadata implements AbstractAssessment {
    private static final long serialVersionUID = 1l;

    List<CompetencyCategoryMetadata> competencyCategories;

    public List<CompetencyCategoryMetadata> getCompetencyCategories() {
        return competencyCategories;
    }

    public void setCompetencyCategories(List<CompetencyCategoryMetadata> competencyCategories) {
        this.competencyCategories = competencyCategories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompetencyPerformanceMetadata that = (CompetencyPerformanceMetadata) o;

        return competencyCategories.equals(that.competencyCategories);

    }

    @Override
    public int hashCode() {
        return competencyCategories.hashCode();
    }
}
