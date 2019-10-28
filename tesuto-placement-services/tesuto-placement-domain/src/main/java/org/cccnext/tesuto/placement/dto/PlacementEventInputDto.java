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
package org.cccnext.tesuto.placement.dto;

import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.placement.view.DisciplineViewDto;

public class PlacementEventInputDto {
	private String cccid;
	private DisciplineViewDto subjectArea;
	private Set<String> competencyMapDisciplines;
	private Set<String> collegeMisCodes;
	private String placementId;
	private boolean processOnlyNewPlacements;

	private String trackingId;
	
	public String getCccid() {
		return cccid;
	}

	public void setCccid(String cccid) {
		this.cccid = cccid;
	}

	public DisciplineViewDto getSubjectArea() {
		return subjectArea;
	}
	
	public void setSubjectArea(DisciplineViewDto subjectArea) {
		this.subjectArea = subjectArea;
	}
	
	public Set<String> getCompetencyMapDisciplines() {
		return competencyMapDisciplines;
	}
	
	public void setCompetencyMapDisciplines(Set<String> competencyMapDisciplines) {
		this.competencyMapDisciplines = competencyMapDisciplines;
	}
	
	public Set<String> getCollegeMisCodes() {
		return collegeMisCodes;
	}
	
	public void setCollegeMisCodes(Set<String> collegeMisCodes) {
		this.collegeMisCodes = collegeMisCodes;
	}
	
	public boolean isProcessOnlyNewPlacements() {
		return processOnlyNewPlacements;
	}
	
	public void setProcessOnlyNewPlacements(boolean processOnlyNewPlacements) {
		this.processOnlyNewPlacements = processOnlyNewPlacements;
	}

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}
	
	public String getPlacementId() {
		return placementId;
	}

	public void setPlacementId(String placementId) {
		this.placementId = placementId;
	}

	@Override
	public  String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
