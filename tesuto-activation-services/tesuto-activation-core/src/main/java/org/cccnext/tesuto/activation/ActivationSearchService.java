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

import org.cccnext.tesuto.activation.model.Activation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Created by bruce on 8/8/16.
 */
@Service("activationSearchService")
public class ActivationSearchService {

    @Autowired
	ActivationService activationService;

    @Autowired
    ActivationDao dao;

    // This initial value is useful for testing and for local deployments but
    // not for deployment on a multi-node environment
    Cache searchSetCache = new ConcurrentMapCache("searchSetCache");

    public Cache getSearchSetCache() {
        return searchSetCache;
    }

    public void setSearchSetCache(Cache searchSetCache) {
        this.searchSetCache = searchSetCache;
    }

    public ActivationDao getDao() {
        return dao;
    }

    public void setDao(ActivationDao dao) {
        this.dao = dao;
    }

    /**
     * @param searchParameters
     * @return set of all unexpired, uncanceled activations for users who have activations that meet the search criteria
     */
    public Set<Activation> userCentricSearch(SearchParameters searchParameters) {
        SearchParameters parameters = new SearchParameters();
        parameters.setIncludeCanceled(false);
        //A more complicated underlying search query could avoid returning the expired activations that have to be filtered out
        return dao.search(parameters, Optional.of(searchParameters)).stream().filter(act -> act.getStatus() != Activation.Status.EXPIRED).collect(Collectors.toSet());
    }

    /**
     * @param parameters
     *            the SearchParameters
     * @return All activations for the user that meet the parameters. Null
     *         parameters are ignored. At least userId, createdBy or locationId
     *         must be non-null or a runtime exception is thrown.
     */
    public Set<Activation> search(SearchParameters parameters) {
        if (!parameters.isValid()) {
            throw new BadSearchParametersException(parameters);
        }
        return dao.search(parameters);
    }


    /**
     * Create a search set (set of activationId's to be cached) and return an id
     * that can be used to access them later
     *
     * @param parameters
     * @return id of search set
     */
    public String createSearchSet(SearchParameters parameters) {
        // TODO: Add a dao method that just returns the id's (as an
        // optimization)
        Set<String> activationIds = search(parameters).stream().map(Activation::getActivationId)
                .collect(Collectors.toSet());
        String searchId = UUID.randomUUID().toString();
        searchSetCache.put(searchId, activationIds);
        return searchId;
    }

    /**
     * Search on a previously created search set.
     *
     * @param searchId
     * @return Optionally, a set of activations (freshly retrieved from the
     *         database), or Optional.empty if the searchId cannot be found
     */
    public Optional<Set<Activation>> search(String searchId) {
        Set<String> activationIds = searchSetCache.get(searchId, Set.class);
        if (activationIds == null) {
            return Optional.empty();
        } else {
            return Optional.of(activationService.find(activationIds));
        }
    }

    /**
     * Add a new set of activationIds under a search id. Primary use case is to
     * add activationIds to an existing search result for future polling.
     *
     * @param searchId
     * @param activationIds
     * @return true iff the search set already existed
     */
    public boolean putSearchSet(String searchId, Set<String> activationIds) {
        boolean returnval = searchSetCache.get(searchId) != null;
        searchSetCache.put(searchId, activationIds);
        return returnval;
    }

    /**
     * Notification that a searchId can be discarded.
     *
     * @param searchId
     */
    public void deleteSearchSet(String searchId) {
        searchSetCache.evict(searchId);
    }

    /**
     * Retrieve a summary of today's activations at a set of locations.
     *
     * @param query
     * @return a map of maps: locationId -> activation status -> count
     */
    Map<String,Map<Activation.Status,Integer>> summarizeByLocation(SearchParameters query) {
        return dao.summarizeByStatus(query);
    }
}
