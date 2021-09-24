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

import org.cccnext.tesuto.content.dto.competency.CompetencyMapDisciplineDto;
import org.cccnext.tesuto.content.model.competency.CompetencyMapDiscipline;
import org.springframework.stereotype.Service;

/**
 * Created by jasonbrown on 7/7/16.
 */
@Service(value = "competencyMapDisciplineDtoAssembler")
public class CompetencyMapDisciplineDtoAssemblerImpl implements CompetencyMapDisciplineDtoAssembler{
    @Override
    public CompetencyMapDisciplineDto assembleDto(CompetencyMapDiscipline competencyMapDiscipline) {
        if(competencyMapDiscipline == null){
            return null;
        }

        CompetencyMapDisciplineDto competencyMapDisciplineDto = new CompetencyMapDisciplineDto();
        competencyMapDisciplineDto.setId(competencyMapDiscipline.getId());
        competencyMapDisciplineDto.setDisciplineName(competencyMapDiscipline.getDisciplineName());

        return competencyMapDisciplineDto;
    }

    @Override
    public CompetencyMapDiscipline disassembleDto(CompetencyMapDisciplineDto competencyMapDisciplineDto) {
        if(competencyMapDisciplineDto == null){
            return null;
        }

        CompetencyMapDiscipline competencyMapDiscipline = new CompetencyMapDiscipline();
        competencyMapDiscipline.setId(competencyMapDisciplineDto.getId());
        competencyMapDiscipline.setDisciplineName(competencyMapDisciplineDto.getDisciplineName());

        return competencyMapDiscipline;
    }
}
