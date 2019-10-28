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

import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.admin.viewdto.ProctorViewDto;
import org.cccnext.tesuto.content.model.DeliveryType;
import org.cccnext.tesuto.delivery.service.AssessmentSessionReader;
import org.cccnext.tesuto.delivery.service.util.TesutoUtil;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.cccnext.tesuto.user.assembler.view.ProctorViewDtoAssembler;
import org.cccnext.tesuto.user.service.StudentReader;
import org.cccnext.tesuto.web.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@Controller
public class AssessmentSessionUIController extends BaseController {
    
	@Autowired
    AssessmentSessionReader deliveryService;
	
    @Autowired
    StudentReader studentService;

    @Autowired
    ProctorViewDtoAssembler proctorViewDtoAssembler;
    
    @Autowired
    private UrlService urlService;
    
    private String uiIdleTimeoutDuration = "1000000";

    @PreAuthorize("hasAnyAuthority('VIEW_ASSESSMENT_SESSION')")
    @RequestMapping(value = "assessment", method = RequestMethod.GET)
	public String uploadAssessmentViewer(Model model, @RequestParam("assessmentSessionId") String assessmentSessionId, HttpSession session, HttpServletRequest request)
            throws JsonProcessingException {

        //if (!TesutoUtil.hasAssessmentSessionPermission(session, assessmentSessionId, DeliveryType.ONLINE)) {
        //    return "NotFound";
        //}

        ObjectMapper objectMapper = new ObjectMapper();
        Object assessmentSession = deliveryService.findAssessmentSession(assessmentSessionId);

        String jsonAssessmentSession = objectMapper.writeValueAsString(assessmentSession);
        model.addAttribute("assessmentSession", jsonAssessmentSession);
        // TODO Work out what needs to go to ui for various windows
        ProctorViewDto proctorView = proctorViewDtoAssembler.assembleViewDto(getUser());
        proctorView.setSecurityGroups(null);
        proctorView.setUsername(null);
        proctorView.setSecurityPermissions(null);
        String jsonUser = objectMapper.writeValueAsString(proctorView);
        model.addAttribute("user", jsonUser);
        model.addAttribute("uiIdleTimeoutDuration", uiIdleTimeoutDuration);
        urlService.addBaseUrls(model);
        return "/Assessment";
    }

    /* Disabled as per CCCAS-4392
    @PreAuthorize("hasAnyAuthority('PRINT_ASSESSMENT_SESSION')")
    @RequestMapping(value = "/print/{assessmentSessionId}", method = RequestMethod.GET)
    public String printableAssessmentViewer(Model model, @PathVariable String assessmentSessionId, HttpSession session)
            throws JsonProcessingException {

        if (!assessmentSessionId.equals("prototypeAssessmentSessionId") &&
                !TesutoUtil.hasAssessmentSessionPermission(session, assessmentSessionId, DeliveryType.PAPER) ) {
            return "NotFound";
        }
        ObjectMapper objectMapper = new ObjectMapper();
        if (!assessmentSessionId.equals("prototypeAssessmentSessionId")) {
            Object assessmentSession = deliveryService.findAssessmentSession(assessmentSessionId);
            String jsonAssessmentSession = objectMapper.writeValueAsString(assessmentSession);
            model.addAttribute("assessmentSession", jsonAssessmentSession);O
        } else {
            model.addAttribute("assessmentSession", "{}");
        }
        // TODO Work out what needs to go to ui for various windows
        ProctorViewDto proctorView = proctorViewDtoAssembler.assembleViewDto(getUser());
        proctorView.setSecurityGroups(null);
        proctorView.setUsername(null);
        proctorView.setSecurityPermissions(null);
        String jsonUser = objectMapper.writeValueAsString(proctorView);
        model.addAttribute("user", jsonUser);

        return "PrintableAssessmentSession";
    } */

    @PreAuthorize("hasAuthority('PRINT_ASSESSMENT')")
    @RequestMapping(value = "assessment/print/{namespace}/{identifier}", method = RequestMethod.GET)
    public String printableAssessmentViewer(Model model, @PathVariable("namespace") String namespace,
                                            @PathVariable("identifier") String identifier,
                                            @RequestParam(value = "miscode", required = true) String misCode,
                                            @RequestParam(value = "cccid", required = false) String cccid) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        Object assessmentSession = deliveryService.createContentAssessmentSession(identifier, namespace);
        String jsonAssessmentSession = objectMapper.writeValueAsString(assessmentSession);
        model.addAttribute("assessmentSession", jsonAssessmentSession);
        model.addAttribute("misCode", misCode);
        if (StringUtils.isNotBlank(cccid)) {
        	try {
        		model.addAttribute("studentProfile", objectMapper.writeValueAsString(studentService.getStudentById(cccid)));
        	} catch (PersistenceException pe) {
        		// We don't want to break the JSP rendering of the cccid was not found. The exception was already logged in the student service. 
        	}
        }
        urlService.addBaseUrls(model);
        return "/PrintableAssessment";
    }
}
