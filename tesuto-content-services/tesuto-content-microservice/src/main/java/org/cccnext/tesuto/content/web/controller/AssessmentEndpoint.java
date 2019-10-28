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

import org.cccnext.tesuto.content.controller.AssessmentController;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.viewdto.AssessmentViewDto;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author James Travis Stanley <jstanley@unicon.net>
 */
@Controller
@RequestMapping(value = "/service/v1/assessments")
public class AssessmentEndpoint extends BaseController {

	@Autowired
	AssessmentController controller;
	
    @PreAuthorize("hasAnyAuthority('VIEW_ASSESSMENTS', 'API')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<AssessmentViewDto>> read() {
        return controller.read();
    }
    
    @PreAuthorize("hasAnyAuthority('VIEW_ASSESSMENTS', 'API')")
    @RequestMapping(value= "{assessment-id}", method = RequestMethod.GET)
    public ResponseEntity<AssessmentDto> read(@PathVariable("assessment-id") String assessmentId) {
        return controller.read(assessmentId);
    }


    @PreAuthorize("hasAuthority('VIEW_ASSESSMENTS_BY_LOCATION')")
    //Removing endpoint for now as not need currently but will most likely in future.
    @RequestMapping(value = "location/{locationId}", method = RequestMethod.GET)
    public ResponseEntity<List<AssessmentViewDto>> readAllowedAssessments(@PathVariable String locationId) {
        return controller.readAllowedAssessments(getCurrentUserId(), locationId);
    }

    /**
     * PUT
     * service/v1/assessments/publish-assessment?namespace=DEVELOPER&identifier=
     * a200000000002&version=1&publish=true
     *
     * Don't forget to add the CSRF token when using a rest client: X-CSRF-TOKEN: fab163e9-23e2-4115-8f0b-48ad26b76175
     * This is an example value.  The real one can be obtained from the page source after doing a valid login.
     */
    @PreAuthorize("hasAuthority('PUBLISH_ASSESSMENT')")
    @RequestMapping(value = "publish-assessment", method = RequestMethod.PUT)
    public @ResponseBody boolean publishAssessment(@RequestParam String namespace, @RequestParam String identifier,
            @RequestParam int version, @RequestParam boolean publish) {
        return controller.publishAssessment(namespace, identifier, version, publish);
    }



    /**
     * Production support to examine Assessment content.
     *
     * @param namespace
     * @param identifier
     * @return
     */
    @PreAuthorize("hasAnyAuthority('VIEW_ASSESSMENT', 'API')")
    @RequestMapping(value = "view/latest/{namespace}/{identifier}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody AssessmentDto readAssessmentByNamespaceAndIdentifier(@PathVariable("namespace") String namespace,
    		@PathVariable("identifier") String identifier) {
        return controller.readAssessmentByNamespaceAndIdentifier(namespace, identifier);
    }

    /**
     * Returns only the version numbers of an Assessment.
     *
     * @param namespace
     * @param assessmentIdentifier
     * @return all version numbers of an assessment
     */
    @PreAuthorize("hasAuthority('VIEW_ASSESSMENT_VERSIONS')")
    @RequestMapping(value = "{namespace}/{identifier}/versions", method = RequestMethod.GET)
    public ResponseEntity<List<Integer>> readAssessmentVersionsByNamespaceAndIdentifier(@PathVariable("namespace") String namespace,
                                                                                        @PathVariable("identifier") String identifier) {
        return controller.readAssessmentVersionsByNamespaceAndIdentifier(namespace, identifier);
    }
    
    /**
     * Returns only the version numbers of an Assessment.
     *
     * @param namespace
     * @param assessmentIdentifier
     * @return all version numbers of an assessment
     */
    @PreAuthorize("hasAnyAuthority('VIEW_ASSESSMENT_VERSIONS', 'API')")
    @RequestMapping(value = "{namespace}/{identifier}/{version}", method = RequestMethod.GET)
    public ResponseEntity<AssessmentDto> readAssessmentByVersion(@PathVariable("namespace") String namespace,
                                                                                        @PathVariable("identifier") String identifier,
                                                                                        @PathVariable("version") Integer version) {
        return controller.readAssessmentByVersion(namespace, identifier, version);
    }

    @PreAuthorize("hasAuthority('VIEW_ASSESSMENT_REVISIONS')")
    @RequestMapping(value = "view/assessment-revisions/", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<AssessmentDto> readAssessmentRevisionsByNamespaceAndIdentifier(@RequestParam String namespace, @RequestParam String identifier) {
        return controller.readAssessmentRevisionsByNamespaceAndIdentifier(namespace, identifier);
    }
    
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value = "competency-map-discipline/{competency-map-discipline}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<AssessmentDto> readByCompetencyMapDiscipline(@PathVariable("competency-map-discipline") String competencyMapDiscipline) {
        return controller.readByCompetencyMapDisicpline(competencyMapDiscipline);
    }
    
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value = "competency-map-discipline/{competency-map-discipline}/partial-identifier/{partial-identifier}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<AssessmentDto> readByCompetencyMapDisciplineAndParialIdentifider(@PathVariable("competency-map-discipline") String competencyMapDiscipline,
    		@PathVariable("partial-identifier") String partialIdentifier) {
        return controller.readByCompetencyMapDisicplineOrPartialIdentifier(competencyMapDiscipline, partialIdentifier);
    }
    
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value = "{namespace}/{identifier}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody List<AssessmentDto> readAssessmentsByNamespaceAndIdentifier(@PathVariable("namespace") String namespace, @PathVariable("identifier") String identifier) {
        return controller.readAssessmentRevisionsByNamespaceAndIdentifier(namespace, identifier);
    }
    
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value = "viewdto/{namespace}/{identifier}/published", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody AssessmentViewDto readPublishedAssessmentViewDto(@PathVariable("namespace") String namespace,
    		@PathVariable("identifier") String identifier) {
        return controller.readPublishedAssessmentViewDto(namespace, identifier);
    }

}
