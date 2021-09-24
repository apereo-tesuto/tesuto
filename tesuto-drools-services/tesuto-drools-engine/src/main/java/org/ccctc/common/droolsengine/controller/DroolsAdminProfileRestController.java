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
package org.ccctc.common.droolsengine.controller;

import java.util.Map;

import org.ccctc.common.droolsengine.facts.StudentProfileFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = DroolsAdminProfileRestController.REQUEST_ROOT, produces = MediaType.APPLICATION_JSON_VALUE)
public class DroolsAdminProfileRestController {
    static final String REQUEST_ROOT = "/ccc/api/drools/v1/profile";

    @Autowired
    private StudentProfileFacade studentProfileService;
    
    @RequestMapping(method = RequestMethod.GET, value="/{cccid}")
    public Map<String, Object> getProfile(@PathVariable("cccid") String cccid) {
        Map<String, Object> result = studentProfileService.getStudentProfile(cccid);
        return result;        
    }

}
