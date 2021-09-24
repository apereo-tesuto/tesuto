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
package org.cccnext.tesuto.delivery.controller;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDisciplineDto;
import org.cccnext.tesuto.content.service.CompetencyMapDisciplineReader;
import org.cccnext.tesuto.delivery.repository.mongo.AssessmentSessionRepository;
import org.cccnext.tesuto.delivery.service.PostDeliveryAssessmentCompletionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Service
public class AssessmentPostCompletionController  {

    @Autowired
    AssessmentSessionRepository assessmentSessionRepository;

    @Autowired 
    CompetencyMapDisciplineReader competencyMapDisciplineService;
    
    @Autowired
    PostDeliveryAssessmentCompletionService postDeliveryService;

    public ResponseEntity<?> sendRequestPlacementNotifications( @RequestParam("cccid") String cccid, 
            @RequestParam(name="college-miscode", required=false) String collegeId, 
            @RequestParam(name="subject-area", required=false) String subjectArea, 
            @RequestParam(required = false, defaultValue = "true") boolean newPlacementsOnly){
        
        if (StringUtils.isNotBlank(subjectArea)) {
            CompetencyMapDisciplineDto competencyMapDiscipline = competencyMapDisciplineService.read(subjectArea);
            assessmentSessionRepository.findByUserIdIgnoreCase(cccid).stream()
                // remove all the assessment session, that do not have been assessed. 
                .filter( a -> a.getAssessment() != null && 
                    (competencyMapDiscipline == null || 
                    a.getAssessment().getAssessmentMetadata().getCompetencyMapDisciplines().contains(competencyMapDiscipline.getDisciplineName())))
                .forEach(a -> postDeliveryService.requestPlacements(a, newPlacementsOnly));

        } else {
            assessmentSessionRepository.findByUserIdIgnoreCase(cccid).stream()
                .forEach(a -> postDeliveryService.requestPlacements(a, collegeId, newPlacementsOnly));
        }
        
        return new ResponseEntity<String>(HttpStatus.OK);
    }

}
