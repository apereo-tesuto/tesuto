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

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;

import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

@Entity
@DiscriminatorValue("MATH")
@Audited
@AuditTable(schema="public", value ="history_competency_attributes")
public class MathCompetencyAttributes extends CompetencyAttributes {

	private static final long serialVersionUID = 1L;

	@Column(name="prereq_general_education")
	private String prerequisiteGeneralEducation;

	@Column(name="prereq_statistics")
	private String prerequisiteStatistics;

	@Transient
	public CompetencyAttributeEnum getCompetencyCode() {
		return CompetencyAttributeEnum.MATH;
	}

	public String getPrerequisiteGeneralEducation() {
		return prerequisiteGeneralEducation;
	}

	public void setPrerequisiteGeneralEducation(String prerequisiteGeneralEducation) {
		this.prerequisiteGeneralEducation = prerequisiteGeneralEducation;
	}

	public String getPrerequisiteStatistics() {
		return prerequisiteStatistics;
	}

	public void setPrerequisiteStatistics(String prerequisiteStatistics) {
		this.prerequisiteStatistics = prerequisiteStatistics;
	}

}
