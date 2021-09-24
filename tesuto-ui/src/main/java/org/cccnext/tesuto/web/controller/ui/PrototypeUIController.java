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
package org.cccnext.tesuto.web.controller.ui;

import org.cccnext.tesuto.web.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class PrototypeUIController {

	@Value("${disable.assessments}")
    private boolean disableAssessments;
    @Autowired
    private UrlService urlService;
    
    @PreAuthorize("hasAuthority('VIEW_PROTOTYPE_ASSESSMENT')")
    @RequestMapping(value = "/prototype_assessment", method = RequestMethod.GET)
    public String prototypeAssessment(Model model) {
    	if(disableAssessments)
    		return "NotAvailable";
        
    	urlService.addBaseUrls(model);
        return "PrototypeAssessment";
    }
}
