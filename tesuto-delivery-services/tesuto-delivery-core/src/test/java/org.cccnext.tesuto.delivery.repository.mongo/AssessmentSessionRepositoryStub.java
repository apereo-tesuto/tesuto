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
package org.cccnext.tesuto.delivery.repository.mongo;
import java.util.ArrayList;
import java.util.List;

import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class AssessmentSessionRepositoryStub implements AssessmentSessionRepository {
    List<AssessmentSession> assessmentSessions = new ArrayList<>();

    @Override
    public <S extends AssessmentSession> Iterable<S> save(Iterable<S> iterable) {
        for (S s : iterable) {
            this.assessmentSessions.add(s);
        }
        return null;
    }

    @Override
    public List<AssessmentSession> findByUserIdIgnoreCase(String userId) {
        return this.assessmentSessions;
    }

    @Override
    public <S extends AssessmentSession> Iterable<S> save(Iterable<S> iterable) {
        for (S s : iterable) {
            this.assessmentSessions.add(s);
        }
        return null;
    }
}
