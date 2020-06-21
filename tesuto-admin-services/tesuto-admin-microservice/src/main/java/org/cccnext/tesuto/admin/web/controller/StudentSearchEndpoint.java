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

import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import org.cccnext.tesuto.admin.controller.StudentSearchControllerV2;
import org.cccnext.tesuto.admin.form.StudentSearchForm;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.springboot.web.BaseController;
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
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping(value = "service/v2/students/search")
public class StudentSearchEndpoint extends BaseController {

    @Autowired
    StudentSearchControllerV2 controller;
    
    @PreAuthorize("hasAnyAuthority('FIND_STUDENT','FIND_ANY_STUDENT', 'API')")
    @RequestMapping(method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<StudentViewDto>> findBySearchForm(
            @RequestBody(required = true) StudentSearchForm studentSearchForm) throws URISyntaxException {
        return controller.findBySearchForm(this.getUser(),studentSearchForm);
    }
    
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value="oauth2", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<StudentViewDto>> findBySearchFormOauth(
            @RequestBody(required = true) StudentSearchForm studentSearchForm) throws URISyntaxException {
        return controller.findBySearchForm(this.getUser(),studentSearchForm);
    }
    
    @PreAuthorize("hasAnyAuthority('API')")
    @RequestMapping(value = "oauth2/filtered", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public ResponseEntity<List<StudentViewDto>> findBySearchForm(
            @RequestBody(required = true) StudentSearchForm studentSearchForm, @RequestParam("miscodes")Set<String> miscodes) throws URISyntaxException {
    	return new ResponseEntity<List<StudentViewDto>>(controller.findBySearchForm(studentSearchForm, miscodes),
				HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('FIND_STUDENT','FIND_ANY_STUDENT','API')")
    @RequestMapping(value = "{cccid}", method = RequestMethod.GET)
    public @ResponseBody StudentViewDto find(@PathVariable String cccid) throws URISyntaxException {
        log.info(String.format("Reading cccid: %s", cccid));
        return buildStudent(cccid);
    }
    
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value = "{cccid}/colleges", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Set<String>> collegesAppliedTo(@PathVariable("cccid") String cccid) throws URISyntaxException {
    	return new ResponseEntity<Set<String>>(controller.collegesAppliedTo(cccid),
				HttpStatus.OK);
    }
    
}
