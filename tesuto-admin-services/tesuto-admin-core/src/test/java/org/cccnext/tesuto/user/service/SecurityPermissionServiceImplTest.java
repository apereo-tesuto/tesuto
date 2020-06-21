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

import javax.annotation.Resource;

import org.cccnext.tesuto.admin.dto.SecurityPermissionDto;
import org.cccnext.tesuto.user.model.SecurityPermission;
import org.cccnext.tesuto.user.service.SecurityGroupService;
import org.cccnext.tesuto.user.service.SecurityPermissionService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/userTestApplicationContext.xml" })
@Transactional
public class SecurityPermissionServiceImplTest {
	@Resource(name = "securityPermissionService")
	SecurityPermissionService securityPermissionService;

	public SecurityPermissionServiceImplTest() {

	}

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAllSecurityPermissions() throws Exception {
		System.out.println("SecurityPermissionServiceImpl.getAllSecurityPermissions()");
		String permissionId = "VIEW_PROCTOR_DASHBOARD";
		String permissionId1 = "VIEW_PREVIEW_ASSESSMENT_UPLOAD_UI";
		List<SecurityPermissionDto> securityPermissionDtoList = securityPermissionService.getAllSecurityPermissions();
		// Verify a couple of different permissions that should be in the list
		SecurityPermissionDto securityPermissionDto = new SecurityPermissionDto();
		securityPermissionDto.setSecurityPermissionId(permissionId);
		securityPermissionDto.setDescription("View the user's dashboard");
		SecurityPermissionDto securityPermissionDto1 = new SecurityPermissionDto();
		securityPermissionDto1.setSecurityPermissionId(permissionId1);
		securityPermissionDto1.setDescription("View the assessment preview upload UI");
		assertTrue(securityPermissionDtoList.contains(securityPermissionDto));
		assertTrue(securityPermissionDtoList.contains(securityPermissionDto1));
	}

	@Test
	public void testGetSecurityPermission() throws Exception {
		System.out.println("SecurityPermissionServiceImpl.getSecurityPermission()");
		SecurityPermissionDto securityPermissionDto = securityPermissionService.getSecurityPermission("VIEW_ASSESSMENT_SESSION");
		assertEquals("VIEW_ASSESSMENT_SESSION", securityPermissionDto.getSecurityPermissionId());
	}

	@Test
	public void testUpdate() throws Exception {
		System.out.println("SecurityPermissionServiceImpl.update()");
		SecurityPermissionDto securityPermissionDto = securityPermissionService.getSecurityPermission("VIEW_PREVIEW_ASSESSMENT_UPLOAD_UI");
		String description = "View the assessment preview upload UI with a change ;-)";
		securityPermissionDto.setDescription(description);
		securityPermissionService.update(securityPermissionDto);
		SecurityPermissionDto securityPermissionDto1 = securityPermissionService.getSecurityPermission("VIEW_PREVIEW_ASSESSMENT_UPLOAD_UI");
		assertEquals(description, securityPermissionDto1.getDescription());
	}
}
