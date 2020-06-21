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
package org.cccnext.tesuto.user.assembler;

import org.cccnext.tesuto.admin.dto.SecurityGroupApiDto;
import org.cccnext.tesuto.admin.dto.UserAccountApiDto;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.user.model.SecurityGroupApi;
import org.cccnext.tesuto.user.model.UserAccountApi;
import org.cccnext.tesuto.user.repository.SecurityGroupApiRepository;
import org.cccnext.tesuto.user.repository.SecurityGroupRepository;
import org.cccnext.tesuto.user.repository.UserAccountApiRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James T Stanley <jstanley@unicon.net>
 */
@Slf4j
@Component(value = "userAccountApiDtoAssembler")
public class UserAccountApiDtoAssemblerImpl implements UserAccountApiDtoAssembler {

    @Autowired UserAccountApiRepository userAccountApiRepository;
    @Autowired SecurityGroupApiDtoAssembler securityGroupApiDtoAssembler;
    @Autowired SecurityGroupApiRepository securityGroupApiRepository;

    @Override
    public UserAccountApiDto create(UserAccountApiDto userAccountApiDto) {
        UserAccountApi userAccountApi = disassembleDto(userAccountApiDto);

        // Protect if there is no user returned.
        if (userAccountApi != null) {
            Date currentTimeStamp = new Date(System.currentTimeMillis());
            userAccountApi.setLastUpdatedDate(currentTimeStamp);
            userAccountApi.setCreatedOnDate(currentTimeStamp);
        }

        userAccountApi = userAccountApiRepository.save(userAccountApi);
        return assembleDto(userAccountApi);
    }

    public UserAccountApiDto update(UserAccountApiDto userAccountApiDto) {
        UserAccountApi oldUserApi = userAccountApiRepository.getOne(userAccountApiDto.getUserAccountApiId());
        if (oldUserApi == null) {
            throw new NotFoundException(userAccountApiDto.getUserAccountApiId());
        }
        userAccountApiDto.setUserAccountApiId(oldUserApi.getUserAccountApiId());
        UserAccountApi userAccountApi = disassembleDto(userAccountApiDto);

        Date currentTimeStamp = new Date(System.currentTimeMillis());
        userAccountApi.setLastLoginDate(currentTimeStamp);

        userAccountApi = userAccountApiRepository.save(userAccountApi);
        return assembleDto(userAccountApi);
    }

    @Override
    public UserAccountApiDto readDtoByUsername(String username) {
        UserAccountApi userAccountApi = userAccountApiRepository.findByUsername(username);
        return assembleDto(userAccountApi);
    }

    @Override
    public UserAccountApiDto readDtoById(String userAccountApiId) {
        UserAccountApi userAccountApi = userAccountApiRepository.findByUserAccountApiId(userAccountApiId);
        return assembleDto(userAccountApi);
    }

    @Override
    public List<UserAccountApiDto> readAll() {
        List<UserAccountApi> userAccountApiList = userAccountApiRepository.findAll();
        List<UserAccountApiDto> userAccountApis = new ArrayList<UserAccountApiDto>();
        for (UserAccountApi userAccountApi : userAccountApiList) {
            userAccountApis.add(assembleDto(userAccountApi));
        }
        return userAccountApis;
    }

    @Override
    public UserAccountApiDto assembleDto(UserAccountApi userAccountApi) {
        // Drop out of here immediately if there is nothing to assemble.
        if (userAccountApi == null) {
            return null;
        }

        UserAccountApiDto userAccountApiDto = new UserAccountApiDto();
        userAccountApiDto.setCreatedOnDate(userAccountApi.getCreatedOnDate());
        userAccountApiDto.setLastUpdatedDate(userAccountApi.getLastUpdatedDate());
        userAccountApiDto.setUserAccountApiId(userAccountApi.getUserAccountApiId());
        userAccountApiDto.setUsername(userAccountApi.getUsername());
        userAccountApiDto.setPassword(userAccountApi.getPassword());
        userAccountApiDto.setDisplayName(userAccountApi.getDisplayName());

        userAccountApiDto.setEnabled(userAccountApi.isEnabled());
        userAccountApiDto.setAccountExpired(userAccountApi.isAccountExpired());
        userAccountApiDto.setLastLoginDate(userAccountApi.getLastLoginDate());
        userAccountApiDto.setAccountLocked(userAccountApi.isAccountLocked());
        userAccountApiDto.setFailedLogins(userAccountApi.getFailedLogins());

        Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
        for (SecurityGroupApi securityGroupApi : userAccountApi.getSecurityGroupApis()) {
            grantedAuthorities.addAll(securityGroupApi.getPermissions());
        }
        userAccountApiDto.setGrantedAuthorities(grantedAuthorities);

        Set<SecurityGroupApiDto> securityGroupApiDtos = new HashSet<SecurityGroupApiDto>();
        for (SecurityGroupApi securityGroupApi : userAccountApi.getSecurityGroupApis()) {
            SecurityGroupApiDto securityGroupApiDto = securityGroupApiDtoAssembler.assembleDto(securityGroupApi);
            securityGroupApiDtos.add(securityGroupApiDto);
        }
        userAccountApiDto.setSecurityGroupApiDtos(securityGroupApiDtos);

        return userAccountApiDto;
    }

    @Override
    public UserAccountApi disassembleDto(UserAccountApiDto userAccountApiDto) {
        // If there is nothing to disassemble, just return null
        if (userAccountApiDto == null) {
            return null;
        }
        UserAccountApi userAccountApi = new UserAccountApi();
        userAccountApi.setCreatedOnDate(userAccountApiDto.getCreatedOnDate());
        userAccountApi.setLastUpdatedDate(userAccountApiDto.getLastUpdatedDate());
        userAccountApi.setUserAccountApiId(userAccountApiDto.getUserAccountApiId());
        // transform the username to lower case
        userAccountApi.setUsername(userAccountApiDto.getUsername().toLowerCase());
        userAccountApi.setPassword(userAccountApiDto.getPassword());
        userAccountApi.setDisplayName(userAccountApiDto.getDisplayName());

        userAccountApi.setEnabled(userAccountApiDto.isEnabled());
        userAccountApi.setExpired(userAccountApiDto.isAccountExpired());
        userAccountApi.setLastLoginDate(userAccountApiDto.getLastLoginDate());
        userAccountApi.setAccountLocked(userAccountApiDto.isAccountLocked());
        userAccountApi.setFailedLogins(userAccountApiDto.getFailedLogins());

        // TODO: Perhaps the null object pattern is in order here.
        if (userAccountApiDto.getSecurityGroupApiDtos() != null) {
            Set<SecurityGroupApi> securityGroupApis = new HashSet<SecurityGroupApi>();
            for (SecurityGroupApiDto securityGroupApiDto : userAccountApiDto.getSecurityGroupApiDtos()) {
                SecurityGroupApi securityGroupApi = securityGroupApiDtoAssembler.disassembleDto(securityGroupApiDto);
                securityGroupApis.add(securityGroupApi);
            }
            userAccountApi.setSecurityGroupApis(securityGroupApis);
        }

        return userAccountApi;
    }
}
