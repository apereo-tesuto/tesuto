package org.cccnext.tesuto.placement.model;

import java.util.Date;

public class CAPlacementComponentCCCAssess extends CAPlacementComponent {

    private String assessmentAttemptId;
    private String assessmentTitle;
    private Date completeDate;

    public CAPlacementComponentCCCAssess() {
        super(CAPlacementComponentType.CCCAssess);
    }

    public String getAssessmentAttemptId() {
        return assessmentAttemptId;
    }

    public void setAssessmentAttemptId(String assessmentAttemptId) {
        this.assessmentAttemptId = assessmentAttemptId;
    }

    public String getAssessmentTitle() {
        return assessmentTitle;
    }

    public void setAssessmentTitle(String assessmentTitle) {
        this.assessmentTitle = assessmentTitle;
    }

    public Date getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(Date completeDate) {
        this.completeDate = completeDate;
    }

    @Override
    public String toString() {
        return "CAPlacementComponentCCCAssess{" +
                "assessmentAttemptId='" + assessmentAttemptId + '\'' +
                ", assessmentTitle='" + assessmentTitle + '\'' +
                ", completeDate=" + completeDate +
                ", cb21=" + cb21 +
                ", courseGroup='" + courseGroup + '\'' +
                ", placementComponentDate=" + placementComponentDate +
                ", placementComponentId='" + placementComponentId + '\'' +
                ", elaIndicator=" + elaIndicator +
                ", trigger='" + trigger + '\'' +
                ", courses=" + courses +
                '}';
    }
}
