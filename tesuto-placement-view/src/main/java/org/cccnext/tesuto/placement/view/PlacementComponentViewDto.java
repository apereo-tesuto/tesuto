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

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PlacementComponentViewDto implements Serializable {
    private static final long serialVersionUID = 2L;

    private String id;

    private String trackingId;

    private String cccid;

    private Character cb21Code;

    private CB21ViewDto cb21;

    private Integer courseGroup;

    private String collegeId;

    private String triggerData;

    private Integer subjectAreaId;

    private Integer subjectAreaVersion;

    private Set<CourseViewDto> courses;

    private Date createdOn;

    private String assessmentSessionId;

    private String assessmentTitle;

    private Date assessmentDate;

    private String entityTargetClass;
    
    private String mmapRuleId;

    private String mmapVariableSetId;

    private String mmapRuleSetId;

    private String mmapRuleSetTitle;

    private String mmapRuleSetRowId;

    private Integer mmapRowNumber;

    private List<String> mmapCourseCategories;

    private VersionedSubjectAreaViewDto versionedSubjectAreaViewDto;

    private String dataSource;

    private Date dataSourceDate;

    private MmapDataSourceType dataSourceType;
    
    private Double studentAbility;
    
    private String elaIndicator;
    
    private Boolean standalonePlacement;
    
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

    public String getTriggerData() {
        return triggerData;
    }

    public void setTriggerData(String trigger) {
        this.triggerData = trigger;
    }

    public void setSubjectAreaId(Integer subjectAreaId) {
        this.subjectAreaId = subjectAreaId;
    }

    public Set<CourseViewDto> getCourses() {
        return courses;
    }

    public void setCourses(Set<CourseViewDto> courses) {
        this.courses = courses;
    }

    public VersionedSubjectAreaViewDto getVersionedSubjectAreaViewDto() {
        return versionedSubjectAreaViewDto;
    }

    public Integer getSubjectAreaId() {
        return subjectAreaId;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public List<String> getMmapCourseCategories() {
        return mmapCourseCategories;
    }

    public void setMmapCourseCategories(List<String> mmapCourseCategories) {
        this.mmapCourseCategories = mmapCourseCategories;
    }

    public String getAssessmentSessionId() {
        return assessmentSessionId;
    }

    public Integer getSubjectAreaVersion() {
        return subjectAreaVersion;
    }

    public void setSubjectAreaVersion(Integer subjectAreaVersion) {
        this.subjectAreaVersion = subjectAreaVersion;
    }

    public void setVersionedSubjectAreaViewDto(VersionedSubjectAreaViewDto versionedSubjectAreaViewDto) {
        this.versionedSubjectAreaViewDto = versionedSubjectAreaViewDto;
    }

    public void setAssessmentSessionId(String assessmentSessionId) {
        this.assessmentSessionId = assessmentSessionId;
    }

    public String getAssessmentTitle() {
        return assessmentTitle;
    }

    public void setAssessmentTitle(String setAssessmentTitle) {
        this.assessmentTitle = setAssessmentTitle;
    }

    public Date getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(Date setAssessmentDate) {
        this.assessmentDate = setAssessmentDate;
    }

    public String getEntityTargetClass() {
        return entityTargetClass;
    }

    public void setEntityTargetClass(String entityTargetClass) {
        this.entityTargetClass = entityTargetClass;
    }

    public String getMmapVariableSetId() {
        return mmapVariableSetId;
    }

    public void setMmapVariableSetId(String mmapVariableSetId) {
        this.mmapVariableSetId = mmapVariableSetId;
    }

    public String getMmapRuleId() {
        return mmapRuleId;
    }

    public void setMmapRuleId(String mmapRuleId) {
        this.mmapRuleId = mmapRuleId;
    }

    public String getMmapRuleSetId() {
        return mmapRuleSetId;
    }

    public void setMmapRuleSetId(String mmapRuleSetId) {
        this.mmapRuleSetId = mmapRuleSetId;
    }

    public String getMmapRuleSetRowId() {
        return mmapRuleSetRowId;
    }

    public void setMmapRuleSetRowId(String mmapRuleSetRowId) {
        this.mmapRuleSetRowId = mmapRuleSetRowId;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public Date getDataSourceDate() {
        return dataSourceDate;
    }

    public void setDataSourceDate(Date dataSourceDate) {
        this.dataSourceDate = dataSourceDate;
    }

    public MmapDataSourceType getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(MmapDataSourceType dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public Integer getMmapRowNumber() {
        return mmapRowNumber;
    }

    public void setMmapRowNumber(Integer mmapRowNumber) {
        this.mmapRowNumber = mmapRowNumber;
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

    public Double getStudentAbility() {
        return studentAbility;
    }

    public void setStudentAbility(Double studentAbility) {
        this.studentAbility = studentAbility;
    }

    public Boolean getStandalonePlacement() {
        return standalonePlacement;
    }

    public void setStandalonePlacement(Boolean standalonePlacement) {
        this.standalonePlacement = standalonePlacement;
    }

    /**
     * Used in placement rules
     * @return
     */
    public boolean isSelfReported() {
        return this.dataSourceType == MmapDataSourceType.SELF_REPORTED;
    }

    @Override
    public boolean equals(Object o) {
    	boolean isEqual = EqualsBuilder.reflectionEquals(this, o, "courses");
    	if(!isEqual) return false;
    	PlacementComponentViewDto that = (PlacementComponentViewDto) o;
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

    public String getMmapRuleSetTitle() {
        return mmapRuleSetTitle;
    }

    public void setMmapRuleSetTitle(String mmapRuleSetTitle) {
        this.mmapRuleSetTitle = mmapRuleSetTitle;
    }
}
