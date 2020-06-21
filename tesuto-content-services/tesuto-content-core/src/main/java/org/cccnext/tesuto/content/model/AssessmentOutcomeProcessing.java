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
package org.cccnext.tesuto.content.model;

/**
 * @author Josh Corbin <jcorbin@unicon.net>
 */
public class AssessmentOutcomeProcessing implements AbstractAssessment {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((exitTest == null) ? 0 : exitTest.hashCode());
        result = prime * result + ((lookupOutcomeValue == null) ? 0 : lookupOutcomeValue.hashCode());
        result = prime * result + ((outcomeElse == null) ? 0 : outcomeElse.hashCode());
        result = prime * result + ((outcomeElseIf == null) ? 0 : outcomeElseIf.hashCode());
        result = prime * result + ((outcomeIf == null) ? 0 : outcomeIf.hashCode());
        result = prime * result + ((setOutcomeValue == null) ? 0 : setOutcomeValue.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AssessmentOutcomeProcessing other = (AssessmentOutcomeProcessing) obj;
        if (exitTest == null) {
            if (other.exitTest != null)
                return false;
        } else if (!exitTest.equals(other.exitTest))
            return false;
        if (lookupOutcomeValue == null) {
            if (other.lookupOutcomeValue != null)
                return false;
        } else if (!lookupOutcomeValue.equals(other.lookupOutcomeValue))
            return false;
        if (outcomeElse == null) {
            if (other.outcomeElse != null)
                return false;
        } else if (!outcomeElse.equals(other.outcomeElse))
            return false;
        if (outcomeElseIf == null) {
            if (other.outcomeElseIf != null)
                return false;
        } else if (!outcomeElseIf.equals(other.outcomeElseIf))
            return false;
        if (outcomeIf == null) {
            if (other.outcomeIf != null)
                return false;
        } else if (!outcomeIf.equals(other.outcomeIf))
            return false;
        if (setOutcomeValue == null) {
            if (other.setOutcomeValue != null)
                return false;
        } else if (!setOutcomeValue.equals(other.setOutcomeValue))
            return false;
        return true;
    }
}
