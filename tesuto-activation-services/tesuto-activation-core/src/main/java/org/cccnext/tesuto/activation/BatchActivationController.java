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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.util.TesutoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class BatchActivationController {

    @Autowired
    private ActivationService service;

    @Autowired
    private ActivationSearchService searchService;

    private String createActivation(String currentUserId, ProtoActivation activation) throws ImproperActivationException {
        String errors = "";
        if (activation.getAssessmentScopedIdentifier() == null) {
            errors += "AssessmentId must be specified.  ";
        }
        if (activation.getUserId() == null) {
            errors += "UserId must be specified.  ";
        }
        if (errors.length() > 0) {
            throw new ImproperActivationException(errors);
        }
        return service.create(currentUserId, activation);
    }

    public ResponseEntity<?> post(UserAccountDto user, String currentUserId, Set<ProtoActivation> activations, UriComponentsBuilder uriBuilder) {

        if (user.getTestLocations() == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        Set<String> authorizedLocationIds = user.getTestLocations().stream().map(loc -> loc.getId()).collect(Collectors.toSet());

        if (activations.stream().anyMatch(act -> act.getLocationId() == null || !authorizedLocationIds.contains(act.getLocationId()))) {
            return new ResponseEntity<>("Unauthorized Location", HttpStatus.FORBIDDEN);
        }

        List<String> errors = new ArrayList<String>(activations.size());
        Set<String> activationIds = new HashSet<String>(activations.size());
        activations.forEach(activation -> {
            activation.setBulk(true);
            try {
                activationIds.add(createActivation(currentUserId, activation));
            } catch (ImproperActivationException e) {
                errors.add(e.getMessage());
            }
        });

        HttpStatus status = (errors.size() == 0 ? HttpStatus.CREATED
                : activationIds.size() > 0 ? HttpStatus.MULTI_STATUS : HttpStatus.BAD_REQUEST);

        HttpHeaders headers = new HttpHeaders();
        if (activationIds.size() > 0) {
            String searchId = TesutoUtils.newId();
            searchService.putSearchSet(searchId, activationIds);
            URI locationUri = uriBuilder.path("/service/v1/activation-search/" + searchId).build().toUri();
            headers.setLocation(locationUri);
        }

        return new ResponseEntity<>(headers, status);
    }
}
