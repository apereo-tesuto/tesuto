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
import org.cccnext.tesuto.domain.assembler.DtoAssembler;
import org.cccnext.tesuto.user.model.SecurityGroupApi;

import java.util.List;
import java.util.Set;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public interface SecurityGroupApiDtoAssembler extends DtoAssembler<SecurityGroupApiDto, SecurityGroupApi> {
    SecurityGroupApiDto create(SecurityGroupApiDto securityGroupApiDto);

    SecurityGroupApiDto update(SecurityGroupApiDto securityGroupApiDto);

    SecurityGroupApiDto readById(Integer securityGroupApiId);

    SecurityGroupApiDto readByGroupName(String groupName);

    Set<SecurityGroupApiDto> readByUserAccountApiId(String userAccountId);

    List<SecurityGroupApiDto> readAll();

    void removeSecurityPermission(Integer securityGroupApiId, String securityPermissionId);

    void addSecurityPermission(Integer securityGroupApiId, String securityPermissionId);

    @Override
    SecurityGroupApiDto assembleDto(SecurityGroupApi securityGroupApi);

    @Override
    SecurityGroupApi disassembleDto(SecurityGroupApiDto securityGroupApiDto);
}
