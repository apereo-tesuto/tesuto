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
package org.ccctc.common.droolsdb.dynamodb.utils;

import org.ccctc.common.droolsdb.dynamodb.model.Rule;
import org.ccctc.common.droolsdb.model.RuleDTO;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

public class RuleDTOMapper extends AbstractMapper<RuleDTO, Rule> {

	@Autowired
	Mapper mapper;

	@Autowired
	ActionRowDTOMapper actionMapper;

	@Autowired
	ConditionRowDTOMapper condtionMapper;

	@Override
	protected RuleDTO doMapTo(Rule rule) {
		RuleDTO ruleDTO = mapper.map(rule, RuleDTO.class);
		ruleDTO.setConditionRows(condtionMapper.mapTo(rule.getConditionRows()));
		ruleDTO.setActionRows(actionMapper.mapTo(rule.getActionRows()));
		return ruleDTO;
	}

	@Override
	protected Rule doMapFrom(RuleDTO ruleDTO) {
		Rule rule = mapper.map(ruleDTO, Rule.class);
		rule.setConditionRows(condtionMapper.mapFrom(ruleDTO.getConditionRows()));
		rule.setActionRows(actionMapper.mapFrom(ruleDTO.getActionRows()));
		return rule;
	}
}
