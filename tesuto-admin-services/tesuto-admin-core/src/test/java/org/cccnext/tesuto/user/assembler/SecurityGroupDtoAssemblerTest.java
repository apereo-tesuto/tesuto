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

import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.cccnext.tesuto.user.assembler.SecurityGroupDtoAssemblerImpl;
import org.cccnext.tesuto.user.model.SecurityGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/userTestApplicationContext.xml" })
public class SecurityGroupDtoAssemblerTest {

    @Autowired
    SecurityGroupDtoAssemblerImpl securityGroupDtoAssembler;

    public static SecurityGroup getSecurityGroup() {

        SecurityGroup securityGroup = new SecurityGroup();
        securityGroup.setCreatedOnDate(new Date());
        securityGroup.setDescription("description");
        securityGroup.setSecurityGroupId(10);
        securityGroup.setLastUpdatedDate(new Date());
        securityGroup.setGroupName("groupName");
        securityGroup.setPermissions(SecurityPermissionDtoAssemblerTest.getSecurityPermissions());
        return securityGroup;
    }

    public static Set<SecurityGroup> getSecurityGroups() {
        Set<SecurityGroup> securityGroups = new HashSet<SecurityGroup>();
        securityGroups.add(getSecurityGroup());
        return securityGroups;
    }

    @Test
    public void testAssembler() {
        SecurityGroup securityGroup = getSecurityGroup();

        SecurityGroupDto securityGroupDto = securityGroupDtoAssembler.assembleDto(securityGroup);

        SecurityGroup securityGroupAssembled = securityGroupDtoAssembler.disassembleDto(securityGroupDto);

        assertEquals("Assembled Security Group should be the same", securityGroup, securityGroupAssembled);
    }
}
