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
package org.cccnext.tesuto.rules.validation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cccnext.tesuto.domain.multiplemeasures.Fact;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementComponentActionResult;
import org.cccnext.tesuto.domain.multiplemeasures.VariableSet;

public class PlacementComponentError {
    private String expectedActionName;
    private String actualActionName;
    private String fieldErrors;
    private Integer testNumber;
    public String getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(String fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    private Integer numberOfActions;
    private Integer expectedNumberOfActions;
    private Integer numberOfPlacements;
    private Integer expectedNumberOfPlacements;
    private Integer numberOfCollegePlacements;
    private Integer expectedNumberOfCollegePlacements;
    private List<PlacementComponentActionResult> expectedPlacements;
    private List<PlacementComponentActionResult> actualPlacements;

    public Integer getNumberOfActions() {
        return numberOfActions;
    }

    public void setNumberOfActions(Integer numberOfActions) {
        this.numberOfActions = numberOfActions;
    }

    public Integer getExpectedNumberOfActions() {
        return expectedNumberOfActions;
    }

    public void setExpectedNumberOfActions(Integer expectedNumberOfActions) {
        this.expectedNumberOfActions = expectedNumberOfActions;
    }

    public Integer getNumberOfPlacements() {
        return numberOfPlacements;
    }

    public void setNumberOfPlacements(Integer numberOfPlacements) {
        this.numberOfPlacements = numberOfPlacements;
    }

    public Integer getExpectedNumberOfPlacements() {
        return expectedNumberOfPlacements;
    }

    public void setExpectedNumberOfPlacements(Integer expectedNumberOfPlacements) {
        this.expectedNumberOfPlacements = expectedNumberOfPlacements;
    }

    public List<PlacementComponentActionResult> getExpectedPlacements() {
        return expectedPlacements;
    }

    public void setExpectedPlacements(List<PlacementComponentActionResult> expectedPlacements) {
        this.expectedPlacements = expectedPlacements;
    }

    public List<PlacementComponentActionResult> getActualPlacements() {
        return actualPlacements;
    }

    public void setActualPlacements(List<PlacementComponentActionResult> actualPlacements) {
        this.actualPlacements = actualPlacements;
    }

    public String getExpectActionName() {
        return expectedActionName;
    }

    public void setExpectActionName(String expectedActionName) {
        this.expectedActionName = expectedActionName;
    }

    public String getActualActionName() {
        return actualActionName;
    }

    public void setActualActionName(String actualActionName) {
        this.actualActionName = actualActionName;
    }

    public Integer getNumberOfCollegePtestNumberlacements() {
        return numberOfCollegePlacements;
    }

    public void setNumberOfCollegePlacements(Integer numberOfCollegePlacements) {
        this.numberOfCollegePlacements = numberOfCollegePlacements;
    }

    public Integer getExpectedNumberOfCollegePlacements() {
        return expectedNumberOfCollegePlacements;
    }

    public String getExpectedActionName() {
        return expectedActionName;
    }

    public void setExpectedActionName(String expectedActionName) {
        this.expectedActionName = expectedActionName;
    }

    public Integer getTestNumber() {
        return testNumber;
    }

    public void setTestNumber(Integer testNumber) {
        this.testNumber = testNumber;
    }

    public void setExpectedNumberOfCollegePlacements(Integer expectedNumberOfCollegePlacements) {
        this.expectedNumberOfCollegePlacements = expectedNumberOfCollegePlacements;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public String fieldErrors(List<String> names, Map<String, Fact> facts) {
        int i = 0;
        Map<String, Field> fieldMap = mapOfResultFields();

        StringBuilder values = new StringBuilder();
        if (CollectionUtils.isNotEmpty(expectedPlacements)) {
            for (PlacementComponentActionResult exp : expectedPlacements) {
                String delimiter = "";
                PlacementComponentActionResult act = actualPlacements.get(i++);
                for (String name : names) {
                    if (facts.containsKey(name)) {
                        values.append(delimiter).append(facts.get(name).getValue());
                    } else if (fieldMap.containsKey(name)) {
                        addResultError(exp, act, fieldMap.get(name), values, delimiter);
                    } else {
                        values.append(delimiter);
                    }
                    delimiter = ",";
                }
                values.append(delimiter);
                values.append(testNumber);
                if(StringUtils.isNotBlank(fieldErrors)) {
                    values.append(", field errors:");
                    values.append(fieldErrors);
                }
                values.append("\n");
            }
            return values.toString();
        }
        return "";
    }

    private Map<String, Field> mapOfResultFields() {
        Field[] declaredFields = PlacementComponentActionResult.class.getDeclaredFields();
        Map<String, Field> fieldMap = new HashMap<>();
        for (Field field : declaredFields) {
            fieldMap.put(field.getName(), field);
        }
        return fieldMap;
    }

    private void addResultError(PlacementComponentActionResult exp, PlacementComponentActionResult act, Field field,
            StringBuilder values, String delimiter) {
        field.setAccessible(true);
        boolean notEqual = false;
        Object actObj;
        Object expObj;
        try { 
            actObj = field.get(act);
            expObj = field.get(exp);
            if (expObj == null && actObj != null) {
                values.append(delimiter).append(useSpecialDelimiter(actObj));
                return;
            }
        } catch (IllegalArgumentException | IllegalAccessException exception) {
            throw new RuntimeException("Unable to use reflection to process equivalancy.", exception);
        }
        if (actObj instanceof String) {
            if (notEqual((String) actObj, (String) expObj)) {
                notEqual = true;
            }
        } else {
            if (notEqual(actObj, expObj)) {
                notEqual = true;
            }
        }
        if (notEqual) {
            values.append(delimiter).append(useSpecialDelimiter(expObj)).append("::")
                    .append(useSpecialDelimiter(actObj));
        } else {
            values.append(delimiter).append("same:").append(useSpecialDelimiter(actObj));
        }
    }

    public String actionErrors(List<String> names, Map<String, Fact> facts) {
        StringBuilder objectResults = new StringBuilder("");
        if (expectedActionName != null && !expectedActionName.equals(actualActionName)) {
            buildActionError(objectResults, testNumber, "action type", expectedActionName, actualActionName, names,
                    facts);
            ;
        }

        if (numberOfActions != null && !numberOfActions.equals(expectedNumberOfActions)) {
            buildActionError(objectResults, testNumber, "actions", expectedNumberOfActions.toString(),
                    numberOfActions.toString(), names, facts);
        }

        if (numberOfPlacements != null && !numberOfPlacements.equals(expectedNumberOfPlacements)) {
            buildActionError(objectResults, testNumber, "placements", expectedNumberOfPlacements.toString(),
                    numberOfPlacements.toString(), names, facts);
        }

        if (numberOfCollegePlacements != null && !numberOfCollegePlacements.equals(expectedNumberOfCollegePlacements)) {
            buildActionError(objectResults, testNumber, "colleges", expectedNumberOfCollegePlacements.toString(),
                    numberOfCollegePlacements.toString(), names, facts);
        }

        return objectResults.toString();
    }

    private String useSpecialDelimiter(Object obj) {
        if (obj instanceof Iterable<?>) {
            return StringUtils.join((Iterable<?>) obj, ":");
        }
        return obj.toString();
    }

    private void buildActionError(StringBuilder objectResults, Integer testNumber, String type, String expected,
            String actual, List<String> names, Map<String, Fact> facts) {
        objectResults.append(testNumber).append(",").append(type).append(",").append(expected).append(",")
                .append(actual);

        for (String name : names) {
            if (facts.containsKey(name)) {
                objectResults.append(",").append(facts.get(name).getValue());
            } else {
                objectResults.append(",");
            }
        }
        objectResults.append("\n");
    }

    private boolean notEqual(Object obj1, Object obj2) {
        if (obj1 == obj2) {
            return false;
        }
        if (obj1 != null) {
            return !obj1.equals(obj2);
        }
        return !obj2.equals(obj1);
    }

    private boolean notEqual(String str1, String str2) {
        if (StringUtils.isBlank(str1) && StringUtils.isBlank(str2)) {
            return false;
        }
        if (str1 == str2) {
            return false;
        }
        if (str1 != null) {
            return !str1.equals(str2);
        }
        return !str2.equals(str1);
    }

}
