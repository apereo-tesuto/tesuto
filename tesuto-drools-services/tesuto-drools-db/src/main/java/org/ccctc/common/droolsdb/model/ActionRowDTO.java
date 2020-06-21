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
package org.ccctc.common.droolsdb.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ActionRowDTO {
    private String actionType;
    private List<ActionParameterDTO> parameters;
    
    public String getActionType() {
        return actionType;
    }
    public ActionRowDTO setActionType(String actionType) {
        this.actionType = actionType;
        return this;
    }
    public List<ActionParameterDTO> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList<ActionParameterDTO>();
        }
        return parameters;
    }
    public ActionRowDTO setParameters(List<ActionParameterDTO> parameters) {
        this.parameters = parameters;
        return this;
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
		return ToStringBuilder.reflectionToString(this);
	}
}
