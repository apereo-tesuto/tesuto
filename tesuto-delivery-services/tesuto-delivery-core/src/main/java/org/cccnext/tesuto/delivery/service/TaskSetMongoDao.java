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

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.cccnext.tesuto.delivery.model.internal.TaskSet;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.util.TesutoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

/**
 * Created by bruce on 11/10/15.
 */
@Service
public class TaskSetMongoDao implements TaskSetDao {

    @Autowired
    MongoOperations mongoOperations;

    public boolean exists(String id) {
        return mongoOperations.exists(query(where("_id").is(id)), TaskSet.class);
    }

    private String getNewId() {
        return TesutoUtils.newId();
    }

    /**
     * @param session
     * @return the id of the new TaskSet
     */
    @Override
    public String create(TaskSet session) {
        String id = TesutoUtils.newId();
        session.setTaskSetId(id);
        mongoOperations.save(session);
        return id;
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
        if (mongoOperations.findById(id, TaskSet.class) == null) {
            return false;
        } else {
            mongoOperations.remove(query(where("_id").is(id)), TaskSet.class);
            return true;
        }
    }

    /**
     * @param id
     * @return TaskSet with the desired id
     * @throws NotFoundException
     *             if no TaskSet found
     */
    @Override
    public TaskSet doFind(String id) {
        TaskSet session = mongoOperations.findById(id, TaskSet.class);
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
        if (session.getTaskSetId() == null) {
            session.setTaskSetId(getNewId());
        }
        mongoOperations.save(session);
        return session;
    }
}
