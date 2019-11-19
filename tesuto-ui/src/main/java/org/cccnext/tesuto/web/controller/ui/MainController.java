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

import javax.servlet.ServletContext;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.viewdto.ProctorViewDto;
import org.cccnext.tesuto.domain.util.StandaloneRunCommand;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.cccnext.tesuto.user.assembler.view.ProctorViewDtoAssembler;
import org.cccnext.tesuto.web.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping(value = "/")
public class MainController extends BaseController {

    @Autowired protected ServletContext servletContext;
    @Autowired private ProctorViewDtoAssembler proctorViewDtoAssembler;

    @Value("${disable.assessments}")
    private boolean disableAssessments;
    
    @Value("${disable.placements}")
    private boolean disablePlacements;
    
    @Autowired
    private UrlService urlService;
    

    @Value("${uiIdleTimeoutDuration}")
    private String uiIdleTimeoutDuration;
    
	
	@Value("${tesuto.qti.upload.url}")
    private String uploadUrl;
    
    @Autowired
    private ObjectMapper objectMapper;

    @PreAuthorize("hasAnyAuthority('VIEW_PROCTOR_DASHBOARD, VIEW_ADMIN_DASHBOARD')")
    @RequestMapping(value = { "/home"}, method = RequestMethod.GET)
    public String rootContext(Model model) throws JsonProcessingException {
        UserAccountDto currentUser = getUser();

        ProctorViewDto proctorViewDto = proctorViewDtoAssembler.assembleViewDto(currentUser);
        String jsonUser = objectMapper.writeValueAsString(proctorViewDto);
        model.addAttribute("user", jsonUser);

        model.addAttribute("disableAssessments", disableAssessments);
        model.addAttribute("disableAssessments", disablePlacements);
        model.addAttribute("uiIdleTimeoutDuration", uiIdleTimeoutDuration);
        urlService.addBaseUrls(model);
        
        return "Home";
    }
   
    

    @PreAuthorize("hasAuthority('CREATE_QA_OBJECTS')")
    @RequestMapping(value = { "/qa"}, method = RequestMethod.GET)
    public String qaContext(Model model) throws JsonProcessingException {
        UserAccountDto currentUser = getUser();

        ProctorViewDto proctorViewDto = proctorViewDtoAssembler.assembleViewDto(currentUser);
        // TODO: Dependency inject this from Spring. We can reuse this.
        // It will decrease performance.  So, commons-pool might be a good choice for the mapper.
        // Best of both worlds, no garbage collection overhead and single instance is used per injection.
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonUser = objectMapper.writeValueAsString(proctorViewDto);
        model.addAttribute("user", jsonUser);

        model.addAttribute("disableAssessments", disableAssessments);
        model.addAttribute("uiIdleTimeoutDuration", uiIdleTimeoutDuration);
        urlService.addBaseUrls(model);
        
        return "QA";
    }

}
