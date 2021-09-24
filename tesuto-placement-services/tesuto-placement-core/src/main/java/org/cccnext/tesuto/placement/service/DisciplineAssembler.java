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
import org.cccnext.tesuto.placement.model.CompetencyAttributes;
import org.cccnext.tesuto.placement.model.Discipline;
import org.cccnext.tesuto.placement.view.CompetencyAttributesViewDto;
import org.cccnext.tesuto.placement.view.DisciplineViewDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by bruce on 7/20/16.
 */
@Service
public class DisciplineAssembler extends AbstractAssembler<DisciplineViewDto, Discipline> {

	@Autowired
	Mapper mapper;

	@Autowired
	CompetencyAttributesAssembler attributeAssembler;
	
	@Autowired
	CompetencyAttributesService attributesService;

	@Override
	protected DisciplineViewDto doAssemble(Discipline discipline) {
		DisciplineViewDto dto = mapper.map(discipline, DisciplineViewDto.class);
		if(discipline.getCompetencyAttributes() != null) {
			int attributeId = discipline.getCompetencyAttributes().getCompetencyAttributeId();
			CompetencyAttributesViewDto attr =attributesService.get(attributeId);
			if(attr != null)
				dto.setCompetencyAttributes(attr);
			else
				dto.setCompetencyAttributes(attributeAssembler.assembleDto(discipline.getCompetencyAttributes()));
		}
		return dto;
	}

	@Override
	protected Discipline doDisassemble(DisciplineViewDto view) {
		Discipline entity = mapper.map(view, Discipline.class);
		entity.setCompetencyAttributes(attributeAssembler.disassembleDto(view.getCompetencyAttributes()));
		return entity;
	}

}
