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

public class CompetencyAttributesViewDto  implements Serializable {
	private static final long serialVersionUID = 2L;

	private String competencyCode;
	private int competencyAttributeId;
	private boolean optInMultiMeasure;
	private boolean placementComponentMmap;
	private boolean placementComponentAssess;
	private boolean useSelfReportedDataForMM;
	private String highestLevelReadingCourse;
	private boolean showPlacementToEsl;
	private boolean showPlacementToNativeSpeaker;
	private String prerequisiteGeneralEducation;
	private String prerequisiteStatistics;
	private String mmDecisionLogic;
	private String mmPlacementLogic;
	private String mmAssignedPlacementLogic;

	public CompetencyAttributesViewDto() {
		placementComponentAssess = true;
	}

	public String getCompetencyCode() {
		return competencyCode;
	}
	public void setCompetencyCode(String competencyCode) {
		this.competencyCode = competencyCode;
	}
	public int getCompetencyAttributeId() {
		return competencyAttributeId;
	}
	public void setCompetencyAttributeId(int competencyAttributeId) {
		this.competencyAttributeId = competencyAttributeId;
	}
	public boolean isOptInMultiMeasure() {
		return optInMultiMeasure;
	}
	public void setOptInMultiMeasure(boolean mmOptIn) {
		this.optInMultiMeasure = mmOptIn;
	}

	public boolean isPlacementComponentMmap() {
		return placementComponentMmap;
	}
	public void setPlacementComponentMmap(boolean placementComponentMmap) {
		this.placementComponentMmap = placementComponentMmap;
	}
	public boolean isPlacementComponentAssess() {
		return placementComponentAssess;
	}
	public void setPlacementComponentAssess(boolean placementComponentAssess) {
		this.placementComponentAssess = placementComponentAssess;
	}
	public boolean isUseSelfReportedDataForMM() {
		return useSelfReportedDataForMM;
	}
	public void setUseSelfReportedDataForMM(boolean useSelfReportedDataForMM) {
		this.useSelfReportedDataForMM = useSelfReportedDataForMM;
	}
	public String getHighestLevelReadingCourse() {
		return highestLevelReadingCourse;
	}
	public void setHighestLevelReadingCourse(String highestLevelReadingCourse) {
		this.highestLevelReadingCourse = highestLevelReadingCourse;
	}
	public boolean isShowPlacementToEsl() {
		return showPlacementToEsl;
	}
	public void setShowPlacementToEsl(boolean showPlacementToEsl) {
		this.showPlacementToEsl = showPlacementToEsl;
	}
	public boolean isShowPlacementToNativeSpeaker() {
		return showPlacementToNativeSpeaker;
	}
	public void setShowPlacementToNativeSpeaker(boolean showPlacementToNativeSpeaker) {
		this.showPlacementToNativeSpeaker = showPlacementToNativeSpeaker;
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

	public String getMmDecisionLogic() {
		return mmDecisionLogic;
	}
	public void setMmDecisionLogic(String placementRuleSetRowId) {
		this.mmDecisionLogic = placementRuleSetRowId;
	}

	public String getMmPlacementLogic() {
		return mmPlacementLogic;
	}
	public void setMmPlacementLogic(String mmPlacementLogic) {
		this.mmPlacementLogic = mmPlacementLogic;
	}
	public String getMmAssignedPlacementLogic() {
		return mmAssignedPlacementLogic;
	}
	public void setMmAssignedPlacementLogic(String mmAssignedPlacementLogic) {
		this.mmAssignedPlacementLogic = mmAssignedPlacementLogic;
	}
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object other) {
		return EqualsBuilder.reflectionEquals(this, other);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
