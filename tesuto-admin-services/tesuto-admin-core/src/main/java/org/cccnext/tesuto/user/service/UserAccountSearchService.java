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
package org.cccnext.tesuto.user.service;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.admin.form.SearchParameters;
import org.cccnext.tesuto.user.model.UserAccount;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by bruce on 6/3/16.
 */
@Service
public class UserAccountSearchService {

    @PersistenceContext
    @Qualifier(value = "entityManagerFactoryAdmin")
    EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<UserAccount> search(SearchParameters parameters) {
        List<String> clauses = new ArrayList<>();
        Map<String,Object> queryParameters = new HashMap<>();

        clauses.add("u.deleted = false");

        if (parameters.getFirstName() != null) {
            clauses.add("lower(u.firstName) = :firstName");
            queryParameters.put("firstName", parameters.getFirstName().toLowerCase());
        }
        if (parameters.getLastName() != null) {
            clauses.add("lower(u.lastName) = :lastName");
            queryParameters.put("lastName", parameters.getLastName().toLowerCase());
        }
        
        if (!CollectionUtils.isEmpty(parameters.getUsernames())) {
            clauses.add("lower(u.username) in (:usernames)");
            queryParameters.put("usernames", parameters.getUsernames().stream().map( u -> u.toLowerCase()).collect(Collectors.toList()));
        }
        
        if (!CollectionUtils.isEmpty(parameters.getCollegeIds())) {
            clauses.add("exists (select uc from u.userAccountColleges uc where uc.college.cccId in (:collegeIds))");
            queryParameters.put("collegeIds", parameters.getCollegeIds());
        }

        // TODO: Restrict by district association as well.
        if (clauses.size() == 1) {
            return Collections.emptyList();
        }
        String queryString = " from UserAccount u where " + clauses.stream().collect(Collectors.joining(" and "));
        Query query = entityManager.createQuery(queryString);
        queryParameters.entrySet().forEach(entry -> query.setParameter(entry.getKey(), entry.getValue()));
        // TODO: Consider catching any possible exceptions?
        List<UserAccount> userAccountList = query.getResultList();
        return userAccountList;
    }

}
