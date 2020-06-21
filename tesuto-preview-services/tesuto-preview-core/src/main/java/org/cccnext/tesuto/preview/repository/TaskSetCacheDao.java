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
package org.cccnext.tesuto.preview.repository;

import org.cccnext.tesuto.delivery.model.internal.TaskSet;
import org.cccnext.tesuto.delivery.service.TaskSetDao;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.util.TesutoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;

@Service("taskSetDao")
public class TaskSetCacheDao implements TaskSetDao {

	@Autowired
    private AssessmentSessionCacheDao dao;

    public TaskSetCacheDao(AssessmentSessionCacheDao dao) {
       this.dao = dao;
   }
    
    public Cache cache() {
        return dao.sessionCache();
    }

    /**
     * @param session
     * @return the id of the new TaskSet
     */
    @Override
    public String create(TaskSet session) {
        if (session.getTaskSetId() == null) {
            session.setTaskSetId(TesutoUtils.newId());
        }
        cache().put(session.getTaskSetId(), session);
        return session.getTaskSetId();
    }

    /**
     * Delete an TaskSet from storage
     *
     * @param id
     * @return boolean indicating whether the TaskSet actually
     *         existed or not.
     */
    @Override
    public boolean delete(String id) {
        TaskSet session = cache().get(id, TaskSet.class);
        if (session != null) {
            cache().evict(id);
        }
        return session != null;
    }

    /**
     * @param id
     * @return TaskSet with the desired id
     * @throws NotFoundException
     *             if no TaskSet found
     */
    @Override
    public TaskSet doFind(String id) {
        TaskSet session = cache().get(id, TaskSet.class);
        if (session == null) {
            throw new NotFoundException(id);
        }
        return session;
    }

    /**
     * Update (or create) the TaskSet
     *
     * @param session
     * @return the updated TaskSet
     */
    @Override
    public TaskSet save(TaskSet session) {
        cache().put(session.getTaskSetId(), session);
        return session;
    }

}
