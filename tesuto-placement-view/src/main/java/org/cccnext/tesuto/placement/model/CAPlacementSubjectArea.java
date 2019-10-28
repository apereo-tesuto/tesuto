package org.cccnext.tesuto.placement.model;

import java.util.List;

public class CAPlacementSubjectArea {

    private CACompetencyMapDiscipline competencyMapDiscipline;
    private Integer subjectAreaId;
    private Integer subjectAreaVersion;
    private String title;
    private String sisTestName;
    private Boolean optInMultipleMeasures;
    private Boolean optInMmap;
    private Boolean optInSelfReported;
    private List<CAPlacement> placements;
    private List<CAPlacementComponent> placementComponents;

    public CACompetencyMapDiscipline getCompetencyMapDiscipline() {
        return competencyMapDiscipline;
    }

    public void setCompetencyMapDiscipline(CACompetencyMapDiscipline competencyMapDiscipline) {
        this.competencyMapDiscipline = competencyMapDiscipline;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSisTestName() {
        return sisTestName;
    }

    public void setSisTestName(String sisTestName) {
        this.sisTestName = sisTestName;
    }

    public Boolean getOptInMultipleMeasures() {
        return optInMultipleMeasures;
    }

    public void setOptInMultipleMeasures(Boolean optInMultipleMeasures) {
        this.optInMultipleMeasures = optInMultipleMeasures;
    }

    public Boolean getOptInMmap() {
        return optInMmap;
    }

    public void setOptInMmap(Boolean optInMmap) {
        this.optInMmap = optInMmap;
    }

    public Boolean getOptInSelfReported() {
        return optInSelfReported;
    }

    public void setOptInSelfReported(Boolean optInSelfReported) {
        this.optInSelfReported = optInSelfReported;
    }

    public List<CAPlacement> getPlacements() {
        return placements;
    }

    public void setPlacements(List<CAPlacement> placements) {
        this.placements = placements;
    }

    public List<CAPlacementComponent> getPlacementComponents() {
        return placementComponents;
    }

    public void setPlacementComponents(List<CAPlacementComponent> placementComponents) {
        this.placementComponents = placementComponents;
    }

    @Override
    public String toString() {
        return "CAPlacementSubjectArea{" +
                "competencyMapDiscipline=" + competencyMapDiscipline +
                ", subjectAreaId=" + subjectAreaId +
                ", subjectAreaVersion=" + subjectAreaVersion +
                ", title='" + title + '\'' +
                ", sisTestName='" + sisTestName + '\'' +
                ", optInMultipleMeasures=" + optInMultipleMeasures +
                ", optInMmap=" + optInMmap +
                ", optInSelfReported=" + optInSelfReported +
                ", placements=" + placements +
                ", placementComponents=" + placementComponents +
                '}';
    }
}
