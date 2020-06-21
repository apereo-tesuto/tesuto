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
package org.cccnext.tesuto.delivery.view;

/**
 * Created by bruce on 11/19/15.
 */
public class ResponseVarViewDto {

    private String identifier;
    private String cardinality;
    private String baseType;
    private String defaultValue;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getCardinality() {
        return cardinality;
    }

    public void setCardinality(String cardinality) {
        this.cardinality = cardinality;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof ResponseVarViewDto))
            return false;

        ResponseVarViewDto that = (ResponseVarViewDto) o;

        if (getIdentifier() != null ? !getIdentifier().equals(that.getIdentifier()) : that.getIdentifier() != null)
            return false;
        if (getCardinality() != null ? !getCardinality().equals(that.getCardinality()) : that.getCardinality() != null)
            return false;
        if (getBaseType() != null ? !getBaseType().equals(that.getBaseType()) : that.getBaseType() != null)
            return false;
        return !(getDefaultValue() != null ? !getDefaultValue().equals(that.getDefaultValue())
                : that.getDefaultValue() != null);

    }

    @Override
    public int hashCode() {
        int result = getIdentifier() != null ? getIdentifier().hashCode() : 0;
        result = 31 * result + (getCardinality() != null ? getCardinality().hashCode() : 0);
        result = 31 * result + (getBaseType() != null ? getBaseType().hashCode() : 0);
        result = 31 * result + (getDefaultValue() != null ? getDefaultValue().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ResponseVarViewDto{" + "identifier='" + identifier + '\'' + ", cardinality='" + cardinality + '\''
                + ", baseType='" + baseType + '\'' + ", defaultValue='" + defaultValue + '\'' + '}';
    }
}
