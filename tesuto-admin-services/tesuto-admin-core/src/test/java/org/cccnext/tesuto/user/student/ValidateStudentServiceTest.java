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
package org.cccnext.tesuto.user.student;

import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.persistence.NonUniqueResultException;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.domain.util.upload.CsvImportError;
import org.cccnext.tesuto.user.service.StudentService;
import org.cccnext.tesuto.user.service.ValidateStudentServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author jstanley
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidateStudentServiceTest {

	@Mock
	StudentService studentService;

	@InjectMocks
	ValidateStudentServiceImpl validateStudentService;

	@Before
	public void setup() throws IllegalAccessException {
		StudentViewDto student = createStudentViewDto("A123456", "Larry", "Local");
		when(studentService.findByCccid(eq("A123456"), anySetOf(String.class))).thenReturn(student);
		when(studentService.findByCccid(eq("B654321"), anySetOf(String.class))).thenThrow(new NonUniqueResultException());
	}

	@Test
	public void testEmptyFields() throws IllegalAccessException {
		FieldUtils.writeField(validateStudentService, "validateStudentName", true, true);
		Collection<CsvImportError> errors = validateStudentService.validateShort(new StudentViewDto(), Collections.singleton("ZZZ1"));
		Assert.assertEquals(3, errors.size());
		Collection<CsvImportError.CsvImportErrorCode> emptyErrorCodes = emptyErrorCodes();
		for (CsvImportError csvImportError : errors) {
			Assert.assertTrue(emptyErrorCodes.contains(csvImportError.getErrorCode()));
		}
	}

	private Collection<CsvImportError.CsvImportErrorCode> emptyErrorCodes() {
		Collection<CsvImportError.CsvImportErrorCode> collection = new ArrayList<>();
		collection.add(CsvImportError.CsvImportErrorCode.FIRST_NAME_EMPTY);
		collection.add(CsvImportError.CsvImportErrorCode.LAST_NAME_EMPTY);
		collection.add(CsvImportError.CsvImportErrorCode.CCCID_EMPTY);
		return collection;
	}

	@Test
	public void testValidRecord() {
		StudentViewDto student = createStudentViewDto("A123456", "Larry", "Local");
		Collection<CsvImportError> errors = validateStudentService.validateShort(student, Collections.singleton("ZZZ1"));
		Assert.assertEquals(0, errors.size());
	}

	@Test
	public void testInvalidCccidLength() {
		StudentViewDto student = createStudentViewDto("A156", "Larry", "Local");
		Collection<CsvImportError> errors = validateStudentService.validateShort(student, Collections.singleton("ZZZ1"));
		Assert.assertEquals(1, errors.size());
		CsvImportError error = errors.iterator().next();
		Assert.assertEquals("cccid", error.getColumnName());
		Assert.assertEquals(CsvImportError.CsvImportErrorCode.INVALID_CCCID_LENGTH, error.getErrorCode());
		Assert.assertEquals(student.getCccid(), error.getColumnValue());
	}

	@Test
	public void testInvalidCccid() {
		StudentViewDto student = createStudentViewDto("A156777", "Larry", "Local");
		Collection<CsvImportError> errors = validateStudentService.validateShort(student, Collections.singleton("ZZZ1"));
		Assert.assertEquals(1, errors.size());
		CsvImportError error = errors.iterator().next();
		Assert.assertEquals("cccid", error.getColumnName());
		Assert.assertEquals(CsvImportError.CsvImportErrorCode.INVALID_STUDENT, error.getErrorCode());
		Assert.assertEquals(student.getCccid(), error.getColumnValue());
	}

	@Test
	public void testInvalidCccidWithException() {
		StudentViewDto student = createStudentViewDto("B654321", "Larry", "Local");
		Collection<CsvImportError> errors = validateStudentService.validateShort(student, Collections.singleton("ZZZ1"));
		Assert.assertEquals(1, errors.size());
		CsvImportError error = errors.iterator().next();
		Assert.assertEquals("cccid", error.getColumnName());
		Assert.assertEquals(CsvImportError.CsvImportErrorCode.INVALID_STUDENT, error.getErrorCode());
		Assert.assertEquals(student.getCccid(), error.getColumnValue());
	}

	@Test
	public void testIncorrectNameIgnored() {
		StudentViewDto student = createStudentViewDto("A123456", "Harry", "Remote");
		Collection<CsvImportError> errors = validateStudentService.validateShort(student, Collections.singleton("ZZZ1"));
		Assert.assertEquals(0, errors.size());
	}

	@Test
	public void testIncorrectName() throws IllegalAccessException {
		StudentViewDto student = createStudentViewDto("A123456", "Harry", "Remote");
		FieldUtils.writeField(validateStudentService, "validateStudentName", true, true);
		Collection<CsvImportError> errors = validateStudentService.validateShort(student, Collections.singleton("ZZZ1"));
		Assert.assertEquals(2, errors.size());
		Iterator<CsvImportError> errorIt = errors.iterator();
		CsvImportError error = errorIt.next();
		Assert.assertEquals("firstName", error.getColumnName());
		Assert.assertEquals(CsvImportError.CsvImportErrorCode.NAME_MISMATCH, error.getErrorCode());
		Assert.assertEquals(student.getFirstName(), error.getColumnValue());
		error = errorIt.next();
		Assert.assertEquals("lastName", error.getColumnName());
		Assert.assertEquals(CsvImportError.CsvImportErrorCode.NAME_MISMATCH, error.getErrorCode());
		Assert.assertEquals(student.getLastName(), error.getColumnValue());
	}


	private StudentViewDto createStudentViewDto(String cccid, String firstName, String lastName) {
		StudentViewDto dto = new StudentViewDto();
		dto.setCccid(cccid);
		dto.setFirstName(firstName);
		dto.setLastName(lastName);
		return dto;
	}

}
