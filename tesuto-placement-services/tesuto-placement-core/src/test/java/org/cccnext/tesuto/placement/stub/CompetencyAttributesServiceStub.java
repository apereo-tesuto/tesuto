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
package org.cccnext.tesuto.placement.stub;

import java.util.HashMap;
import java.util.Map;

import org.cccnext.tesuto.placement.model.CompetencyAttributes;
import org.cccnext.tesuto.placement.service.CompetencyAttributesService;
import org.cccnext.tesuto.placement.view.CompetencyAttributesViewDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

public class CompetencyAttributesServiceStub implements CompetencyAttributesService {

	@Autowired
    Mapper mapper;
	Map<Integer, CompetencyAttributesViewDto> attributes = new HashMap<>();
	
	@Override
	public CompetencyAttributesViewDto get(Integer competencyAttributesId) {
		//Auto-generated method stub
		return attributes.get(competencyAttributesId);
	}

	@Override
	public CompetencyAttributesViewDto upsert(CompetencyAttributesViewDto competencyAttributes) {
		//Auto-generated method stub
		return attributes.put(competencyAttributes.getCompetencyAttributeId(),competencyAttributes);
	}

	@Override
	public void delete(int competencyAttributesId) {
		//Auto-generated method stub

	}

	@Override
	public CompetencyAttributes upsert(CompetencyAttributes competencyAttributes) {
		CompetencyAttributesViewDto dto = new CompetencyAttributesViewDto();
    	dto.setCompetencyCode(competencyAttributes.getCompetencyCode().name());
    	mapper.map(competencyAttributes, dto);
		attributes.put(competencyAttributes.getCompetencyAttributeId(),dto);
		return competencyAttributes;
	}

}
