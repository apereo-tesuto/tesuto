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
package org.cccnext.tesuto.content.dto.competency;

import java.io.Serializable;
import java.util.List;

public class CompetencyDifficultyRef  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String competencyIdentifier;
    private String discipline;
    private int version;
    private List<String> assessmentItemIds;
    private double difficulty;
    private List<String> parents;

    public String getCompetencyIdentifier() {
        return competencyIdentifier;
    }
    public void setCompetencyIdentifier(String competencyIdentifier) {
        this.competencyIdentifier = competencyIdentifier;
    }
    public List<String> getAssessmentItemIds() {
        return assessmentItemIds;
    }
    public void setAssessmentItemIds(List<String> assessmentItemIds) {
        this.assessmentItemIds = assessmentItemIds;
    }
    public Double getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(double difficulty) {
        this.difficulty = difficulty;
    }
    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }
    public String getDiscipline() {
        return discipline;
    }
    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }

    public List<String> getParents() {
        return parents;
    }
    public void setParents(List<String> parents) {
        this.parents = parents;
    }
}
