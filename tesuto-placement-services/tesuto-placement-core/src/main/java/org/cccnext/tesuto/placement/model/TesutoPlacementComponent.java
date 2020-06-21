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
package org.cccnext.tesuto.placement.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@DiscriminatorValue("tesuto")
@NamedQueries({
		@NamedQuery(name = "TesutoPlacementComponent.findByPlacementId",
				query = "select capc " +
						"from TesutoPlacementComponent capc " +
						"inner join capc.placements p " +
						"where p.id = :placementId " +
						"order by capc.studentAbility desc")
})
public class TesutoPlacementComponent extends PlacementComponent {

	private static final long serialVersionUID = 1L;

	@Column(name="assessment_session_id")
	private String assessmentSessionId;

	@Column(name="assessment_title")
	private String assessmentTitle;

	@Column(name="assessment_date")
	private Date assessmentDate;

	@Column(name="student_ability")
	private Double studentAbility;

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

	public Date getAssessmentDate() {
		return assessmentDate;
	}

	public void setAssessmentDate(Date assessmentDate) {
		this.assessmentDate = assessmentDate;
	}

	public Double getStudentAbility() {
		return studentAbility;
	}

	public void setStudentAbility(Double studentAbility) {
		this.studentAbility = studentAbility;
	}

}
