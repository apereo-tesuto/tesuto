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
package org.cccnext.tesuto.content.model.scoring;

import org.cccnext.tesuto.util.TesutoUtils;

import java.util.HashMap;
import java.util.SortedSet;

/**
 * Created by jasonbrown on 5/5/16.
 */
public class EvaluationScoringModel {

    public enum EvaluationType{
        VALIDATION,
        DELIVERY
    }

    EvaluationType type;
    SortedSet<Double> possibleScores;
    HashMap<String, Double> sectionScores;

    public EvaluationScoringModel(SortedSet<Double> possibleScores){
        this.type = EvaluationType.VALIDATION;
        this.possibleScores = possibleScores;
    }

    public EvaluationScoringModel(HashMap<String, Double> sectionScores){
        this.type = EvaluationType.DELIVERY;
        this.sectionScores = decodeMap(sectionScores);
    }

    public HashMap<String, Double> decodeMap(HashMap<String, Double> sectionScores){
        HashMap<String, Double> decodedMap = new HashMap<>();
        sectionScores.forEach((k, v) -> decodedMap.put(TesutoUtils.decodeKey(k), v));
        return decodedMap;
    }

    public EvaluationType getType() {
        return type;
    }

    public void setType(EvaluationType type) {
        this.type = type;
    }

    public SortedSet<Double> getPossibleScores() {
        return possibleScores;
    }

    public void setPossibleScores(SortedSet<Double> possibleScores) {
        if(getType() != EvaluationType.VALIDATION){
            throw new IllegalArgumentException(String.format("Can not set possible scores for an evaluationScoringModel with type of %s", getType().toString()));
        }
        this.possibleScores = possibleScores;
    }

    public HashMap<String, Double> getSectionScores() {
        return sectionScores;
    }

    public void setSectionScores(HashMap<String, Double> sectionScores) {
        if(getType() != EvaluationType.DELIVERY){
            throw new IllegalArgumentException(String.format("Can not set sectionScores for an evaluationScoringModel with type of %s", getType().toString()));
        }
        this.sectionScores = sectionScores;
    }
}
