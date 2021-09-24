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

/**
 * Created by bruce on 11/18/15.
 */
public class TaskViewDto {

    private String taskId;
    private String stimulus;
    private List<ItemSessionViewDto> itemSessions;
    private int taskIndex;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStimulus() {
        return stimulus;
    }

    public void setStimulus(String stimulus) {
        this.stimulus = stimulus;
    }

    public List<ItemSessionViewDto> getItemSessions() {
        return itemSessions;
    }

    public void setItemSessions(List<ItemSessionViewDto> itemSessions) {
        this.itemSessions = itemSessions;
    }

    public int getTaskIndex() {
        return taskIndex;
    }

    public void setTaskIndex(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TaskViewDto))
            return false;

        TaskViewDto taskViewDto = (TaskViewDto) o;

        if (getTaskIndex() != taskViewDto.getTaskIndex())
            return false;
        if (getTaskId() != null ? !getTaskId().equals(taskViewDto.getTaskId()) : taskViewDto.getTaskId() != null)
            return false;
        if (getStimulus() != null ? !getStimulus().equals(taskViewDto.getStimulus())
                : taskViewDto.getStimulus() != null)
            return false;
        return !(getItemSessions() != null ? !getItemSessions().equals(taskViewDto.getItemSessions())
                : taskViewDto.getItemSessions() != null);

    }

    @Override
    public int hashCode() {
        int result = getTaskId() != null ? getTaskId().hashCode() : 0;
        result = 31 * result + (getStimulus() != null ? getStimulus().hashCode() : 0);
        result = 31 * result + (getItemSessions() != null ? getItemSessions().hashCode() : 0);
        result = 31 * result + getTaskIndex();
        return result;
    }

    @Override
    public String toString() {
        return "TaskViewDto{" + "taskId='" + taskId + '\'' + ", stimulus='" + stimulus + '\'' + ", itemSessions="
                + itemSessions + ", taskIndex=" + taskIndex + '}';
    }
}
