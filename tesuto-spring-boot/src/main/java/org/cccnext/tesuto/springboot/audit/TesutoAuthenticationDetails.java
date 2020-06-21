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
package org.cccnext.tesuto.springboot.audit;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

/**
 * This is here to satisfy a serialization error in the placement module.
 * This is not the best location for it but it does satisfy the runtime dependency.
 * I would expect this to change at some point when we are not sharing the sessions
 * across the 2 services. -scott smith
 */
public class TesutoAuthenticationDetails extends WebAuthenticationDetails {
    private static final long serialVersionUID = 1L;

    private String userAgent;
    private String cccid;
    private String roles;
    private String colleges;

    public TesutoAuthenticationDetails(HttpServletRequest request) {
        super(request);

        this.userAgent = request.getHeader("user-agent");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !(authentication.getPrincipal() instanceof String)) {
            UserAccountDto account = (UserAccountDto)authentication.getPrincipal();
            this.cccid = account.getUsername();
            setRoles(authentication);
            this.colleges = StringUtils.join(account.getCollegeIds(), ",");
        }
    }

    private void setRoles(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> rolesNames = authorities.stream().map(a-> a.getAuthority()).collect(Collectors.toList());
        this.roles = StringUtils.join(rolesNames, ",");
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getRoles() {
        return roles;
    }

    public String getCccid() {
        return cccid;
    }

    public String getColleges() {
        return colleges;
    }

}
