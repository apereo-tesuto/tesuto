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
package org.cccnext.tesuto.activation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by bruce on 7/27/15.
 */
public class MongoActivationDao implements ActivationDao {

    @Autowired
    MongoOperations mongoOperations;

    public MongoActivationDao() {
    }


    public MongoActivationDao(MongoOperations mongoOperations){
        this.mongoOperations = mongoOperations;
    }


    private String getNewActivationId() {
        String id = null;
        do {
            id = UUID.randomUUID().toString();
        } while (mongoOperations.exists(query(where("_id").is(id)), Activation.class));
        return id;
    }


    /**
     * Persist an Activation
     *
     * @param activation -- an almostValid activation object (an activation object with a possibly null activationId)
     * @return the activationId
     */
    @Override
    public String create(Activation activation) {
        activation.setActivationId(getNewActivationId());
        mongoOperations.save(activation);
        return activation.getActivationId();
    }

    /**
     * Cancel an activation (soft delete)
     *
     * @param activationId
     */
    @Override
    public boolean delete(String activationId) {
        if (mongoOperations.findById(activationId, Activation.class) == null) {
            return false;
        } else {
            mongoOperations.remove(query(where("_id").is(activationId)), Activation.class);
            return true;
        }
    }


    /**
     * @param activationIds
     * @return A set of activations with the desired activationIds
     */
    @Override
    public Set<Activation> find(Set<String> activationIds) {
        if (activationIds.size() == 0) {
            return new HashSet<Activation>(0);
        } else {
            return new HashSet<>(mongoOperations.find(query(where("_id").in(activationIds)), Activation.class));
        }
    }

    /**
     * @param parameters  the SearchParameters
     * @return All activations for the user that meet the parameters. Null parameters are ignored.
     */
    public Set<Activation> search(SearchParameters parameters) {

        List<Criteria> criterias = new ArrayList<>();

        if (parameters.getUserIds() != null) {
            if (parameters.getUserIds().size() == 0) {
                return new HashSet<>(0);
            }
            criterias.add(where("userId").in(parameters.getUserIds()));
        }

        if (parameters.getLocationIds() != null) {
            if (parameters.getLocationIds().size() == 0) {
                return new HashSet<>(0);
            }
            criterias.add(where("locationId").in(parameters.getLocationIds()));
        }

        if (parameters.getCreatorIds() != null) {
            if (parameters.getCreatorIds().size() == 0) {
                return new HashSet<>(0);
            }
            criterias.add(where("creatorId").in(parameters.getCreatorIds()));
        }

        if (parameters.getAssessmentIds() != null) {
            if (parameters.getAssessmentIds().size() == 0) {
                return new HashSet<Activation>(0);
            }
            criterias.add(where("assessmentId").in(parameters.getAssessmentIds()));
        }

        Criteria criteria = criterias.get(0);
        for (int i=1; i<criterias.size(); ++i) {
            criteria = criteria.andOperator(criterias.get(i));
        }

        if (!parameters.isIncludeCanceled()) {
            criteria = criteria.andOperator(where("status").ne(Activation.Status.DEACTIVATED));
        }
        if (parameters.getMaxStartDate() != null) {
            criteria = criteria.andOperator(where("startDate").lte(parameters.getMaxStartDate()));
        }
        if (parameters.getMinStartDate() != null) {
            criteria = criteria.andOperator(where("startDate").gte(parameters.getMinStartDate()));
        }
        if (parameters.getMaxEndDate() != null) {
            criteria = criteria.andOperator(where("endDate").lte(parameters.getMaxEndDate()));
        }
        if (parameters.getMinEndDate() != null) {
            criteria = criteria.andOperator(where("endDate").gte(parameters.getMinEndDate()));
        }
        if (parameters.getMaxCreateDate() != null) {
            criteria = criteria.andOperator(where("createDate").lte(parameters.getMaxCreateDate()));
        }
        if (parameters.getMinCreateDate() != null) {
            criteria = criteria.andOperator(where("createDate").gte(parameters.getMinCreateDate()));
        }

        return new HashSet<>(mongoOperations.find(query(criteria), Activation.class));
    }


    @Override
    public void update(Activation activation) {
        mongoOperations.save(activation);
    }
}
