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
package org.cccnext.tesuto.content.dto.item.interaction;

import java.util.List;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class AssessmentMatchInteractionDto extends AssessmentInteractionDto {
    private static final long serialVersionUID = 1l;

    private boolean shuffle;
    private Integer maxAssociations;
    private Integer minAssociations;
    private String prompt;
    private List<AssessmentSimpleMatchSetDto> matchSets;

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

    public List<AssessmentSimpleMatchSetDto> getMatchSets() {
        return matchSets;
    }

    public void setMatchSets(List<AssessmentSimpleMatchSetDto> matchSets) {
        this.matchSets = matchSets;
    }

    @Override
    boolean doValidateResponses(List<String> responses) {
        //The business logic here is commented out to make it clear the code is not actually tested
        return true;
        /*
        //validate the number of associations provided
        if ((maxAssociations != null && responses.size() > maxAssociations)
                || (minAssociations != null && responses.size() < minAssociations)) {
            return false;
        }

        //This map will be used to ensure that each response is a set of ids' from different match sets
        Map<String,Integer> memberships = new HashMap<>();
        for (int i=0; i<matchSets.size(); ++i) {
            AssessmentSimpleMatchSetDto matchSet = matchSets.get(i);
            for (AssessmentSimpleAssociableChoiceDto choice: matchSet.getMatchSet()) {
                memberships.put(choice.getIdentifier(), i);
            }
        }

        //This map will be used to count the number of matches for each choice
        Map<String,Integer> matchCounts = new HashMap<>();

        for (String value: responses) {
            String[] ids = value.split("\\s+");
            Set<Integer> matchSetsSeen = new HashSet<>();
            for (int i=0; i<ids.length; ++i){
                Integer matchSetIndex = memberships.get(value);
                if (matchSetIndex == null) {
                    return false; //This must be an invalid choice
                }
                if (matchSetsSeen.contains(matchSetIndex)) {
                    return false; //Attempting to match items from the same match set
                }
                matchSetsSeen.add(matchSetIndex);
                matchCounts.merge(ids[i], 1, ((m,n)->m+n));
            }
        }

        //Validate that the matchCounts meet the constraints for each choice
        return matchSets.stream().allMatch( matchSet -> matchSet.getMatchSet().stream().allMatch(
                choice -> {
                    int count = matchCounts.computeIfAbsent(choice.getIdentifier(), (k -> 0));
                    return (choice.getMatchMin() == null || count >= choice.getMatchMin())
                            && (choice.getMatchMax() == null || count <= choice.getMatchMax());
                }
        ));
        */
    }

}
