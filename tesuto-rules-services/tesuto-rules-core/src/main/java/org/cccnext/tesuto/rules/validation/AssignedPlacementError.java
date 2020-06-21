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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementActionResult;
import org.cccnext.tesuto.placement.view.PlacementViewDto;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class AssignedPlacementError {
    private String expectedActionName;
    private String actualActionName;
    private Integer testNumber;
    private Integer numberOfActions;
    private Integer expectedNumberOfActions;
    private PlacementViewDto expectedPlacement;
    private PlacementViewDto actualPlacement;

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

    public PlacementViewDto getExpectedPlacement() {
        return expectedPlacement;
    }

    public void setExpectedPlacement(PlacementViewDto expectedPlacement) {
        this.expectedPlacement = expectedPlacement;
    }

    public PlacementViewDto getActualPlacement() {
        return actualPlacement;
    }

    public void setActualPlacement(PlacementViewDto actualPlacement) {
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
            objectResults.append("    Assigned Placement Difference ")
                    .append(i)
                    .append(" ")
                    .append(expectedPlacement.getAssignedRuleSetId())
                    .append("\n");
            if (!actualPlacement.getId().equals(expectedPlacement.getId())) {
                objectResults.append("        ")
                        .append("ID Expected: " )
                        .append(expectedPlacement.getId())
                        .append(" ID Actual: ")
                        .append(actualPlacement.getId())
                        .append("\n");
            }
            if (actualPlacement.isAssigned() != expectedPlacement.isAssigned()) {
                objectResults.append("        ")
                        .append("isAssigned Expected: " )
                        .append(expectedPlacement.isAssigned())
                        .append(" isAssigned Actual: ")
                        .append(actualPlacement.isAssigned())
                        .append("\n");
            }
            if (!actualPlacement.getAssignedDate().equals(expectedPlacement.getAssignedDate())) {
                objectResults.append("        ")
                        .append("Assigned Date Expected: " )
                        .append(expectedPlacement.getAssignedDate())
                        .append(" Assigned Date Actual: ")
                        .append(actualPlacement.getAssignedDate())
                        .append("\n");
            }
            if (!actualPlacement.getAssignedRuleSetId().equals(expectedPlacement.getAssignedRuleSetId())) {
                objectResults.append("        ")
                        .append("Assigned Rule Set Id Expected: " )
                        .append(expectedPlacement.getAssignedRuleSetId())
                        .append(" Assigned Rule Set Id Expected Actual: ")
                        .append(actualPlacement.getAssignedRuleSetId())
                        .append("\n");
            }
        }
        return objectResults.toString();
    }
}
