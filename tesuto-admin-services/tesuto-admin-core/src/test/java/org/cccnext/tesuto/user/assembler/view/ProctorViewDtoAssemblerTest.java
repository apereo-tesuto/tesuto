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

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.cccnext.tesuto.admin.dto.SecurityPermissionDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.viewdto.ProctorViewDto;
import org.cccnext.tesuto.user.assembler.view.ProctorViewDtoAssembler;
import org.cccnext.tesuto.user.assembler.view.ProctorViewDtoAssemblerImpl;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class ProctorViewDtoAssemblerTest {

    ProctorViewDtoAssemblerImpl proctorViewDtoAssembler;

    public static UserAccountDto getUserAccountDto() {
        UserAccountDto userAccount = new UserAccountDto();
        userAccount.setAccountLocked(true);
        userAccount.setDisplayName("displayName");
        userAccount.setNamespace("authorNamespace");
        userAccount.setCreatedOnDate(new Date());
        userAccount.setEmailAddress("emailAddress");
        userAccount.setEnabled(true);
        userAccount.setAccountExpired(true);
        userAccount.setFailedLogins(10);
        userAccount.setFirstName("firstName:");
        userAccount.setLastName("lastName");
        userAccount.setLastLoginDate(new Date());
        userAccount.setLastUpdatedDate(new Date());
        userAccount.setPassword("password");
        userAccount.setUserAccountId("userAccountId");
        userAccount.setUsername("username");
        Set<SecurityGroupDto> securityGroupDtos = new HashSet<SecurityGroupDto>();
        SecurityGroupDto securityGroupDto = new SecurityGroupDto();
        securityGroupDto.setGroupName("GROUP_NAME");
        securityGroupDto.setSecurityGroupId(10);
        Set<SecurityPermissionDto> securityPermissionDtos = new HashSet<SecurityPermissionDto>();
        SecurityPermissionDto securityPermissionDto = new SecurityPermissionDto();
        securityPermissionDto.setSecurityPermissionId("securityPermissionId");
        securityPermissionDto.setDescription("description");
        securityPermissionDtos.add(securityPermissionDto);

        securityGroupDto.setSecurityPermissionDtos(securityPermissionDtos);
        userAccount.setSecurityGroupDtos(securityGroupDtos);
        Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("securityPermissionId");
        grantedAuthorities.add(grantedAuthority);
        userAccount.setGrantedAuthorities(grantedAuthorities);
        return userAccount;
    }

    @Test
    public void testAssembler() {
        ProctorViewDtoAssembler proctorViewDtoAssembler = new ProctorViewDtoAssemblerImpl();
        UserAccountDto userAccountDto = getUserAccountDto();
        ProctorViewDto poctorViewDto = proctorViewDtoAssembler.assembleViewDto(userAccountDto);

        assertEquals("Proctor Display Name Should Equal Account", userAccountDto.getDisplayName(),
                poctorViewDto.getDisplayName());

        assertEquals("Proctor User Name Should Equal Account username", userAccountDto.getUsername(),
                poctorViewDto.getUsername());

        assertEquals("Proctor User AccountId  Should Equal Account Id", userAccountDto.getUserAccountId(),
                poctorViewDto.getUserAccountId());
        for (SecurityGroupDto securityGroupDto : userAccountDto.getSecurityGroupDtos()) {
            assertTrue("Proctor Should Contain Security Group",
                    poctorViewDto.getSecurityGroups().contains(securityGroupDto.getGroupName()));
        }

        for (GrantedAuthority grantedAuthority : userAccountDto.getGrantedAuthorities()) {
            assertTrue("Proctor Should Contain Granted Authority",
                    poctorViewDto.getSecurityPermissions().contains(grantedAuthority.getAuthority()));
        }
    }

    @Test
    public void testAssemblerNoDisplayName() {
        ProctorViewDtoAssembler proctorViewDtoAssembler = new ProctorViewDtoAssemblerImpl();
        UserAccountDto userAccountDto = getUserAccountDto();
        userAccountDto.setDisplayName(null);
        ProctorViewDto poctorViewDto = proctorViewDtoAssembler.assembleViewDto(userAccountDto);
        String displayName = userAccountDto.getFirstName() + " " + userAccountDto.getLastName();
        assertEquals("Proctor Display Name Should Equal Account", displayName, poctorViewDto.getDisplayName());

        assertEquals("Proctor Display Name Should Equal Account", userAccountDto.getUsername(),
                poctorViewDto.getUsername());
        for (SecurityGroupDto securityGroupDto : userAccountDto.getSecurityGroupDtos()) {
            assertTrue("Proctor Should Contain Security Group",
                    poctorViewDto.getSecurityGroups().contains(securityGroupDto.getGroupName()));
        }

        for (GrantedAuthority grantedAuthority : userAccountDto.getGrantedAuthorities()) {
            assertTrue("Proctor Should Contain Granted Authority",
                    poctorViewDto.getSecurityPermissions().contains(grantedAuthority.getAuthority()));
        }
    }
}
