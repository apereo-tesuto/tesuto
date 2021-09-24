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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bruce on 11/10/15.
 */
public class Task implements Serializable {

    private static final long serialVersionUID = 1;

    private String taskId;
    private String sectionId; // the item bundle id, if any (otherwise null)
    private String stimulus; // we may not need this, if the stimulus is
                             // determined by the sectionId
    private int taskIndex = 0;
    private Map<String, ItemSession> itemSessions = new HashMap<>(); // itemSessionId
                                                                     // ->
                                                                     // itemSession

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getStimulus() {
        return stimulus;
    }

    public void setStimulus(String stimulus) {
        this.stimulus = stimulus;
    }

    public int getTaskIndex() {
        return taskIndex;
    }

    public void setTaskIndex(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    public ItemSession getItemSession(String itemSessionId) {
        return itemSessions.get(itemSessionId);
    }

    public Collection<ItemSession> getItemSessions() {
        return itemSessions.values();
    }

    public List<ItemSession> getItemSessionsOrderedByIndex() {
        List<ItemSession> list = new ArrayList<>(itemSessions.size());
        list.addAll(itemSessions.values());
        list.sort((session1, session2) -> session1.getItemSessionIndex() - session2.getItemSessionIndex());
        return list;
    }

    public void addItemSession(ItemSession itemSession) {
        itemSessions.put(itemSession.getItemSessionId(), itemSession);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Task))
            return false;

        Task task = (Task) o;

        if (getTaskIndex() != task.getTaskIndex())
            return false;
        if (getTaskId() != null ? !getTaskId().equals(task.getTaskId()) : task.getTaskId() != null)
            return false;
        if (getStimulus() != null ? !getStimulus().equals(task.getStimulus()) : task.getStimulus() != null)
            return false;
        return !(itemSessions != null ? !itemSessions.equals(task.itemSessions) : task.itemSessions != null);

    }

    @Override
    public int hashCode() {
        int result = getTaskId() != null ? getTaskId().hashCode() : 0;
        result = 31 * result + (getStimulus() != null ? getStimulus().hashCode() : 0);
        result = 31 * result + getTaskIndex();
        result = 31 * result + (itemSessions != null ? itemSessions.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Task{" + "taskId='" + taskId + '\'' + ", stimulus='" + stimulus + '\'' + ", taskIndex=" + taskIndex
                + ", itemSessions=" + itemSessions + '}';
    }
}
