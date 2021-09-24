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
package org.cccnext.tesuto.preview.test;

import java.util.List;

import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.service.CompetencyMapService;

public class CompetencyMapServiceStub implements CompetencyMapService {

	@Override
	public CompetencyMapDto readLatestPublishedVersion(
			String competencyDisciplinName) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public CompetencyMapDto create(CompetencyMapDto competencyMapDto) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public List<CompetencyMapDto> create(
			List<CompetencyMapDto> competencyMapDtos) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public void delete(String identifier) {
		//Auto-generated method stub

	}

	@Override
	public CompetencyMapDto readById(String identifier) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public List<CompetencyMapDto> read() {
		//Auto-generated method stub
		return null;
	}

	@Override
	public List<CompetencyMapDto> readPublishedUnique() {
		//Auto-generated method stub
		return null;
	}

	@Override
	public int getNextVersion(String discipline) {
		//Auto-generated method stub
		return 0;
	}

	@Override
	public Boolean setPublishFlag(String discipline, String identifier,
			int version, boolean isPublished) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public List<CompetencyMapDto> read(String discipline) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public CompetencyMapDto readByVersion(String discipline, Integer version) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public CompetencyMapDto readByTitleAndVersion(String title, Integer version) {
		//Auto-generated method stub
		return null;
	}

}
