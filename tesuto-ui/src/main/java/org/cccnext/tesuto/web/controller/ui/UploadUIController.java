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

import org.cccnext.tesuto.admin.viewdto.ProctorViewDto;
import org.cccnext.tesuto.delivery.service.AssessmentSessionReader;
import org.cccnext.tesuto.domain.util.StandaloneRunCommand;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.cccnext.tesuto.user.assembler.view.ProctorViewDtoAssembler;
import org.cccnext.tesuto.web.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class UploadUIController extends BaseController {

	@Value("${disable.assessments}")
    private boolean disableAssessments;
	
	@Value("${tesuto.qti.upload.url}")
    private String uploadUrl;
	
	@Value("${tesuto.admin.homepage.url}")
	String adminHomePageUrl;
	
    @Autowired
    private UrlService urlService;

	@Autowired
	AssessmentSessionReader deliveryService;

	@Autowired
	ProctorViewDtoAssembler proctorViewDtoAssembler;
	

    @Value("${uiIdleTimeoutDuration}")
    private String uiIdleTimeoutDuration;
    
	
    @PreAuthorize("hasAuthority('UPLOAD_ASSESSMENT_PACKAGE_UI')")
    @RequestMapping(value="upload",method = RequestMethod.GET)
    public String rootContext(Model model) {
    	if(disableAssessments) 
    		return "NotAvailable";
        model.addAttribute("standaloneRunCommand", new StandaloneRunCommand());
        model.addAttribute("uploadUrl", uploadUrl);
        urlService.addBaseUrls(model);
        model.addAttribute("homePageUrl", adminHomePageUrl);
        return "/Upload";
    }
    
    @PreAuthorize("hasAuthority('UPLOAD_ASSESSMENT_PACKAGE_UI')")
    @RequestMapping(value="import",method = RequestMethod.GET)
    public String importData(Model model) {
    	if(disableAssessments) 
    		return "NotAvailable";
        model.addAttribute("standaloneRunCommand", new StandaloneRunCommand());
        model.addAttribute("uploadAssessmentUrl", uploadUrl);
        urlService.addBaseUrls(model);
        model.addAttribute("homePageUrl", adminHomePageUrl);
        return "/Import";
    }

    @RequestMapping(value="class-report",method = RequestMethod.GET)
    public String classReportUpload(Model model) {
    	if(disableAssessments)
    		return "NotAvailable";
        model.addAttribute("standaloneRunCommand", new StandaloneRunCommand());
        model.addAttribute("homePageUrl", adminHomePageUrl);
        return "TestUpload";
    }
    
	@PreAuthorize("hasAuthority('VIEW_UPLOADED_ASSESSMENT_PACKAGE')")
	@RequestMapping(value = "assessmentUpload", method = RequestMethod.GET)
	public String uploadAssessmentViewer(Model model, @RequestParam("assessmentSessionId") String assessmentSessionId)
			throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		Object assessmentSession = deliveryService.findAssessmentSession(assessmentSessionId);

		String jsonAssessmentSession = objectMapper.writeValueAsString(assessmentSession);
		model.addAttribute("assessmentSession", jsonAssessmentSession);
		ProctorViewDto proctorView = proctorViewDtoAssembler.assembleViewDto(getUser());
		String jsonUser = objectMapper.writeValueAsString(proctorView);
		model.addAttribute("user", jsonUser);
		model.addAttribute("uiIdleTimeoutDuration", uiIdleTimeoutDuration);
		model.addAttribute("homePageUrl", adminHomePageUrl);
        urlService.addBaseUrls(model);
		return "AssessmentPreview";
	}



}
