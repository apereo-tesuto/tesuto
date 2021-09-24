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

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ActivationSearchController {

    @Autowired
    private ActivationSearchService service;

    private Set<String> getAuthorizedLocationIds(UserAccountDto user) {
        return user.getTestLocations().stream().map(loc -> loc.getId()).collect(Collectors.toSet());
    }

    public ResponseEntity<?> delete(String searchId) {
        service.deleteSearchSet(searchId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    public ResponseEntity<?> get(String searchId) {
        Optional<Set<Activation>> activations = service.search(searchId);

        if (activations.isPresent()) {
            return new ResponseEntity<>(activations.get(), HttpStatus.OK);

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    public ResponseEntity<?> post(SearchParameters parameters, UriComponentsBuilder uriBuilder) {
        if (parameters.getUserIds() == null || parameters.getUserIds().size() > 1) {
            return new ResponseEntity("Exactly one userId must be supplied", HttpStatus.BAD_REQUEST);
        }
        try {
            String searchId = service.createSearchSet(parameters);
            URI locationUri = uriBuilder.path("/service/v1/activation-search/" + searchId).build().toUri();
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(locationUri);
            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        } catch (BadSearchParametersException e) {
            return new ResponseEntity<>(ActivationController.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> userCentricSearch(UserAccountDto user, SearchParameters parameters) {
        if (user.getTestLocations() == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Set<String> authorizedLocationIds = getAuthorizedLocationIds(user);
        if (parameters.getLocationIds() == null) {
            parameters.setLocationIds(authorizedLocationIds);
        } else if (parameters.getLocationIds().stream().anyMatch(id -> !authorizedLocationIds.contains(id))) {
            return new ResponseEntity<>("Unauthorized location in search request", HttpStatus.FORBIDDEN);
        }
        try {
            Set<Activation> results = service.userCentricSearch(parameters);
            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (BadSearchParametersException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> locationSearch(UserAccountDto user, SearchParameters parameters) {
        Set<String> authorizedLocationIds = getAuthorizedLocationIds(user);
        if (parameters.getLocationIds() == null) {
            parameters.setLocationIds(authorizedLocationIds);
        } else if (parameters.getLocationIds().stream().anyMatch(id -> !authorizedLocationIds.contains(id))) {
            return new ResponseEntity<>("Unauthorized location in search request", HttpStatus.FORBIDDEN);
        }
        try {
            Set<Activation> results = service.search(parameters);
            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (BadSearchParametersException e) {
            return new ResponseEntity<>(ActivationController.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<?> quickSearch(SearchParameters parameters) {
        //Security restriction -- since there is no location based authorization on this endpoint we restrict the search
        //to return results for exactly one user.
        if (parameters.getUserIds() == null || parameters.getUserIds().size() > 1) {
            return new ResponseEntity("Exactly one userId must be supplied", HttpStatus.BAD_REQUEST);
        }
        try {
            Set<Activation> results = service.search(parameters);
            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (BadSearchParametersException e) {
            return new ResponseEntity<>(ActivationController.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    // This controller is used to alter the set of activationIds in the search
    // set
    public ResponseEntity<?> put(String searchId, Set<String> activationIds) {
        boolean exists = service.putSearchSet(searchId, activationIds);
        // Http return code consistent with RFC 723
        return new ResponseEntity<>(exists ? HttpStatus.NO_CONTENT : HttpStatus.CREATED);
    }


    public ResponseEntity<Map<String,Map<Activation.Status,Integer>>> summary(UserAccountDto user, SearchParameters parameters) {
        if (user.getTestLocations() == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Set<String> locationIds = user.getTestLocations().stream()
                .map(location -> location.getId()).collect(Collectors.toSet());
        parameters.setLocationIds(locationIds);
        Map<String,Map<Activation.Status,Integer>> result = service.summarizeByLocation(parameters);
        locationIds.forEach(locationId -> {
            if (result.get(locationId) == null) {
                result.put(locationId, new HashMap<>());
            }
        });
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
