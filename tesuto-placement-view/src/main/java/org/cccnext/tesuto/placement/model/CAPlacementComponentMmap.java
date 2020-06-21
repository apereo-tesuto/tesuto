package org.cccnext.tesuto.placement.model;

import java.util.Date;
import java.util.List;

public class CAPlacementComponentMmap extends CAPlacementComponent {

    private String dataSource;
    private CADataSourceType dataSourceType;
    private Date dataSourceDate;
    private List<String> mmapQualifiedCourses;
    private Character mmapCb21Code;

    public CAPlacementComponentMmap() {
        super(CAPlacementComponentType.Mmap);
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public CADataSourceType getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(CADataSourceType dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public Date getDataSourceDate() {
        return dataSourceDate;
    }

    public void setDataSourceDate(Date dataSourceDate) {
        this.dataSourceDate = dataSourceDate;
    }

    public List<String> getMmapQualifiedCourses() {
        return mmapQualifiedCourses;
    }

    public void setMmapQualifiedCourses(List<String> mmapQualifiedCourses) {
        this.mmapQualifiedCourses = mmapQualifiedCourses;
    }

    public Character getMmapCb21Code() {
        return mmapCb21Code;
    }

    public void setMmapCb21Code(Character mmapCb21Code) {
        this.mmapCb21Code = mmapCb21Code;
    }

    @Override
    public String toString() {
        return "CAPlacementComponentMmap{" +
                "dataSource='" + dataSource + '\'' +
                ", dataSourceType=" + dataSourceType +
                ", dataSourceDate=" + dataSourceDate +
                ", mmapQualifiedCourses=" + mmapQualifiedCourses +
                ", mmapCb21Code=" + mmapCb21Code +
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
