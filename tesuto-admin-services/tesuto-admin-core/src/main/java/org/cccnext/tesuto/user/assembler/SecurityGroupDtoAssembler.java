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

import java.util.List;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.cccnext.tesuto.domain.assembler.DtoAssembler;
import org.cccnext.tesuto.user.model.SecurityGroup;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public interface SecurityGroupDtoAssembler extends DtoAssembler<SecurityGroupDto, SecurityGroup> {
    SecurityGroupDto create(SecurityGroupDto securityGroupDto);

    SecurityGroupDto update(SecurityGroupDto securityGroupDto);

    SecurityGroupDto readById(Integer securityGroupId);

    SecurityGroupDto readByGroupName(String groupName);

    Set<SecurityGroupDto> readByUserAccountId(String userAccountId);

    List<SecurityGroupDto> readAll();

    void removeSecurityPermission(Integer securityGroupId, String securityPermissionId);

    void addSecurityPermission(Integer securityGroupId, String securityPermissionId);

    @Override
    SecurityGroupDto assembleDto(SecurityGroup securityGroup);

    @Override
    SecurityGroup disassembleDto(SecurityGroupDto securityGroupDto);
}
