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
package org.cccnext.tesuto.delivery.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.model.internal.TaskSet;

public interface TaskReader {
	
	
    public TaskSetDao getTaskSetDao();
    
	default public List<TaskSet> allTaskSets(AssessmentSession session) {
        return session.getTaskSetIds().stream().map(id ->  getTaskSet(id, session)).collect(Collectors.toList());
    }
	
    default public TaskSet getTaskSet(String taskSetId, AssessmentSession session) {
        if (taskSetId == null) {
            return null;
        }
        Map<String,TaskSet> cache = session.getTaskSets();
        if (cache.get(taskSetId) == null) {
            cache.put(taskSetId, getTaskSetDao().find(taskSetId));
        }
        return cache.get(taskSetId);
    }

}
