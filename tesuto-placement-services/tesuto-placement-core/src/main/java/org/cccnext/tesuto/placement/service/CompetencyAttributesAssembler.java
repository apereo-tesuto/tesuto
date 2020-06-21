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
import org.cccnext.tesuto.placement.view.CompetencyAttributesViewDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jayway.jsonpath.spi.mapper.MappingException;

/**
 * Created by tbehlau on 4/19/17.
 */
@Service("competencyAttributesAssembler")
public class CompetencyAttributesAssembler extends AbstractAssembler<CompetencyAttributesViewDto, CompetencyAttributes> {
    @Autowired
    Mapper mapper;

    @Override
    protected CompetencyAttributesViewDto doAssemble(CompetencyAttributes entity) {
    	CompetencyAttributesViewDto dto = new CompetencyAttributesViewDto();
    	dto.setCompetencyCode(entity.getCompetencyCode().name());
    	mapper.map(entity, dto);
    	return dto;
    }

    @Override
    protected CompetencyAttributes doDisassemble(CompetencyAttributesViewDto view) {
    	try {
	    	CompetencyAttributes attr = CompetencyAttributes.createInstance(view.getCompetencyCode());
	    	mapper.map(view, attr);
	    	return attr;
    	} catch (ReflectiveOperationException e)  {
    		throw new MappingException(e);
    	}
    }
}
