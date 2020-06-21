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
package org.cccnext.tesuto.placement.web.controller;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.placement.service.OnboardCollegeService;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "service/v1/onboard")
public class OnboardCollegeController extends BaseController {
	@Autowired
	OnboardCollegeService service;

	@PreAuthorize("hasAuthority('CREATE_DISCIPLINE')")
	@RequestMapping(method = RequestMethod.PUT, value = "{ccc-mis-code}/{description}")
	public ResponseEntity<Void> insertValues(@PathVariable("ccc-mis-code") String cccMisCode,
			@PathVariable(value = "description", required = false) String description) throws IOException {
		if (userIsAffiliated(cccMisCode)) {
			if (StringUtils.isBlank(description)) {
				description = cccMisCode + " College";
			}
			service.onboardCollege(cccMisCode, description);
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.UNAUTHORIZED);
	}

	@PreAuthorize("hasAuthority('API')")
	@RequestMapping(method = RequestMethod.PUT, value = "{ccc-mis-code}/{description}/oauth2")
	public ResponseEntity<Void> insertValuesOauth2(@PathVariable("ccc-mis-code") String cccMisCode,
			@PathVariable(value = "description", required = false) String description) throws IOException {
		if (StringUtils.isBlank(description)) {
			description = cccMisCode + " College";
		}
		service.onboardCollege(cccMisCode, description);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "save-current-subject-areas-as-defaults/{ccc-mis-code}")
	public ResponseEntity<String> saveDefaults(@PathVariable("ccc-mis-code") String cccMisCode) throws IOException {

		return new ResponseEntity<String>(service.saveCurrentSubjectAreasAsDefaults(cccMisCode), HttpStatus.OK);
	}
	
	@PreAuthorize("hasAuthority('CREATE_DISCIPLINE')")
    @RequestMapping(method = RequestMethod.POST, consumes = { "application/x-www-form-urlencoded",
            "multipart/form-data" }, produces = "application/json")
    public ResponseEntity<Void> uploadMultipartFile(@RequestParam Map<String, String> body )  throws IOException {
		
		service.onboardCollege(body.get("college"), body.get("decription"));
		return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
