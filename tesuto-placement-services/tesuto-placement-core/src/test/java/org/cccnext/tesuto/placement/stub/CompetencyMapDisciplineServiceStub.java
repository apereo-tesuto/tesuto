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
package org.cccnext.tesuto.placement.stub;


import java.util.List;

import org.cccnext.tesuto.content.dto.competency.CompetencyMapDisciplineDto;
import org.cccnext.tesuto.content.service.CompetencyMapDisciplineService;
import org.springframework.stereotype.Service;
@Service
public class CompetencyMapDisciplineServiceStub implements CompetencyMapDisciplineService {

    @Override
    public CompetencyMapDisciplineDto create(CompetencyMapDisciplineDto competencyMapDisciplineDto) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public List<CompetencyMapDisciplineDto> create(List<CompetencyMapDisciplineDto> competencyMapDisciplineDtos) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public List<CompetencyMapDisciplineDto> read() {
        //Auto-generated method stub
        return null;
    }

    @Override
    public CompetencyMapDisciplineDto read(String disciplineName) {
        CompetencyMapDisciplineDto dto = new CompetencyMapDisciplineDto();
        dto.setDisciplineName(disciplineName);
        return dto;
    }

    @Override
    public void delete(CompetencyMapDisciplineDto competencyMapDisciplineDto) {
        //Auto-generated method stub

    }

    @Override
    public void delete(List<CompetencyMapDisciplineDto> competencyMapDisciplineDtos) {
        //Auto-generated method stub

    }

    @Override
    public CompetencyMapDisciplineDto readById(String id) {
        //Auto-generated method stub
        return null;
    }

}
