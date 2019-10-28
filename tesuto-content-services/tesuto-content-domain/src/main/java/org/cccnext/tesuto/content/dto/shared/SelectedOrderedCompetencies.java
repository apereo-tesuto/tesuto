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
package org.cccnext.tesuto.content.dto.shared;

import java.io.Serializable;
import java.util.List;

import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyDto;


public class SelectedOrderedCompetencies implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public SelectedOrderedCompetencies() {
    	
    }
    
    public SelectedOrderedCompetencies(List<CompetencyDifficultyDto> mastered, List<CompetencyDifficultyDto> tolearn) {
        this.mastered = mastered;
        this.tolearn = tolearn;
    }
    
    List<CompetencyDifficultyDto> mastered;
    List<CompetencyDifficultyDto> tolearn;
    
    CompetencyDto parent;
    
    Double minimumDifficulty;
    Double maximumDifficulty;
    Double studentAbility;
    
    
    public List<CompetencyDifficultyDto> getMastered() {
        return mastered;
    }
    public void setMastered(List<CompetencyDifficultyDto> mastered) {
        this.mastered = mastered;
    }
    public List<CompetencyDifficultyDto> getTolearn() {
        return tolearn;
    }
    public void setTolearn(List<CompetencyDifficultyDto> tolearn) {
        this.tolearn = tolearn;
    }
    public CompetencyDto getParent() {
        return parent;
    }
    public void setParent(CompetencyDto parent) {
        this.parent = parent;
    }
    public Double getMinimumDifficulty() {
        return minimumDifficulty;
    }
    public void setMinimumDifficulty(Double minimumDifficulty) {
        this.minimumDifficulty = minimumDifficulty;
    }
    public Double getMaximumDifficulty() {
        return maximumDifficulty;
    }
    public void setMaximumDifficulty(Double maximumDifficulty) {
        this.maximumDifficulty = maximumDifficulty;
    }
    public Double getStudentAbility() {
        return studentAbility;
    }
    public void setStudentAbility(Double studentAbility) {
        this.studentAbility = studentAbility;
    }
}
