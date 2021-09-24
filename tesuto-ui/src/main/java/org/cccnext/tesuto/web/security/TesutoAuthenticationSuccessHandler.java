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
package org.cccnext.tesuto.web.security;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.util.UrlUtils;
import org.cccnext.tesuto.springboot.web.RequestForwardService;
import org.cccnext.tesuto.user.service.UserAccountReader;
import org.cccnext.tesuto.web.security.exception.TesutoInvalidAccountException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service("tesutoAuthenticationSuccessHandler")
public class TesutoAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    String errorPageUrl = "/error";

    @Autowired
    UserAccountReader userAccountReader;
;
    @Autowired RequestForwardService requestForwardService;
    
    @Autowired UrlUtils urlUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws ServletException, IOException {

        clearFailedLogins(authentication);
        clearAuthenticationAttributes(request);
        handle(request, response, authentication);
    }

    protected void clearFailedLogins(Authentication authentication) {
        UserAccountDto userAccountDto = (UserAccountDto) authentication.getPrincipal();


        userAccountReader.clearFailedLogins(userAccountDto.getUserAccountId());
    }

    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, TesutoInvalidAccountException {
        UserAccountDto userAccountDto = userAccountReader.getUserAccountByUsername(authentication.getName());
        Set<SecurityGroupDto> securityGroupDtos = userAccountDto.getSecurityGroupDtos();
        String targetUrl = urlUtils.determineTargetUrl(securityGroupDtos);
        log.debug("targetUrl: {}", targetUrl);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        String forwardUrl = requestForwardService.getForwardUrl(request, targetUrl);

        // These next few lines are for debug logging only
        log.debug("forwardUrl: {}", forwardUrl);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.debug("Request Header Name: {}", headerName);
            log.debug("Request Header Value: {}", request.getHeader(headerName));
        }

        response.sendRedirect(forwardUrl);
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            session = request.getSession(true);
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
