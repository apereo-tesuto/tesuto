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
package org.cccnext.tesuto.admin.viewdto;

import org.cccnext.tesuto.domain.dto.Dto;

import java.util.Date;
import java.util.List;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class RemoteProctorDto implements Dto {
	// TODO: Add the name of the event, clarify with Nichole.  Comes from the TestEvent Entity Bean.  Wire via assembler adapter.
	private String remoteProctorDisplayName;
	private List<String> assessmentNameList;
	private Date startDate;
	private Date endDate;
	private List<String> studentNameList;
	private String linkForProctorPasscode;
	private String remoteProctorEmail;
	private String eventCreatorEmail;

	public String getRemoteProctorDisplayName() {
		return remoteProctorDisplayName;
	}

	public void setRemoteProctorDisplayName(String remoteProctorDisplayName) {
		this.remoteProctorDisplayName = remoteProctorDisplayName;
	}

	public List<String> getAssessmentNameList() {
		return assessmentNameList;
	}

	public void setAssessmentNameList(List<String> assessmentNameList) {
		this.assessmentNameList = assessmentNameList;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public List<String> getStudentNameList() {
		return studentNameList;
	}

	public void setStudentNameList(List<String> studentNameList) {
		this.studentNameList = studentNameList;
	}

	public String getLinkForProctorPasscode() {
		return linkForProctorPasscode;
	}

	public void setLinkForProctorPasscode(String linkForProctorPasscode) {
		this.linkForProctorPasscode = linkForProctorPasscode;
	}

	public String getRemoteProctorEmail() {
		return remoteProctorEmail;
	}

	public void setRemoteProctorEmail(String remoteProctorEmail) {
		this.remoteProctorEmail = remoteProctorEmail;
	}

	public String getEventCreatorEmail() {
		return eventCreatorEmail;
	}

	public void setEventCreatorEmail(String eventCreatorEmail) {
		this.eventCreatorEmail = eventCreatorEmail;
	}
}
