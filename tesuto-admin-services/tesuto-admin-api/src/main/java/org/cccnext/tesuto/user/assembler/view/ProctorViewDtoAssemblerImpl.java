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
package org.cccnext.tesuto.user.assembler.view;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.admin.assembler.view.TestLocationViewDtoAssembler;
import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.viewdto.ProctorViewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component(value = "proctorViewDtoAssembler")
public class ProctorViewDtoAssemblerImpl implements ProctorViewDtoAssembler {

    @Autowired
    TestLocationViewDtoAssembler testLocationViewDtoAssembler;

    public ProctorViewDto assembleViewDto(UserAccountDto userAccountDto) {
        if (userAccountDto == null) {
            return null;
        }

        ProctorViewDto proctorViewDto = new ProctorViewDto();

        proctorViewDto.setUsername(userAccountDto.getUsername());

        proctorViewDto.setColleges(userAccountDto.getColleges());

        proctorViewDto.setEnabled(userAccountDto.isEnabled());

        String displayName = userAccountDto.getDisplayName();
        if (StringUtils.isBlank(displayName)) {
            if (StringUtils.isNotBlank(userAccountDto.getFirstName())) {
                displayName = userAccountDto.getFirstName();
            }
            if (StringUtils.isNotBlank(userAccountDto.getLastName())) {
                displayName += " " + userAccountDto.getLastName();
            }
        }
        proctorViewDto.setDisplayName(displayName);

        proctorViewDto.setUserAccountId(userAccountDto.getUserAccountId());
        List<String> securityGroups = new ArrayList<String>();

        if (CollectionUtils.isNotEmpty(userAccountDto.getSecurityGroupDtos())) {
            for (SecurityGroupDto securityGroupDto : userAccountDto.getSecurityGroupDtos()) {
                securityGroups.add(securityGroupDto.getGroupName());
            }
        }
        proctorViewDto.setSecurityGroups(securityGroups);

        List<String> securityPermissions = new ArrayList<String>();

        if (CollectionUtils.isNotEmpty(userAccountDto.getAuthorities())) {
            for (GrantedAuthority grantedAuthority : userAccountDto.getAuthorities()) {
                securityPermissions.add(grantedAuthority.getAuthority());
            }
        }
        proctorViewDto.setSecurityPermissions(securityPermissions);
        return proctorViewDto;
    }

    public UserAccountDto disassembleViewDto(ProctorViewDto dto) {
        throw new UnsupportedOperationException(
                "If you're calling this method, you will need to complete this method.");
    }
}
