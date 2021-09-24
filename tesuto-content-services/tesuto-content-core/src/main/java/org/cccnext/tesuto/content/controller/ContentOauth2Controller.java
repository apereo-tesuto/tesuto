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

import javax.servlet.http.HttpServletRequest;

import org.cccnext.tesuto.content.assembler.competency.CompetencyAssemblyService;
import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDisciplineDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.service.CompetencyMapDisciplineService;
import org.cccnext.tesuto.content.service.CompetencyMapOrderService;
import org.cccnext.tesuto.content.service.CompetencyMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Service
public class ContentOauth2Controller {

    @Autowired
    @Qualifier("competencyMapOrderService")
    CompetencyMapOrderService competencyMapOrderedService;
    
    @Autowired
    CompetencyMapDisciplineService competencyMapDisciplineService;
    
    @Autowired
    CompetencyMapService competencyMapService;
    
    @Autowired
    CompetencyAssemblyService competencyAssemblyService;

    
    public 
    List<CompetencyDifficultyDto> getOrderedCompetencies(HttpServletRequest request, String orderedCompetencyMapId) throws IllegalAccessException {
        List<CompetencyDifficultyDto> competencies = competencyMapOrderedService.getOrderedCompetencies(orderedCompetencyMapId);
        return competencies;
    }

    public ResponseEntity
     getCurrentCompetencyMapOrderId(HttpServletRequest request, String competencyDisciplineName) throws IllegalAccessException {
        return new ResponseEntity<String>(competencyMapOrderedService.findLatestPublishedIdByCompetencyMapDiscipline(competencyDisciplineName), HttpStatus.OK);
    }

    public ResponseEntity getLatestPublishedCompetencyMapOauth(HttpServletRequest request, String discipline) throws IllegalAccessException {
        CompetencyMapDto competencyMapDto = competencyMapService.readLatestPublishedVersion(discipline);
        return new ResponseEntity<CompetencyMapDto>(competencyMapDto, HttpStatus.OK);
    }

    public ResponseEntity getCompetencyMapDiscipline(String competencyMapDiscipline) {
        CompetencyMapDisciplineDto competencyMapDisciplineDto = competencyMapDisciplineService.read(competencyMapDiscipline);
        return new ResponseEntity<CompetencyMapDisciplineDto>(competencyMapDisciplineDto, HttpStatus.OK);
    }
    
    public ResponseEntity getCompetencyMapDisciplines() {
        return new ResponseEntity<List<CompetencyMapDisciplineDto>>(competencyMapDisciplineService.read(), HttpStatus.OK);
    }
}
