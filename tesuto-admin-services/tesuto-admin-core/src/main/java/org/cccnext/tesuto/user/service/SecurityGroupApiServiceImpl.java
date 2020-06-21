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

import org.cccnext.tesuto.admin.dto.SecurityGroupApiDto;
import org.cccnext.tesuto.user.assembler.SecurityGroupApiDtoAssembler;
import org.cccnext.tesuto.user.assembler.SecurityPermissionDtoAssembler;
import org.cccnext.tesuto.user.assembler.UserAccountApiDtoAssembler;
import org.cccnext.tesuto.user.service.SecurityGroupApiService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James T Stanley <jstanley@unicon.net>
 */
@Slf4j
@Service(value = "securityGroupApiService")
public class SecurityGroupApiServiceImpl implements SecurityGroupApiService {

    @Autowired private UserAccountApiDtoAssembler userAccountApiDtoAssembler;
    @Autowired private SecurityGroupApiDtoAssembler securityGroupApiDtoAssembler;
    @Autowired private SecurityPermissionDtoAssembler securityPermissionDtoAssembler;

    @Override
    @Transactional
    public void addSecurityPermissionToSecurityGroupApi(Integer securityGroupApiId, String securityPermissionId) {
        securityGroupApiDtoAssembler.addSecurityPermission(securityGroupApiId, securityPermissionId);
    }

    @Override
    @Transactional
    public void removeSecurityPermissionFromSecurityGroupApi(Integer securityGroupApiId, String securityPermissionId) {
        securityGroupApiDtoAssembler.removeSecurityPermission(securityGroupApiId, securityPermissionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SecurityGroupApiDto> getAllSecurityGroupApis() {
        return securityGroupApiDtoAssembler.readAll();
    }

    @Override
    @Transactional(readOnly = true)
    public SecurityGroupApiDto getSecurityGroupApi(Integer id) {
        return securityGroupApiDtoAssembler.readById(id);
    }

    @Override
    @Transactional
    public SecurityGroupApiDto upsert(SecurityGroupApiDto securityGroupApiDto) {
        SecurityGroupApiDto securityGroupApiDto1 = securityGroupApiDtoAssembler.readById(securityGroupApiDto.getSecurityGroupApiId());
        if (securityGroupApiDto1 == null) {
            securityGroupApiDto = securityGroupApiDtoAssembler.create(securityGroupApiDto);
        } else {
            securityGroupApiDto = securityGroupApiDtoAssembler.update(securityGroupApiDto);
        }
        return securityGroupApiDto;
    }

    @Override
    @Transactional(readOnly = true)
    public SecurityGroupApiDto getSecurityGroupApiByGroupName(String groupName) {
        SecurityGroupApiDto securityGroupApiDto = securityGroupApiDtoAssembler.readByGroupName(groupName);
        return securityGroupApiDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<SecurityGroupApiDto> getSecurityGroupApisByUserAccountApiId(String userAccountApiId) {
        Set<SecurityGroupApiDto> securityGroupApiDtoSet = securityGroupApiDtoAssembler.readByUserAccountApiId(userAccountApiId);
        return securityGroupApiDtoSet;
    }


}
