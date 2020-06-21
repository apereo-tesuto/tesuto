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

import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.service.AssessmentSessionNotFoundException;

public interface AssessmentSessionDao {

    /**
     *
     * @param session
     * @return the id of the new AssessmentSession
     */
    String create(AssessmentSession session);

    /**
     * Delete an assessment session from storage
     * 
     * @param id
     * @return boolean indicating whether the assessment session actually
     *         existed or not.
     */
    boolean delete(String id);

    /**
     *
     * @param id
     * @return assessment session with the desired id
     * @throws AssessmentSessionNotFoundException
     *             if no assessment session found
     */
    AssessmentSession find(String id);

    /**
     * Update (or create) the assessment session
     * 
     * @param session
     * @return the updated AssessmentSession
     */
    AssessmentSession save(AssessmentSession session);

    List<AssessmentSession> search(DeliverySearchParameters search);
}
