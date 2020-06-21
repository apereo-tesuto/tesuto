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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.cccnext.tesuto.activation.model.Activation;

/**
 * Created by bruce on 7/22/15.
 */
public interface ActivationDao {

    /**
     * Persist an Activation
     *
     * @param activation
     *            -- an almostValid activation object (an activation object with
     *            a possibly null activationId)
     * @return the activationId
     */
    String create(Activation activation);

    /**
     * Persist a set of activations
     * @param activations
     *   a set of almostValid activation object (an activation object with
     *    a possibly null activationId)
     */
    void create(Collection<Activation> activations);

    /**
     * delete an activation
     *
     * @param activationId
     * @return whether an activation with that id was actually found and deleted
     */
    boolean delete(String activationId);

    /**
     * @param activationIds
     * @return A set of activations with the desired activationIds
     */
    Set<Activation> find(Set<String> activationIds);

    /**
     * Retrieve activations controlling a particular assessment session Id.
     *
     * @param assessmentSessionId
     */
    Set<Activation> findActivationsByAssessmentSessionId(String assessmentSessionId);

    /**
     * Retrieve activations for a particular testEvent
     *
     * @param testEventId
     */
    Set<Activation> findActivationsByTestEventId(int testEventId);

    /**
     * Persist a set of new and a set of existing activations
     * @param newActivations
     * @param updatedActivations
     */
    void persist(Set<Activation>  newActivations, Set<Activation> updatedActivations);

    /**
     * Get a list of activation counts by location, asessment, and proctor.
     *
     * @param from
     *            (start date at which activations start)
     * @param to
     *            (end date at which.
     * @return List of activation counts
     */
    List<ActivationCount> report(Calendar from, Calendar to);


    /**
     * @param parameters
     *            the SearchParameters
     * @return All activations for the user that meet the parameters. Null
     *         parameters are ignored.
     */
    default Set<Activation> search(SearchParameters parameters) {
        return search(parameters, Optional.empty());
    }

    /**
     * @param parameters
     *            the SearchParameters
     * @param userSearchParameters - if present, the search will be restricted to queries for users that also
     *                             have activations in a subquery constructed from userSearchParameters
     * @return All userIds with activations meet the parameters. Null
     *         parameters are ignored.
     */
    Set<Activation> search(SearchParameters parameters, Optional<SearchParameters> userSearchParameters);


    /**
     * @param parameters
     *            the SearchParameters
     * @return All userIds with activations meet the parameters. Null
     *         parameters are ignored.
     */
    Set<String> searchForUserIds(SearchParameters parameters);

    /**
     * Retrieve a count of activations for each status that meet the given search parameters.
     *
     * @param parameters
     * @return a map of maps: locationId -> activation status -> count
     */
    Map<String,Map<Activation.Status,Integer>> summarizeByStatus(SearchParameters parameters);

    /**
     * Change the status of an activation.
     *
     * @param activation
     *            to be changed
     */
    default void update(Activation activation) {
        update(new ArrayList<Activation>(Arrays.asList(activation)));
    }

    /**
     * Change the status of a collection of activations.
     *
     * @param activations
     *            to be changed
     */
    void update(Collection<Activation> activations);

}
