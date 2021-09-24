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
import org.cccnext.tesuto.admin.dto.SecurityPermissionDto;
import org.cccnext.tesuto.user.model.SecurityGroupApi;
import org.cccnext.tesuto.user.model.SecurityPermission;
import org.cccnext.tesuto.user.repository.SecurityGroupApiRepository;
import org.cccnext.tesuto.user.repository.UserAccountApiRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
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
@Component(value = "securityGroupApiDtoAssembler")
public class SecurityGroupApiDtoAssemblerImpl implements SecurityGroupApiDtoAssembler {

    @Autowired UserAccountApiRepository userAccountApiRepository;
    @Autowired SecurityGroupApiRepository securityGroupApiRepository;
    @Autowired SecurityPermissionDtoAssembler securityPermissionDtoAssembler;

    @Override
    public SecurityGroupApiDto create(SecurityGroupApiDto securityGroupApiDto) {
        SecurityGroupApi securityGroupApi = disassembleDto(securityGroupApiDto);

        // Protect if there is no security group returned.
        if (securityGroupApi != null) {
            Date currentTimeStamp = new Date(System.currentTimeMillis());
            securityGroupApi.setLastUpdatedDate(currentTimeStamp);
            securityGroupApi.setCreatedOnDate(currentTimeStamp);
        }

        securityGroupApi = securityGroupApiRepository.save(securityGroupApi);
        return assembleDto(securityGroupApi);
    }

    @Override
    public SecurityGroupApiDto update(SecurityGroupApiDto securityGroupApiDto) {
        SecurityGroupApi oldUser = securityGroupApiRepository.findById(securityGroupApiDto.getSecurityGroupApiId()).get();
        securityGroupApiDto.setSecurityGroupApiId(oldUser.getSecurityGroupApiId());
        SecurityGroupApi securityGroupApi = disassembleDto(securityGroupApiDto);

        securityGroupApi = securityGroupApiRepository.save(securityGroupApi);
        return assembleDto(securityGroupApi);
    }

    @Override
    public SecurityGroupApiDto readById(Integer securityGroupApiId) {
        SecurityGroupApi securityGroupApi = securityGroupApiRepository.findById(securityGroupApiId).get();
        return assembleDto(securityGroupApi);
    }

    @Override
    public SecurityGroupApiDto readByGroupName(String groupName) {
        SecurityGroupApi securityGroupApi = securityGroupApiRepository.findByGroupName(groupName);
        return assembleDto(securityGroupApi);
    }

    @Override
    public Set<SecurityGroupApiDto> readByUserAccountApiId(String userAccountApiId) {
        Set<SecurityGroupApi> securityGroupApiSet = userAccountApiRepository.findByUserAccountApiId(userAccountApiId).getSecurityGroupApis();
        return assembleDto(securityGroupApiSet);
    }

    @Override
    public List<SecurityGroupApiDto> readAll() {
        List<SecurityGroupApi> securityGroupApiList = securityGroupApiRepository.findAll();
        List<SecurityGroupApiDto> securityGroupApis = new ArrayList<SecurityGroupApiDto>();
        for (SecurityGroupApi securityGroupApi : securityGroupApiList) {
            securityGroupApis.add(assembleDto(securityGroupApi));
        }
        return securityGroupApis;
    }

    @Override
    public void removeSecurityPermission(Integer securityGroupApiId, String securityPermissionId) {
        try {
            securityGroupApiRepository.removeSecurityPermission(securityGroupApiId, securityPermissionId);
        } catch (JpaSystemException jse) {
            if (jse.getCause().getMessage().equals("could not extract ResultSet")) {
                // Ignore, this is expected.
            } else {
                throw jse;
            }
        }
    }

    @Override
    public void addSecurityPermission(Integer securityGroupApiId, String securityPermissionId) {
        try {
            securityGroupApiRepository.addSecurityPermission(securityGroupApiId, securityPermissionId);
        } catch (JpaSystemException jse) {
            if (jse.getCause().getMessage().equals("could not extract ResultSet")) {
                // Ignore, this is expected.
            } else {
                throw jse;
            }
        }
    }

    @Override
    public SecurityGroupApiDto assembleDto(SecurityGroupApi securityGroupApi) {
        // Drop out of here immediately if there is nothing to assemble.
        if (securityGroupApi == null) {
            return null;
        }

        SecurityGroupApiDto securityGroupApiDto = new SecurityGroupApiDto();
        securityGroupApiDto.setCreatedOnDate(securityGroupApi.getCreatedOnDate());
        securityGroupApiDto.setLastUpdatedDate(securityGroupApi.getLastUpdatedDate());
        securityGroupApiDto.setSecurityGroupApiId(securityGroupApi.getSecurityGroupApiId());
        securityGroupApiDto.setGroupName(securityGroupApi.getGroupName());
        securityGroupApiDto.setDescription(securityGroupApi.getDescription());

        Set<SecurityPermissionDto> securityPermissionDtos = new HashSet<SecurityPermissionDto>();
        for (SecurityPermission securityPermission : securityGroupApi.getPermissions()) {
            SecurityPermissionDto securityPermissionDto = new SecurityPermissionDto();
            securityPermissionDtos.add(securityPermissionDtoAssembler.assembleDto(securityPermission));
        }
        securityGroupApiDto.setSecurityPermissionDtos(securityPermissionDtos);

        return securityGroupApiDto;
    }

    @Override
    public SecurityGroupApi disassembleDto(SecurityGroupApiDto securityGroupApiDto) {
        // If there is nothing to disassemble, just return null
        if (securityGroupApiDto == null) {
            return null;
        }
        SecurityGroupApi securityGroupApi = new SecurityGroupApi();
        securityGroupApi.setCreatedOnDate(securityGroupApiDto.getCreatedOnDate());
        securityGroupApi.setLastUpdatedDate(securityGroupApiDto.getLastUpdatedDate());
        securityGroupApi.setSecurityGroupApiId(securityGroupApiDto.getSecurityGroupApiId());
        securityGroupApi.setGroupName(securityGroupApiDto.getGroupName());
        securityGroupApi.setDescription(securityGroupApiDto.getDescription());

        Set<SecurityPermission> securityPermissions = new HashSet<SecurityPermission>();
        for (SecurityPermissionDto securityPermissionDto : securityGroupApiDto.getSecurityPermissionDtos()) {
            SecurityPermission securityPermission = securityPermissionDtoAssembler
                    .disassembleDto(securityPermissionDto);
            securityPermissions.add(securityPermission);
        }
        securityGroupApi.setPermissions(securityPermissions);

        return securityGroupApi;
    }
}
