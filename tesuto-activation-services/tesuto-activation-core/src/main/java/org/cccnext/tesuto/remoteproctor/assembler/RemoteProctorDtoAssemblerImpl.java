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
package org.cccnext.tesuto.remoteproctor.assembler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.cccnext.tesuto.activation.ActivationDao;
import org.cccnext.tesuto.activation.TestEventDao;
import org.cccnext.tesuto.activation.TestEventWithUuid;
import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.form.StudentSearchForm;
import org.cccnext.tesuto.admin.viewdto.RemoteProctorDto;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.content.service.AssessmentReader;
import org.cccnext.tesuto.user.service.StudentReader;
import org.cccnext.tesuto.user.service.UserAccountReader;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component(value = "remoteProctorDtoAssembler")
public class RemoteProctorDtoAssemblerImpl implements RemoteProctorDtoAssembler {

	@Value("${activation.service.base.url}") private String baseUrl;

	//@Autowired AssessmentService assessmentService;
	@Autowired protected ActivationDao activationDao;
	//@Autowired StudentService studentService;
	@Autowired protected UserAccountReader userAccountService;
	@Autowired protected TestEventDao testEventDao;
	@Autowired protected AssessmentReader assessmentService;
	@Autowired protected StudentReader studentService;

	@Override
	public RemoteProctorDto readById(Integer testEventId) {
		TestEventWithUuid testEvent = testEventDao.findWithUuid(testEventId);
		return assembleDto(testEvent);
	}

	@Override
	public RemoteProctorDto assembleDto(TestEventWithUuid testEvent) {
		// Drop out of here immediately if there is nothing to assemble.
		if (testEvent == null) {
			return null;
		}

		RemoteProctorDto remoteProctorDto = new RemoteProctorDto();
		// First get the information of the person setting up the event
		// and the Assessment start and end date.
		remoteProctorDto.setStartDate(testEvent.getStartDate());
		remoteProctorDto.setEndDate(testEvent.getEndDate());
		StringBuilder displayName = new StringBuilder(testEvent.getProctorFirstName())
				.append(" ")
				.append(testEvent.getProctorLastName());
		remoteProctorDto.setRemoteProctorDisplayName(displayName.toString());
		remoteProctorDto.setRemoteProctorEmail(testEvent.getProctorEmail());

		// Second get a list of the Assessments.
		List<String> assessmentNameList = new LinkedList<>();
		for (ScopedIdentifier scopedIdentifier : testEvent.getAssessmentIdentifiers()) {
			// TODO: Refactor this with a better query to get all of them?  Or just the information we need?
			AssessmentDto assessmentDto = assessmentService.readLatestPublishedVersion(scopedIdentifier);
			assessmentNameList.add(assessmentDto.getTitle());
		}
		remoteProctorDto.setAssessmentNameList(assessmentNameList);

		List<String> studentIdList = new LinkedList<>();
		for (Activation activation : activationDao.findActivationsByTestEventId(testEvent.getTestEventId())) {
			studentIdList.add(activation.getUserId());
		}
		StudentSearchForm studentSearchForm = new StudentSearchForm();
		studentSearchForm.setCccids(studentIdList);

		// College filter extracted from user object, which is the event creator.
		UserAccountDto userAccountDto = userAccountService.getUserAccount(testEvent.getCreatedBy());
		remoteProctorDto.setEventCreatorEmail(userAccountDto.getEmailAddress());
		List<StudentViewDto> studentViewDtoList = (studentIdList.size() > 0 ?
				studentService.findBySearchForm(studentSearchForm, userAccountDto.getCollegeIds()) : new ArrayList<>());

		// Create the student name list
		List<String> studentNameList = new LinkedList<>();
		for (StudentViewDto studentViewDto : studentViewDtoList) {
			studentNameList.add(studentViewDto.getDisplayName());
		}
		remoteProctorDto.setStudentNameList(studentNameList);
		remoteProctorDto.setLinkForProctorPasscode(baseUrl + "v1/remote-proctor/authorize?uuid=" + testEvent.getUuid());

		return remoteProctorDto;
	}

	@Override
	public TestEventWithUuid disassembleDto(RemoteProctorDto remoteProctorDto) {
		throw new UnsupportedOperationException("Method not implmented.");
	}
}
