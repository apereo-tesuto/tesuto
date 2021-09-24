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
package org.cccnext.tesuto.domain.multiplemeasures;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Set;

/**
 * Created by bruce on 4/5/17.
 */
public class Student {

    private String cccId;
    private String ssId;
    private Set<VariableSet> variableSets;

    public String getCccId() {
        return cccId;
    }

    public void setCccId(String cccId) {
        this.cccId = cccId;
    }

    public String getSsId() {
        return ssId;
    }

    public void setSsId(String ssId) {
        this.ssId = ssId;
    }

    public Set<VariableSet> getVariableSets() {
        return variableSets;
    }

    public void setVariableSets(Set<VariableSet> variableSets) {
        this.variableSets = variableSets;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this,o, "variableSets");
    }


    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "variableSets");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
