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
package org.cccnext.tesuto.content.controller;

import java.util.List;

import org.cccnext.tesuto.content.dto.competency.CompetencyMapDisciplineDto;
import org.cccnext.tesuto.content.service.CompetencyMapDisciplineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Created by jasonbrown on 7/7/16.
 */
@Service
public class CompetencyMapDisciplineController {

    @Autowired
    CompetencyMapDisciplineService competencyMapDisciplineService;

    public ResponseEntity getAllCompetencyMapDisciplines() {
        List<CompetencyMapDisciplineDto> competencyMapDisciplineDtos = competencyMapDisciplineService.read();
        return new ResponseEntity<List<CompetencyMapDisciplineDto>>(competencyMapDisciplineDtos, HttpStatus.OK);
    }
    
    public ResponseEntity getCompetencyMapDiscipline(String competencyMapDiscipline) {
        CompetencyMapDisciplineDto competencyMapDisciplineDto = competencyMapDisciplineService.read(competencyMapDiscipline);
        return new ResponseEntity<CompetencyMapDisciplineDto>(competencyMapDisciplineDto, HttpStatus.OK);
    }

    public ResponseEntity createCompetencyMapDiscipline(String disciplineName) {
        CompetencyMapDisciplineDto competencyMapDisciplineDto = new CompetencyMapDisciplineDto();
        competencyMapDisciplineDto.setDisciplineName(disciplineName);

        CompetencyMapDisciplineDto competencyMapDisciplineDto1 = competencyMapDisciplineService.create(competencyMapDisciplineDto);
        return new ResponseEntity<CompetencyMapDisciplineDto>(competencyMapDisciplineDto1, HttpStatus.OK);
    }

    public ResponseEntity deleteCompetencyMapDiscipline(String disciplineName) {
        CompetencyMapDisciplineDto competencyMapDisciplineDto = competencyMapDisciplineService.read(disciplineName);
        if(competencyMapDisciplineDto != null) {
            competencyMapDisciplineService.delete(competencyMapDisciplineDto);
        }else{
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Void>(HttpStatus.ACCEPTED);
    }
}
