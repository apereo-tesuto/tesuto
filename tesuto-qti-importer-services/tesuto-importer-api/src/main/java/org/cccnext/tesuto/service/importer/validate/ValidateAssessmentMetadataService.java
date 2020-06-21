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
package org.cccnext.tesuto.service.importer.validate;

import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDisciplineDto;
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.service.importer.validate.ValidationMessage;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Jason Brown jbrown@unicon.net on 8/16/16.
 */
public interface ValidateAssessmentMetadataService {

    List<ValidationMessage> processMetadataMap(HashMap<String, AssessmentMetadataDto> metadataDtoMap, List<CompetencyMapDisciplineDto> competencyMapDisciplineDtos);
    List<ValidationMessage> validateMetadataMapKeysMatchAssessmentIdentifiers(HashMap<String, AssessmentMetadataDto> metadataDtoHashMap, List<AssessmentDto> assessmentDtos);
}
