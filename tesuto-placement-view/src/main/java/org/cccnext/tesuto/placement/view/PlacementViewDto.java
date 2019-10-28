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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class PlacementViewDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String cccid;

	private String eppn;

    private Character cb21Code;

    private CB21ViewDto cb21;

    private Integer courseGroup;

    private String collegeId;

    private Integer disciplineId;

    private String subjectAreaName;

    private Integer subjectAreaVersion;

    private boolean isAssigned;

    private String elaIndicator;

    private Date assignedDate;

    private String createRuleSetId;

    private String createRuleSetTitle;

    private String assignedRuleSetId;

    private String assignedRuleSetTitle;

    private String trackingId;

    private Date createdOn;

    private Set<String> placementComponentIds;
    
    private Set<PlacementComponentViewDto> placementComponents;

    private VersionedSubjectAreaViewDto versionedSubjectAreaViewDto;

    private Set<CourseViewDto> courses;

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

	public String getEppn() {
		return eppn;
	}

	public void setEppn(String eppn) {
		this.eppn = eppn;
	}

	public Character getCb21Code() {
        return cb21Code;
    }

    public void setCb21Code(Character cb21Code) {
        this.cb21Code = cb21Code;
    }

    public CB21ViewDto getCb21() {
        return cb21;
    }
    
    public void setCb21(CB21ViewDto cb21) {
        this.cb21 = cb21;
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

    public Integer getDisciplineId() {
        return disciplineId;
    }

    public void setDisciplineId(Integer disciplineId) {
        this.disciplineId = disciplineId;
    }

    public String getSubjectAreaName() {
        return subjectAreaName;
    }

    public void setSubjectAreaName(String subjectAreaName) {
        this.subjectAreaName = subjectAreaName;
    }

    public Integer getSubjectAreaVersion() {
        return subjectAreaVersion;
    }

    public void setSubjectAreaVersion(Integer subjectAreaVersion) {
        this.subjectAreaVersion = subjectAreaVersion;
    }

    public VersionedSubjectAreaViewDto getVersionedSubjectAreaViewDto() {
        return versionedSubjectAreaViewDto;
    }

    public void setVersionedSubjectAreaViewDto(VersionedSubjectAreaViewDto versionedSubjectAreaViewDto) {
        this.versionedSubjectAreaViewDto = versionedSubjectAreaViewDto;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public void setAssigned(boolean isAssigned) {
        this.isAssigned = isAssigned;
    }

    public String getElaIndicator() {
        return elaIndicator;
    }

    public void setElaIndicator(String elaIndicator) {
        this.elaIndicator = elaIndicator;
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

    public Set<String> getPlacementComponentIds() {
        return placementComponentIds;
    }

    public void setPlacementComponentIds(Set<String> placementComponentIds) {
        this.placementComponentIds = placementComponentIds;
    }

    public void addPlacementComponentId(String placementComponentId) {
        if (placementComponentIds == null) {
            placementComponentIds = new HashSet<>();
        }
        placementComponentIds.add( placementComponentId);
    }

    public Set<PlacementComponentViewDto> getPlacementComponents() {
        return placementComponents;
    }

    public void setPlacementComponents(Set<PlacementComponentViewDto> placementComponents) {
        this.placementComponents = placementComponents;
    }

    public Set<CourseViewDto> getCourses() { return courses; }

    public void setCourses(Set<CourseViewDto> courses) { this.courses = courses;}

    
    @Override
    public boolean equals(Object o) {
    	boolean isEqual = EqualsBuilder.reflectionEquals(this, o, "courses");
    	if(!isEqual) return false;
    	PlacementViewDto that = (PlacementViewDto) o;
    	if (coursesIsEmpty(courses) && coursesIsEmpty(that.courses)) return true;
    	return courses != null ? courses.equals(that.courses) : that.courses == null;
    }
    
    private Boolean coursesIsEmpty(Set<CourseViewDto> courses) {
    	return courses == null || courses.isEmpty() ? true : false;
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
