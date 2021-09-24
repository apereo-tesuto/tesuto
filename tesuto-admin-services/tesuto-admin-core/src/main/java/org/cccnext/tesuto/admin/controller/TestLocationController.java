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
package org.cccnext.tesuto.admin.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.service.TestLocationAssessmentService;
import org.cccnext.tesuto.admin.service.TestLocationService;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.exception.ValidationException;
import org.cccnext.tesuto.user.service.UserAccountExistsException;
import org.cccnext.tesuto.user.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Created by jasonbrown on 8/4/16.
 */
@Service
public class TestLocationController {

    @Autowired
    TestLocationService testLocationService;
    
    @Autowired
    UserAccountService userService;
    
    @Autowired
    TestLocationAssessmentService testLocationAssessmentService;

    public ResponseEntity<Set<TestLocationDto>> getLocationByLocationId(Set<String> locationIds){
        Set<TestLocationDto> testLocations = testLocationService.read(locationIds);
        return new ResponseEntity<Set<TestLocationDto>>(testLocations, HttpStatus.OK);
    }
    
    public ResponseEntity<List<TestLocationDto>> read(){
        List<TestLocationDto> testLocations = testLocationService.read();
        return new ResponseEntity<List<TestLocationDto>>(testLocations, HttpStatus.OK);
    }
    

    public ResponseEntity<TestLocationDto> read(String locationId){
        TestLocationDto testLocation = testLocationService.read(locationId);
        return new ResponseEntity<TestLocationDto>(testLocation, HttpStatus.OK);
    }

    public ResponseEntity<?> createTestLocation(TestLocationFormData formData) throws ValidationException, NotFoundException, UserAccountExistsException {

        TestLocationDto testLocation = formData.getTestLocationDto();
        List<String> messages = testLocationService.validateTestLocation(testLocation);
        if (messages.size() > 0) {
            throw new ValidationException(messages);
        }

        TestLocationDto savedTestLocation = testLocationService.createTestLocationWithAssessments(formData.getTestLocationDto(), formData.getAssessments());
       
        return new ResponseEntity<TestLocationDto>(savedTestLocation, HttpStatus.OK);
    }

    public ResponseEntity<?> editTestLocation(TestLocationFormData formData, String testLocationId) {
        TestLocationDto testLocation = formData.getTestLocationDto();
        List<String> messages = testLocationService.validateTestLocation(testLocation);
        if (messages.size() > 0) {
            throw new ValidationException(messages);
        }

        TestLocationDto savedTestLocation = testLocationService.editTestLocationWithAssessments(testLocationId, formData.getTestLocationDto(), formData.getAssessments());

        return new ResponseEntity<TestLocationDto>(savedTestLocation, HttpStatus.OK);
    }

    public ResponseEntity<?> enableTestLocation(String testLocationId, Map<String,Object> requestBody) {
        testLocationService.enableTestLocation(testLocationId, (boolean)requestBody.get("enabled"));

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
