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

import org.cccnext.tesuto.content.dto.AbstractAssessmentDto;

public class CompetencyDifficultyDto implements AbstractAssessmentDto {

    private static final long serialVersionUID = 1L;
    
    CompetencyDto competency;
    Double difficulty;
    
    public CompetencyDifficultyDto(CompetencyDto competency, Double difficulty) {
        this.competency = competency;
        this.difficulty = difficulty;
    }
    
    public CompetencyDifficultyDto() {
    }   

    
    public CompetencyDto getCompetency() {
        return competency;
    }
    public void setCompetency(CompetencyDto competency) {
        this.competency = competency;
    }
    public Double getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(Double difficulty) {
        this.difficulty = difficulty;
    }
}
