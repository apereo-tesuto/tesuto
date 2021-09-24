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
package org.cccnext.tesuto.delivery.model.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.AssessmentPartNavigationMode;
import org.cccnext.tesuto.content.dto.AssessmentPartSubmissionMode;
import org.cccnext.tesuto.content.model.DeliveryType;
import org.cccnext.tesuto.util.TesutoUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document // Even though it's used in places besides Mongo. It will get ignored
          // in those cases anyway.
@CompoundIndexes({
        @CompoundIndex(name = "userId_contentIdentifier_contentId_idx",
                def = "{'userId' : 1, 'contentIdentifier' : 1, 'contentId' : 1}") })
public class AssessmentSession implements Serializable {

    private static final long serialVersionUID = 1;

    @Id
    private String assessmentSessionId; // primary key

    private String userId;
    private String contentId; // actually an assessmentId, but having an
                              // assessmentId and an assessmentSessionId gets
                              // confusing
    private String contentIdentifier;
    private DeliveryType deliveryType;
    private Map<String, String> assessmentSettings;
    private String currentTaskSetId;
    private List<Activity> activityLog = new ArrayList<>();
    private Date startDate;
    private Date completionDate; // null until the assessment session is completed
    private Date deadline; // could be null
    private String nextSectionId = "";  // section id that needs to be processed next (Has a precondition or branchrule)
    private Integer taskSetCount = 0;
    private Integer taskCount = 0;
    private Integer itemSessionCount = 0;
    private String prevTaskSetId = "";
    private Map<String, String> sequence = new LinkedHashMap<String, String>(); // ComponentId
                                                                                // ->
                                                                                // ComponentId
                                                                                // (AssessmentPart,
                                                                                // AssessmentSection,
                                                                                // AssessmentItemRef)

    Map<String, Outcome> outcomes = new HashMap<>();  // outcomeIdentifier -> Outcome
    Map<String,Double> scoringMap = new HashMap<>();
    Map<String,String> competencyMapOrderIds;
    private String competencyMapDisciplineFromEntryTestlet;

    private List<String> taskSetIds = new ArrayList<>();

    @Transient private Map<String, TaskSet> taskSets = new HashMap<>(); // taskSetId -> TaskSet                                                                // taskSet;
    @Transient private Set<String> taskSetsToSave = new HashSet<>();

    public String getCompetencyMapDisciplineFromEntryTestlet() {
        return competencyMapDisciplineFromEntryTestlet;
    }

    public void setCompetencyMapDisciplineFromEntryTestlet(String competencyMapDisciplineFromEntryTestlet) {
        this.competencyMapDisciplineFromEntryTestlet = competencyMapDisciplineFromEntryTestlet;
    }


    public Outcome getOutcome(String outcomeIdentifier) {
        return outcomes.get(TesutoUtils.encodeKey(outcomeIdentifier));
    }

    public Collection<Outcome> getOutcomes() { return outcomes.values(); }


    public void addOutcome(Outcome outcome) {
        outcomes.put(TesutoUtils.encodeKey(outcome.getOutcomeIdentifier()), outcome);
    }

    public void addOutcomes(Collection<Outcome> outcomes) {
        outcomes.forEach(o -> addOutcome(o));
    }

    public double getScore(String id) {
        return scoringMap.getOrDefault(TesutoUtils.encodeKey(id), 0.0);
    }

    public Map<String, Double> getScoringMap() {
        return scoringMap;
    }

    public void setScore(String id, Double score) {
        scoringMap.put(TesutoUtils.encodeKey(id), score);
    }

    public void setScoringMap(Map<String, Double> scoringMap) {
        this.scoringMap = scoringMap;
    }

    public Map<String, String> getCompetencyMapOrderIds() {
        return competencyMapOrderIds;
    }

    public void setCompetencyMapOrderIds(Map<String, String> competencyMapOrderIds) {
        this.competencyMapOrderIds = competencyMapOrderIds;
    }

    @Transient
    transient private AssessmentDto assessment; // Not to be persisted

    public String getNextSectionId() {
        return nextSectionId;
    }

    public void setNextSectionId(String nextSectionId) {
        this.nextSectionId = nextSectionId;
    }

    public HashMap<String, Double> getTestContextMap() {
        HashMap<String, Double> testContextMap = new HashMap<>();
        getOutcomes().forEach(o -> testContextMap.put(o.getOutcomeIdentifier(), o.getValue()));
        return testContextMap;
    }

    public String getAssessmentSessionId() {
        return assessmentSessionId;
    }

    public void setAssessmentSessionId(String assessmentSessionId) {
        this.assessmentSessionId = assessmentSessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentIdentifier() {
        return contentIdentifier;
    }

    public void setContentIdentifier(String contentIdentifier) {
        this.contentIdentifier = contentIdentifier;
    }

    public Map<String, String> getAssessmentSettings() {
        return assessmentSettings;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public void setAssessmentSettings(Map<String, String> assessmentSettings) {
        this.assessmentSettings = assessmentSettings;
    }

    public Map<String, TaskSet> getTaskSets() {
        return taskSets;
    }

    public void addTaskSet(TaskSet taskSet) {
        incrementTaskSetCount();
        taskSets.put(taskSet.getTaskSetId(), taskSet);
        taskSetIds.add(taskSet.getTaskSetId());
        taskSetsToSave.add(taskSet.getTaskSetId());
    }

    public List<String> getTaskSetIds() {
        return taskSetIds;
    }

    public String getCurrentTaskSetId() {
        return currentTaskSetId;
    }

    public void setCurrentTaskSetId(String taskSetId) {
        this.currentTaskSetId = taskSetId;
    }

    public List<Activity> getActivityLog() {
        return activityLog;
    }

    public void addActivities(Collection<Activity> newActivities) {
        activityLog.addAll(newActivities);
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

    public boolean isCompleted() {
        return completionDate != null;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public AssessmentDto getAssessment() {
        return assessment;
    }

    public void setAssessment(AssessmentDto assessment) {
        this.assessment = assessment;
    }

    public String getNextTaskSetId() {
        return getTaskSetIdAfter(getCurrentTaskSetId());
    }

    public String getTaskSetIdAfter(String taskSetId) {
        if (taskSetId == null) {
            return null;
        }
        Iterator<String> iterator = taskSetIds.iterator();
        while (iterator.hasNext() && !iterator.next().equals(taskSetId)) {
            //nuthin
        }
        return (iterator.hasNext() ? iterator.next() : null);
    }

    public AssessmentPartNavigationMode getNavigationMode() {
        AssessmentPartNavigationMode assessmentPartNavigationMode = null;
        if (assessment != null) {
            assessmentPartNavigationMode = assessment.getAssessmentParts().get(0).getAssessmentPartNavigationMode();
        }
        return assessmentPartNavigationMode;
    }

    public AssessmentPartSubmissionMode getSubmissionMode() {
        AssessmentPartSubmissionMode assessmentPartSubmissionMode = null;
        if (assessment != null) {
            assessmentPartSubmissionMode = assessment.getAssessmentParts().get(0).getAssessmentPartSubmission();
        }
        return assessmentPartSubmissionMode;
    }

    public Integer getTaskSetCount() {
        return taskSetCount;
    }

    public void incrementTaskSetCount() {
        this.taskSetCount = this.taskSetCount + 1;
    }

    public Integer getTaskCount() {
        return taskCount;
    }

    public void incrementTaskCount() {
        this.taskCount = this.taskCount + 1;
    }

    public Integer getItemSessionCount() {
        return itemSessionCount;
    }

    public void incrementItemSessionCount() {
        this.itemSessionCount = this.itemSessionCount + 1;
    }

    public String getPrevTaskSetId() {
        return prevTaskSetId;
    }

    public void setPrevTaskSetId(String prevTaskSetId) {
        this.prevTaskSetId = prevTaskSetId;
    }

    public Map<String, String> getSequence() {
        return sequence;
    }

    public void setSequence(Map<String, String> sequence) {
        this.sequence = sequence;
    }

    public boolean addNewSequence(String sequenceEntry) {
        if (sequenceEntry == null) {
            return true; // No sequence is added.
        }
        String result = sequence.putIfAbsent(sequenceEntry, sequenceEntry);
        return (result == null);
        // If null, return true, value was added
    }

    public void saveTaskSet(String taskSetId) {
        taskSetsToSave.add(taskSetId);
    }

    public void saveTaskSet(TaskSet taskSet) {
        taskSetsToSave.add(taskSet.getTaskSetId());
    }

    public Set<String> getTaskSetsToSave() {
        return taskSetsToSave;
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
