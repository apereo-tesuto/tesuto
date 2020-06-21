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
package org.cccnext.tesuto.delivery.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.content.service.StudentUploadService;
import org.cccnext.tesuto.delivery.exception.InvalidRosterMasteryReportException;
import org.cccnext.tesuto.delivery.exception.NotEnoughStudentsAssessedException;
import org.cccnext.tesuto.delivery.exception.NotEnoughStudentsScoredException;
import org.cccnext.tesuto.delivery.form.ClassReportForm;
import org.cccnext.tesuto.delivery.report.dto.ClassRosterMasteryReportDto;
import org.cccnext.tesuto.delivery.service.ClassRosterMasteryReportService;
import org.cccnext.tesuto.delivery.service.MissingAssessmentPerformanceMetadataException;
import org.cccnext.tesuto.domain.util.upload.CsvImportError;
import org.cccnext.tesuto.domain.util.upload.CsvImportLineResult;
import org.cccnext.tesuto.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

@Service
public class InstructorClassReportController {
	private static final String TEXT_CSV_MEDIA = "text/csv";

	@Autowired
	StudentUploadService studentUploadService;

	@Autowired
	ClassRosterMasteryReportService classRosterMasteryService;

	/**
	 * The request expects a standard multipart file for upload. the file will be copies to a temporary location and
	 * the file name will be returned in the response body.
	 * In case the file does not an CSV file, and 400 response will be set.
	 */
	public @ResponseBody String uploadStudentsFile(MultipartFile file) {
		validateAsCsvFile(file);
		try {
			Path tempFile = Files.createTempFile(file.getName(), "temp");
			IOUtils.copyLarge(file.getInputStream(), new FileOutputStream(tempFile.toFile()));
			return tempFile.toString();
		} catch (IOException ioe) {
			throw new ValidationException(ioe.getMessage());
		}
	}

	/**
	 * The request expects the JSON data containing the collegeId, courseId and the file name that was previously uploaded.
	 * The data will be validated and a list of error will be returned if the validation fails with the response code 400.
	 *
	 * If the data does validate, the list of students will be returned (for now).
	 */
	public ResponseEntity<?> validateAndGenerateReport(ClassReportForm classReportForm) throws InvalidRosterMasteryReportException {
		List<CsvImportLineResult<StudentViewDto>> importMessages = new LinkedList<CsvImportLineResult<StudentViewDto>>();
		InputStream csvInputStream = null;
		ClassRosterMasteryReportDto report = null;
		try {
			if (classReportForm.getFile() != null) {
				csvInputStream = classReportForm.getFile().getInputStream();
			} else {
				File uploadedFile = new File(classReportForm.getImportedFilename());
				if (uploadedFile.exists()) {
					csvInputStream = FileUtils.openInputStream(uploadedFile);
				} else {
					CsvImportError error = new CsvImportError(CsvImportError.CsvImportErrorCode.FILE_DOESNT_EXIST, new Object[]{classReportForm.getImportedFilename()});
					importMessages.add(new CsvImportLineResult<StudentViewDto>(Collections.singleton(error)));
				}
			}

			if (csvInputStream != null) {
				importMessages.addAll(
						studentUploadService.validateStudentCsvData(IOUtils.toString(csvInputStream, StandardCharsets.UTF_8.name()), Collections.singleton(classReportForm.getCollegeId())));
			}

		} catch (Exception ioe) {
			CsvImportError error = new CsvImportError(CsvImportError.CsvImportErrorCode.PARSING_FAILED, new Object[]{ioe.getMessage()});
			importMessages.add(new CsvImportLineResult<StudentViewDto>(Collections.singleton(error)));
		} finally {
			if (csvInputStream != null) {
				try { csvInputStream.close(); }
				catch (Exception ignore) {}
			}
		}

		if (importMessages.stream().anyMatch(it -> it.getErrors() != null && it.getErrors().size() > 0)) {
			return new ResponseEntity<>(
					importMessages.stream().filter(it -> it.getErrors() != null)
							.filter(it -> it.getErrors().size() > 0).collect(Collectors.toList()), HttpStatus.BAD_REQUEST);
		} else {
			try {
				report = classRosterMasteryService.getClassRosterMasteryRestrictedView(classReportForm.getCompetencyName(), getStudentListFromImportMessages(importMessages));
			} catch (NotEnoughStudentsAssessedException e) {
				CsvImportError error = new CsvImportError(CsvImportError.CsvImportErrorCode.MINIMUM_ASSESSED_STUDENTS_NOT_MET,
						new Object[]{e.getStudentCount(), ClassRosterMasteryReportService.MIN_REPORT_SIZE});
				return handleReportGenerationError(error, importMessages);
			} catch (NotEnoughStudentsScoredException e) {
				CsvImportError error = new CsvImportError(CsvImportError.CsvImportErrorCode.MINIMUM_SCORED_STUDENTS_NOT_MET,
						new Object[]{e.getStudentCount(), ClassRosterMasteryReportService.MIN_REPORT_SIZE});
				return handleReportGenerationError(error, importMessages);
			} catch (MissingAssessmentPerformanceMetadataException e) {
				return handleReportGenerationError(new CsvImportError(CsvImportError.CsvImportErrorCode.MISSING_ASSESSMENT_METADATA), importMessages);
			}
			return new ResponseEntity<>(report, HttpStatus.OK);
		}
	}

	private ResponseEntity<?> handleReportGenerationError(CsvImportError error, List<CsvImportLineResult<StudentViewDto>> importMessages) {
		importMessages.add(new CsvImportLineResult<>(-1, Collections.singleton(error)));
		return new ResponseEntity<>(
				importMessages.stream().filter(it -> it.getErrors() != null)
						.filter(it -> it.getErrors().size() > 0).collect(Collectors.toList()), HttpStatus.BAD_REQUEST);
	}

	// TODO: Make this method/endpoint go away if we're not going to use it?
	public ResponseEntity<?> generateReport(ClassReportForm classReportForm) throws InvalidRosterMasteryReportException {
		List<CsvImportLineResult<StudentViewDto>> importMessages = new LinkedList<CsvImportLineResult<StudentViewDto>>();
		ClassRosterMasteryReportDto report = null;
		try {
			File uploadedFile = new File(classReportForm.getImportedFilename());
			InputStream csvInputStream = FileUtils.openInputStream(uploadedFile);
			importMessages.addAll(
					studentUploadService.validateStudentCsvData(IOUtils.toString(csvInputStream, StandardCharsets.UTF_8.name()), Collections.singleton(classReportForm.getCollegeId())));
		} catch (Exception ioe) {
			CsvImportError error = new CsvImportError(CsvImportError.CsvImportErrorCode.PARSING_FAILED, new Object[]{ioe.getMessage()});
			importMessages.add(new CsvImportLineResult<StudentViewDto>(-1, Collections.singleton(error)));
		}

		if (importMessages.stream().anyMatch(it -> it.getErrors() != null && it.getErrors().size() > 0)) {
			return new ResponseEntity<>(
					importMessages.stream().filter(it -> it.getErrors() != null)
							.filter(it -> it.getErrors().size() > 0).collect(Collectors.toList()), HttpStatus.BAD_REQUEST);
		} else {
			try {
				report = classRosterMasteryService.getClassRosterMasteryRestrictedView(classReportForm.getCompetencyName(), getStudentListFromImportMessages(importMessages));
			} catch (NotEnoughStudentsAssessedException e) {
				CsvImportError error = new CsvImportError(CsvImportError.CsvImportErrorCode.MINIMUM_ASSESSED_STUDENTS_NOT_MET,
						new Object[]{e.getStudentCount(), ClassRosterMasteryReportService.MIN_REPORT_SIZE});
				return handleReportGenerationError(error, importMessages);
			} catch (NotEnoughStudentsScoredException e) {
				CsvImportError error = new CsvImportError(CsvImportError.CsvImportErrorCode.MINIMUM_SCORED_STUDENTS_NOT_MET,
						new Object[]{e.getStudentCount(), ClassRosterMasteryReportService.MIN_REPORT_SIZE});
				return handleReportGenerationError(error, importMessages);
			} catch (MissingAssessmentPerformanceMetadataException e) {
				return handleReportGenerationError(new CsvImportError(CsvImportError.CsvImportErrorCode.MISSING_ASSESSMENT_METADATA), importMessages);
			}
			return new ResponseEntity<>(report, HttpStatus.OK);
		}
	}

	private void validateAsCsvFile(MultipartFile file) {
		/*
		 * TODO: due to the content type problem, that we see in Windows we need to skip the content type test.
		 * If we find a better solution for Windows, we should enable that test again.
		 *
		String contentType = file.getContentType();
		if (StringUtils.isNotBlank(contentType) && !TEXT_CSV_MEDIA.toString().equals(contentType)) {
			throw new ValidationException("tesuto.expected.csv.file ["+contentType+"]");
		}
		 */
		String fileName = file.getOriginalFilename();
		if (!FilenameUtils.getExtension(fileName).equalsIgnoreCase("csv") &&
				!FilenameUtils.getExtension(fileName).equalsIgnoreCase("txt")) {
			throw new ValidationException("tesuto.expected.csv.file ["+fileName+"]");
		}
	}

	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody String handleValidationException(ValidationException exception) {
		return exception.getMessage();
	}

	private List<StudentViewDto> getStudentListFromImportMessages(List<CsvImportLineResult<StudentViewDto>> importMessages) {
		List<StudentViewDto> studentList = new ArrayList<>();
		for (CsvImportLineResult<StudentViewDto> importMessage : importMessages) {
			studentList.add(importMessage.getImportedValue());
		}
		return studentList;
	}
}
