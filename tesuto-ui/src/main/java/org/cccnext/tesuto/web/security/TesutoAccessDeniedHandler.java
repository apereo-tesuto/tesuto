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
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.cccnext.tesuto.admin.util.UrlUtils;
import org.cccnext.tesuto.springboot.web.RequestForwardService;
import org.cccnext.tesuto.user.service.UserContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Service;

@Service("tesutoAccessDeniedHandler")
public class TesutoAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired UserContextService userContextService;
    @Autowired RequestForwardService requestForwardService;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired UrlUtils urlUtils;
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException arg2)
            throws IOException, ServletException {
        Authentication authentication = userContextService.getCurrentAuthentication();
        String targetUrl;
        if (authentication == null) {
            targetUrl = urlUtils.getUnauthorizedUrl();
        } else {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            targetUrl = urlUtils.determineTargetUrl(authorities);
        }
        redirectStrategy.sendRedirect(request, response, requestForwardService.getForwardUrl(request, targetUrl));
    }

    

    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

}
