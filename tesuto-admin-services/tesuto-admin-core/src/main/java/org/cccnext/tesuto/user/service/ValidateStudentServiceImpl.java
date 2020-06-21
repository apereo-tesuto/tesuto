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
package org.cccnext.tesuto.user.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import javax.persistence.NonUniqueResultException;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.domain.util.upload.CsvImportError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ValidateStudentServiceImpl implements ValidateStudentService {
	private static final int CCCID_LENGTH = 7;

	@Autowired
	StudentService studentService;

	@Value("${class.importer.validate.studentname}")
	private boolean validateStudentName;

	/**
	 * The short validation will only check first and last name, and the cccid
	 * @param student
	 * @return
	 */
	public Collection<CsvImportError> validateShort(StudentViewDto student, Collection<String> collegeIds) {
		Collection<CsvImportError> lineErrors = new LinkedList<CsvImportError>();
		if (validateStudentName) {
			if (StringUtils.isBlank(student.getFirstName())) {
				lineErrors.add(new CsvImportError("firstName", "", CsvImportError.CsvImportErrorCode.FIRST_NAME_EMPTY, null));
			}
			if (StringUtils.isBlank(student.getLastName())) {
				lineErrors.add(new CsvImportError("lastName", "", CsvImportError.CsvImportErrorCode.LAST_NAME_EMPTY, null));
			}
		}
		
		if (StringUtils.isBlank(student.getCccid())) {
			lineErrors.add(new CsvImportError("cccid", "", CsvImportError.CsvImportErrorCode.CCCID_EMPTY, null));
		} else if (student.getCccid().length() != CCCID_LENGTH) {
			lineErrors.add(new CsvImportError("cccid", student.getCccid(), CsvImportError.CsvImportErrorCode.INVALID_CCCID_LENGTH, null));
		} else {
			try {
				StudentViewDto foundStudent = studentService.findByCccid(student.getCccid(), new HashSet<>(collegeIds));
				if (foundStudent == null) {
					lineErrors.add(new CsvImportError("cccid", student.getCccid(), CsvImportError.CsvImportErrorCode.INVALID_STUDENT, null));
				} else {
					if (validateStudentName) {
						if ( !StringUtils.equalsIgnoreCase(foundStudent.getFirstName(), student.getFirstName())) {
							lineErrors.add(new CsvImportError("firstName", student.getFirstName(), CsvImportError.CsvImportErrorCode.NAME_MISMATCH, null));
						}
						if ( !StringUtils.equalsIgnoreCase(foundStudent.getLastName(), student.getLastName())) {
							lineErrors.add(new CsvImportError("lastName", student.getLastName(), CsvImportError.CsvImportErrorCode.NAME_MISMATCH, null));
						}
					}
				}
			} catch (NonUniqueResultException ne) {
				lineErrors.add(new CsvImportError("cccid", student.getCccid(), CsvImportError.CsvImportErrorCode.INVALID_STUDENT, null));
			}
		}
		return lineErrors;
	}
}
