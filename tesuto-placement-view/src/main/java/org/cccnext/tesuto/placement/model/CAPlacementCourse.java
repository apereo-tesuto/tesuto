package org.cccnext.tesuto.placement.model;

public class CAPlacementCourse {

    private String subject;
    private String number;
    private String sisTestMappingLevel;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSisTestMappingLevel() {
        return sisTestMappingLevel;
    }

    public void setSisTestMappingLevel(String sisTestMappingLevel) {
        this.sisTestMappingLevel = sisTestMappingLevel;
    }

    @Override
    public String toString() {
        return "CAPlacementCourse{" +
                "subject='" + subject + '\'' +
                ", number='" + number + '\'' +
                ", sisTestMappingLevel='" + sisTestMappingLevel + '\'' +
                '}';
    }
}
