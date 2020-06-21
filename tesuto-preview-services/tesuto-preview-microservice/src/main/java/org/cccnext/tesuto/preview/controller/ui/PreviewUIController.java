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
package org.cccnext.tesuto.preview.controller.ui;

import java.util.Collection;

import org.cccnext.tesuto.admin.dto.SecurityGroupApiDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.viewdto.ProctorViewDto;
import org.cccnext.tesuto.delivery.service.AssessmentSessionReader;
import org.cccnext.tesuto.domain.util.StandaloneRunCommand;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.cccnext.tesuto.user.assembler.view.ProctorViewDtoAssembler;
import org.cccnext.tesuto.user.service.SecurityGroupApiReader;
import org.cccnext.tesuto.user.service.UserContextService;
import org.cccnext.tesuto.util.TesutoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class PreviewUIController extends BaseController {

    @Autowired
    AssessmentSessionReader deliveryService;
    
    @Autowired
    ProctorViewDtoAssembler proctorViewDtoAssembler;

    @Autowired
    UserContextService userContextService;

    @Autowired
    SecurityGroupApiReader securityGroupApiReader;

    @Override
    public UserAccountDto getUser() {
    	UserAccountDto userAccount = new UserAccountDto();
        userAccount.setDisplayName("Preview Session");
        userAccount.setUsername(TesutoUtils.newId());
        userAccount.setUserAccountId("previewer");
        userAccount.setEnabled(true);
        userAccount.setAccountLocked(false);
        return userAccount;
    }

    @PreAuthorize("hasAuthority('VIEW_PREVIEW_ASSESSMENT_UPLOAD_UI')")
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    public String previewUpload(Model model) {
        model.addAttribute("standaloneRunCommand", new StandaloneRunCommand());
        return "PreviewUpload";
    }

    @PreAuthorize("permitAll()")
    @RequestMapping(value = "/assessmentUpload", method = RequestMethod.GET)
    public String previewAssessmentViewer(Model model, @RequestParam("assessmentSessionId") String assessmentSessionId)
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Object assessmentSession = deliveryService.findAssessmentSession(assessmentSessionId);

        Authentication currentAuthentication = userContextService.getCurrentAuthentication();
        if (isAnonymousAuthentication(currentAuthentication)) { // if we're here, we found a session, so let them in to view...
            SecurityGroupApiDto securityGroupApi = securityGroupApiReader.getSecurityGroupApiByGroupName("PREVIEW_PLAYER");
            Authentication newAuthentication = new PreAuthenticatedAuthenticationToken(getUser(),
                    userContextService.getCurrentAuthentication().getCredentials(),
                    securityGroupApi.getGrantedAuthorities());
            SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        }

        String jsonAssessmentSession = objectMapper.writeValueAsString(assessmentSession);
        model.addAttribute("assessmentSession", jsonAssessmentSession);
        ProctorViewDto proctorView = proctorViewDtoAssembler.assembleViewDto(getUser());
        String jsonUser = objectMapper.writeValueAsString(proctorView);
        model.addAttribute("user", jsonUser);
        return "AssessmentPreview";
    }

    private boolean isAnonymousAuthentication(Authentication authentication) {
        boolean isAnonymous = false;
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        if (authorities.size() == 1 && authorities.iterator().next().getAuthority().equals("ROLE_ANONYMOUS")) {
            isAnonymous = true;
        }
        return isAnonymous;
    }
}
