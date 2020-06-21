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

import java.util.ArrayList;
import java.util.List;

import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.service.AssessmentSessionNotFoundException;
import org.cccnext.tesuto.delivery.service.DeliverySearchParameters.ValidParameters;
import org.cccnext.tesuto.util.TesutoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.amazonaws.util.CollectionUtils;

/**
 * Created by bruce on 11/10/15.
 */
@Service("assessmentSessionMongoDao")
public class AssessmentSessionMongoDao implements AssessmentSessionDao {

    @Autowired
    MongoOperations mongoOperations;

    public boolean exists(String id) {
        return mongoOperations.exists(query(where("_id").is(id)), AssessmentSession.class);
    }

    private String getNewId() {
        // Perhaps the exists check is overkill
        // do { id = TesutoUtils.newId();} while (exists(id));
        return TesutoUtils.newId();
    }

    /**
     * @param session
     * @return the id of the new AssessmentSession
     */
    @Override
    public String create(AssessmentSession session) {
        String id = TesutoUtils.newId();
        session.setAssessmentSessionId(id);
        mongoOperations.save(session);
        return id;
    }

    /**
     * Delete an assessment session from storage
     *
     * @param id
     * @return boolean indicating whether the assessment session actually
     *         existed or not.
     */
    @Override
    public boolean delete(String id) {
        if (mongoOperations.findById(id, AssessmentSession.class) == null) {
            return false;
        } else {
            mongoOperations.remove(query(where("_id").is(id)), AssessmentSession.class);
            return true;
        }
    }

    /**
     * @param id
     * @return assessment session with the desired id
     * @throws AssessmentSessionNotFoundException
     *             if no assessment session found
     */
    @Override
    public AssessmentSession find(String id) {
        AssessmentSession session = mongoOperations.findById(id, AssessmentSession.class);
        if (session == null) {
            throw new AssessmentSessionNotFoundException(id);
        }
        return session;
    }

    /**
     * Update (or create) the assessment session
     *
     * @param session
     * @return the updated AssessmentSession
     */
    @Override
    public AssessmentSession save(AssessmentSession session) {
        if (session.getAssessmentSessionId() == null) {
            session.setAssessmentSessionId(getNewId());
        }
        mongoOperations.save(session);
        return session;
    }

    @Override
    public List<AssessmentSession> search(DeliverySearchParameters search) {
        if(!search.isUsingIndexedSearch()){
            throw new IllegalArgumentException(String.format("Search must contain one of the indexed attributes", search));
        }

        if (search.hasValidParameters() == ValidParameters.INVALID
                || search.hasValidParameters() == ValidParameters.EMPTY) {
            return new ArrayList<>(0);
        }
        Criteria criteria = null;

        if (search.contentIds != null) {
            criteria = where("contentId").in(search.contentIds);
        }

        if (search.contentIdentifiers != null) {
            if (criteria == null) {
                criteria = where("contentIdentifier").in(search.contentIdentifiers);
            } else {
                criteria.and("contentIdentifier").in(search.contentIdentifiers);
            }
        }

        if (search.userIds != null) {
            if (criteria == null) {
                criteria = where("userId").in(search.userIds);
            } else {
                criteria.and("userId").in(search.userIds);
            }
        }

        if (search.ids != null) {
            if (criteria == null) {
                criteria = where("_id").in(search.ids);
            } else {
                criteria.and("_id").in(search.ids);
            }
        }

        if (search.startDateLowerBound != null && search.startDateUpperBound != null) {
            if (criteria == null) {
                criteria =
                        new Criteria().andOperator( Criteria.where("startDate").lt(search.startDateUpperBound),
                        Criteria.where("startDate").gte(search.startDateLowerBound)
                    );
            } else {
                criteria.andOperator(
                        Criteria.where("startDate").lt(search.startDateUpperBound),
                        Criteria.where("startDate").gte(search.startDateLowerBound)
                    );
            }

        } else if (search.startDateLowerBound != null) {
            if (criteria == null) {
                criteria = where("startDate").gte(search.startDateLowerBound);
            } else {
                criteria.and("startDate").gte(search.startDateLowerBound);
            }
        } else if (search.startDateUpperBound != null) {
            if (criteria == null) {
                criteria = where("startDate").lt(search.startDateUpperBound);
            } else {
                criteria.and("startDate").lt(search.startDateUpperBound);
            }
        }

        if (search.completionDateLowerBound != null && search.completionDateUpperBound != null) {
            if (criteria == null) {
                criteria =
                        new Criteria().andOperator( Criteria.where("completionDate").lt(search.completionDateUpperBound),
                        Criteria.where("completionDate").gte(search.completionDateLowerBound)
                    );
            } else {
                criteria.andOperator(
                        Criteria.where("completionDate").lt(search.completionDateUpperBound),
                        Criteria.where("completionDate").gte(search.completionDateLowerBound)
                    );
            }

        } else if (search.completionDateLowerBound != null) {
            if (criteria == null) {
                criteria = where("completionDate").gte(search.completionDateLowerBound);
            } else {
                criteria.and("completionDate").gte(search.completionDateLowerBound);
            }
        } else if (search.completionDateUpperBound != null) {
            if (criteria == null) {
                criteria = where("completionDate").lt(search.completionDateUpperBound);
            } else {
                criteria.and("completionDate").lt(search.completionDateUpperBound);
            }
        }

        if (criteria == null) {
            return new ArrayList<AssessmentSession>();
        }
        Query query = query(criteria);
        if (search.limit != null) {
            query.limit(search.limit);
        }
        if (search.skip != null) {
            query.skip(search.skip);
        }

        if (!CollectionUtils.isNullOrEmpty(search.fields)) {

            search.fields.forEach(f -> query.fields().include(f));
        }
        return mongoOperations.find(query, AssessmentSession.class);
    }
}
