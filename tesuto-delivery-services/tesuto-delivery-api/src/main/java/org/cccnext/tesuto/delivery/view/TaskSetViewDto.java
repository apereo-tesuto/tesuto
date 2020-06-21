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
package org.cccnext.tesuto.delivery.view;

import java.util.List;

import org.cccnext.tesuto.content.dto.AssessmentPartNavigationMode;
import org.cccnext.tesuto.content.dto.AssessmentPartSubmissionMode;

/**
 * Created by bruce on 11/18/15.
 */
public class TaskSetViewDto {

    private String taskSetId;
    private List<TaskViewDto> tasks;
    private int taskSetIndex;
    private AssessmentPartNavigationMode navigationMode;
    private AssessmentPartSubmissionMode submissionMode;
    private Long duration;

    public String getTaskSetId() {
        return taskSetId;
    }

    public void setTaskSetId(String taskSetId) {
        this.taskSetId = taskSetId;
    }

    public List<TaskViewDto> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskViewDto> tasks) {
        this.tasks = tasks;
    }

    public int getTaskSetIndex() {
        return taskSetIndex;
    }

    public void setTaskSetIndex(int taskSetIndex) {
        this.taskSetIndex = taskSetIndex;
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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TaskSetViewDto))
            return false;

        TaskSetViewDto that = (TaskSetViewDto) o;

        if (getTaskSetIndex() != that.getTaskSetIndex())
            return false;
        if (getTaskSetId() != null ? !getTaskSetId().equals(that.getTaskSetId()) : that.getTaskSetId() != null)
            return false;
        if (getTasks() != null ? !getTasks().equals(that.getTasks()) : that.getTasks() != null)
            return false;
        if (getNavigationMode() != that.getNavigationMode())
            return false;
        return getSubmissionMode() == that.getSubmissionMode();

    }

    @Override
    public int hashCode() {
        int result = getTaskSetId() != null ? getTaskSetId().hashCode() : 0;
        result = 31 * result + (getTasks() != null ? getTasks().hashCode() : 0);
        result = 31 * result + getTaskSetIndex();
        result = 31 * result + (getNavigationMode() != null ? getNavigationMode().hashCode() : 0);
        result = 31 * result + (getSubmissionMode() != null ? getSubmissionMode().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TaskSetViewDto{" + "taskSetId='" + taskSetId + '\'' + ", tasks=" + tasks + ", taskSetIndex="
                + taskSetIndex + ", navigationMode=" + navigationMode + ", submissionMode=" + submissionMode + '}';
    }
}
