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

import org.springframework.beans.factory.annotation.Autowired;
import org.ccctc.common.droolscommon.model.RuleSetRowDTO;
import org.ccctc.common.droolsdb.dynamodb.model.RuleSetRow;
import org.dozer.Mapper;


public class RuleSetRowDTOMapper extends AbstractMapper<RuleSetRowDTO, RuleSetRow> {

	@Autowired 
	Mapper mapper;
	
	@Autowired
	RuleVariableRowDTOMapper variableRowMapper;
	   
	@Override
	protected RuleSetRowDTO doMapTo(RuleSetRow from) {
		RuleSetRowDTO to = mapper.map(from, RuleSetRowDTO.class);
		to.setVariableRows(variableRowMapper.mapTo(from.getVariableRows()));
		return to;
	}

	@Override
	protected RuleSetRow doMapFrom(RuleSetRowDTO to) {
		RuleSetRow from = mapper.map(to, RuleSetRow.class);
		from.setVariableRows(variableRowMapper.mapFrom(to.getVariableRows()));
		return from;
	}
}
