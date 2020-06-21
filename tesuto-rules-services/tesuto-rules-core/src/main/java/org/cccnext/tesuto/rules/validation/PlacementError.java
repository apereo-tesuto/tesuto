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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementActionResult;

import java.util.List;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class PlacementError {
    private String expectedActionName;
    private String actualActionName;
    private Integer testNumber;
    private Integer numberOfActions;
    private Integer expectedNumberOfActions;
    private PlacementActionResult expectedPlacement;
    private PlacementActionResult actualPlacement;

    public String getExpectedActionName() {
        return expectedActionName;
    }

    public void setExpectedActionName(String expectedActionName) {
        this.expectedActionName = expectedActionName;
    }

    public String getActualActionName() {
        return actualActionName;
    }

    public void setActualActionName(String actualActionName) {
        this.actualActionName = actualActionName;
    }

    public Integer getTestNumber() {
        return testNumber;
    }

    public void setTestNumber(Integer testNumber) {
        this.testNumber = testNumber;
    }

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

    public PlacementActionResult getExpectedPlacement() {
        return expectedPlacement;
    }

    public void setExpectedPlacement(PlacementActionResult expectedPlacement) {
        this.expectedPlacement = expectedPlacement;
    }

    public PlacementActionResult getActualPlacement() {
        return actualPlacement;
    }

    public void setActualPlacement(PlacementActionResult actualPlacement) {
        this.actualPlacement = actualPlacement;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        StringBuilder objectResults = new StringBuilder();
        objectResults.append("Placement Differences for test:")
                .append(testNumber)
                .append("\n");
        if(expectedActionName != null && !expectedActionName.equals(actualActionName)){
            objectResults.append("expectedActionName: ")
                    .append(expectedActionName)
                    .append(" actualActionName: ")
                    .append(actualActionName);
        }

        if(numberOfActions != null && !numberOfActions.equals(expectedNumberOfActions)){
            objectResults.append("expectedNumberOfActions: ")
                    .append(expectedNumberOfActions)
                    .append(" numberOfActions: ")
                    .append(numberOfActions);
        }

        int i = 0;

        if (expectedPlacement != null && actualPlacement != null) {
            objectResults.append("    Placement Difference ")
                    .append(i)
                    .append(" ")
                    .append(expectedPlacement.getRuleSetId())
                    .append("\n");
            if (!actualPlacement.getCccid().equals(expectedPlacement.getCccid())) {
                objectResults.append("        ")
                        .append("CCCID Expected: " )
                        .append(expectedPlacement.getCccid())
                        .append(" CCCID Actual: ")
                        .append(actualPlacement.getCccid())
                        .append("\n");
            }
            if (!actualPlacement.getCb21Code().equals(expectedPlacement.getCb21Code())) {
                objectResults.append("        ")
                        .append("CB21Code Expected: " )
                        .append(expectedPlacement.getCb21Code())
                        .append(" CB21Code Actual: ")
                        .append(actualPlacement.getCb21Code())
                        .append("\n");
            }
            if (!actualPlacement.getCollegeId().equals(expectedPlacement.getCollegeId())) {
                objectResults.append("        ")
                        .append("CollegeId Expected: " )
                        .append(expectedPlacement.getCollegeId())
                        .append(" CollegeId Actual: ")
                        .append(actualPlacement.getCollegeId())
                        .append("\n");
            }
            if (!actualPlacement.getCourseGroup().equals(expectedPlacement.getCourseGroup())) {
                objectResults.append("        ")
                        .append("CourseGroup Expected: " )
                        .append(expectedPlacement.getCourseGroup())
                        .append(" CourseGroup Actual: ")
                        .append(actualPlacement.getCourseGroup())
                        .append("\n");
            }
            if (!actualPlacement.getPlacementComponentIds().equals(expectedPlacement.getPlacementComponentIds())) {
                objectResults.append("        ")
                        .append("PlacementComponentIds Expected: " )
                        .append(expectedPlacement.getPlacementComponentIds())
                        .append(" PlacementComponentIds Actual: ")
                        .append(actualPlacement.getPlacementComponentIds())
                        .append("\n");
            }
            if (!actualPlacement.getRuleSetId().equals(expectedPlacement.getRuleSetId())) {
                objectResults.append("        ")
                        .append("RuleSetId Expected: " )
                        .append(expectedPlacement.getRuleSetId())
                        .append(" RuleSetId Actual: ")
                        .append(actualPlacement.getRuleSetId())
                        .append("\n");
            }
            if (!actualPlacement.getRuleId().equals(expectedPlacement.getRuleId())) {
                objectResults.append("        ")
                        .append("RuleId Expected: " )
                        .append(expectedPlacement.getRuleId())
                        .append(" RuleId Actual: ")
                        .append(actualPlacement.getRuleId())
                        .append("\n");
            }
            if (!actualPlacement.getSubjectAreaId().equals(expectedPlacement.getSubjectAreaId())) {
                objectResults.append("        ")
                        .append("SubjectAreaId Expected: " )
                        .append(expectedPlacement.getSubjectAreaId())
                        .append(" SubjectAreaId Actual: ")
                        .append(actualPlacement.getSubjectAreaId())
                        .append("\n");
            }
            if (!actualPlacement.getSubjectAreaVersion().equals(expectedPlacement.getSubjectAreaVersion())) {
                objectResults.append("        ")
                        .append("SubjectAreaVersion Expected: " )
                        .append(expectedPlacement.getSubjectAreaVersion())
                        .append(" SubjectAreaVersion Actual: ")
                        .append(actualPlacement.getSubjectAreaVersion())
                        .append("\n");
            }
            if (!actualPlacement.getElaIndicator().equals(expectedPlacement.getElaIndicator())) {
                objectResults.append("        ")
                        .append("ElaIndicator Expected: " )
                        .append(expectedPlacement.getElaIndicator())
                        .append(" ElaIndicator Actual: ")
                        .append(actualPlacement.getElaIndicator())
                        .append("\n");
            }
        }
        return objectResults.toString();
    }
}
