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
package org.cccnext.tesuto.delivery.web.controller;

import org.cccnext.tesuto.delivery.controller.InstructorClassReportController;
import org.cccnext.tesuto.delivery.exception.InvalidRosterMasteryReportException;
import org.cccnext.tesuto.delivery.form.ClassReportForm;
import org.cccnext.tesuto.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value = "/service/v1/class-report")
public class InstructorClassReportEndpoint {
	private static final String TEXT_CSV_MEDIA = "text/csv";

	@Autowired
	InstructorClassReportController controller;
	/**
	 * The request expects a standard multipart file for upload. the file will be copies to a temporary location and
	 * the file name will be returned in the response body.
	 * In case the file does not an CSV file, and 400 response will be sent.
	 */
	@PreAuthorize("hasAuthority('VIEW_INSTRUCTOR_CLASS_REPORT')")
	@PostMapping(value="/upload")
	@ResponseStatus(HttpStatus.OK)
	public @ResponseBody String uploadStudentsFile(@RequestParam("file") MultipartFile file) {
		return controller.uploadStudentsFile(file);
	}

	/**
	 * The request expects the JSON data containing the collegeId, courseId and the file name that was previously uploaded.
	 * The data will be validated and a list of error will be returned if the validation fails with the response code 400.
	 *
	 * If the data does validate, the list of students will be returned (for now).
	 */
	@PreAuthorize("hasAuthority('VIEW_INSTRUCTOR_CLASS_REPORT')")
	@PostMapping(value="/validateAndGenerate")
	public ResponseEntity<?> validateAndGenerateReport(@ModelAttribute ClassReportForm classReportForm) throws InvalidRosterMasteryReportException {
		return controller.validateAndGenerateReport(classReportForm);
	}

	

	// TODO: Make this method/endpoint go away if we're not going to use it?
	@PreAuthorize("hasAuthority('VIEW_INSTRUCTOR_CLASS_REPORT')")
	@PostMapping(value="/generate")
	public ResponseEntity<?> generateReport(@RequestBody ClassReportForm classReportForm) throws InvalidRosterMasteryReportException {
		return controller.generateReport(classReportForm);
	}

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody String handleValidationException(ValidationException exception) {
		return exception.getMessage();
	}

}
