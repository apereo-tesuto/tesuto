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
package org.cccnext.tesuto.content.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.collections.Bag;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.bag.HashBag;
import org.apache.commons.io.IOUtils;
import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.cccnext.tesuto.admin.service.CollegeReader;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.content.model.AssessmentAccess;
import org.cccnext.tesuto.content.service.AssessmentAccessService;
import org.cccnext.tesuto.content.service.AssessmentService;
import org.cccnext.tesuto.content.service.StudentUploadService;
import org.cccnext.tesuto.domain.util.upload.CsvImportError;
import org.cccnext.tesuto.domain.util.upload.CsvImportLineResult;
import org.cccnext.tesuto.user.service.ValidateStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

@Service
public class StudentUploadServiceImpl implements StudentUploadService {
	private static final String CCCID_COLUMN_HEADER = "cccid";
	
	@Autowired
	private ValidateStudentService validateStudentService;

	@Autowired
	private CollegeReader collegeReader;

	@Autowired
	AssessmentService assessmentService;

	@Autowired
	private AssessmentAccessService assessmentAccessService;

	@Value("${class.report.min.student.count:10}")
	private int minStudentCount;
	
	public List<CsvImportLineResult<StudentViewDto>> validateStudentCsvData(String studentCSV, Collection<String> collegeIds) throws IOException {
		return validateStudentCsvData(IOUtils.toInputStream(studentCSV), collegeIds);
	}

	public List<CsvImportLineResult<StudentViewDto>> validateStudentCsvData(InputStream inputStream, Collection<String> collegeIds) throws IOException {
		List<CsvImportLineResult<StudentViewDto>> importMessages = new LinkedList<CsvImportLineResult<StudentViewDto>>();
		final CsvMapper mapper = buildMapper();
		CsvSchema csvSchema = buildSchema(true);
		MappingIterator<Map<String,String>> iterator = mapper.readerFor(Map.class).with(csvSchema).readValues(inputStream);
		int studentCount = 0;
		Bag studentBag = new HashBag();
		while (iterator.hasNext()) {
			try {
				Map<String,String> studentData = iterator.next();
				Map<String, String> studentDataTreeMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
				studentDataTreeMap.putAll(studentData);
				if (!studentDataTreeMap.containsKey(CCCID_COLUMN_HEADER)) {
					// return from validation, if the header does not contain CCCID
					importMessages.add(new CsvImportLineResult<>(Collections.singleton(new CsvImportError(CsvImportError.CsvImportErrorCode.CCCID_HEADER_MISSING))));
					return importMessages;
				}
				StudentViewDto student = createStudentViewDto(studentDataTreeMap);
				studentCount++;
				studentBag.add(student.getCccid());
				Collection<CsvImportError> errors = validateStudentService.validateShort(student, collegeIds);
				if (errors.isEmpty()) {
					importMessages.add(new CsvImportLineResult<StudentViewDto>(student));
				} else {
					importMessages.add( new CsvImportLineResult<StudentViewDto>(studentCount, errors));
				}
			} catch (RuntimeJsonMappingException ex) {
				importMessages.add(new CsvImportLineResult<StudentViewDto>(studentCount,
						Collections.singleton(new CsvImportError(CsvImportError.CsvImportErrorCode.JSON_MAPPING_ERROR, new Object[]{ex.getLocalizedMessage()}))));
			}
		}
		if (studentBag.size() < minStudentCount) {
			importMessages.add(new CsvImportLineResult<StudentViewDto>(
					Collections.singleton(new CsvImportError(CsvImportError.CsvImportErrorCode.MINIMUM_STUDENTS_NOT_MET, new Object[]{studentCount, minStudentCount}))));
		}
		
		@SuppressWarnings("unchecked")
		Set<String> duplicates = (Set<String>) studentBag.stream().filter(it -> studentBag.getCount(it) > 1).collect(Collectors.toSet());
		if ( CollectionUtils.isNotEmpty(duplicates)) {
			importMessages.add(new CsvImportLineResult<StudentViewDto>(
					Collections.singleton(new CsvImportError(CsvImportError.CsvImportErrorCode.DUPLICATE_CCCID, duplicates.toArray()))));
		}
		return importMessages;
	}

	private StudentViewDto createStudentViewDto(Map<String, String> studentMap) {
		StudentViewDto student = new StudentViewDto();
		student.setCccid(studentMap.get("cccid"));
		student.setFirstName(studentMap.get("firstName"));
		student.setLastName(studentMap.get("lastName"));
		return student;
	}

	private CsvMapper buildMapper() {
		CsvMapper mapper = new CsvMapper();
		mapper.configure(CsvParser.Feature.TRIM_SPACES, true);
		mapper.configure(CsvParser.Feature.IGNORE_TRAILING_UNMAPPABLE, true);
		return mapper;
	}


	private void validateAssessmentAccess(Collection<String> collegeIds, Set<String> studentIds) {
		for (String collegeId : collegeIds) {
			CollegeDto college = collegeReader.read(collegeId);
			Collection<? extends AssessmentAccess> assessementAccess = assessmentAccessService.findByLocationsAndUserIds(
					college.getTestLocations(), studentIds);
			for (AssessmentAccess assessmentAccess : assessementAccess) {
				assessmentService.read(assessmentAccess.getScopedIdentifier()).iterator().next().getAssessmentMetadata();
			}
		}
	}

	private CsvSchema buildSchema(boolean withHeader) {
		if (withHeader) {
			return CsvSchema.emptySchema().withHeader();
		} else {
			return CsvSchema.builder().addColumn("cccid").addColumn("firstName").addColumn("lastName").build();
		}
	}



}
