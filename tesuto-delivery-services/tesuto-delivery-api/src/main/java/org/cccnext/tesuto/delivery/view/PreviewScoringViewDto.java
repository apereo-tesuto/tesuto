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
package org.cccnext.tesuto.delivery.view;

import java.util.Collection;
import java.util.List;

public class PreviewScoringViewDto {
    private List<ItemScoreViewDto> scores;
    private Collection<ItemOutcomeViewDto> outcomes;

    public List<ItemScoreViewDto> getScores() {
        return scores;
    }

    public void setScores(List<ItemScoreViewDto> scores) {
        this.scores = scores;
    }

    public Collection<ItemOutcomeViewDto> getOutcomes() {
        return outcomes;
    }

    public void setOutcomes(Collection<ItemOutcomeViewDto> outcomes) {
        this.outcomes = outcomes;
    }
}
