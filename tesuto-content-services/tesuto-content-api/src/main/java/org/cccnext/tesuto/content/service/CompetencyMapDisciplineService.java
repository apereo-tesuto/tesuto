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

import java.util.List;

import org.cccnext.tesuto.content.dto.competency.CompetencyMapDisciplineDto;

/**
 * Created by jasonbrown on 7/7/16.
 */
public interface CompetencyMapDisciplineService extends CompetencyMapDisciplineReader {
    public CompetencyMapDisciplineDto create(CompetencyMapDisciplineDto competencyMapDisciplineDto);

    public List<CompetencyMapDisciplineDto> create(List<CompetencyMapDisciplineDto> competencyMapDisciplineDtos);

    public List<CompetencyMapDisciplineDto> read();

    public void delete(CompetencyMapDisciplineDto competencyMapDisciplineDto);

    public void delete(List<CompetencyMapDisciplineDto> competencyMapDisciplineDtos);

    CompetencyMapDisciplineDto readById(String id);
}
