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
package org.cccnext.tesuto.activation.web.controller;

import java.util.Map;
import java.util.Set;

import org.cccnext.tesuto.activation.ActivationSearchController;
import org.cccnext.tesuto.activation.SearchParameters;
import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping(value = "service/v1/activation-search")
public class ActivationSearchEndpoint extends BaseController {

    @Autowired
    private ActivationSearchController controller;

    @PreAuthorize("hasAuthority('DELETE_ACTIVATION_SEARCH_SET')")
    @RequestMapping(value = "{searchId}", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<?> delete(@PathVariable("searchId") String searchId) {
        return controller.delete(searchId);
    }

    @PreAuthorize("hasAuthority('VIEW_ACTIVATION_SEARCH_SET')")
    @RequestMapping(value = "{searchId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> get(@PathVariable("searchId") String searchId) {
       return controller.get(searchId);
    }

    @PreAuthorize("hasAuthority('CREATE_ACTIVATION_SEARCH_SET')")
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> post(@RequestBody SearchParameters parameters, UriComponentsBuilder uriBuilder) {
        return controller.post(parameters, uriBuilder);
    }

    @PreAuthorize("hasAuthority('FIND_ACTIVATIONS')")
    @RequestMapping(value = "usercentricsearch", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> userCentricSearch(@RequestBody SearchParameters parameters) {
        return controller.userCentricSearch(getUser(), parameters);
    }

    @PreAuthorize("hasAuthority('FIND_ACTIVATIONS')")
    @RequestMapping(value = "locationsearch", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> locationSearch(@RequestBody SearchParameters parameters) {
    	return controller.locationSearch(getUser(), parameters);
    }

    @PreAuthorize("hasAuthority('FIND_ACTIVATIONS')")
    @RequestMapping(value = "quicksearch", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> quickSearch(@RequestBody SearchParameters parameters) {
        return controller.quickSearch(parameters);
    }
    
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value = "search/oauth2", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> search(@RequestBody SearchParameters parameters) {
        return controller.quickSearch(parameters);
    }

    // This controller is used to alter the set of activationIds in the search
    // set
    @PreAuthorize("hasAuthority('UPDATE_ACTIVATION_SEARCH_SET')")
    @RequestMapping(value = "{searchId}", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<?> put(@PathVariable String searchId, @RequestBody Set<String> activationIds) {
        return controller.put(searchId, activationIds);
    }
    
    // This controller is used to alter the set of activationIds in the search
    // set
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value = "{searchId}/oauth2", method = RequestMethod.PUT, produces = "application/json")
    public ResponseEntity<?> putOauth2(@PathVariable String searchId, @RequestBody Set<String> activationIds) {
        return controller.put(searchId, activationIds);
    }


    @PreAuthorize("hasAuthority('VIEW_LOCATION_SUMMARY')")
    @RequestMapping(value="location-summary", method=RequestMethod.POST, produces="application/json")
    public ResponseEntity<Map<String,Map<Activation.Status,Integer>>> summary(@RequestBody SearchParameters parameters) {
        return controller.summary(getUser(), parameters);
    }

}
