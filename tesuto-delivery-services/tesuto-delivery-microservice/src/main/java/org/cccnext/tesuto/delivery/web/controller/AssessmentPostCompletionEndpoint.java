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
package org.cccnext.tesuto.delivery.web.controller;

import org.cccnext.tesuto.delivery.controller.AssessmentPostCompletionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("service/v1/delivery/assessment-post-completion")
public class AssessmentPostCompletionEndpoint  {

    @Autowired
    AssessmentPostCompletionController controller;

    @PreAuthorize("hasAnyAuthority('CREATE_PLACEMENT_DECISION')")
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<?> sendRequestPlacementNotifications( @RequestParam("cccid") String cccid, 
            @RequestParam(name="college-miscode", required=false) String collegeId, 
            @RequestParam(name="subject-area", required=false) String subjectArea, 
            @RequestParam(required = false, defaultValue = "true") boolean newPlacementsOnly){
        
        return controller.sendRequestPlacementNotifications(cccid, collegeId, subjectArea, newPlacementsOnly);
    }
    
    @PreAuthorize("hasAuthority('API')")
    @RequestMapping(value="oauth2", method = RequestMethod.PUT)
    public ResponseEntity<?> sendRequestPlacementNotificationsOauth( @RequestParam("cccid") String cccid, 
            @RequestParam(name="college-miscode", required=false) String collegeId, 
            @RequestParam(name="subject-area", required=false) String subjectArea, 
            @RequestParam(required = false, defaultValue = "true") boolean newPlacementsOnly){
        
        return controller.sendRequestPlacementNotifications(cccid, collegeId, subjectArea, newPlacementsOnly);
    }


}
