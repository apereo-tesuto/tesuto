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
package org.cccnext.tesuto.reports.model;

import java.text.DateFormat;
import java.util.Date;

import javax.persistence.Column;

import org.cccnext.tesuto.domain.dto.Dto;

public class AttemptRecord implements Dto {
    private static final long serialVersionUID = 1L;

    private String attemptId;

    private String assessmentId;

    private String cccid;

    private String testLocationLabel;

    private String testLocationId;

    private String testletSequence;

    private Date startDate;
    
    private long totalDuration = 0L;

    private Date completionDate;
    
    private String totalDurationFormatted;
    
    private String resultsByColumn;
    
    private String collegeLabel;
    
    private String collegeId;
    
    private Double studentAbility;
    
    private Double pointsScored;
    
    private Double percentScore;
    
    private Double oddsSuccess;
    
    private Double averageItemDifficulty;
    
    private Double itemDifficultyCount;
    
    private Double reportedScale;

    public String getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(String attemptId) {
        this.attemptId = attemptId;
    }

    public String getCccid() {
        return cccid;
    }

    public void setCccid(String cccid) {
        this.cccid = cccid;
    }

    public String getTestLocationLabel() {
        return testLocationLabel;
    }

    public void setTestLocationLabel(String testLocationLabel) {
        this.testLocationLabel = testLocationLabel;
    }

    public String getTestLocationId() {
        return testLocationId;
    }

    public void setTestLocationId(String testLocationId) {
        this.testLocationId = testLocationId;
    }

    public String getTestletSequence() {
        return testletSequence;
    }

    public void setTestletSequence(String testletSequence) {
        this.testletSequence = testletSequence;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getResultsByColumn() {
        return resultsByColumn;
    }

    public void setResultsByColumn(String resultsByColumn) {
        this.resultsByColumn = resultsByColumn;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public String getCollegeLabel() {
        return collegeLabel;
    }

    public void setCollegeLabel(String collegeLabel) {
        this.collegeLabel = collegeLabel;
    }

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }

    public Double getStudentAbility() {
        return studentAbility;
    }

    public void setStudentAbility(Double studentAbility) {
        this.studentAbility = studentAbility;
    }

    public Double getPointsScored() {
        return pointsScored;
    }

    public void setPointsScored(Double pointsScored) {
        this.pointsScored = pointsScored;
    }

    public Double getPercentScore() {
        return percentScore;
    }

    public void setPercentScore(Double percentScore) {
        this.percentScore = percentScore;
    }

    public Double getOddsSuccess() {
        return oddsSuccess;
    }

    public void setOddsSuccess(Double oddsSuccess) {
        this.oddsSuccess = oddsSuccess;
    }

    public Double getAverageItemDifficulty() {
        return averageItemDifficulty;
    }

    public void setAverageItemDifficulty(Double averageItemDifficulty) {
        this.averageItemDifficulty = averageItemDifficulty;
    }

    public Double getItemDifficultyCount() {
        return itemDifficultyCount;
    }

    public void setItemDifficultyCount(Double itemDifficultyCount) {
        this.itemDifficultyCount = itemDifficultyCount;
    }

    public Double getReportedScale() {
        return reportedScale;
    }

    public void setReportedScale(Double reportedScale) {
        this.reportedScale = reportedScale;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }
    
    public void sumTotalDuration(long duration) {
        this.totalDuration += duration;
    }
    
    public String getTotalDurationFormatted() {
        return totalDurationFormatted;
    }

    public void setTotalDurationFormatted(String totalDurationFormatted) {
        this.totalDurationFormatted = totalDurationFormatted;
    }

    public String toCsv() {
        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        StringBuilder csv = new StringBuilder();
        csv.append(attemptId).append(",").append(cccid).append(",").append(this.assessmentId).append(",")
                .append(this.testLocationLabel).append(",").append(this.testletSequence).append(",")
                .append(dateFormat.format(this.startDate)).append(",").append(dateFormat.format(this.completionDate))
                .append(",");
        return csv.toString();
    }

}
