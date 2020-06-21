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
package org.cccnext.tesuto.springboot.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.user.service.UserContextService;

/**
 * Production implementation of the UserContextService.
 *
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@Service("userContextService")
public class UserContextServiceImpl implements UserContextService {

    @Override
    public String getCurrentUserId() {
        /*
         * When a 404 page is generated, the authentication is null, so we need
         * to protect against a null pointer exception in that case
         */
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        } else {
            return "anonymousUser";
        }
    }

    @Override
    public Authentication getCurrentAuthentication() {
        /*
         * When a 404 page is generated, the authentication is null, so we need
         * to protect against a null pointer exception in that case
         */
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return SecurityContextHolder.getContext().getAuthentication();
        } else {
            return null;
        }
    }

    @Override
    public void updateUserAuthentication(UserAccountDto user) {
        /*
         * When a 404 page is generated, the authentication is null, so we need
         * to protect against a null pointer exception in that case
         */
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Authentication updated = new PreAuthenticatedAuthenticationToken(user, authentication.getCredentials(), user.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(updated);
        }
    }
}
