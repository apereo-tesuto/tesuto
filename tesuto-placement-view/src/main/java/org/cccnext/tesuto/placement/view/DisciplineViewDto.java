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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

public class DisciplineViewDto implements Serializable {

	static final long serialVersionUID=2l;

	private Integer disciplineId;
	private String collegeId;
	private String title;
	private String description;
	private String competencyMapDiscipline;
	private Integer competencyMapVersion;
	private String sisCode;
	private String noPlacementMessage;
	private boolean usePrereqPlacementMethod;
	private CompetencyAttributesViewDto competencyAttributes;

	public Integer getDisciplineId() {
		return disciplineId;
	}

	public void setDisciplineId(Integer disciplineId) {
		this.disciplineId = disciplineId;
	}

	public String getCollegeId() {
		return collegeId;
	}

	public void setCollegeId(String collegeId) {
		this.collegeId = collegeId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCompetencyMapDiscipline() {
		return competencyMapDiscipline;
	}

	public void setCompetencyMapDiscipline(String competencyMapDiscipline) {
		this.competencyMapDiscipline = competencyMapDiscipline;
	}

	public String getSisCode() {
		return sisCode;
	}

	public void setSisCode(String sisCode) {
		this.sisCode = sisCode;
	}

	public String getNoPlacementMessage() {
		return noPlacementMessage;
	}

	public void setNoPlacementMessage(String noPlacementMessage) {
		this.noPlacementMessage = noPlacementMessage;
	}

	public boolean isUsePrereqPlacementMethod() {

		return usePrereqPlacementMethod;
	}

	public void setUsePrereqPlacementMethod(boolean usePrereqPlacementMethod) {
		this.usePrereqPlacementMethod = usePrereqPlacementMethod;
	}

	public Integer getCompetencyMapVersion() {
		return competencyMapVersion;
	}

	public void setCompetencyMapVersion(Integer competencyMapVersion) {
		this.competencyMapVersion = competencyMapVersion;
	}

	public CompetencyAttributesViewDto getCompetencyAttributes() {
		return competencyAttributes;
	}

	public void setCompetencyAttributes(CompetencyAttributesViewDto competencyAttributes) {
		this.competencyAttributes = competencyAttributes;
	}

	@Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
