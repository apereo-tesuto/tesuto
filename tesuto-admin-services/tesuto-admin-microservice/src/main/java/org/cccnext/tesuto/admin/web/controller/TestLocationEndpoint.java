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
package org.cccnext.tesuto.admin.web.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cccnext.tesuto.admin.controller.TestLocationController;
import org.cccnext.tesuto.admin.controller.TestLocationFormData;
import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.exception.ValidationException;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.cccnext.tesuto.user.service.UserAccountExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by jasonbrown on 8/4/16.
 */
@Controller
@RequestMapping(value = "/service/v1/test-locations")
public class TestLocationEndpoint extends BaseController{

	@Autowired
    TestLocationController controller;

    @PreAuthorize("permitAll()")
    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Set<TestLocationDto>> getLocationByLocationId(@RequestParam(value = "locationIds", required = true) Set<String> locationIds){
    	return controller.getLocationByLocationId(locationIds);
    }

    @PreAuthorize("permitAll()")
    @RequestMapping(value="mine", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Set<TestLocationDto>> getUserTestLocations() {
        return new ResponseEntity<Set<TestLocationDto>>(getUser().getTestLocations(), HttpStatus.OK);
    }
    
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value="{testLocationId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<TestLocationDto> getTestLocation(@PathVariable String testLocationId) {
        return controller.read(testLocationId);
    }
    
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value="all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<TestLocationDto>> getTestLocation() {
        return controller.read();
    }

    @PreAuthorize("hasAuthority('CREATE_TEST_LOCATION')")
    @RequestMapping(value="", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> createTestLocation(@RequestBody TestLocationFormData formData) throws ValidationException, NotFoundException, UserAccountExistsException {
        if (formData.getTestLocationDto() == null) {
            return new ResponseEntity<>(error("Missing form data"), HttpStatus.BAD_REQUEST);
        }
        return controller.createTestLocation(formData);
    }

    @PreAuthorize("hasAuthority('CREATE_TEST_LOCATION')")
    @RequestMapping(value = "{testLocationId}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<?> editTestLocation(@RequestBody(required = true) TestLocationFormData formData, @PathVariable String testLocationId) {
         return controller.editTestLocation(formData, testLocationId);
    }


    @PreAuthorize("hasAuthority('ENABLE_TEST_LOCATION')")
    @RequestMapping(value = "{testLocationId}/enabled", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> enableTestLocation(@PathVariable String testLocationId, @RequestBody Map<String,Object> requestBody) {
       return controller.enableTestLocation(testLocationId, requestBody);
    }
}
