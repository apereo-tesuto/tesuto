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
import org.cccnext.tesuto.placement.model.TesutoPlacementComponent;
import org.cccnext.tesuto.placement.view.TesutoPlacementComponentViewDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@Component
public class TesutoPlacementComponentViewDtoAssembler extends AbstractAssembler<TesutoPlacementComponentViewDto, TesutoPlacementComponent> {

	@Autowired
	Mapper mapper;

	@Override
	protected TesutoPlacementComponentViewDto doAssemble(TesutoPlacementComponent entity) {
		if (entity == null) {
			// Nothing to assemble.
			return null;
		}
		TesutoPlacementComponentViewDto viewDto = mapper.map(entity, TesutoPlacementComponentViewDto.class);
		viewDto.setEntityTargetClass(entity.getClass().getName());
		viewDto.setCollegeMisCode(entity.getCollegeId());
		viewDto.setAssessmentCompletedDate(entity.getAssessmentDate());
		return viewDto;
	}

	@Override
	protected TesutoPlacementComponent doDisassemble(TesutoPlacementComponentViewDto dto) {
		try {
			TesutoPlacementComponent entity = (TesutoPlacementComponent) Class.forName(dto.getEntityTargetClass()).newInstance();
			mapper.map(dto, entity);
			return entity;
		} catch (Exception ex) {
			return null;
		}
	}

}
