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

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.SecurityPermissionDto;
import org.cccnext.tesuto.user.assembler.SecurityPermissionDtoAssemblerImpl;
import org.cccnext.tesuto.user.model.SecurityPermission;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/userTestApplicationContext.xml" })
public class SecurityPermissionDtoAssemblerTest {

    @Autowired
    SecurityPermissionDtoAssemblerImpl securityPermissionDtoAssembler;

    public static SecurityPermission getSecurityPermission() {
        SecurityPermission securityPermission = new SecurityPermission();
        securityPermission.setDescription("description");
        securityPermission.setCreatedOnDate(new Date());
        securityPermission.setSecurityPermissionId("securityPermissionId");
        securityPermission.setLastUpdatedDate(new Date());
        return securityPermission;
    }

    public static Set<SecurityPermission> getSecurityPermissions() {
        Set<SecurityPermission> securityPermissions = new HashSet<SecurityPermission>();
        securityPermissions.add(getSecurityPermission());
        return securityPermissions;
    }

    @Test
    public void testAssembler() {
        SecurityPermission securityPermission = getSecurityPermission();

        SecurityPermissionDto securityPermissionDto = securityPermissionDtoAssembler.assembleDto(securityPermission);

        SecurityPermission securityPermissionAssembled = securityPermissionDtoAssembler
                .disassembleDto(securityPermissionDto);

        assertEquals("Assembled Permission should be the same", securityPermission, securityPermissionAssembled);
    }

}
