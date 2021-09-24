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

import java.io.UnsupportedEncodingException;

import org.cccnext.tesuto.admin.controller.CollegeAssociatedUserController;
import org.cccnext.tesuto.admin.dto.CollegeAssociatedUser;
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

@Controller
@RequestMapping(value = "service/v1/college-associated-user")
public class CollegeAssociatedUserEndPoint extends BaseController {

    @Autowired
    CollegeAssociatedUserController controller;

    @PreAuthorize("hasAnyAuthority('FIND_ANY_STUDENT','API')")
    @RequestMapping(method = RequestMethod.GET)
    public  @ResponseBody ResponseEntity<CollegeAssociatedUser> findByCccId(@RequestParam("username") String username) throws UnsupportedEncodingException {
        return controller.findByCccId(username);
    }
    
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(method = RequestMethod.GET, value="colleges")
    public @ResponseBody ResponseEntity<String> isAssociatedToCollege(@RequestParam("username") String username, @RequestParam("miscode")String cccCollegeMisCode) throws UnsupportedEncodingException {
    	return controller.isAssociatedToCollege(username, cccCollegeMisCode);
    }
}
