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

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.admin.dto.StudentCollegeAffiliationDto;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.web.service.StudentCollegeAffiliationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Service
public class StudentCollegeAffiliationController {

	@Resource(name = "studentCollegeAffiliationService")
	StudentCollegeAffiliationService service;

	public ResponseEntity<List<StudentCollegeAffiliationDto>> read() {
		return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
	}

	public ResponseEntity<StudentCollegeAffiliationDto> findByCccIdAndMisCode(String cccId, String misCode) {
		return new ResponseEntity<StudentCollegeAffiliationDto>(service.findByCccIdAndMisCode(cccId, misCode),
				HttpStatus.OK);
	}

	public ResponseEntity<List<StudentCollegeAffiliationDto>> findByCccId(String cccId) {
		return new ResponseEntity<List<StudentCollegeAffiliationDto>>(service.findByStudentCccId(cccId), HttpStatus.OK);
	}
	
	public ResponseEntity<List<StudentCollegeAffiliationDto>> recent() {
		List<StudentCollegeAffiliationDto> recent = service.findTenMostRecent();
		if(CollectionUtils.isEmpty(recent)) {
			new ResponseEntity<List<StudentCollegeAffiliationDto>>(HttpStatus.NO_CONTENT);
		}
	     return new ResponseEntity<List<StudentCollegeAffiliationDto>>(recent, HttpStatus.OK);
	 }

	public ResponseEntity<?> delete(String eppn, String cccId, String misCode) {
		StudentCollegeAffiliationDto affiliation = service.find(eppn, cccId, misCode);
		if (affiliation == null) {
			throw new NotFoundException(
					"Unable to find an affiliation for eppn: " + eppn + " cccId: " + cccId + " misCode: " + misCode);
		}
		service.delete(affiliation);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}
}
