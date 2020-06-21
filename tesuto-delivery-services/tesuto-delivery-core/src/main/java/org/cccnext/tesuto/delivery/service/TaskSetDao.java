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

import org.cccnext.tesuto.delivery.model.internal.TaskSet;
import org.cccnext.tesuto.exception.NotFoundException;

public interface TaskSetDao {

    /**
     *
     * @param taskSet
     * @return the id of the new TaskSet
     */
    String create(TaskSet taskSet);

    /**
     * Delete an taskSet from storage
     * 
     * @param id
     * @return boolean indicating whether the TaskSet actually
     *         existed or not.
     */
    boolean delete(String id);

    /**
     *
     * @param id
     * @return TaskSet with the desired id
     * @throw NotFoundException
     */
    default TaskSet find(String id) {
        if (id == null) {
            throw new NotFoundException("no id given");
        }
        return doFind(id);
    }

    /**
     *
     * @param id
     * @return TaskSet with the desired id
     * @throw NotFoundException
     */
    TaskSet doFind(String id);

    /**
     * Update (or create) the TaskSet
     *
     * @param session
     * @return the updated TaskSet
     */
    TaskSet save(TaskSet session);
}
