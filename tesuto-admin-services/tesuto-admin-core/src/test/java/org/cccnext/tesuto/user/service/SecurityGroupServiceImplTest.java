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

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.cccnext.tesuto.admin.dto.SecurityPermissionDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.user.service.SecurityGroupService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/userTestApplicationContext.xml" })
@Transactional
public class SecurityGroupServiceImplTest {
	@Resource(name = "securityGroupService")
	SecurityGroupService securityGroupService;
	
	private String userAccountId;
	
	@Autowired
	private UserGenerator userGenerator;

	public SecurityGroupServiceImplTest() {

	}

	@Before
	public void setUp() throws Exception {
	    userAccountId = userGenerator.createProctorUserAccount("UNIT_TEST_AUTHOR");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddSecurityPermissionToSecurityGroup() throws Exception {
		System.out.println("SecurityGroupServiceImpl.addSecurityPermissionToSecurityGroup()");
		String permissionId = "VIEW_PROCTOR_DASHBOARD";
		securityGroupService.addSecurityPermissionToSecurityGroup(2,permissionId);
		SecurityPermissionDto securityPermissionDto = new SecurityPermissionDto();
		securityPermissionDto.setSecurityPermissionId(permissionId);
		securityPermissionDto.setDescription("View the user's dashboard");
		SecurityGroupDto securityGroupDto = securityGroupService.getSecurityGroup(2);
		assertTrue(securityGroupDto.getSecurityPermissionDtos().contains(securityPermissionDto));
	}

	@Test
	public void testRemoveSecurityPermissionFromSecurityGroup() throws Exception {
		System.out.println("SecurityGroupServiceImpl.removeSecurityPermissionFromSecurityGroup()");
		String permissionId = "VIEW_PREVIEW_ASSESSMENT_UPLOAD_UI";
		securityGroupService.removeSecurityPermissionFromSecurityGroup(2,permissionId);
		SecurityPermissionDto securityPermissionDto = new SecurityPermissionDto();
		securityPermissionDto.setSecurityPermissionId(permissionId);
		securityPermissionDto.setDescription("View the assessment preview upload UI");
		SecurityGroupDto securityGroupDto = securityGroupService.getSecurityGroup(2);
		assertFalse(securityGroupDto.getSecurityPermissionDtos().contains(securityPermissionDto));
	}

	@Test
	public void testGetAllSecurityGroups() throws Exception {
		System.out.println("SecurityGroupServiceImpl.getAllSecurityGroups()");
		int expResult = 5;
		List<SecurityGroupDto> securityGroupDtoList = securityGroupService.getAllSecurityGroups();
		assertTrue(expResult <= securityGroupDtoList.size());
	}

	@Test
	public void testGetSecurityGroup() throws Exception {
		System.out.println("SecurityGroupServiceImpl.getSecurityGroup()");
		SecurityGroupDto securityGroupDto = securityGroupService.getSecurityGroup(2);
		assertEquals("LOCAL_ADMIN", securityGroupDto.getGroupName());
	}

	@Test
	public void testUpsert() throws Exception {
		System.out.println("SecurityGroupServiceImpl.getSecurityGroup()");
		// First let's test and update
		SecurityGroupDto securityGroupDto = securityGroupService.getSecurityGroup(2);
		securityGroupDto.setGroupName("SUPER_ADMIN");
		securityGroupService.upsert(securityGroupDto);
		SecurityGroupDto securityGroupDto1 = securityGroupService.getSecurityGroup(2);
		assertEquals("SUPER_ADMIN", securityGroupDto1.getGroupName());
	}

	@Test
	public void testGetSecurityGroupByGroupName() throws Exception {
		System.out.println("SecurityGroupServiceImpl.getSecurityGroupByGroupName()");
		SecurityGroupDto securityGroupDto = securityGroupService.getSecurityGroupByGroupName("LOCAL_ADMIN");
		assertEquals(2, securityGroupDto.getSecurityGroupId().intValue());
	}

	@Test
	public void testGetSecurityGroupByUserAccountId() throws Exception {
		System.out.println("SecurityGroupServiceImpl.getSecurityGroupByUserAccountId()");
		
		Set<SecurityGroupDto> securityGroupDtoSet = securityGroupService.getSecurityGroupsByUserAccountId(userAccountId);
		assertEquals("LOCAL_ADMIN", securityGroupDtoSet.iterator().next().getGroupName());
	}
}
