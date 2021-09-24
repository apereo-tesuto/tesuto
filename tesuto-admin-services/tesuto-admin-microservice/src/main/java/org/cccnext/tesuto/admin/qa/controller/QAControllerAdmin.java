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
package org.cccnext.tesuto.admin.qa.controller;

import java.util.Set;

import org.cccnext.tesuto.admin.qa.TestLocationQAService;
import org.cccnext.tesuto.admin.qa.UserAccountResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/service/v1/qa")
public class QAControllerAdmin {

	@Autowired
	UserAccountResourceService service;

	@Autowired
	TestLocationQAService testLocationService;

	@PreAuthorize("hasAuthority('CREATE_QA_OBJECTS')")
	@RequestMapping(value = "/{generatePasswords}", method = RequestMethod.POST)
	void generateSeedData(@PathVariable("generatePasswords") Boolean generatePasswords) throws Exception {
		testLocationService.setDefaults();
		service.seedData(generatePasswords);
	}

	@PreAuthorize("hasAuthority('CREATE_QA_OBJECTS')")
	@RequestMapping(value = "collegeIds/{collegeIds}", method = RequestMethod.DELETE)
	void deleteSeedDataSeedData(Set<String> collegeIds) throws Exception {
		service.deleteUsersByCollegeIds(collegeIds);
	}

	@PreAuthorize("hasAuthority('API')")
	@RequestMapping(value = "oauth2", method = RequestMethod.POST)
	public ResponseEntity<Void> seedDataServer() throws Exception {
		testLocationService.setDefaults();
		service.seedData(false);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
}
