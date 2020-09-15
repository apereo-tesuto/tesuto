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
package org.cccnext.tesuto.remoteproctor.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.ccctc.web.client.rest.CCCRestCallHandler;

import org.cccnext.tesuto.admin.viewdto.CollegeViewDto;
import org.cccnext.tesuto.admin.viewdto.RemoteProctorDto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
//TODO Fix Velocity Engine
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailSenderService {


	//@Autowired private VelocityEngine velocityEngine;

	@Autowired private CCCRestCallHandler restCallHandler;

	public static final String remoteProctorTemplateNameText = "email/remote-proctor-event-confirmation.txt.vm";
	public static final String remoteProctorTemplateNameHtml = "email/remote-proctor-event-confirmation.html.vm";

	public static final String remoteProctorCancelTemplateText = "email/remote-proctor-event-cancel.txt.vm";
	public static final String remoteProctorCancelTemplateHtml = "email/remote-proctor-event-cancel.html.vm";

	@Value("${email.service.url}")
	String emailServiceUrl;

	@Value("${email.oauth.url}")
	String oauthUrl;

	@Value("${email.oauth.user}")
	String oauthUser;

	@Value("${email.oauth.password}")
	String oauthPassword;

	public void sendRemoteEventCancellationEmail(RemoteProctorDto remoteProctorDto, CollegeViewDto college) {
		Map<String, Object> model = new HashMap<>();
		model.put("remoteProctorViewDto", remoteProctorDto);
		model.put("collegeName", college.getName());
		HashMap<String, Object> params = new HashMap<>();
		params.put("to", remoteProctorDto.getRemoteProctorEmail());
		params.put("subject", "Remote Proctor Event - CANCELED");
//		params.put("message-body", VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, remoteProctorCancelTemplateText, "UTF-8", model));
//		params.put("message-body-html", VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, remoteProctorCancelTemplateHtml, "UTF-8", model));
		params.put("senderMisCode", college.getCccId());
		HashMap<String, String> headerParams = new HashMap<>();
		sendEmailToAddress(params, headerParams);
	}

	public void sendRemoteEventCreationEmails(RemoteProctorDto remoteProctorDto, CollegeViewDto college) {
		Map<String, Object> model = new HashMap<>();
		model.put("remoteProctorViewDto", remoteProctorDto);
		sendRemoteEventCreationEmailToRemoteProctor(remoteProctorDto.getRemoteProctorEmail(), model, college, remoteProctorDto.getStartDate());
		sendRemoteEventCreationEmailToProctor(remoteProctorDto.getEventCreatorEmail(), model, college, remoteProctorDto.getStartDate());
	}

	private void sendRemoteEventCreationEmailToRemoteProctor(String toEmail, Map<String, Object> model, CollegeViewDto college, Date startDate) {
		sendRemoteEventCreationEmail(toEmail, model, college, startDate, true);
	}


	private void sendRemoteEventCreationEmailToProctor(String toEmail, Map<String, Object> model, CollegeViewDto college, Date startDate) {
		sendRemoteEventCreationEmail(toEmail, model, college, startDate, false);
	}

	private void sendRemoteEventCreationEmail(String toEmail, Map<String, Object> model, CollegeViewDto college, Date startDate, boolean activateRemoteProctorLink) {
		HashMap<String, Object> params = new HashMap<>();
		params.put("to", toEmail);
		StringBuffer sb = new StringBuffer();
		sb.append("Remote Event for ").append(college.getName()).append(" - ").append(startDate);
		params.put("subject", sb.toString());
		//params.put("message-body", VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, remoteProctorTemplateNameText, "UTF-8",model));
		// TODO: Disable the proctor link based on activeRemoteProctorLink
		//params.put("message-body-html", VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, remoteProctorTemplateNameHtml, "UTF-8", model));
		params.put("senderMisCode", college.getCccId());
		HashMap<String, String> headerParams = new HashMap<>();
		sendEmailToAddress(params, headerParams);
	}

	@Async
	private void sendEmailToAddress(HashMap<String, Object> params, HashMap<String, String> headerParams) {
		/*TODO Generalize restCallHandler.(
				HttpMethod.POST,
				emailServiceUrl,
				params,
				headerParams,
				oauthUrl,
				oauthUser,
				oauthPassword);*/
	}
}
