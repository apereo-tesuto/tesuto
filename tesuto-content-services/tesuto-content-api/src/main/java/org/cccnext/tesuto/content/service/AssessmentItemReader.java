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
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.shared.AssessmentOutcomeDeclarationDto;

public interface AssessmentItemReader {

    public AssessmentItemDto read(String id);
        
    default public AssessmentOutcomeDeclarationDto getOutcomeDeclaration(AssessmentItemDto assessmentItem, String outcomeIdentifier) {
        if(CollectionUtils.isEmpty(assessmentItem.getOutcomeDeclarationDtos())){
            return null;
        }
        Optional<AssessmentOutcomeDeclarationDto> result = assessmentItem.getOutcomeDeclarationDtos().stream()
                .filter(r -> r.getIdentifier().equals(outcomeIdentifier)).findFirst();
        return result.isPresent() ? result.get() : null;
    }
    public AssessmentItemDto readLatestPublishedVersion(String namespace, String identifier);
    public List<AssessmentItemDto> getItemsByCompetency(String mapDiscipline, String competencyIdentifier);
    public List<AssessmentItemDto> getItemsByCompetencyMapDiscipline(String competencyMapDiscipline, List<String> fields);

    public List<AssessmentItemDto> getAllVersions(String namespace, String identifier);

}
