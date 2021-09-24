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

import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.model.competency.CompetencyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jasonbrown on 6/17/16.
 */
@Component(value = "competencyMapDtoAssembler")
public class CompetencyMapDtoAssemblerImpl implements CompetencyMapDtoAssembler {

    @Autowired
    CompetencyRefDtoAssembler competencyRefDtoAssembler;
    
    @Override
    public CompetencyMapDto assembleDto(CompetencyMap competencyMap) {
        if(competencyMap == null){
            return null;
        }

        CompetencyMapDto competencyMapDto = new CompetencyMapDto();
        competencyMapDto.setTitle(competencyMap.getTitle());
        competencyMapDto.setId(competencyMap.getId());
        competencyMapDto.setIdentifier(competencyMap.getIdentifier());
        competencyMapDto.setVersion(competencyMap.getVersion());
        competencyMapDto.setPublished(competencyMap.isPublished());
        competencyMapDto.setDiscipline(competencyMap.getDiscipline());
        competencyMap.getCompetencyRefs().forEach(cr -> cr.setDiscipline(competencyMap.getDiscipline()));
        competencyMapDto.setCompetencyRefs(competencyRefDtoAssembler.assembleDto(competencyMap.getCompetencyRefs()));
        return competencyMapDto;
    }

    @Override
    public CompetencyMap disassembleDto(CompetencyMapDto competencyMapDto) {
        if(competencyMapDto == null){
            return null;
        }

        CompetencyMap competencyMap = new CompetencyMap();
        competencyMap.setTitle(competencyMapDto.getTitle());
        competencyMap.setId(competencyMapDto.getId());
        competencyMap.setIdentifier(competencyMapDto.getIdentifier());
        competencyMap.setVersion(competencyMapDto.getVersion());
        competencyMap.setPublished(competencyMapDto.isPublished());
        competencyMap.setDiscipline(competencyMapDto.getDiscipline());
        competencyMapDto.getCompetencyRefs().forEach(cr -> cr.setDiscipline(competencyMap.getDiscipline()));
        competencyMap.setCompetencyRefs(competencyRefDtoAssembler.disassembleDto(competencyMapDto.getCompetencyRefs()));
        return competencyMap;
    }
}
