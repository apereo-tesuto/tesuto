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
import org.cccnext.tesuto.placement.model.VersionedSubjectArea;
import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by bruce on 7/20/16.
 */
@Service
public class VersionedSubjectAreaAssembler extends AbstractAssembler<VersionedSubjectAreaViewDto, VersionedSubjectArea> {

  private ObjectMapper mapper = new ObjectMapper();

  @Autowired Mapper dozer;

	@Override
	protected VersionedSubjectAreaViewDto doAssemble(VersionedSubjectArea subjectArea) {
	  try {
      return mapper.readValue(subjectArea.getJson(), VersionedSubjectAreaViewDto.class);
    } catch (Exception e) {
	    throw new RuntimeException(e);
    }
	}

	@Override
	protected VersionedSubjectArea doDisassemble(VersionedSubjectAreaViewDto dto) {
		VersionedSubjectArea entity = dozer.map(dto, VersionedSubjectArea.class);
		try {
      entity.setJson(mapper.writeValueAsString(dto));
    } catch (Exception e) {
		  throw new RuntimeException(e);
    }
		return entity;
	}

}
