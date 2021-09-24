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

import org.cccnext.tesuto.admin.dto.SecurityPermissionDto;
import org.cccnext.tesuto.user.assembler.SecurityPermissionDtoAssembler;
import org.cccnext.tesuto.user.service.SecurityPermissionService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James T Stanley <jstanley@unicon.net>
 */
@Slf4j
@Service(value = "securityPermissionService")
public class SecurityPermissionServiceImpl implements SecurityPermissionService {

    @Autowired private SecurityPermissionDtoAssembler securityPermissionDtoAssembler;

    @Override
    @Transactional(readOnly = true)
    public List<SecurityPermissionDto> getAllSecurityPermissions() {
        return securityPermissionDtoAssembler.readAll();
    }

    @Override
    @Transactional(readOnly = true)
    public SecurityPermissionDto getSecurityPermission(String securityPermissionId) {
        SecurityPermissionDto securityPermissionDto = securityPermissionDtoAssembler.readById(securityPermissionId);
        return securityPermissionDto;
    }

    @Override
    @Transactional
    public SecurityPermissionDto update(SecurityPermissionDto securityPermissionDto) {
        return securityPermissionDtoAssembler.update(securityPermissionDto);
    }
}
