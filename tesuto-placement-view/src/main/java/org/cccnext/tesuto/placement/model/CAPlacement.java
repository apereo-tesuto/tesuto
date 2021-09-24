package org.cccnext.tesuto.placement.model;

import java.util.Date;
import java.util.List;

public class CAPlacement {

    private Character cb21;
    private String courseGroup;
    private Date placementDate;
    private String placementId;
    private CAElaIndicator elaIndicator;
    private Boolean isAssigned;
    private Date assignedDate;
    private List<CAPlacementCourse> courses;
    private List<String> placementComponentIds;

    public Character getCb21() {
        return cb21;
    }

    public void setCb21(Character cb21) {
        this.cb21 = cb21;
    }

    public String getCourseGroup() {
        return courseGroup;
    }

    public void setCourseGroup(String courseGroup) {
        this.courseGroup = courseGroup;
    }

    public Date getPlacementDate() {
        return placementDate;
    }

    public void setPlacementDate(Date placementDate) {
        this.placementDate = placementDate;
    }

    public String getPlacementId() {
        return placementId;
    }

    public void setPlacementId(String placementId) {
        this.placementId = placementId;
    }

    public CAElaIndicator getElaIndicator() {
        return elaIndicator;
    }

    public void setElaIndicator(CAElaIndicator elaIndicator) {
        this.elaIndicator = elaIndicator;
    }

    // note: if re-creating getters/setter do NOT let this get renamed!
    public Boolean getIsAssigned() {
        return isAssigned;
    }

    // note: if re-creating getters/setter do NOT let this get renamed!
    public void setIsAssigned(Boolean assigned) {
        isAssigned = assigned;
    }

    public Date getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(Date assignedDate) {
        this.assignedDate = assignedDate;
    }

    public List<CAPlacementCourse> getCourses() {
        return courses;
    }

    public void setCourses(List<CAPlacementCourse> courses) {
        this.courses = courses;
    }

    public List<String> getPlacementComponentIds() {
        return placementComponentIds;
    }

    public void setPlacementComponentIds(List<String> placementComponentIds) {
        this.placementComponentIds = placementComponentIds;
    }

    @Override
    public String toString() {
        return "CAPlacement{" +
                "cb21=" + cb21 +
                ", courseGroup='" + courseGroup + '\'' +
                ", placementDate=" + placementDate +
                ", placementId='" + placementId + '\'' +
                ", elaIndicator=" + elaIndicator +
                ", isAssigned=" + isAssigned +
                ", assignedDate=" + assignedDate +
                ", courses=" + courses +
                ", placementComponentIds=" + placementComponentIds +
                '}';
    }
}
