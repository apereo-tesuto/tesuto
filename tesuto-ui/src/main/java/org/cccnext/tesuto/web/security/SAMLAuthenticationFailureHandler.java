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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cccnext.tesuto.springboot.web.RequestForwardService;
import org.cccnext.tesuto.user.service.UserAccountReader;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class SAMLAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired UserAccountReader userAccountReader;
    @Autowired RequestForwardService requestForwardService;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private String targetUrlOnSamlFailure;

    public void setTargetUrlOnSamlFailure(String targetUrlOnSamlFailure) {
        this.targetUrlOnSamlFailure = targetUrlOnSamlFailure;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        super.onAuthenticationFailure(request, response, exception);

        if (response.isCommitted()) {
            log.debug("Response has already been committed. Unable to redirect to " + targetUrlOnSamlFailure);
            return;
        }
        redirectStrategy.sendRedirect(request, response, requestForwardService.getForwardUrl(request, targetUrlOnSamlFailure));
    }
}
