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

import org.cccnext.tesuto.placement.model.CompetencyAttributes;
import org.cccnext.tesuto.placement.repository.jpa.CompetencyAttributesRepository;
import org.cccnext.tesuto.placement.view.CompetencyAttributesViewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("competencyAttributesService")
public class CompetencyAttributesServiceImpl implements CompetencyAttributesService {
	
	@Autowired
	private CompetencyAttributesRepository competencyAttributesRepository;

	@Autowired
	CompetencyAttributesAssembler competencyAttributesAssembler;

	@Override
	public CompetencyAttributesViewDto get(Integer competencyAttributesId) {
		return competencyAttributesAssembler.assembleDto(competencyAttributesRepository.findById(competencyAttributesId).get());
	}

	@Override
	public CompetencyAttributesViewDto upsert(CompetencyAttributesViewDto competencyAttributes) {
		CompetencyAttributes attributes = competencyAttributesAssembler.disassembleDto(competencyAttributes);
		return competencyAttributesAssembler.assembleDto( competencyAttributesRepository.save(attributes));
	}
	
	@Override
	public CompetencyAttributes upsert(CompetencyAttributes competencyAttributes) {
		return competencyAttributesRepository.save(competencyAttributes);
	}

	@Override
	public void delete(int competencyAttributesId) {
		competencyAttributesRepository.deleteById( competencyAttributesId);
	}
}
