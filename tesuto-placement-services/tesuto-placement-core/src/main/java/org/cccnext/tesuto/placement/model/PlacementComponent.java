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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Inheritance
@DiscriminatorColumn(name = "placement_component_type")
@Table(schema = "public", name = "placement_component")
public class PlacementComponent implements Serializable {

	private static final long serialVersionUID = 2L;

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

	@Column(name = "tracking_id")
	private String trackingId;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(schema = "public", name = "placement_placement_component", joinColumns = {
			@JoinColumn(name = "placement_component_id") }, inverseJoinColumns = { @JoinColumn(name = "placement_id") })
	private Set<Placement> placements = new HashSet<Placement>();

	@Column(name = "trigger_data")
	private String triggerData;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="created_on")
	private Date createdOn;

	@Column(name="subject_area_id")
	private Integer subjectAreaId;

	@Column(name="subject_area_version")
	private Integer subjectAreaVersion;

	@Column(name="ela_indicator")
	private String elaIndicator;

	@OneToOne
  @JoinColumns({
    @JoinColumn(name = "subject_area_id", updatable = false, insertable = false),
    @JoinColumn(name = "subject_area_version", updatable = false, insertable = false)
  })
  VersionedSubjectArea versionedSubjectArea;

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

	public String getCollegeId() {
		return collegeId;
	}

	public void setCollegeId(String collegeId) {
		this.collegeId = collegeId;
	}

	public String getTriggerData() {
		return triggerData;
	}

	public void setTriggerData(String trigger) {
		this.triggerData = trigger;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Integer getSubjectAreaId() {
		return subjectAreaId;
	}

	public void setSubjectAreaId(Integer subjectAreaId) {
		this.subjectAreaId = subjectAreaId;
	}

	public Integer getSubjectAreaVersion() {
		return subjectAreaVersion;
	}

	public void setSubjectAreaVersion(Integer subjectAreaVersion) {
		this.subjectAreaVersion = subjectAreaVersion;
	}

	public String getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(String trackingId) {
		this.trackingId = trackingId;
	}

	public String getElaIndicator() {
		return elaIndicator;
	}

	public void setElaIndicator(String elaIndicator) {
		this.elaIndicator = elaIndicator;
	}

	public Set<Placement> getPlacements() {
		return placements;
	}

	public void setPlacements(Set<Placement> placements) {
		this.placements = placements;
	}

  public VersionedSubjectArea getVersionedSubjectArea() {
    return versionedSubjectArea;
  }

  public void setVersionedSubjectArea(VersionedSubjectArea versionedSubjectArea) {
	  if (versionedSubjectArea != null) {
      setSubjectAreaId(versionedSubjectArea.getDisciplineId());
      setSubjectAreaVersion(versionedSubjectArea.getVersion());
    }
    this.versionedSubjectArea = versionedSubjectArea;
  }

  @Override
	public boolean equals(Object o) {
		return EqualsBuilder.reflectionEquals(this, o, "placements");
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, "placements");
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
