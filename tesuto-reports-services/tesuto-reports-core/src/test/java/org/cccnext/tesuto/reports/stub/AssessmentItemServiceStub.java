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
package org.cccnext.tesuto.reports.stub;

import java.util.List;

import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.service.AssessmentItemService;

public class AssessmentItemServiceStub implements AssessmentItemService {

	@Override
	public AssessmentItemDto read(String id) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public AssessmentItemDto readLatestPublishedVersion(String namespace, String identifier) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public List<AssessmentItemDto> getItemsByCompetency(String mapDiscipline, String competencyIdentifier) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public List<AssessmentItemDto> getItemsByCompetencyMapDiscipline(String competencyMapDiscipline,
			List<String> fields) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public List<AssessmentItemDto> getAllVersions(String namespace, String identifier) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public AssessmentItemDto create(AssessmentItemDto assessmentItem) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public List<AssessmentItemDto> create(List<AssessmentItemDto> assessmentItem) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public AssessmentItemDto readLatestVersion(String namespace, String itemId) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public List<AssessmentItemDto> readAllRevisions(String namespace, String identifier) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public List<AssessmentItemDto> read() {
		//Auto-generated method stub
		return null;
	}

	@Override
	public int getNextVersion(String namespace, String identifier) {
		//Auto-generated method stub
		return 0;
	}

	@Override
	public Boolean setPublishFlag(String identifier, String namespace, int version, boolean isPublished) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public void delete(String id) {
		//Auto-generated method stub

	}

}
