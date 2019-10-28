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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="competency_code")
@Audited
@AuditTable(schema="public", value ="history_competency_attributes")
@Table(schema="public",name="competency_attributes")
@JsonIgnoreProperties(ignoreUnknown = true)
//TODO set this up so it is more generic maybe by setting the mapper DefaultTyping
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes({
    @JsonSubTypes.Type(value = EnglishCompetencyAttributes.class, name = "ENGLISH"),
    @JsonSubTypes.Type(value = EslCompetencyAttributes.class, name = "ESL"),
    @JsonSubTypes.Type(value = MathCompetencyAttributes.class, name = "MATH") }
)
public abstract class CompetencyAttributes implements Serializable {

	private static final long serialVersionUID = 1L;

	public static CompetencyAttributes createInstance(String discriminator) throws ReflectiveOperationException {
		return CompetencyAttributeEnum.valueOf(discriminator).getAttributesClass().newInstance();
	}

	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	@Column(name="competency_attribute_id")
	private int competencyAttributeId;

	@Column(name="opt_in_multi_measure")
	private boolean optInMultiMeasure;

	@Column(name="placement_component_mmap")
	private boolean placementComponentMmap;

	@Column(name="placement_component_assess")
	private boolean placementComponentAssess;

	@Column(name="use_self_reported_data_for_mm")
	private boolean useSelfReportedDataForMM;

	@Column(name="mm_decision_logic")
	private String mmDecisionLogic;

	@Column(name="mm_placement_logic")
	private String mmPlacementLogic;

	@Column(name="mm_asigne_placement_login")
	private String mmAssignedPlacementLogic;

	abstract public CompetencyAttributeEnum getCompetencyCode();

	public int getCompetencyAttributeId() {
		return competencyAttributeId;
	}

	public void setCompetencyAttributeId(int subjectAreaId) {
		this.competencyAttributeId = subjectAreaId;
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

	public void setUseSelfReportedDataForMM(boolean userSelfReportedDataForMM) {
		this.useSelfReportedDataForMM = userSelfReportedDataForMM;
	}

	public String getMmAssignedPlacementLogic() {
		return mmAssignedPlacementLogic;
	}

	public void setMmAssignedPlacementLogic(String placementRuleSetRowId) {
		this.mmAssignedPlacementLogic = placementRuleSetRowId;
	}


	public String getMmDecisionLogic() {
		return mmDecisionLogic;
	}

	public void setMmDecisionLogic(String mmDecisionLogic) {
		this.mmDecisionLogic = mmDecisionLogic;
	}

	public String getMmPlacementLogic() {
		return mmPlacementLogic;
	}

	public void setMmPlacementLogic(String mmPlacementLogic) {
		this.mmPlacementLogic = mmPlacementLogic;
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
