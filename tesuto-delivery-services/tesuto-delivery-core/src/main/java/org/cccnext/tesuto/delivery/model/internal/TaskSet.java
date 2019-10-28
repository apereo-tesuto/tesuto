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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.dto.AssessmentPartNavigationMode;
import org.cccnext.tesuto.content.dto.AssessmentPartSubmissionMode;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by bruce on 11/9/15.
 */
public class TaskSet implements Serializable {

    private static final long serialVersionUID = 1;

    @Id
    private String taskSetId;

    private AssessmentPartNavigationMode navigationMode;
    private AssessmentPartSubmissionMode submissionMode;
    private int taskSetIndex = 0;
    private Map<String, Task> tasks = new HashMap<>(); // taskId -> Task
    private Date startDate;
    private Date updateDate;
    private Date completionDate; // non-null if the task is completed, null
                                 // otherwise
    private Long duration;

    public String getTaskSetId() {
        return taskSetId;
    }

    public void setTaskSetId(String taskSetId) {
        this.taskSetId = taskSetId;
    }

    public AssessmentPartNavigationMode getNavigationMode() {
        return navigationMode;
    }

    public void setNavigationMode(AssessmentPartNavigationMode navigationMode) {
        this.navigationMode = navigationMode;
    }

    public AssessmentPartSubmissionMode getSubmissionMode() {
        return submissionMode;
    }

    public void setSubmissionMode(AssessmentPartSubmissionMode submissionMode) {
        this.submissionMode = submissionMode;
    }

    public Task getTask(String taskId) {
        return tasks.get(taskId);
    }

    public List<Task> getTasksOrderedByIndex() {
        List<Task> taskList = new ArrayList<>(tasks.size());
        taskList.addAll(tasks.values());
        taskList.sort((task1, task2) -> task1.getTaskIndex() - task2.getTaskIndex());
        return taskList;
    }

    public Collection<Task> getTasks() {
        return tasks.values();
    }

    public int getTaskSetIndex() {
        return taskSetIndex;
    }

    public void setTaskSetIndex(int taskSetIndex) {
        this.taskSetIndex = taskSetIndex;
    }

    public void addTask(Task task) {
        tasks.put(task.getTaskId(), task);
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public boolean isCompleted() {
        return this.completionDate != null;
    }

    public List<ItemSession> getAllItemSessions() {
        return getTasks().stream().flatMap( task -> task.getItemSessions().stream() ).collect(Collectors.toList());
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
