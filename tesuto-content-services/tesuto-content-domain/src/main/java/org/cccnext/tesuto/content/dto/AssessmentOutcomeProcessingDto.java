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
package org.cccnext.tesuto.content.dto;

/**
 * @author Josh Corbin <jcorbin@unicon.net>
 */
public class AssessmentOutcomeProcessingDto implements AbstractAssessmentDto {
    private static final long serialVersionUID = 1l;

    private String lookupOutcomeValue; // Enum?
    private String setOutcomeValue; // Enum?
    private String exitTest; // Enum?
    private String outcomeElse; // Enum?
    private String outcomeElseIf; // Enum?
    private String outcomeIf; // Enum?

    public String getLookupOutcomeValue() {
        return lookupOutcomeValue;
    }

    public void setLookupOutcomeValue(String lookupOutcomeValue) {
        this.lookupOutcomeValue = lookupOutcomeValue;
    }

    public String getSetOutcomeValue() {
        return setOutcomeValue;
    }

    public void setSetOutcomeValue(String setOutcomeValue) {
        this.setOutcomeValue = setOutcomeValue;
    }

    public String getExitTest() {
        return exitTest;
    }

    public void setExitTest(String exitTest) {
        this.exitTest = exitTest;
    }

    public String getOutcomeElse() {
        return outcomeElse;
    }

    public void setOutcomeElse(String outcomeElse) {
        this.outcomeElse = outcomeElse;
    }

    public String getOutcomeElseIf() {
        return outcomeElseIf;
    }

    public void setOutcomeElseIf(String outcomeElseIf) {
        this.outcomeElseIf = outcomeElseIf;
    }

    public String getOutcomeIf() {
        return outcomeIf;
    }

    public void setOutcomeIf(String outcomeIf) {
        this.outcomeIf = outcomeIf;
    }
}
