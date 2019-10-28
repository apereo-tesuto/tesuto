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

import java.util.Arrays;
import java.util.List;

import org.cccnext.tesuto.content.assembler.view.assessment.AssessmentViewDtoAssembler;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.content.service.AssessmentService;
import org.cccnext.tesuto.content.viewdto.AssessmentViewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author James Travis Stanley <jstanley@unicon.net>
 */
@Service
public class AssessmentController {

    @Autowired
    @Qualifier("assessmentService")
    AssessmentService assessmentService;

    @Autowired
    @Qualifier("assessmentViewDtoAssembler")
    AssessmentViewDtoAssembler assembler;

    public void setAssessmentService(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    public AssessmentService getAssessmentService() {
        return assessmentService;
    }

    public ResponseEntity<List<AssessmentViewDto>> read() {
        return new ResponseEntity<List<AssessmentViewDto>>(
                assembler.assembleViewDto(assessmentService.readPublishedUnique()), HttpStatus.OK);
    }
    
    public ResponseEntity<AssessmentDto> read(String id) {
        return new ResponseEntity<AssessmentDto>(assessmentService.read(id), HttpStatus.OK);
    }



    public ResponseEntity<List<AssessmentViewDto>> readAllowedAssessments(String currentUserId, String locationId) {
        return new ResponseEntity<List<AssessmentViewDto>>(
                assembler.assembleViewDto(assessmentService.readPublishedUniqueForUserAndLocation(currentUserId, locationId)),
                HttpStatus.OK);
    }

    /**
     * PUT
     * service/v1/assessments/publish-assessment?namespace=DEVELOPER&identifier=
     * a200000000002&version=1&publish=true
     *
     * Don't forget to add the CSRF token when using a rest client: X-CSRF-TOKEN: fab163e9-23e2-4115-8f0b-48ad26b76175
     * This is an example value.  The real one can be obtained from the page source after doing a valid login.
     */
    public  boolean publishAssessment(String namespace, String identifier,
            int version, boolean publish) {
        return assessmentService.setPublishFlag(namespace, identifier, version, publish);
    }



    /**
     * Production support to examine Assessment content.
     *
     * @param namespace
     * @param identifier
     * @return
     */
    public AssessmentDto readAssessmentByNamespaceAndIdentifier(String namespace,
            String identifier) {
        AssessmentDto assessmentDto = assessmentService.readLatestPublishedVersion(namespace, identifier);
        return assessmentDto;
    }
    


    /**
     * Returns only the version numbers of an Assessment.
     *
     * @param namespace
     * @param identifier
     * @return all version numbers of an assessment
     */
    public ResponseEntity<List<Integer>> readAssessmentVersionsByNamespaceAndIdentifier(String namespace,
                                                                                         String identifier) {
        return new ResponseEntity<>(assessmentService.readVersions(new ScopedIdentifier(namespace, identifier)), HttpStatus.OK);
    }
    
    public ResponseEntity<AssessmentDto> readAssessmentByVersion(String namespace,
            String identifier, int assessmentVersion) {
    	return new ResponseEntity<>(assessmentService.readVersion(new ScopedIdentifier(namespace, identifier), assessmentVersion), HttpStatus.OK);
    }

    public List<AssessmentDto> readAssessmentRevisionsByNamespaceAndIdentifier(String namespace, String identifier) {
        List<AssessmentDto> assessmentDtoList = assessmentService.readAllRevisions(namespace, identifier);
        return assessmentDtoList;
    }
    
	public List<AssessmentDto> readByCompetencyMapDisicpline(String competencyMapDiscipline) {
		return assessmentService.readByCompetencyMapDisicpline(competencyMapDiscipline);
	}

	
	public List<AssessmentDto> readByCompetencyMapDisicplineOrPartialIdentifier(String competencyMapDiscipline,
			String partialIdentifier) {
		return assessmentService.readByCompetencyMapDisicplineOrPartialIdentifier(competencyMapDiscipline, partialIdentifier);
	}
	
	public AssessmentViewDto readPublishedAssessmentViewDto(String namespace, String identifier) {
		return assessmentService.readViewDto(namespace, identifier);
	}

}
