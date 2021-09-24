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
package org.cccnext.tesuto.web.controller;

import java.util.List;

import org.cccnext.tesuto.admin.dto.StudentCollegeAffiliationDto;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@RestController
@RequestMapping(value = "service/v1/student-affiliations")
public class StudentCollegeAffiliationEndpoint extends BaseController {

    @Autowired
    StudentCollegeAffiliationController controller;

    @PreAuthorize("hasAuthority('VIEW_ALL_STUDENT_COLLEGE_AFFILIATIONS')")
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<StudentCollegeAffiliationDto>> read() {
        return controller.read();
    }

	@PreAuthorize("hasAuthority('API')")
	@RequestMapping(method = RequestMethod.GET, value="cccid/{cccId}/miscode/{misCode}")
	public ResponseEntity<StudentCollegeAffiliationDto> findByCccIdAndMisCode(@PathVariable String cccId, @PathVariable String misCode) {
		return controller.findByCccIdAndMisCode(cccId, misCode);
	}
	
	 @PreAuthorize("hasAuthority('API')")
	 @RequestMapping(method = RequestMethod.GET, value="cccid/{cccId}")
	 public ResponseEntity<List<StudentCollegeAffiliationDto>> findByCccId(@PathVariable String cccId) {
		 return controller.findByCccId(cccId);
	 }
	 
	 @PreAuthorize("hasAuthority('API')")
	 @RequestMapping(method = RequestMethod.GET, value="recent")
	 public ResponseEntity<List<StudentCollegeAffiliationDto>> recent() {
		 return controller.recent();
	 }

    @PreAuthorize("hasAuthority('DELETE_STUDENT_COLLEGE_AFFILIATION')")
    @RequestMapping(value = "eppn/{eppn}/cccid/{cccId}/miscode/{misCode}", method=RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable String eppn, @PathVariable String cccId, @PathVariable String misCode) {
       return controller.delete(eppn, cccId, misCode);
    }
}
