package org.cccnext.tesuto.placement.model;

import java.util.Date;
import java.util.List;

public class CAPlacementComponent {

    protected Character cb21;
    protected String courseGroup;
    protected Date placementComponentDate;
    protected String placementComponentId;
    protected CAElaIndicator elaIndicator;
    protected String trigger;
    protected List<CAPlacementCourse> courses;
    private CAPlacementComponentType type;

    public CAPlacementComponent(CAPlacementComponentType type) {
        this.type = type;
    }

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

    public Date getPlacementComponentDate() {
        return placementComponentDate;
    }

    public void setPlacementComponentDate(Date placementComponentDate) {
        this.placementComponentDate = placementComponentDate;
    }

    public String getPlacementComponentId() {
        return placementComponentId;
    }

    public void setPlacementComponentId(String placementComponentId) {
        this.placementComponentId = placementComponentId;
    }

    public CAElaIndicator getElaIndicator() {
        return elaIndicator;
    }

    public void setElaIndicator(CAElaIndicator elaIndicator) {
        this.elaIndicator = elaIndicator;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public List<CAPlacementCourse> getCourses() {
        return courses;
    }

    public void setCourses(List<CAPlacementCourse> courses) {
        this.courses = courses;
    }

    public CAPlacementComponentType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "CAPlacementComponent{" +
                "cb21=" + cb21 +
                ", courseGroup='" + courseGroup + '\'' +
                ", placementComponentDate=" + placementComponentDate +
                ", placementComponentId='" + placementComponentId + '\'' +
                ", elaIndicator=" + elaIndicator +
                ", trigger='" + trigger + '\'' +
                ", courses=" + courses +
                ", type=" + type +
                '}';
    }
}
