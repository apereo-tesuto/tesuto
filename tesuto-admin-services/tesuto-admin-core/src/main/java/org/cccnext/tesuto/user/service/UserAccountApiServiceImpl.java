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

import java.util.Calendar;
import java.util.List;

import org.cccnext.tesuto.admin.dto.UserAccountApiDto;
import org.cccnext.tesuto.domain.util.RandomGenerator;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.user.assembler.SecurityGroupApiDtoAssembler;
import org.cccnext.tesuto.user.assembler.UserAccountApiDtoAssembler;
import org.cccnext.tesuto.user.repository.UserAccountRepository;
import org.cccnext.tesuto.user.service.UserAccountApiService;
import org.cccnext.tesuto.user.service.UserAccountExistsException;
import org.cccnext.tesuto.user.service.UserContextService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James T Stanley <jstanley@unicon.net>
 */
@Slf4j
@Service(value = "userAccountApiService")
public class UserAccountApiServiceImpl implements UserAccountApiService {
   
    @Autowired private UserAccountApiDtoAssembler userAccountApiDtoAssembler;
    @Autowired private SecurityGroupApiDtoAssembler securityGroupApiDtoAssembler;
    @Autowired private RandomGenerator randomGenerator;
    @Autowired private UserContextService userContextService;
    @Autowired private PasswordEncoder passwordEncoder; // TODO: Upgrade PasswordEncoder
    @Autowired private UserAccountSearchService searchService;
    @Autowired private UserAccountRepository userAccountRepository;
    
    @Override
    @Transactional
    public UserAccountApiDto create(UserAccountApiDto userAccountApiDto) throws UserAccountExistsException {
        return userAccountApiDtoAssembler.create(userAccountApiDto);
    }

    @Override
    @Transactional
    public void update(UserAccountApiDto apiUserToBeUpdated) throws NotFoundException, UserAccountExistsException {
        userAccountApiDtoAssembler.update(apiUserToBeUpdated);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<UserAccountApiDto> getAllUserAccountApis() {
        return userAccountApiDtoAssembler.readAll();
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccountApiDto getUserAccountApi(String id) {
        UserAccountApiDto userAccountApiDto = userAccountApiDtoAssembler.readDtoById(id);
        return userAccountApiDto;
    }

    @Override
    @Transactional
    public UserAccountApiDto upsert(UserAccountApiDto userAccountApiDto) {
        UserAccountApiDto oldUserAccountApiDto = userAccountApiDtoAssembler.readDtoByUsername(userAccountApiDto.getUsername());

        Calendar calendar = Calendar.getInstance();

        if (oldUserAccountApiDto == null) {
            // Passwords do not exist for Open ID, Oauth 2 based users.  But if we set one it should be hashed in the database.
            userAccountApiDto.setPassword(passwordEncoder.encode(userAccountApiDto.getPassword()));
            userAccountApiDto = userAccountApiDtoAssembler.create(userAccountApiDto);
        } else {
            // TODO: What the foo is this, do we need it?
            // Guarantee a created on date is set in case it is null at some
            // point in the future.
            if (userAccountApiDto.getCreatedOnDate() != null) {
                oldUserAccountApiDto.setCreatedOnDate(userAccountApiDto.getCreatedOnDate());
            } else {
                oldUserAccountApiDto.setCreatedOnDate(calendar.getTime());
            }
            oldUserAccountApiDto.setDisplayName(userAccountApiDto.getDisplayName());
            oldUserAccountApiDto.setEnabled(userAccountApiDto.isEnabled());
            oldUserAccountApiDto.setAccountExpired(userAccountApiDto.isAccountExpired());
            oldUserAccountApiDto.setFailedLogins(userAccountApiDto.getFailedLogins());
            oldUserAccountApiDto.setLastUpdatedDate(calendar.getTime());
            oldUserAccountApiDto.setAccountLocked(userAccountApiDto.isAccountLocked());
            oldUserAccountApiDto.setUsername(userAccountApiDto.getUsername());
            userAccountApiDto = userAccountApiDtoAssembler.update(oldUserAccountApiDto);
        }
        return userAccountApiDto;
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccountApiDto getUserAccountApiByUsername(String username) {
        UserAccountApiDto userAccountApiDto = userAccountApiDtoAssembler.readDtoByUsername(username);
        if (userAccountApiDto == null) {
            return null;
        }
        return userAccountApiDto;
    }

    @Override
    @Transactional
    public UserAccountApiDto setUserAccountApiCredential(UserAccountApiDto userAccountApiDto, String password) {
        if (null != userAccountApiDto) {
            userAccountApiDto.setPassword(passwordEncoder.encode(password));
            userAccountApiDtoAssembler.update(userAccountApiDto);
        }
        return userAccountApiDto;
    }
}
