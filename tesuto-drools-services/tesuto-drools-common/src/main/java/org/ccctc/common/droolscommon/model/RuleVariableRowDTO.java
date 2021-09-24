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
package org.ccctc.common.droolscommon.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RuleVariableRowDTO {
    private String name;
    
    private List<RuleVariableDTO> variables;

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof RuleVariableRowDTO))
            return false;

        RuleVariableRowDTO obj2 = (RuleVariableRowDTO) o;
        if (name != null && obj2.getName() != null) {
            if (!name.equals(obj2.getName())) {
                return false;
            }
        } else if (name != null || obj2.getName() != null) {
            return false;
        }
        if (variables == null && obj2.getVariables() != null) {
            return false;
        }
        if (variables != null && obj2.getVariables() == null) {
            return false;
        }
        if (variables == null && obj2.getVariables() == null) {
            return true;
        }
        Set<RuleVariableDTO> variables1 = new HashSet<RuleVariableDTO>(variables);
        Set<RuleVariableDTO> variables2 = new HashSet<RuleVariableDTO>(obj2.getVariables());
        if (!variables1.containsAll(variables2)) {
            return false;
        }
        return true;
    }

    public String getName() {
        return name;
    }

    public List<RuleVariableDTO> getVariables() {
        if (variables == null) {
            variables = new ArrayList<RuleVariableDTO>();
        }
        return variables;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVariables(List<RuleVariableDTO> variables) {
        this.variables = variables;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
