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

import org.cccnext.tesuto.content.dto.competency.CompetencyDto;
import org.cccnext.tesuto.content.model.competency.Competency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jasonbrown on 6/17/16.
 */
@Component(value = "competencyDtoAssembler")
public class CompetencyDtoAssemblerImpl implements CompetencyDtoAssembler {

    @Autowired CompetencyRefDtoAssembler competencyRefDtoAssembler;

    @Override
    public CompetencyDto assembleDto(Competency competency) {
        if(competency == null){
            return null;
        }

        CompetencyDto competencyDto = new CompetencyDto();
        competencyDto.setVersion(competency.getVersion());
        competencyDto.setDescription(competency.getDescription());
        competencyDto.setId(competency.getId());
        competencyDto.setIdentifier(competency.getIdentifier());
        competencyDto.setDiscipline(competency.getDiscipline());
        competencyDto.setSampleItem(competency.getSampleItem());
        competencyDto.setStudentDescription(competency.getStudentDescription());
        competencyDto.setPublished(competency.isPublished());
        if(competency.getChildCompetencyRefs() != null) {
            competency.getChildCompetencyRefs().forEach(cr -> cr.setDiscipline(competency.getDiscipline()));
        }
        competencyDto.setChildCompetencyDtoRefs(competencyRefDtoAssembler.assembleDto(competency.getChildCompetencyRefs()));
        return competencyDto;
    }

    @Override
    public Competency disassembleDto(CompetencyDto competencyDto){
        if(competencyDto == null){
            return null;
        }

        Competency competency = new Competency();
        competency.setVersion(competencyDto.getVersion());
        competency.setDescription(competencyDto.getDescription());
        competency.setId(competencyDto.getId());
        competency.setIdentifier(competencyDto.getIdentifier());
        competency.setDiscipline(competencyDto.getDiscipline());
        competency.setSampleItem(competencyDto.getSampleItem());
        competency.setStudentDescription(competencyDto.getStudentDescription());
        competency.setPublished(competencyDto.isPublished());
        if(competencyDto.getChildCompetencyDtoRefs() != null) {
            competencyDto.getChildCompetencyDtoRefs().forEach(cr -> cr.setDiscipline(competencyDto.getDiscipline()));
        }
        competency.setChildCompetencyRefs(competencyRefDtoAssembler.disassembleDto(competencyDto.getChildCompetencyDtoRefs()));
        return competency;
    }
}
