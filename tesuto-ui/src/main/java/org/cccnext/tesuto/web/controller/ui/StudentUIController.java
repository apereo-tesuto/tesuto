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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.cccnext.tesuto.admin.service.CollegeReader;
import org.cccnext.tesuto.admin.viewdto.CollegeViewDto;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.cccnext.tesuto.user.service.UserContextService;
import org.cccnext.tesuto.web.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class StudentUIController extends BaseController {
    @Autowired
    private UserContextService userContextService;

    @Autowired
    private CollegeReader collegeService;

    @Autowired
    private UrlService urlService;

    @Value("${landingPageUrl}")
    String landingPageUrl;

    @Value("${uiIdleTimeoutDuration}")
    String uiIdleTimeoutDuration;

    @Value("${google.analytics.environment}")
    String googleAnalyticsEnvironment;

    @Value("${disable.assessments}")
    private boolean disableAssessments;

    @PreAuthorize("hasAuthority('VIEW_STUDENT_DASHBOARD')")
    @RequestMapping(value = {"/student", "/student/**", "/student/*"  }, method = RequestMethod.GET)
    public String rootContext(Model model, 
    		@RequestParam(required = false, name = "cccMisCode") String cccMisCode,
    		 @RequestParam(required = false, name = "activationstatus") String activationId) throws JsonProcessingException {

    	addModel(model);
        return "/Student";
    }
    
    @PreAuthorize("hasAuthority('VIEW_STUDENT_DASHBOARD')")
    @RequestMapping(value = {"/student/activationstatus/{activationId}"  }, method = RequestMethod.GET)
    public String rootActivationContext(Model model, @PathVariable(required = false, name = "activationId") String activationId) throws JsonProcessingException {
    	addModel(model);
        return "../Student";
    }
    
    
    private void addModel(Model model) throws JsonProcessingException {
    	if (userContextService.getCurrentAuthentication().isAuthenticated()) {

        	// TODO: Dependency inject this from Spring. We can reuse this.
			// It will decrease performance.  So, commons-pool might be a good choice for the mapper.
            // Best of both worlds, no garbage collection overhead and single instance is used per injection.
            ObjectMapper objectMapper = new ObjectMapper();
            StudentViewDto student = buildStudent();
            model.addAttribute("user", objectMapper.writeValueAsString(student));

            if(student.getCollegeStatuses()!= null && !student.getCollegeStatuses().isEmpty()) {
                Set<CollegeViewDto>  colleges = collegeService.read(new HashSet<String>(student.getCollegeStatuses().keySet()));
                model.addAttribute("studentCollegesInfo", objectMapper.writeValueAsString(colleges));
            } else {
                model.addAttribute("studentCollegesInfo",  objectMapper.writeValueAsString(new ArrayList<String>()));
            }

            // TODO: Remove, this is already in the GlobalUrlInterceptor
            model.addAttribute("landingPageUrl", landingPageUrl);
            model.addAttribute("uiIdleTimeoutDuration", uiIdleTimeoutDuration);
            model.addAttribute("google.analytics.environment", googleAnalyticsEnvironment);

            model.addAttribute("disableAssessments", disableAssessments);
            urlService.addBaseUrls(model);
        }
    }
    
}
