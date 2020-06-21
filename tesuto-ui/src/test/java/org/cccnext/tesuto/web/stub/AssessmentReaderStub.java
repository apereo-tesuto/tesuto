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
package org.cccnext.tesuto.web.stub;

import java.util.List;

import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.content.service.AssessmentReader;
import org.cccnext.tesuto.content.viewdto.AssessmentViewDto;
import org.springframework.stereotype.Service;

@Service
public class AssessmentReaderStub implements AssessmentReader {

	@Override
	public AssessmentViewDto readViewDto(String namespace, String identifier) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public AssessmentDto read(String assessmentId) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public AssessmentDto readLatestPublishedVersion(String namespace, String identifier) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public AssessmentDto readVersion(ScopedIdentifier scopedIdentifer, int assessmentVersion) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public AssessmentDto readLatestPublishedVersion(ScopedIdentifier scopedIdentifier) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public List<AssessmentDto> read(ScopedIdentifier identifier) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public List<AssessmentDto> readByCompetencyMapDisicpline(String competencyMapDiscipline) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public List<AssessmentDto> readByCompetencyMapDisicplineOrPartialIdentifier(String competencyMapDiscipline,
			String partialIdentifier) {
		//Auto-generated method stub
		return null;
	}

}
