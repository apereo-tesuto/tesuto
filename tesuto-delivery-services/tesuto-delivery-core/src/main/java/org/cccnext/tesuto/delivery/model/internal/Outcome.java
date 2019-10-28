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
package org.cccnext.tesuto.delivery.model.internal;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.AssessmentBaseType;
import org.cccnext.tesuto.delivery.model.internal.enums.OutcomeDeclarationType;

import uk.ac.ed.ph.jqtiplus.value.Cardinality;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Outcome implements Serializable {

    public static final String SCORE = "SCORE";

    public static final String CAI_POINTS_SCORE = "CAI_POINTS_SCORE";
    public static final String CAI_PERCENT_SCORE = "CAI_PERCENT_SCORE";
    public static final String CAI_ODDS_SUCCESS = "CAI_ODDS_SUCCESS";
    public static final String CAI_AVG_ITEM_DIFFICULTY = "CAI_AVG_ITEM_DIFFICULTY";
    public static final String CAI_ITEM_DIFFICULTY_COUNT = "CAI_ITEM_DIFFICULTY_COUNT";
    public static final String CAI_STUDENT_ABILITY = "CAI_STUDENT_ABILITY";
    public static final String CAI_REPORTED_SCALE = "CAI_REPORTED_SCALE";

    public static final String SCORE_DEFAULT_VALUE = "0.0";
    private static final long serialVersionUID = 1L;

    private OutcomeDeclarationType declarationType; //not in QTI
    private String outcomeIdentifier;
    private AssessmentBaseType baseType;
    private Cardinality cardinality;
    private List<String> values = new ArrayList<String>();
    private Double normalMaximum;
    private Double normalMinimum;

    private Double value;

    public Outcome() {
    }

    public Outcome(String outcomeIdentifier, Double value, AssessmentBaseType baseType, OutcomeDeclarationType declarationType) {
        this.outcomeIdentifier = outcomeIdentifier;
        this.value = value;
        this.baseType = baseType;
        this.declarationType = declarationType;
    }

    public Double getNormalMinimum() {
        return normalMinimum;
    }

    public void setNormalMinimum(Double normalMinimum) {
        this.normalMinimum = normalMinimum;
    }

    public Double getNormalMaximum() {
        return normalMaximum;
    }

    public void setNormalMaximum(Double normalMaximum) {
        this.normalMaximum = normalMaximum;
    }

    public String getOutcomeIdentifier() {
        return outcomeIdentifier;
    }

    public void setOutcomeIdentifier(String outcomeIdentifier) {
        this.outcomeIdentifier = outcomeIdentifier;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public void addValues(List<String> values) {
        this.values.addAll(values);
    }

    public void replaceValues(String value) {
        this.values.clear();
        values.add(value);
    }

    public void replaceValues(float value) {
        this.values.add(Float.toString(value));
        this.values.clear();
        values.add(Float.toString(value));
    }

    public AssessmentBaseType getBaseType() {
        return baseType;
    }

    public void setBaseType(AssessmentBaseType baseType) {
        this.baseType = baseType;
    }

    public Cardinality getCardinality() {
        return cardinality;
    }

    public void setCardinality(Cardinality cardinality) {
        this.cardinality = cardinality;
    }

    public OutcomeDeclarationType getDeclarationType() {
        return declarationType;
    }

    public void setDeclarationType(OutcomeDeclarationType declarationType) {
        this.declarationType = declarationType;
    }

    public Double getSumOfValues(){
        return getValuesAsDoubles().stream().mapToDouble(Double::doubleValue).sum();
    }

    private List<Double> convertToDoubles(List<String> outcomeValues){
        List<Double> doubles = new ArrayList<>(outcomeValues.size());
        for(String v: outcomeValues){
            doubles.add(Double.valueOf(v));
        }
        return doubles;
    }

    public List<Double> getValuesAsDoubles(){
        return convertToDoubles(this.getValues());
    }

    @Override
    public boolean equals(Object o) {
        double epsilon = 0.00000001;
        if (this == o)
            return true;
        if (!(o instanceof Outcome))
            return false;

        Outcome outcome = (Outcome) o;

        if (outcomeIdentifier != null ? !(outcomeIdentifier.equals(outcome.outcomeIdentifier)) : outcome.outcomeIdentifier != null)
            return false;
        if (declarationType != null ? !(declarationType.equals(outcome.declarationType)) : outcome.declarationType != null)
            return false;
        if (value != null ? !(Math.abs(value - outcome.value) < epsilon) : outcome.value != null)
            return false;
        if (normalMinimum != null ? !(Math.abs(normalMinimum - outcome.normalMinimum) < epsilon) : outcome.normalMinimum != null)
            return false;
        if (normalMaximum != null ? !(Math.abs(normalMaximum - outcome.normalMaximum) < epsilon) : outcome.normalMaximum != null)
            return false;
        return getValues() != null ? getValues().equals(outcome.getValues()) : outcome.getValues() == null;

    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
