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

import java.util.List;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.cccnext.tesuto.user.assembler.SecurityGroupDtoAssembler;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James T Stanley <jstanley@unicon.net>
 */
@Slf4j
@Service(value = "securityGroupService")
public class SecurityGroupServiceImpl implements SecurityGroupService {

    @Autowired private SecurityGroupDtoAssembler securityGroupDtoAssembler;    

	public static final String staffAndFacultyUrl = "/home";
	public static final String studentUrl = "/student";

    @Override
    @Transactional
    public void addSecurityPermissionToSecurityGroup(Integer securityGroupId, String securityPermissionId) {
        securityGroupDtoAssembler.addSecurityPermission(securityGroupId, securityPermissionId);
    }

    @Override
    @Transactional
    public void removeSecurityPermissionFromSecurityGroup(Integer securityGroupId, String securityPermissionId) {
        securityGroupDtoAssembler.removeSecurityPermission(securityGroupId, securityPermissionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SecurityGroupDto> getAllSecurityGroups() {
        return securityGroupDtoAssembler.readAll();
    }

    @Override
    @Transactional(readOnly = true)
    public SecurityGroupDto getSecurityGroup(Integer id) {
        return securityGroupDtoAssembler.readById(id);
    }

    @Override
    @Transactional
    public SecurityGroupDto upsert(SecurityGroupDto securityGroupDto) {
        SecurityGroupDto securityGroupDto1 = securityGroupDtoAssembler.readById(securityGroupDto.getSecurityGroupId());
        if (securityGroupDto1 == null) {
            securityGroupDto = securityGroupDtoAssembler.create(securityGroupDto);
        } else {
            securityGroupDto = securityGroupDtoAssembler.update(securityGroupDto);
        }
        return securityGroupDto;
    }

    @Override
    @Transactional(readOnly = true)
    public SecurityGroupDto getSecurityGroupByGroupName(String groupName) {
        SecurityGroupDto securityGroupDto = securityGroupDtoAssembler.readByGroupName(groupName);
        return securityGroupDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Set<SecurityGroupDto> getSecurityGroupsByUserAccountId(String userAccountId) {
        Set<SecurityGroupDto> securityGroupDtoSet = securityGroupDtoAssembler.readByUserAccountId(userAccountId);
        return securityGroupDtoSet;
    }

}
