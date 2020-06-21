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
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(schema = "public", name = "placement")
public class Placement {

	@Id
	@NotNull
	@Size(max = 100)
	@Column(name = "id", nullable = false, length = 100)
	private String id;

	@Column(name = "cccid")
	private String cccid;

	@Column(name = "cb21_code")
	private Character cb21Code;

	@Column(name = "course_group")
	private Integer courseGroup;

	@Column(name = "college_id")
	private String collegeId;

	@Column(name = "discipline_id")
	private Integer disciplineId;

	@Column(name = "subject_area_name")
	private String subjectAreaName;

	@Column(name = "subject_area_version")
	private Integer subjectAreaVersion;

	@Column(name = "tracking_id")
	private String trackingId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_on")
	private Date createdOn;

	@ManyToMany(fetch= FetchType.LAZY)
	@JoinTable(schema="public", name = "placement_placement_component", joinColumns = {
			@JoinColumn(name = "placement_id") }, inverseJoinColumns = {
			@JoinColumn(name = "placement_component_id") })
	private Set<PlacementComponent> placementComponents = new HashSet<PlacementComponent>();

	@Transient
	private PlacementComponent placementComponentsDisjunctive;

	@Column(name = "is_assigned")
	private boolean isAssigned;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "assigned_date")
	private Date assignedDate;

	@Column(name = "create_rule_set_id")
	private String createRuleSetId;

  @Column(name = "create_rule_set_title")
  private String createRuleSetTitle;

	@Column(name = "assigned_rule_set_id")
	private String assignedRuleSetId;

  @Column(name = "assigned_rule_set_title")
  private String assignedRuleSetTitle;

	@Column(name="ela_indicator")
	private String elaIndicator;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCccid() {
		return cccid;
	}

	public void setCccid(String cccid) {
		this.cccid = cccid;
	}

	public Character getCb21Code() {
		return cb21Code;
	}

	public void setCb21Code(Character cb21Code) {
		this.cb21Code = cb21Code;
	}

	public Integer getCourseGroup() {
		return courseGroup;
	}

	public void setCourseGroup(Integer courseGroup) {
		this.courseGroup = courseGroup;
	}

	public Integer getDisciplineId() {
		return disciplineId;
	}

	public void setDisciplineId(Integer disciplineId) {
		this.disciplineId = disciplineId;
	}

	public Integer getSubjectAreaVersion() {
		return subjectAreaVersion;
	}

	public void setSubjectAreaVersion(Integer subjectAreaVersion) {
		this.subjectAreaVersion = subjectAreaVersion;
	}

	public String getCollegeId() {
		return collegeId;
	}

	public void setCollegeId(String collegeId) {
		this.collegeId = collegeId;
	}

	public String getSubjectAreaName() {
		return subjectAreaName;
	}

	public void setSubjectAreaName(String versionedSubjectAreaId) {
		this.subjectAreaName = versionedSubjectAreaId;
	}

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public PlacementComponent getPlacementComponentsDisjunctive() {
		return placementComponentsDisjunctive;
	}

	public void setPlacementComponentsDisjunctive(PlacementComponent placementComponentsDisjunctive) {
		this.placementComponentsDisjunctive = placementComponentsDisjunctive;
	}

	public boolean isAssigned() {
		return isAssigned;
	}

	public void setAssigned(boolean isAssigned) {
		this.isAssigned = isAssigned;
	}

	public Date getAssignedDate() {
		return assignedDate;
	}

	public void setAssignedDate(Date assignedDate) {
		this.assignedDate = assignedDate;
	}

	public String getCreateRuleSetId() {
		return createRuleSetId;
	}

	public void setCreateRuleSetId(String createRuleSetId) {
		this.createRuleSetId = createRuleSetId;
	}

  public String getCreateRuleSetTitle() {
    return createRuleSetTitle;
  }

  public void setCreateRuleSetTitle(String createRuleSetTitle) {
    this.createRuleSetTitle = createRuleSetTitle;
  }

	public String getAssignedRuleSetId() {
		return assignedRuleSetId;
	}

	public void setAssignedRuleSetId(String assignedRuleSetId) {
		this.assignedRuleSetId = assignedRuleSetId;
	}

  public String getAssignedRuleSetTitle() {
    return assignedRuleSetTitle;
  }

  public void setAssignedRuleSetTitle(String assignedRuleSetTitle) {
    this.assignedRuleSetTitle = assignedRuleSetTitle;
  }

	public String getElaIndicator() {
		return elaIndicator;
	}

	public void setElaIndicator(String elaIndicator) {
		this.elaIndicator = elaIndicator;
	}

	public Set<PlacementComponent> getPlacementComponents() {
		return placementComponents;
	}

	public void setPlacementComponents(Set<PlacementComponent> placements) {
		this.placementComponents = placements;
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
