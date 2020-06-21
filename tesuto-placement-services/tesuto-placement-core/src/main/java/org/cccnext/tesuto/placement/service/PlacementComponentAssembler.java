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

import org.cccnext.tesuto.domain.assembler.AbstractAssembler;
import org.cccnext.tesuto.placement.model.PlacementComponent;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by tbehlau on 6/21/1017.
 */
@Service
public class PlacementComponentAssembler extends AbstractAssembler<PlacementComponentViewDto, PlacementComponent> {

	@Autowired
	Mapper mapper;

	@Autowired
  VersionedSubjectAreaAssembler versionedSubjectAreaAssembler;

	@Override
	protected PlacementComponentViewDto doAssemble(PlacementComponent entity) {
		PlacementComponentViewDto viewDto = mapper.map(entity, PlacementComponentViewDto.class);
		viewDto.setEntityTargetClass(entity.getClass().getName());
		viewDto.setVersionedSubjectAreaViewDto(
				versionedSubjectAreaAssembler.assembleDto(entity.getVersionedSubjectArea())
		);
		return viewDto;
	}

	@Override
	protected PlacementComponent doDisassemble(PlacementComponentViewDto dto) {
		try {
			PlacementComponent entity = (PlacementComponent) Class.forName(dto.getEntityTargetClass()).newInstance();
			mapper.map(dto, entity);
			entity.setVersionedSubjectArea(
			  versionedSubjectAreaAssembler.disassembleDto(dto.getVersionedSubjectAreaViewDto())
      );
			return entity;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
