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
package org.cccnext.tesuto.content.assembler.competency;

import org.cccnext.tesuto.content.dto.competency.CompetencyRefDto;
import org.cccnext.tesuto.content.model.competency.CompetencyRef;
import org.cccnext.tesuto.content.service.CompetencyService;
import org.cccnext.tesuto.domain.assembler.AbstractAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="competencyRefDtoAssembler")
public class CompetencyRefDtoAssembler  extends AbstractAssembler<CompetencyRefDto, CompetencyRef> {

    @Autowired(required=false)
    CompetencyService competencyService;
    
    @Override
    protected CompetencyRefDto doAssemble(CompetencyRef entity) {
        CompetencyRefDto dto = new CompetencyRefDto();
        dto.setCompetencyIdentifier(entity.getCompetencyIdentifier());
        dto.setDiscipline(entity.getDiscipline());
        dto.setVersion(entity.getVersion());
        return dto;
    }

    @Override
    protected CompetencyRef doDisassemble(CompetencyRefDto dto) {
        CompetencyRef entity = new CompetencyRef();
        entity.setCompetencyIdentifier(dto.getCompetencyIdentifier());
        entity.setDiscipline(dto.getDiscipline());
        if(dto.getVersion() == 0) {
            int version = competencyService.getNextVersion(dto.getDiscipline(), dto.getCompetencyIdentifier());
            entity.setVersion(version);
        }else {
            entity.setVersion(dto.getVersion());
        }
        return entity;
    }

}
