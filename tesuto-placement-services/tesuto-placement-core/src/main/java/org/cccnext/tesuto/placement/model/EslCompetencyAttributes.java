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
@DiscriminatorValue("ESL")
@Audited
@AuditTable(schema="public", value ="history_competency_attributes")
public class EslCompetencyAttributes extends CompetencyAttributes {

	private static final long serialVersionUID = 1L;

	@Column(name="highest_level_reading_course")
	private String highestLevelReadingCourse;

	@Column(name="show_placement_to_esl")
	private boolean showPlacementToEsl;

	@Column(name="show_placement_to_native_speaker")
	private boolean showPlacementToNativeSpeaker;

	@Transient
	public CompetencyAttributeEnum getCompetencyCode() {
		return CompetencyAttributeEnum.ESL;
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

}
