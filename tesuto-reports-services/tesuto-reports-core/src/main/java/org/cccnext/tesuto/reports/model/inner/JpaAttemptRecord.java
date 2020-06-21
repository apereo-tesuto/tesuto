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
package org.cccnext.tesuto.reports.model.inner;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.cccnext.tesuto.domain.model.AbstractEntity;

@Entity
@Table(schema="public",name = "report_attempt_record")
public class JpaAttemptRecord extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "attempt_id")
    private String attemptId;

    @Column(name = "assessment_id")
    private String assessmentId;
    
    @Column(name = "cccid")
    private String cccid;

    @Column(name = "test_location_id")
    private String testLocationId;
    
    @Column(name = "total_duration")
    private long totalDuration;
    
    @Column(name = "total_duration_formatted")
    private String totalDurationFormatted;

    @Column(name = "test_location_label")
    private String testLocationLabel;
    
    @Column(name = "college_label")
    private String collegeLabel;
    
    @Column(name = "college_id")
    private String collegeId;
    
    @Column(name = "student_ability")
    private Double studentAbility;
    
    @Column(name = "points_scored")
    private Double pointsScored;
    
    @Column(name = "percent_score")
    private Double percentScore;
    
    @Column(name = "odds_success")
    private Double oddsSuccess;
    
    @Column(name = "average_item_difficulty")
    private Double averageItemDifficulty;
    
    @Column(name = "item_difficulty_count")
    private Double itemDifficultyCount;
    
    @Column(name = "reported_scale")
    private Double reportedScale;
    
    
    @Column(name = "results_by_column")
    private String resultsByColumn;

    @Column(name = "testlet_sequence")
    private String testletSequence;
    
    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

    @Column(name = "completion_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date completionDate;

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

    public String getTestLocationId() {
        return testLocationId;
    }

    public void setTestLocationId(String testLocationId) {
        this.testLocationId = testLocationId;
    }

    public String getTestLocationLabel() {
        return testLocationLabel;
    }

    public void setTestLocationLabel(String testLocationLabel) {
        this.testLocationLabel = testLocationLabel;
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

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getTotalDurationFormatted() {
        return totalDurationFormatted;
    }

    public void setTotalDurationFormatted(String totalDurationFormatted) {
        this.totalDurationFormatted = totalDurationFormatted;
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
    
}
