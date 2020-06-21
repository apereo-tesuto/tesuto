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

import org.cccnext.tesuto.content.controller.AssessmentMetadataController;
import org.cccnext.tesuto.content.service.AssessmentService;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by jasonbrown on 2/11/16.
 */
@Controller
@RequestMapping(value = { "/service/v1/assessment-metadata" })
public class AssessmentMetadataEndPoint extends BaseController {

    @Autowired
    private AssessmentMetadataController controller;

    @Autowired
    private AssessmentService assessmentService;

    @PreAuthorize("hasAuthority('VIEW_ASSESSMENT_METADATA')")
    @RequestMapping(value = "{activationId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getMetadataById(@PathVariable("activationId") String activationId) {
        return controller.getMetadataById(getCurrentUsername(), activationId);
    }
}
