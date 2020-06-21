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
package org.cccnext.tesuto.util;

import org.apache.commons.collections.CollectionUtils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.SortedSet;
import java.util.TreeSet;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class ValidationUtil {


    // /Flag used to turn on full branch rule evaluation.
    //I assume ASSESS will be the only group that will leverage this.
    private static Boolean useFullBranchRuleEvaluation;

    public static Boolean getUseFullBranchRuleEvaluation() {
        return useFullBranchRuleEvaluation;
    }

    public static void setUseFullBranchRuleEvaluation(Boolean useFullBranchRuleEvaluation) {
        ValidationUtil.useFullBranchRuleEvaluation = useFullBranchRuleEvaluation;
    }

    public static SortedSet<Double> addPossibleScore(SortedSet<Double> firstSet, SortedSet<Double> secondSet) {
        //First check the sets
        if (CollectionUtils.isEmpty(firstSet) && CollectionUtils.isNotEmpty(secondSet)) {
            return secondSet;
        } else if ((CollectionUtils.isEmpty(secondSet) && CollectionUtils.isNotEmpty(firstSet)) || (CollectionUtils.isEmpty(secondSet) && CollectionUtils.isEmpty(firstSet))) {
            return firstSet;
        }

        //Next determine if we are doing a full evaluation.
        if (useFullBranchRuleEvaluation) {
            SortedSet<Double> addedSet = new TreeSet<>();
            for (Double score1 : firstSet) {
                for (Double score2 : secondSet) {
                    Double tempScore = 0d;
                    tempScore = score1 + score2;
                    addedSet.add(tempScore);
                }
            }
            return addedSet;
        } else {
            SortedSet<Double> addedSet = new TreeSet<>();

            Double min = firstSet.first() + secondSet.first();
            Double max = firstSet.last() + secondSet.last();

            if (min < firstSet.first()) {
                addedSet.add(min);
            } else {
                addedSet.add(firstSet.first());
            }

            if (max > firstSet.last()) {
                addedSet.add(max);
            } else {
                addedSet.add(firstSet.last());
            }
            return addedSet;
        }
    }
}
