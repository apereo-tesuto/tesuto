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

import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.service.CompetencyMapService;
import org.springframework.stereotype.Service;

@Service
public class CompetencyMapServiceStub implements CompetencyMapService {

    @Override
    public CompetencyMapDto create(CompetencyMapDto competencyMapDto) {
        return null;
    }

    @Override
    public List<CompetencyMapDto> create(List<CompetencyMapDto> competencyMapDtos) {
        return null;
    }

    @Override
    public Boolean setPublishFlag(String discipline, String identifier, int version, boolean isPublished) {
        return null;
    }


    @Override
    public void delete(String identifier) {

    }

    @Override
    public CompetencyMapDto readById(String identifier) {
        CompetencyMapDto competencyMapDto = new CompetencyMapDto();
        competencyMapDto.setVersion(1);
        competencyMapDto.setId("identifier");
        competencyMapDto.setPublished(true);
        return competencyMapDto;
    }

    @Override
    public CompetencyMapDto readLatestPublishedVersion(String  discipline) {
        return readById(discipline);
    }

    @Override
    public List<CompetencyMapDto> read() {
        return null;
    }

    @Override
    public List<CompetencyMapDto> readPublishedUnique() {
        return null;
    }

    @Override
    public int getNextVersion(String discipline) {
        return 2;
    }

    @Override
    public List<CompetencyMapDto> read(String discipline) {
        return null;
    }

    @Override
    public CompetencyMapDto readByVersion(String discipline, Integer version) {
        return readById(discipline);
    }

	@Override
	public CompetencyMapDto readByTitleAndVersion(String title, Integer version) {
		return  readById(title);
	}

}

