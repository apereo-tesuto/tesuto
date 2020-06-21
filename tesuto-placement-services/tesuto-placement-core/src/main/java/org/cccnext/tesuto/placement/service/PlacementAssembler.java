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
package org.cccnext.tesuto.placement.service;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.domain.assembler.AbstractAssembler;
import org.cccnext.tesuto.placement.model.Placement;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by tbehlau on 5/1/1017.
 */
@Service
public class PlacementAssembler extends AbstractAssembler<PlacementViewDto, Placement> {

	@Autowired
	Mapper mapper;


	@Autowired
	PlacementComponentAssembler placementComponentAssembler;

	@Override
	protected PlacementViewDto doAssemble(Placement entity) {
		PlacementViewDto viewDto = mapper.map(entity, PlacementViewDto.class);
		viewDto.setPlacementComponents(placementComponentAssembler.assembleDto(entity.getPlacementComponents()));
		if(CollectionUtils.isNotEmpty(viewDto.getPlacementComponents())) {
		    Set<String> ids = viewDto.getPlacementComponents().stream().map(pc -> pc.getId()).collect(Collectors.toSet());
		    viewDto.setPlacementComponentIds(ids);
		}
		return viewDto;
	}

	@Override
	protected Placement doDisassemble(PlacementViewDto dto) {
		Placement placement = mapper.map(dto, Placement.class);
		placement.setPlacementComponents(placementComponentAssembler.disassembleDto(dto.getPlacementComponents()));
		return placement;
	}

}
