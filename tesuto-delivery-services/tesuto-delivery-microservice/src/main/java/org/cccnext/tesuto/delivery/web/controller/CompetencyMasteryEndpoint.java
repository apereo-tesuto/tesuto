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

import javax.servlet.http.HttpSession;

import org.cccnext.tesuto.delivery.controller.CompetencyMasteryController;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("service/v1/assessmentsessions")
public class CompetencyMasteryEndpoint extends BaseController {

    @Autowired
    CompetencyMasteryController controller;

    /**
     *
     * VIEW_MY_COMPETENCY_MASTERY has been deleted from SECURITY_GROUP_SECURITY_PERMISSION table
     * and will be added back in at a future date.  This permission was tied to the student.
     * See flyway script V2.91
     *
     */
    @PreAuthorize("hasAnyAuthority('VIEW_MY_COMPETENCY_MASTERY, VIEW_COMPETENCY_MASTERY')")
    @RequestMapping(value = "{assessmentSessionId}/competency/studentmastery", method = RequestMethod.GET)
    public ResponseEntity<?> assessmentSessionViewCompetencies(HttpSession httpSession,
            @PathVariable("assessmentSessionId") String assessmentSessionId, @RequestParam(required = false) Integer parentLevel) {


        return controller.assessmentSessionViewCompetencies(getUser(), httpSession, assessmentSessionId, parentLevel);
    }
    
    @PreAuthorize("hasAuthority('VIEW_COMPETENCY_MASTERY')")
    @RequestMapping(value = "{assessmentSessionId}/competency/mastery", method = RequestMethod.GET)
    public ResponseEntity<?> counselorAssessmentSessionViewCompetencies(HttpSession httpSession,
            @PathVariable("assessmentSessionId") String assessmentSessionId, @RequestParam(required = false) Integer parentLevel) {

        return controller.counselorAssessmentSessionViewCompetencies(httpSession, assessmentSessionId, parentLevel);
    }
}
