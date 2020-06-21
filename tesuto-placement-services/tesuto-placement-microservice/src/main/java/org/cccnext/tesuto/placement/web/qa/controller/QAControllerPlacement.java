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
package org.cccnext.tesuto.placement.web.qa.controller;

import java.io.IOException;

import org.cccnext.tesuto.placement.service.OnboardCollegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "service/v1/qa")
public class QAControllerPlacement {

	@Autowired
	OnboardCollegeService service;

	@PreAuthorize("hasAuthority('CREATE_QA_OBJECTS')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void>  seedData() throws Exception {
		createPlacementsAtQAColleges();
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('API')")
	@RequestMapping(value = "oauth2", method = RequestMethod.POST)
	public ResponseEntity<Void> seedDataServer() throws Exception {
		createPlacementsAtQAColleges();
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

	private void createPlacementsAtQAColleges() throws IOException {
		service.onboardCollege("ZZ1", "Unicollege");
		service.onboardCollege("ZZ2", "Rain Forest College");
		service.onboardCollege("ZZ3", "CCCTC College A");
		service.onboardCollege("ZZ4", "CCCTC College B");
		service.onboardCollege("ZZ0", "South Bay JPL Consortium");
	}

}
