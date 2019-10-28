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
package org.cccnext.tesuto.content.model.item.interaction;

import java.util.List;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentMatchInteraction extends AssessmentInteraction {
    private static final long serialVersionUID = 1l;

    private boolean shuffle;
    private Integer maxAssociations;
    private Integer minAssociations = 0;
    private String prompt;
    private List<AssessmentSimpleMatchSet> matchSets;

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public Integer getMaxAssociations() {
        return maxAssociations;
    }

    public void setMaxAssociations(Integer maxAssociations) {
        this.maxAssociations = maxAssociations;
    }

    public Integer getMinAssociations() {
        return minAssociations;
    }

    public void setMinAssociations(Integer minAssociations) {
        this.minAssociations = minAssociations;
    }

    public List<AssessmentSimpleMatchSet> getMatchSets() {
        return matchSets;
    }

    public void setMatchSets(List<AssessmentSimpleMatchSet> matchSets) {
        this.matchSets = matchSets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;

        AssessmentMatchInteraction that = (AssessmentMatchInteraction) o;

        if (shuffle != that.shuffle)
            return false;
        if (maxAssociations != null ? !maxAssociations.equals(that.maxAssociations) : that.maxAssociations != null)
            return false;
        if (minAssociations != null ? !minAssociations.equals(that.minAssociations) : that.minAssociations != null)
            return false;
        if (prompt != null ? !prompt.equals(that.prompt) : that.prompt != null)
            return false;
        return !(matchSets != null ? !matchSets.equals(that.matchSets) : that.matchSets != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (shuffle ? 1 : 0);
        result = 31 * result + (maxAssociations != null ? maxAssociations.hashCode() : 0);
        result = 31 * result + (minAssociations != null ? minAssociations.hashCode() : 0);
        result = 31 * result + (prompt != null ? prompt.hashCode() : 0);
        result = 31 * result + (matchSets != null ? matchSets.hashCode() : 0);
        return result;
    }
}
