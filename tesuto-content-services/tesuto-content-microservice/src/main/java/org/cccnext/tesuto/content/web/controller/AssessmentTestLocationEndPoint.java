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
package org.cccnext.tesuto.content.web.controller;

import org.cccnext.tesuto.content.controller.AssessmentTestLocationController;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = { "/service/v1/assessments/test-location" })
public class AssessmentTestLocationEndPoint extends BaseController {

	@Autowired
	AssessmentTestLocationController controller;

    @PreAuthorize("hasAuthority('VIEW_ASSESSMENTS_BY_LOCATION_AND_COLLEGE_AFFILIATION')")
    @RequestMapping(value = "{testLocationId}/college_affiliation", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getAssessmentsByTestLocationIdAndCollegeAffiliation(@PathVariable String testLocationId) {
        return controller.getAssessmentsByTestLocationIdAndCollegeAffiliation(getUser(), testLocationId);
    }

    @PreAuthorize("hasAnyAuthority('VIEW_ASSESSMENTS_BY_LOCATION', 'API')")
    @RequestMapping(value="{testLocationId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getAssessmentsByTestLocationId(@PathVariable String testLocationId) {
        return controller.getAssessmentsByTestLocationId(getUser(), testLocationId);
    }
}
