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
package org.cccnext.tesuto.delivery.service;

import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.cccnext.tesuto.admin.service.CollegeReader;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.content.service.AssessmentAccessService;
import org.cccnext.tesuto.content.service.AssessmentService;
import org.cccnext.tesuto.content.service.StudentUploadService;
import org.cccnext.tesuto.delivery.controller.InstructorClassReportController;
import org.cccnext.tesuto.delivery.test.StudentUploadServiceStub;
import org.cccnext.tesuto.delivery.test.ValidateStudentServiceStub;
import org.cccnext.tesuto.exception.ValidationException;
import org.cccnext.tesuto.user.service.StudentService;
import org.cccnext.tesuto.user.service.ValidateStudentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;

@RunWith(MockitoJUnitRunner.class)
public class ClassReportUploadParsingTest {
	@Mock
	StudentService studentService;

	@Mock
	CollegeReader collegeRepository;

	@Mock
	AssessmentService assessmentService;

	@Mock
	AssessmentAccessService assessmentAccessService;

	@InjectMocks
	ValidateStudentServiceStub validateStudentService;
	
	@InjectMocks
	StudentUploadServiceStub studentUploadService;

	@InjectMocks
	InstructorClassReportController controller;

	@Before
	public void setup() throws IllegalAccessException {
		FieldUtils.writeField(studentUploadService, "validateStudentService", validateStudentService, true);
		FieldUtils.writeField(controller, "studentUploadService", studentUploadService, true);
		
		StudentViewDto student = createStudentViewDto("aaxxxxx", "Larry", "Local");
		when(studentService.findByCccid(startsWith("aa"), anySetOf(String.class))).thenReturn(student);
	}

	@Test(expected=ValidationException.class)
	public void testIncorrectFormat() throws IOException {
		File file  = ResourceUtils.getFile("classpath:class-report-imports/invalid-mime-type.csv.gz");
		controller.uploadStudentsFile( new MockMultipartFile("file", new FileInputStream(file)));
	}

	@Test(expected=ValidationException.class)
	public void testInvalidExtension() throws IOException {
		File file  = ResourceUtils.getFile("classpath:class-report-imports/invalid-extension.ccc");
		controller.uploadStudentsFile( new MockMultipartFile("file", new FileInputStream(file)));
	}

/* Not sure what to do with these. controller.validateAndGenerateReport no longer returns just a list of students.
	@Test
	public void testCsvWithoutCccidHeader() throws IOException {
		File file  = ResourceUtils.getFile("classpath:class-report-imports/cccis-as-header.csv");
		ClassReportForm classReportForm = new ClassReportForm();
		classReportForm.setFile(  new MockMultipartFile("file", new FileInputStream(file)));
		classReportForm.setCollegeId("ZZ1");
		ResponseEntity<List<CsvImportLineResult<StudentViewDto>>> response = controller.validateAndGenerateReport(classReportForm);
		Assert.assertEquals(400, response.getStatusCodeValue());
		Assert.assertEquals(1, response.getBody().size());
		Assert.assertEquals(CsvImportError.CsvImportErrorCode.CCCID_HEADER_MISSING, response.getBody().get(0).getErrors().iterator().next().getErrorCode());
	}
	
	@Test
	public void testStudentsWithErrors() throws IOException {
		File file  = ResourceUtils.getFile("classpath:class-report-imports/test-student-with-errors.csv");
		ClassReportForm classReportForm = new ClassReportForm();
		classReportForm.setFile(  new MockMultipartFile("file", new FileInputStream(file)));
		classReportForm.setCollegeId("ZZ1");
		ResponseEntity<List<CsvImportLineResult<StudentViewDto>>> response = controller.validateAndGenerateReport(classReportForm);
		Assert.assertEquals(400, response.getStatusCodeValue());
		Assert.assertEquals(3, response.getBody().size());
		Assert.assertEquals(CsvImportError.CsvImportErrorCode.INVALID_CCCID_LENGTH, response.getBody().get(0).getErrors().iterator().next().getErrorCode());
		Assert.assertEquals(1, response.getBody().get(0).getErrors().size());
		Assert.assertEquals(CsvImportError.CsvImportErrorCode.INVALID_STUDENT, response.getBody().get(1).getErrors().iterator().next().getErrorCode());
		Assert.assertEquals(1, response.getBody().get(1).getErrors().size());
		Assert.assertEquals(CsvImportError.CsvImportErrorCode.INVALID_CCCID_LENGTH, response.getBody().get(2).getErrors().iterator().next().getErrorCode());
		Assert.assertEquals(1, response.getBody().get(2).getErrors().size());
	}
	
	@Test
	public void testStudentsWithDups() throws IOException {
		File file  = ResourceUtils.getFile("classpath:class-report-imports/test-student-with-dups.csv");
		ClassReportForm classReportForm = new ClassReportForm();
		classReportForm.setFile(  new MockMultipartFile("file", new FileInputStream(file)));
		classReportForm.setCollegeId("ZZ1");
		ResponseEntity<List<CsvImportLineResult<StudentViewDto>>> response = controller.validateAndGenerateReport(classReportForm);
		Assert.assertEquals(400, response.getStatusCodeValue());
		Assert.assertEquals(1, response.getBody().size());
		Assert.assertEquals(CsvImportError.CsvImportErrorCode.DUPLICATE_CCCID, response.getBody().get(0).getErrors().iterator().next().getErrorCode());
	}
	
	@Test
	public void testStudentsNoErrors() throws IOException {
		File file  = ResourceUtils.getFile("classpath:class-report-imports/test-student-no-errors.csv");
		ClassReportForm classReportForm = new ClassReportForm();
		classReportForm.setFile(  new MockMultipartFile("file", new FileInputStream(file)));
		classReportForm.setCollegeId("ZZ1");
		ResponseEntity<List<CsvImportLineResult<StudentViewDto>>> response = controller.validateAndGenerateReport(classReportForm);
		Assert.assertEquals(200, response.getStatusCodeValue());
		List<CsvImportLineResult<StudentViewDto>> students = response.getBody();
		Assert.assertEquals(11, students.size());
	}
	
	@Test
	public void testStudentsNoErrors2() throws IOException {
		File file  = ResourceUtils.getFile("classpath:class-report-imports/test-student-no-errors-2.csv");
		ClassReportForm classReportForm = new ClassReportForm();
		classReportForm.setFile(  new MockMultipartFile("file", new FileInputStream(file)));
		classReportForm.setCollegeId("ZZ1");
		ResponseEntity<List<CsvImportLineResult<StudentViewDto>>> response = controller.validateAndGenerateReport(classReportForm);
		Assert.assertEquals(200, response.getStatusCodeValue());
		List<CsvImportLineResult<StudentViewDto>> students = response.getBody();
		Assert.assertEquals(11, students.size());
	}
*/

	private StudentViewDto createStudentViewDto(String cccid, String firstName, String lastName) {
		StudentViewDto dto = new StudentViewDto();
		dto.setCccid(cccid);
		dto.setFirstName(firstName);
		dto.setLastName(lastName);
		return dto;
	}


}
