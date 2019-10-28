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
package org.cccnext.tesuto.content.assembler;

import org.cccnext.tesuto.content.viewdto.AssessmentViewDto;
import org.cccnext.tesuto.domain.assembler.DtoAssembler;
import org.cccnext.tesuto.domain.dto.ScopedIdentifierDto;
import org.springframework.stereotype.Service;

@Service
public class ScopedIdentifierDtoAssemblerImpl implements DtoAssembler<ScopedIdentifierDto, AssessmentViewDto> {

	@Override
	public ScopedIdentifierDto assembleDto(AssessmentViewDto entity) {
		ScopedIdentifierDto scopedIdentifierDto = new ScopedIdentifierDto();
		scopedIdentifierDto.setIdentifier(entity.getIdentifier());
		scopedIdentifierDto.setNamespace(entity.getNamespace());
		return scopedIdentifierDto;
	}

	@Override
	public AssessmentViewDto disassembleDto(ScopedIdentifierDto dto) {
		throw new RuntimeException("Please reevaluate your use of this method.");
	}

}
