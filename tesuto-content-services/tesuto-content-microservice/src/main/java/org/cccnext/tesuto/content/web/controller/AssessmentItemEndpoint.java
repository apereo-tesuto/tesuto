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
package org.cccnext.tesuto.content.web.controller;

import java.util.List;

import org.cccnext.tesuto.content.controller.AssessmentItemController;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/service/v1/assessment-items")
public class AssessmentItemEndpoint {

	@Autowired
	AssessmentItemController controller;
	
    /**
     * PUT service/v1/assessments/publish-assessmentItem?namespace=DEVELOPER&
     * identifier=match&version=1&publish=true
	 *
     * Don't forget to add the CSRF token when using a rest client: X-CSRF-TOKEN: fab163e9-23e2-4115-8f0b-48ad26b76175
     * This is an example value.  The real one can be obtained from the page source after doing a valid login.
     */
    @PreAuthorize("hasAuthority('PUBLISH_ASSESSMENT_ITEM')")
    @RequestMapping(value = "publish", method = RequestMethod.PUT)
    public @ResponseBody boolean publishAssessmentItem(@RequestParam String namespace, @RequestParam String identifier,
            @RequestParam int version, @RequestParam boolean publish) {
        return controller.publishAssessmentItem(namespace, identifier, version, publish);
    }
    
    /**
     * PUT service/v1/assessments/publish-assessmentItem?namespace=DEVELOPER&
     * identifier=match&version=1&publish=true
	 *
     * Don't forget to add the CSRF token when using a rest client: X-CSRF-TOKEN: fab163e9-23e2-4115-8f0b-48ad26b76175
     * This is an example value.  The real one can be obtained from the page source after doing a valid login.
     */
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value = "oauth2/publish", method = RequestMethod.PUT)
    public @ResponseBody boolean publishOauthAssessmentItem(@RequestParam String namespace, @RequestParam String identifier,
            @RequestParam int version, @RequestParam boolean publish) {
        return controller.publishAssessmentItem(namespace, identifier, version, publish);
    }
    
    /**
     * Production support to examine Assessment content.
	 *
     * @param namespace
     * @param identifier
     * @return
     */
    @PreAuthorize("hasAnyAuthority('API')")
    @RequestMapping(value = "all/{namespace}/{identifier}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<AssessmentItemDto> readAssessmentItemByNamespaceAndIdentifier(@PathVariable("namespace") String namespace,
    		@PathVariable("identifier") String identifier) {
    	return controller.readAllAssessmentItemByNamespaceAndIdentifier(namespace, identifier);
    }
    
    @PreAuthorize("hasAnyAuthority('API')")
    @RequestMapping(value = "{namespace}/{identifier}/published", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody AssessmentItemDto readLatestPublishedVersion(@PathVariable("namespace") String namespace,
    		@PathVariable("identifier") String identifier) {
    	return controller.readLatestPublishedVersion(namespace, identifier);
    }
    
    @PreAuthorize("hasAnyAuthority('VIEW_ASSESSMENT_ITEM', 'API')")
    @RequestMapping(value = "{assessment-item-id}", method = RequestMethod.GET)
    public @ResponseBody AssessmentItemDto read(@PathVariable("assessment-item-id") String assementItemId) {
        return controller.read(assementItemId);
    }


    @PreAuthorize("hasAuthority('VIEW_ASSESSMENT_ITEM_REVISIONS')")
    @RequestMapping(value = "{namespace}/{identifier}/revisions", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<AssessmentItemDto> readAssessmentItemRevisionsByNamespaceAndIdentifier(@PathVariable("namespace") String namespace,
    		@PathVariable("identifier") String identifier) {
        return controller.readAssessmentItemRevisionsByNamespaceAndIdentifier(namespace, identifier);
    }
    
    @PreAuthorize("hasAnyAuthority('VIEW_ASSESSMENT_ITEM', 'API')")
    @RequestMapping(value = "competency-map-discipline/{competency-map-discipline}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<AssessmentItemDto> getItemsByCompetencyMapDiscipline(@PathVariable("competency-map-discipline") String competencyMapDiscipline,
    		@RequestBody(required=false) List<String> fields) {
    	return controller.getItemsByCompetencyMapDiscipline(competencyMapDiscipline, fields);
    }
    
    
    @PreAuthorize("hasAnyAuthority('VIEW_ASSESSMENT_ITEM', 'API')")
    @RequestMapping(value = "competency-map-discipline/{competency-map-discipline}/{competency-identifier}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<AssessmentItemDto> getItemsByCompetency(@PathVariable("competency-map-discipline") String competencyMapDiscipline,
    		@PathVariable("competency-identifier") String competencyIdentifier) {
    	return controller.getItemsByCompetency(competencyMapDiscipline, competencyIdentifier);
    }
}
