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

import org.cccnext.tesuto.content.dto.competency.CompetencyDto;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.content.service.CompetencyService;

public class CompetencyServiceStub implements CompetencyService {

	@Override
	public List<CompetencyDto> create(List<CompetencyDto> competencyMapDtos) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public void delete(String id) {
		//Auto-generated method stub

	}

	@Override
	public CompetencyDto read(String id) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public CompetencyDto readLatestPublishedVersion(ScopedIdentifier identifier) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public CompetencyDto readByDisciplineIdentifierAndVersion(
			String discipline, String identifier, int version) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public List<CompetencyDto> read() {
		//Auto-generated method stub
		return null;
	}

	@Override
	public List<CompetencyDto> readPublishedUnique() {
		//Auto-generated method stub
		return null;
	}

	@Override
	public int getNextVersion(String namespace, String identifier) {
		//Auto-generated method stub
		return 0;
	}

	@Override
	public Boolean setPublishFlag(String identifier, String namespace,
			int version, boolean isPublished) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public List<CompetencyDto> read(ScopedIdentifier identifier) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public List<CompetencyDto> readAll(List<String> ids) {
		//Auto-generated method stub
		return null;
	}

}
