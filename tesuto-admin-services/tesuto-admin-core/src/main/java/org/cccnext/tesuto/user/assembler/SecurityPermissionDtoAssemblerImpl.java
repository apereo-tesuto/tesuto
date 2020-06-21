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
import java.util.List;

import org.cccnext.tesuto.admin.dto.SecurityPermissionDto;
import org.cccnext.tesuto.user.model.SecurityPermission;
import org.cccnext.tesuto.user.repository.SecurityPermissionRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James T Stanley <jstanley@unicon.net>
 */
@Slf4j
@Component(value = "securityPermissionDtoAssembler")
public class SecurityPermissionDtoAssemblerImpl implements SecurityPermissionDtoAssembler {

    @Autowired
    SecurityPermissionRepository securityPermissionRepository;

    @Override
    public SecurityPermissionDto create(SecurityPermissionDto securityPermissionDto) {
        SecurityPermission securityPermission = disassembleDto(securityPermissionDto);

        // Protect if there is no security permission returned, in which case it
        // would these would not be generated.
        if (securityPermission != null) {
            Date currentTimeStamp = new Date(System.currentTimeMillis());
            securityPermission.setLastUpdatedDate(currentTimeStamp);
            securityPermission.setCreatedOnDate(currentTimeStamp);
        }

        securityPermission = securityPermissionRepository.save(securityPermission);
        return assembleDto(securityPermission);
    }

    @Override
    public SecurityPermissionDto update(SecurityPermissionDto securityPermissionDto) {
        SecurityPermission oldUser = securityPermissionRepository
                .findById(securityPermissionDto.getSecurityPermissionId()).get();
        securityPermissionDto.setSecurityPermissionId(oldUser.getSecurityPermissionId());
        SecurityPermission securityPermission = disassembleDto(securityPermissionDto);

        securityPermission = securityPermissionRepository.save(securityPermission);
        return assembleDto(securityPermission);
    }

    @Override
    public SecurityPermissionDto readById(String securityPermissionId) {
        SecurityPermission securityPermission = securityPermissionRepository.findById(securityPermissionId).get();
        return assembleDto(securityPermission);
    }

    @Override
    public List<SecurityPermissionDto> readAll() {
        List<SecurityPermission> securityPermissionList = securityPermissionRepository.findAll();
        List<SecurityPermissionDto> securityPermissions = new ArrayList<SecurityPermissionDto>();
        for (SecurityPermission securityPermission : securityPermissionList) {
            securityPermissions.add(assembleDto(securityPermission));
        }
        return securityPermissions;
    }

    @Override
    public SecurityPermissionDto assembleDto(SecurityPermission securityPermission) {
        // Drop out of here immediately if there is nothing to assemble.
        if (securityPermission == null) {
            return null;
        }

        SecurityPermissionDto securityPermissionDto = new SecurityPermissionDto();
        securityPermissionDto.setCreatedOnDate(securityPermission.getCreatedOnDate());
        securityPermissionDto.setLastUpdatedDate(securityPermission.getLastUpdatedDate());
        securityPermissionDto.setSecurityPermissionId(securityPermission.getSecurityPermissionId());
        securityPermissionDto.setDescription(securityPermission.getDescription());

        return securityPermissionDto;
    }

    @Override
    public SecurityPermission disassembleDto(SecurityPermissionDto securityPermissionDto) {
        // If there is nothing to disassemble, just return null
        if (securityPermissionDto == null) {
            return null;
        }
        SecurityPermission securityPermission = new SecurityPermission();
        securityPermission.setCreatedOnDate(securityPermissionDto.getCreatedOnDate());
        securityPermission.setLastUpdatedDate(securityPermissionDto.getLastUpdatedDate());
        securityPermission.setSecurityPermissionId(securityPermissionDto.getSecurityPermissionId());
        securityPermission.setDescription(securityPermissionDto.getDescription());

        return securityPermission;
    }
}
