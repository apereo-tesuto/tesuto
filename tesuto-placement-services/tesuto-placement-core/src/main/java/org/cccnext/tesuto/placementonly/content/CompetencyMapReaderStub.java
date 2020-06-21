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
package org.cccnext.tesuto.placementonly.content;

import org.apache.commons.lang3.NotImplementedException;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto;
import org.cccnext.tesuto.content.service.CompetencyMapReader;
import org.springframework.stereotype.Service;

//this is only to be used while there is not assessments. should be removed at at that time
@Service
public class CompetencyMapReaderStub implements CompetencyMapReader {

	@Override
	public CompetencyMapDto readLatestPublishedVersion(String competencyDisciplinName) {
		throw new NotImplementedException("Should not be needed when assessments are not used to place students");
	}

}
