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
package org.cccnext.tesuto.user.service;

import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.cccnext.tesuto.admin.dto.UserAccountApiDto;
import org.cccnext.tesuto.admin.dto.UserPasswordResetDto;
import org.cccnext.tesuto.admin.form.SearchParameters;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.exception.ValidationException;
import org.spockframework.util.Pair;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Set;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public interface UserAccountApiService {
    List<UserAccountApiDto> getAllUserAccountApis();
    UserAccountApiDto getUserAccountApi(String id);
    UserAccountApiDto create(UserAccountApiDto userAccountApiDto) throws UserAccountExistsException;
    void update(UserAccountApiDto userAccountApiDto) throws NotFoundException, UserAccountExistsException;
    UserAccountApiDto upsert(UserAccountApiDto userAccountApiDto);
    UserAccountApiDto getUserAccountApiByUsername(String username);
    UserAccountApiDto setUserAccountApiCredential(UserAccountApiDto userAccountApiDto, String password);
    static boolean doesUserHavePermission(UserAccountApiDto user, String permission) {
        boolean hasPermission = false;
        Set<GrantedAuthority> grantedAuthorities = user.getGrantedAuthorities();
        for (GrantedAuthority grantedAuthority : grantedAuthorities) {
            if (grantedAuthority.getAuthority().equals(permission)) {
                hasPermission = true;
                break;
            }
        }
        return hasPermission;
    }
}
