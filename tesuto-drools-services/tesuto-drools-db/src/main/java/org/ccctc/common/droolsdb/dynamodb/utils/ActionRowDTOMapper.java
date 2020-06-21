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

import org.ccctc.common.droolsdb.dynamodb.model.ActionRow;
import org.ccctc.common.droolsdb.model.ActionRowDTO;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

public class ActionRowDTOMapper extends AbstractMapper<ActionRowDTO, ActionRow>{
	@Autowired 
	Mapper mapper;
	
	@Autowired 
	ActionParameterDTOMapper parameterMapper;
	
	@Override
	protected ActionRowDTO doMapTo(ActionRow entity) {
		ActionRowDTO dto = mapper.map(entity, ActionRowDTO.class);
		dto.setParameters(parameterMapper.mapTo(entity.getParameters()));
		return dto;
	}

	@Override
	protected ActionRow doMapFrom(ActionRowDTO dto) {
		ActionRow entity =  mapper.map(dto, ActionRow.class);
		entity.setParameters(parameterMapper.mapFrom(dto.getParameters()));
		return entity;
	}

}
