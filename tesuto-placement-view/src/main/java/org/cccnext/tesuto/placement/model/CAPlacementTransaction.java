package org.cccnext.tesuto.placement.model;

public class CAPlacementTransaction {

    private String misCode;
    private String cccid;
    private String eppn;
    private CAPlacementSubjectArea subjectArea;

    public String getMisCode() {
        return misCode;
    }

    public void setMisCode(String misCode) {
        this.misCode = misCode;
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

    public CAPlacementSubjectArea getSubjectArea() {
        return subjectArea;
    }

    public void setSubjectArea(CAPlacementSubjectArea subjectArea) {
        this.subjectArea = subjectArea;
    }

    @Override
    public String toString() {
        return "CAPlacementTransaction{" +
                "misCode='" + misCode + '\'' +
                ", cccid='" + cccid + '\'' +
                ", eppn='" + eppn + '\'' +
                ", subjectArea=" + subjectArea +
                '}';
    }
}
