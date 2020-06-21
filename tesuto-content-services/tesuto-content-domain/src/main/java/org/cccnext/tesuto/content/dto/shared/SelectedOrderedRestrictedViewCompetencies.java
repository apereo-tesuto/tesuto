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

import org.cccnext.tesuto.content.viewdto.CompetencyRestrictedViewDto;

public class SelectedOrderedRestrictedViewCompetencies implements Serializable, Comparable<SelectedOrderedRestrictedViewCompetencies> {
    private static final long serialVersionUID = 2L;
    
    List<CompetencyRestrictedViewDto> mastered;
    List<CompetencyRestrictedViewDto> tolearn;
    
    CompetencyRestrictedViewDto parent;

    String title;
    String performance;
    int position;
    double maxDifficulty;

    /**
     * We want highest difficulty first, so reverse order
     * @param o
     * @return
     */
    @Override
    public int compareTo(SelectedOrderedRestrictedViewCompetencies o) {
        int compareTo = Double.compare(o.getMaxDifficulty(), this.maxDifficulty);
        if (compareTo == 0) {
            return title.compareTo(o.getTitle());
        } else {
            return compareTo;
        }
    }

    public List<CompetencyRestrictedViewDto> getMastered() {
        return mastered;
    }
    public void setMastered(List<CompetencyRestrictedViewDto> mastered) {
        this.mastered = mastered;
    }
    public List<CompetencyRestrictedViewDto> getTolearn() {
        return tolearn;
    }
    public void setTolearn(List<CompetencyRestrictedViewDto> tolearn) {
        this.tolearn = tolearn;
    }
    public CompetencyRestrictedViewDto getParent() {
        return parent;
    }
    public void setParent(CompetencyRestrictedViewDto parent) {
        this.parent = parent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public double getMaxDifficulty() {
        return maxDifficulty;
    }

    public void setMaxDifficulty(double maxDifficulty) {
        this.maxDifficulty = maxDifficulty;
    }
}
