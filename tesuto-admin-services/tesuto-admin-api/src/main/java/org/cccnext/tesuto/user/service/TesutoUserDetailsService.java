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

import javax.persistence.NoResultException;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James T Stanley <jstanley@unicon.net>
 */
@Slf4j
@Service(value = "tesutoUserDetailsService")
public class TesutoUserDetailsService implements UserDetailsService {


    @Autowired
    private UserAccountReader userAccountService;

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.core.userdetails.UserDetailsService#
     * loadUserByUsername(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String arg0) throws UsernameNotFoundException {
        UserAccountDto userAccountDto = null;
        try {
            userAccountDto = userAccountService.getUserAccountByUsername(arg0);
        } catch (NoResultException e) {
            throw new UsernameNotFoundException("Username " + arg0 + " not found.");
        }

        if (userAccountDto == null) {
            throw new UsernameNotFoundException("Username " + arg0 + " not found.");
        }

        // Authorities are usually lazily initialized, so we need to fetch them
        // in this transaction.
        userAccountDto.getAuthorities();

        return userAccountDto;
    }

    public UserAccountReader getUserAccountService() {
        return userAccountService;
    }

    public void setUserAccountService(UserAccountReader userAccountService) {
        this.userAccountService = userAccountService;
    }

}
