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
package org.ccctc.common.droolsdb.form;

import org.apache.commons.lang3.StringUtils;
import org.ccctc.common.droolscommon.model.RuleAttributesDTO;

public class RuleAttributeFacetSearchForm extends RuleAttributesDTO {

	private static final long serialVersionUID = 1L;
	 private String ruleId;
	

	public String getRuleId() {
		return ruleId;
	}


	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}


	public void clean() {
		 setEngine(StringUtils.isBlank(getEngine()) ? null : getEngine());
		setFamily(StringUtils.isBlank(getFamily()) ? null : getFamily());
		setCompetencyMapDiscipline(StringUtils.isBlank(getCompetencyMapDiscipline()) ? null : getCompetencyMapDiscipline());
		setCategory(StringUtils.isBlank(getCategory()) ? null : getCategory());
		setId(StringUtils.isBlank(getId()) ? null : getId());
		setStatus(StringUtils.isBlank(getStatus()) ? null : getStatus());
		this.setDescription(StringUtils.isBlank(getDescription()) ? null : getDescription());
		this.setEvent(StringUtils.isBlank(getEvent()) ? null : getEvent());
		this.setTitle(StringUtils.isBlank(getTitle()) ? null : getTitle());
		this.setVersion(StringUtils.isBlank(getVersion()) ? null : getVersion());
		this.setRuleId(StringUtils.isBlank(getRuleId()) ? null : getRuleId());
	}
}
