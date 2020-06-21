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
package org.cccnext.tesuto.content.controller;

import java.util.List;

import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.service.AssessmentItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author James Travis Stanley <jstanley@unicon.net>
 */
@Service
public class AssessmentItemController {

    @Autowired
    AssessmentItemService service;
 

    /**
     * PUT service/v1/assessments/publish-assessmentItem?namespace=DEVELOPER&
     * identifier=match&version=1&publish=true
	 *
     * Don't forget to add the CSRF token when using a rest client: X-CSRF-TOKEN: fab163e9-23e2-4115-8f0b-48ad26b76175
     * This is an example value.  The real one can be obtained from the page source after doing a valid login.
     */
    public  boolean publishAssessmentItem(String namespace, String identifier,
            int version, boolean publish) {
        return service.setPublishFlag(namespace, identifier, version, publish);
    }
    
    public  AssessmentItemDto read(String assessmentItemId) {
        return service.read(assessmentItemId);
    }

    /**
     * Production support to examine Assessment content.
	 *
     * @param namespace
     * @param identifier
     * @return
     */
    public AssessmentItemDto readAssessmentItemByNamespaceAndIdentifier(String namespace,
            String identifier) {
        AssessmentItemDto assessmentItemDto = service.readLatestVersion(namespace, identifier);
        return assessmentItemDto;
    }
    
    public List<AssessmentItemDto> readAllAssessmentItemByNamespaceAndIdentifier(String namespace,
            String identifier) {
        return service.getAllVersions(namespace, identifier);
    }

    public List<AssessmentItemDto> readAssessmentItemRevisionsByNamespaceAndIdentifier(String namespace, String identifier) {
        List<AssessmentItemDto> assessmentItemDtoList = service.readAllRevisions(namespace, identifier);
        return assessmentItemDtoList;
    }
    
    public List<AssessmentItemDto> getItemsByCompetency(String mapDiscipline,
			String competencyIdentifier) {
    	return service.getItemsByCompetency(mapDiscipline, competencyIdentifier);
    }
    
    public List<AssessmentItemDto> getItemsByCompetencyMapDiscipline(
			String competencyMapDiscipline, List<String> fields) {
    	return service.getItemsByCompetencyMapDiscipline(competencyMapDiscipline, fields);
    }
    
    public AssessmentItemDto readLatestPublishedVersion(String namespace,
            String identifier) {
        AssessmentItemDto assessmentItemDto = service.readLatestPublishedVersion(namespace, identifier);
        return assessmentItemDto;
    }
}
