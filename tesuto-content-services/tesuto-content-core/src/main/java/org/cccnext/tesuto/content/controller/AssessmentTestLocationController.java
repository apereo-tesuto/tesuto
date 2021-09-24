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

import java.util.Set;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.service.TestLocationReader;
import org.cccnext.tesuto.content.service.AssessmentTestLocationService;
import org.cccnext.tesuto.content.viewdto.AssessmentViewDto;
import org.cccnext.tesuto.user.service.UserAccountReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Created by jasonbrown on 8/4/16.
 */

@Service
public class AssessmentTestLocationController {
    
    @Autowired
    UserAccountReader userService;
    
    @Autowired
    TestLocationReader testLocationReader;
    
    @Autowired
    AssessmentTestLocationService testLocationAssessmentService;


    public ResponseEntity<?> getAssessmentsByTestLocationIdAndCollegeAffiliation(UserAccountDto userAccountDto, String testLocationId) {
        if (!testLocationReader.isUserAssociatedWithTestLocationCollege(userAccountDto, testLocationId)) {
            return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        }

        Set<AssessmentViewDto> assessments = testLocationAssessmentService.getByTestLocation(testLocationId);

        return new ResponseEntity<Set<AssessmentViewDto>>(assessments, HttpStatus.OK);
    }

    public ResponseEntity<?> getAssessmentsByTestLocationId(UserAccountDto userAccountDto, String testLocationId) {
        if (!testLocationReader.isUserAssociatedWithTestLocation(userAccountDto, testLocationId)) {
            return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        }

        Set<AssessmentViewDto> assessments = testLocationAssessmentService.getByTestLocation(testLocationId);

        return new ResponseEntity<Set<AssessmentViewDto>>(assessments, HttpStatus.OK);
    }

}
