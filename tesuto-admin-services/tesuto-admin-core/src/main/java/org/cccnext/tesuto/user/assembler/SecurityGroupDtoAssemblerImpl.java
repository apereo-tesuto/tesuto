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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.cccnext.tesuto.admin.dto.SecurityPermissionDto;
import org.cccnext.tesuto.user.model.SecurityGroup;
import org.cccnext.tesuto.user.model.SecurityPermission;
import org.cccnext.tesuto.user.repository.SecurityGroupRepository;
import org.cccnext.tesuto.user.repository.UserAccountRepository;
import org.hibernate.exception.SQLGrammarException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James T Stanley <jstanley@unicon.net>
 */
@Slf4j
@Component(value = "securityGroupDtoAssembler")
public class SecurityGroupDtoAssemblerImpl implements SecurityGroupDtoAssembler {

    @Autowired UserAccountRepository userAccountRepository;
    @Autowired SecurityGroupRepository securityGroupRepository;
    @Autowired SecurityPermissionDtoAssembler securityPermissionDtoAssembler;

    @Override
    public SecurityGroupDto create(SecurityGroupDto securityGroupDto) {
        SecurityGroup securityGroup = disassembleDto(securityGroupDto);

        // Protect if there is no security group returned.
        if (securityGroup != null) {
            Date currentTimeStamp = new Date(System.currentTimeMillis());
            securityGroup.setLastUpdatedDate(currentTimeStamp);
            securityGroup.setCreatedOnDate(currentTimeStamp);
        }

        securityGroup = securityGroupRepository.save(securityGroup);
        return assembleDto(securityGroup);
    }

    @Override
    public SecurityGroupDto update(SecurityGroupDto securityGroupDto) {
        SecurityGroup oldUser = securityGroupRepository.findById(securityGroupDto.getSecurityGroupId()).get();
        securityGroupDto.setSecurityGroupId(oldUser.getSecurityGroupId());
        SecurityGroup securityGroup = disassembleDto(securityGroupDto);

        securityGroup = securityGroupRepository.save(securityGroup);
        return assembleDto(securityGroup);
    }

    @Override
    public SecurityGroupDto readById(Integer securityGroupId) {
        SecurityGroup securityGroup = securityGroupRepository.findById(securityGroupId).get();
        return assembleDto(securityGroup);
    }

    @Override
    public SecurityGroupDto readByGroupName(String groupName) {
        SecurityGroup securityGroup = securityGroupRepository.findByGroupName(groupName);
        return assembleDto(securityGroup);
    }

    @Override
    public Set<SecurityGroupDto> readByUserAccountId(String userAccountId) {
        Set<SecurityGroup> securityGroupSet = userAccountRepository.findByUserAccountId(userAccountId).getSecurityGroups();
        return assembleDto(securityGroupSet);
    }

    @Override
    public List<SecurityGroupDto> readAll() {
        List<SecurityGroup> securityGroupList = securityGroupRepository.findAll();
        List<SecurityGroupDto> securityGroups = new ArrayList<SecurityGroupDto>();
        for (SecurityGroup securityGroup : securityGroupList) {
            securityGroups.add(assembleDto(securityGroup));
        }
        return securityGroups;
    }

    @Override
    public void removeSecurityPermission(Integer securityGroupId, String securityPermissionId) {
        try {
            securityGroupRepository.removeSecurityPermission(securityGroupId, securityPermissionId);
        } catch (Exception jse) {
        	//TODO fix this method, not the correct way to remove a many to many relationship
            if (jse.getCause().getMessage().equals("could not extract ResultSet")) {
                // Ignore, this is expected.
            } else {
                throw jse;
            }
        }
    }

    @Override
    public void addSecurityPermission(Integer securityGroupId, String securityPermissionId) {
        try {
            securityGroupRepository.addSecurityPermission(securityGroupId, securityPermissionId);
        } catch (Exception jse) {
            if (jse.getCause().getMessage().equals("could not extract ResultSet")) {
                // Ignore, this is expected.
            } else {
                throw jse;
            }
        }
    }

    @Override
    public SecurityGroupDto assembleDto(SecurityGroup securityGroup) {
        // Drop out of here immediately if there is nothing to assemble.
        if (securityGroup == null) {
            return null;
        }

        SecurityGroupDto securityGroupDto = new SecurityGroupDto();
        securityGroupDto.setCreatedOnDate(securityGroup.getCreatedOnDate());
        securityGroupDto.setLastUpdatedDate(securityGroup.getLastUpdatedDate());
        securityGroupDto.setSecurityGroupId(securityGroup.getSecurityGroupId());
        securityGroupDto.setGroupName(securityGroup.getGroupName());
        securityGroupDto.setDescription(securityGroup.getDescription());

        Set<SecurityPermissionDto> securityPermissionDtos = new HashSet<SecurityPermissionDto>();
        for (SecurityPermission securityPermission : securityGroup.getPermissions()) {
            securityPermissionDtos.add(securityPermissionDtoAssembler.assembleDto(securityPermission));
        }
        securityGroupDto.setSecurityPermissionDtos(securityPermissionDtos);

        return securityGroupDto;
    }

    @Override
    public SecurityGroup disassembleDto(SecurityGroupDto securityGroupDto) {
        // If there is nothing to disassemble, just return null
        if (securityGroupDto == null) {
            return null;
        }
        SecurityGroup securityGroup = new SecurityGroup();
        securityGroup.setCreatedOnDate(securityGroupDto.getCreatedOnDate());
        securityGroup.setLastUpdatedDate(securityGroupDto.getLastUpdatedDate());
        securityGroup.setSecurityGroupId(securityGroupDto.getSecurityGroupId());
        securityGroup.setGroupName(securityGroupDto.getGroupName());
        securityGroup.setDescription(securityGroupDto.getDescription());

        Set<SecurityPermission> securityPermissions = new HashSet<SecurityPermission>();
        for (SecurityPermissionDto securityPermissionDto : securityGroupDto.getSecurityPermissionDtos()) {
            SecurityPermission securityPermission = securityPermissionDtoAssembler
                    .disassembleDto(securityPermissionDto);
            securityPermissions.add(securityPermission);
        }
        securityGroup.setPermissions(securityPermissions);

        return securityGroup;
    }
}
