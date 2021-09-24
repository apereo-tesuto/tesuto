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

import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.content.viewdto.AssessmentViewDto;

public interface AssessmentReader {

	AssessmentViewDto readViewDto(String namespace, String identifier);

	AssessmentDto read(String id);

	AssessmentDto readLatestPublishedVersion(String namespace, String identifier);

	AssessmentDto readVersion(ScopedIdentifier scopedIdentifer, int assessmentVersion);

	AssessmentDto readLatestPublishedVersion(ScopedIdentifier scopedIdentifier);

	List<AssessmentDto> read(ScopedIdentifier identifier);

	List<AssessmentDto> readByCompetencyMapDisicpline(String competencyMapDiscipline);

	List<AssessmentDto> readByCompetencyMapDisicplineOrPartialIdentifier(String competencyMapDiscipline,
			String partialIdentifier);
}
