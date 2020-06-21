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
package org.cccnext.tesuto.content.model.item;

import java.util.Map;

import org.cccnext.tesuto.content.model.AbstractAssessment;

public class AssessmentItemResponseMapping implements AbstractAssessment {

    private static final long serialVersionUID = -5528450998365136137L;
    private Double upperBound;
    private Double lowerBound;
    private Double defaultValue;
    private Map<String, Double> mapping;

    public Double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(Double upperBound) {
        this.upperBound = upperBound;
    }

    public Double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(Double lowerBound) {
        this.lowerBound = lowerBound;
    }

    public Double getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Double defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Map<String, Double> getMapping() {
        return mapping;
    }

    public void setMapping(Map<String, Double> mapping) {
        this.mapping = mapping;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((defaultValue == null) ? 0 : defaultValue.hashCode());
        result = prime * result + ((lowerBound == null) ? 0 : lowerBound.hashCode());
        result = prime * result + ((mapping == null) ? 0 : mapping.hashCode());
        result = prime * result + ((upperBound == null) ? 0 : upperBound.hashCode());
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
        AssessmentItemResponseMapping other = (AssessmentItemResponseMapping) obj;
        if (defaultValue == null) {
            if (other.defaultValue != null)
                return false;
        } else if (!defaultValue.equals(other.defaultValue))
            return false;
        if (lowerBound == null) {
            if (other.lowerBound != null)
                return false;
        } else if (!lowerBound.equals(other.lowerBound))
            return false;
        if (mapping == null) {
            if (other.mapping != null)
                return false;
        } else if (!mapping.equals(other.mapping))
            return false;
        if (upperBound == null) {
            if (other.upperBound != null)
                return false;
        } else if (!upperBound.equals(other.upperBound))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AssessmentResponseMapping [upperBound=" + upperBound + ", lowerBound=" + lowerBound + ", defaultValue="
                + defaultValue + ", mapping=" + mapping + "]";
    }
}
