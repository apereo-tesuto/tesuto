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
import java.util.Set;
import java.util.stream.Collectors;

import org.cccnext.tesuto.admin.controller.CollegeController;
import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.cccnext.tesuto.admin.viewdto.CollegeViewDto;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "service/v1/colleges")
public class CollegeEndPoint  extends BaseController {

    @Autowired
    CollegeController controller;

    @PreAuthorize("hasAnyAuthority('VIEW_COLLEGES_WITH_ALL_LOCATIONS','API')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<CollegeDto>> read() {
        List<String> collegeIds = getUser().getCollegeIds().stream().collect(Collectors.toList());
        return controller.read(collegeIds);
    }

    @PreAuthorize("hasAnyAuthority('VIEW_ANY_COLLEGE','API')")
    @RequestMapping(value = "/{miscode}", method = RequestMethod.GET)
    public @ResponseBody CollegeViewDto getCollegeByMisCode(@PathVariable("miscode") String misCode) throws IllegalAccessException {
    	return controller.getCollegeByMisCode(misCode);
    }
    
    @PreAuthorize("hasAnyAuthority('API')")
    @RequestMapping(value = "/dto/{miscode}", method = RequestMethod.GET)
    public @ResponseBody CollegeDto read(@PathVariable("miscode") String misCode) throws IllegalAccessException {
    	return controller.read(misCode);
    }
    
    @PreAuthorize("hasAnyAuthority('VIEW_ANY_COLLEGE','API')")
    @RequestMapping(value = "miscodes/{miscodes}", method = RequestMethod.GET)  
    public ResponseEntity<List<CollegeDto>> read(@PathVariable("miscodes")List<String> misCodes) {
        return controller.read(misCodes);
    }
    
    @PreAuthorize("hasAnyAuthority('API')")
    @RequestMapping(value = "view/miscodes/{miscodes}", method = RequestMethod.GET)  
    public ResponseEntity<Set<CollegeViewDto>> readView(@PathVariable("miscodes")List<String> misCodes) {
        return controller.readView(misCodes);
    }
}
