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

import javax.persistence.NoResultException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.user.service.UserAccountReader;
import org.cccnext.tesuto.user.service.UserAccountService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Service;

/**
 * Originally this class used the default spring failure handler that is being
 * extended. We needed to add the lockout functionality after too many login
 * attempts.
 * 
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service("tesutoAuthenticationFailureHandler")
public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    UserAccountReader userAccountService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        super.onAuthenticationFailure(request, response, exception);

        String username = request.getParameter("j_username");

        // Increment the number of times the user has attempted to logged in.
        try {
            UserAccountDto userAccountDto = userAccountService.getUserAccountByUsername(username);

            if (userAccountDto != null) {
                userAccountService.failedLogin(userAccountDto.getUserAccountId());
            }
        } catch (NoResultException nre) {
            // There is nothing to increment if the username is not found.
        } catch (BadCredentialsException bce) {
            // Bad credentials don't matter either.
        } catch (EmptyResultDataAccessException erdae) {
            // This is Spring Framework wrapping of JPA exceptions.
            // There is nothing to increment if the username is not found.
        }
    }
}
