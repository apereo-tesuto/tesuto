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

import org.cccnext.tesuto.admin.controller.CollegeAttributeController;
import org.cccnext.tesuto.admin.dto.CollegeAttributeDto;
import org.cccnext.tesuto.springboot.web.AuthorizerService;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by jasonbrown on 8/3/16.
 */
@Controller
@RequestMapping(value = "service/v1/college-attributes")
public class CollegeAttributeEndpoint extends BaseController {

    @Autowired
    CollegeAttributeController controller;

    @Autowired
    AuthorizerService authorizerService;

    @PreAuthorize("hasAuthority('UPDATE_COLLEGE_ATTRIBUTE')")
    @RequestMapping(method = RequestMethod.POST, consumes ="application/json", produces = "application/json")
    public ResponseEntity<CollegeAttributeDto> updateCollegeAttribute(@RequestBody CollegeAttributeDto collegeAttributeDto) {
        authorizerService.authorize(collegeAttributeDto.getCollegeId(), getUser());
        return controller.updateCollegeAttribute(collegeAttributeDto);
    }

    @PreAuthorize("hasAuthority('VIEW_COLLEGE_ATTRIBUTE')")
    @RequestMapping(value="/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<CollegeAttributeDto> get(@PathVariable String id) {
        authorizerService.authorize(id, getUser());
        return controller.get(id);
    }
}
