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
package org.cccnext.tesuto.placement.view;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class TesutoPlacementComponentViewDto implements Serializable {
	private static final long serialVersionUID = 2L;

	// Assessment Placement Component used to determine the placement --> Assessment Session ID

	private String collegeMisCode;

	private String collegeName; // College where it was taken.

	private String assessmentSessionId;

	private String assessmentTitle;

	private Date assessmentCompletedDate;

	private String entityTargetClass;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getCollegeName() {
		return collegeName;
	}

	public void setCollegeName(String collegeName) {
		this.collegeName = collegeName;
	}

	public String getAssessmentSessionId() {
		return assessmentSessionId;
	}

	public void setAssessmentSessionId(String assessmentSessionId) {
		this.assessmentSessionId = assessmentSessionId;
	}

	public String getAssessmentTitle() {
		return assessmentTitle;
	}

	public void setAssessmentTitle(String assessmentTitle) {
		this.assessmentTitle = assessmentTitle;
	}

	public Date getAssessmentCompletedDate() {
		return assessmentCompletedDate;
	}

	public void setAssessmentCompletedDate(Date assessmentCompletedDate) {
		this.assessmentCompletedDate = assessmentCompletedDate;
	}

	public String getEntityTargetClass() {
		return entityTargetClass;
	}

	public void setEntityTargetClass(String entityTargetClass) {
		this.entityTargetClass = entityTargetClass;
	}

	public String getCollegeMisCode() {
		return collegeMisCode;
	}

	public void setCollegeMisCode(String collegeMisCode) {
		this.collegeMisCode = collegeMisCode;
	}
}
