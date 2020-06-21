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

import java.util.Collection;

import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.domain.util.upload.CsvImportError;
import org.cccnext.tesuto.user.service.ValidateStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "service/v1/student-validation/oauth2")
public class ValidateStudentEndPoint {

	@Autowired
	ValidateStudentService service;
	
	@PreAuthorize("hasAuthority('API')")
    @RequestMapping(method = RequestMethod.PUT)
	public @ResponseBody Collection<CsvImportError> validateShort(@RequestBody StudentViewDto student,@RequestParam("miscodes") Collection<String> collegeIds){
		return service.validateShort(student, collegeIds);
	}
}
