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

import org.ccctc.common.droolsdb.dynamodb.model.ActionParameter;
import org.ccctc.common.droolsdb.model.ActionParameterDTO;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

public class ActionParameterDTOMapper extends AbstractMapper<ActionParameterDTO, ActionParameter> {

	@Autowired
	Mapper mapper;
	
	@Override
	protected ActionParameterDTO doMapTo(ActionParameter entity) {
		return mapper.map(entity, ActionParameterDTO.class);
	}

	@Override
	protected ActionParameter doMapFrom(ActionParameterDTO dto) {
		return mapper.map(dto, ActionParameter.class);
	}

}
