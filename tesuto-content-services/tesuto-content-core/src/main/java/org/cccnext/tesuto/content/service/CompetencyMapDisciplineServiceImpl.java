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
package org.cccnext.tesuto.content.service;

import org.cccnext.tesuto.content.assembler.competency.CompetencyMapDisciplineDtoAssembler;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDisciplineDto;
import org.cccnext.tesuto.content.model.competency.CompetencyMapDiscipline;
import org.cccnext.tesuto.content.repository.jpa.CompetencyMapDisciplineRepository;
import org.cccnext.tesuto.content.service.CompetencyMapDisciplineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by jasonbrown on 7/7/16.
 */
@Service(value = "competencyMapDisciplineService")
public class CompetencyMapDisciplineServiceImpl implements CompetencyMapDisciplineService {

    @Autowired
    CompetencyMapDisciplineDtoAssembler competencyMapDisciplineDtoAssembler;

    @Autowired
    CompetencyMapDisciplineRepository competencyMapDisciplineRepository;

    @Override
    @Transactional
    public CompetencyMapDisciplineDto create(CompetencyMapDisciplineDto competencyMapDisciplineDto) {
        CompetencyMapDiscipline competencyMapDiscipline = competencyMapDisciplineDtoAssembler.disassembleDto(competencyMapDisciplineDto);
        CompetencyMapDiscipline savedCompetencyMapDiscipline = competencyMapDisciplineRepository.save(competencyMapDiscipline);
        return competencyMapDisciplineDtoAssembler.assembleDto(savedCompetencyMapDiscipline);
    }

    @Override
    @Transactional
    public List<CompetencyMapDisciplineDto> create(List<CompetencyMapDisciplineDto> competencyMapDisciplineDtos) {
        List<CompetencyMapDiscipline> competencyMapDisciplineList = competencyMapDisciplineDtoAssembler.disassembleDto(competencyMapDisciplineDtos);
        Iterable<CompetencyMapDiscipline> savedCompetencyMapDisciplines = competencyMapDisciplineRepository.saveAll(competencyMapDisciplineList);
        return competencyMapDisciplineDtoAssembler.assembleDto(savedCompetencyMapDisciplines);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompetencyMapDisciplineDto> read() {
        Iterable<CompetencyMapDiscipline> competencyMapDisciplineList = competencyMapDisciplineRepository.findAll();
        return competencyMapDisciplineDtoAssembler.assembleDto(competencyMapDisciplineList);
    }

    @Override
    @Transactional(readOnly = true)
    public CompetencyMapDisciplineDto read(String disciplineName) {
        CompetencyMapDiscipline competencyMapDiscipline= competencyMapDisciplineRepository.findByDisciplineName(disciplineName);
        if(competencyMapDiscipline == null){
            return null;
        }
        return competencyMapDisciplineDtoAssembler.assembleDto(competencyMapDiscipline);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CompetencyMapDisciplineDto readById(String id) {
        CompetencyMapDiscipline competencyMapDiscipline= competencyMapDisciplineRepository.findById(id).get();
        return competencyMapDisciplineDtoAssembler.assembleDto(competencyMapDiscipline);
    }

    @Override
    @Transactional
    public void delete(CompetencyMapDisciplineDto competencyMapDisciplineDto) {
        CompetencyMapDiscipline competencyMapDiscipline = competencyMapDisciplineDtoAssembler.disassembleDto(competencyMapDisciplineDto);
        competencyMapDisciplineRepository.delete(competencyMapDiscipline);
    }

    @Override
    @Transactional
    public void delete(List<CompetencyMapDisciplineDto> competencyMapDisciplineDtos) {
        List<CompetencyMapDiscipline> competencyMapDisciplineList = competencyMapDisciplineDtoAssembler.disassembleDto(competencyMapDisciplineDtos);
        competencyMapDisciplineRepository.deleteAll(competencyMapDisciplineList);
    }
}
