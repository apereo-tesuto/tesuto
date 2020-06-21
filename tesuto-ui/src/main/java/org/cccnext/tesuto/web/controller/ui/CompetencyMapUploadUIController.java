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


import org.cccnext.tesuto.domain.util.StandaloneRunCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by jasonbrown on 6/21/16.
 */
@Controller
@RequestMapping(value = "/competency-map-upload")
public class CompetencyMapUploadUIController {

	@Value("${disable.assessments}")
    private boolean disableAssessments;
	
	@Value("${tesuto.admin.homepage.url}")
	String adminHomePageUrl;
	
    @PreAuthorize("hasAuthority('VIEW_COMPETENCY_MAP_UPLOAD_UI')")
    @RequestMapping(method = RequestMethod.GET)
    public String competencyMapUpload(Model model) {
    	if(disableAssessments) {
    		return "NotAvailable";
    	}
    	model.addAttribute("uploadUrl", adminHomePageUrl);
        model.addAttribute("standaloneRunCommand", new StandaloneRunCommand());
        return "CompetencyMapUpload";
    }
}
