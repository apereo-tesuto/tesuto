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
package org.cccnext.tesuto.domain.multiplemeasures;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PlacementComponentActionResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cccid;
    private String trackingId;
    private String transferLevel;
    private Integer levelsBelowTransfer;
    private String collegeId;
    private String subjectArea;
    private String ruleSetId;
    private String ruleId;
    private String ruleSetRowId;
    private String rowNumber;
    private String multipleMeasureVariableSetId;
    private String competencyMapDiscipline;
    private Set<String> programs;
    private String dataSource;
    private Date dataSourceDate;
    private String dataSourceType;
    private Boolean standalonePlacement;
    // minimum level is set to true when activated by insufficient data to
    // validate transfer level below this level
    private Boolean insufficientData = false;

    public String getCccid() {
        return cccid;
    }

    public void setCccid(String cccid) {
        this.cccid = cccid;
    }

    public String getTransferLevel() {
        return transferLevel;
    }

    public void setTransferLevel(String transferLevel) {
        this.transferLevel = transferLevel;
    }

    public Integer getLevelsBelowTransfer() {
        return levelsBelowTransfer;
    }

    public void setLevelsBelowTransfer(Integer levelsBelowTransfer) {
        this.levelsBelowTransfer = levelsBelowTransfer;
    }

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }

    public String getSubjectArea() {
        return subjectArea;
    }
    public void setSubjectArea(String subjectArea) {
        this.subjectArea = subjectArea;
    }

    public String getRuleSetId() {
        return ruleSetId;
    }

    public void setRuleSetId(String ruleSetId) {
        this.ruleSetId = ruleSetId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleSetRowId() {
        return ruleSetRowId;
    }

    public void setRuleSetRowId(String ruleSetRowId) {
        this.ruleSetRowId = ruleSetRowId;
    }

    public String getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(String rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getCompetencyMapDiscipline() {
        return competencyMapDiscipline;
    }

    public void setCompetencyMapDiscipline(String competencyMapDiscipline) {
        this.competencyMapDiscipline = competencyMapDiscipline;
    }

    public Set<String> getPrograms() {
        return programs;
    }

    public void setPrograms(Set<String> programs) {
        this.programs = programs;
    }

    public void addPrograms(Set<String> programs) {
        if (CollectionUtils.isNotEmpty(programs)) {
            if (CollectionUtils.isEmpty(this.programs)) {
                this.programs = new HashSet<>();
            }
            this.programs.addAll(programs);
        }
    }

    public void addProgram(String program) {
        if (StringUtils.isNotBlank(program)) {
            if (CollectionUtils.isEmpty(this.programs)) {
                this.programs = new HashSet<>();
            }
            this.programs.add(program);
        }
    }

    public String getMultipleMeasureVariableSetId() {
        return multipleMeasureVariableSetId;
    }

    public void setMultipleMeasureVariableSetId(String multipleMeasureVariableSetId) {
        this.multipleMeasureVariableSetId = multipleMeasureVariableSetId;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
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

    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public Boolean isStandalonePlacement() {
        return standalonePlacement;
    }

    public void setStandalonePlacement(Boolean standalonePlacement) {
        this.standalonePlacement = standalonePlacement;
    }

    public Boolean getInsufficientData() {
        return insufficientData;
    }

    public void setInsufficientData(Boolean insufficientData) {
        this.insufficientData = insufficientData;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
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
