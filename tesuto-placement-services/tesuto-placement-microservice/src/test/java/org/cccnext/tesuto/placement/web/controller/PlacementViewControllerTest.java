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
package org.cccnext.tesuto.placement.web.controller;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.cccnext.tesuto.admin.client.UserAccountClient;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.viewdto.StudentViewDto;
import org.cccnext.tesuto.placement.service.PlacementService;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.cccnext.tesuto.placement.view.student.PlacementStudentViewDto;
import org.cccnext.tesuto.springboot.web.RequestForwardService;
import org.cccnext.tesuto.user.service.StudentReader;
import org.cccnext.tesuto.user.service.UserContextService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@RunWith(SpringRunner.class)
public class PlacementViewControllerTest {
    private MockMvc mockMvc;

    @Mock
    PlacementService placementService;
    
    @Mock
    UserContextService userContextService;
    
    @Mock
    UserAccountClient userAccountService;
    
    @Mock
    RequestForwardService requestForwardService;
    
    @Mock
    StudentReader studentService;

    @InjectMocks
    PlacementViewController placementController;

    PlacementViewController placementControllerSpy;

    private String testCollegeId = "ZZZ1";
    private String testCccid = "A12345";

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
        placementControllerSpy = spy(placementController);

        mockMvc = MockMvcBuilders
                .standaloneSetup(placementControllerSpy)
                .build();
    }

    @Test
    public void testNoPlacementForStudent() throws Exception {
    	when(userContextService.getCurrentUserId()).thenReturn(testCccid);
    	when(userContextService.getCurrentAuthentication()).thenReturn(new TestingAuthenticationToken(mockAdminUser(), null));
    	when(studentService.getStudentById(testCccid)).thenReturn(mockStudentUser());
    	when(userAccountService.getUserAccountByUsername(testCccid)).thenReturn(mockAdminUser());
        mockMvc.perform(get("/service/v1/colleges/" + testCollegeId + "/cccid/"+testCccid+"/placements"))
            .andExpect(status().isNotFound());
        verify(placementService).getPlacementsForStudent(testCollegeId, testCccid);
    }

    @Test
    public void testPlacementsForStudents() throws Exception {
    	when(userContextService.getCurrentUserId()).thenReturn(testCccid);
    	when(userContextService.getCurrentAuthentication()).thenReturn(new TestingAuthenticationToken(mockAdminUser(), null));
    	when(userAccountService.getUserAccountByUsername(testCccid)).thenReturn(mockAdminUser());
    	when(studentService.getStudentById(testCccid)).thenReturn(mockStudentUser());
        when( placementService.getPlacementsForStudent(eq(testCollegeId), eq(testCccid))).thenReturn(Collections.singleton(new PlacementViewDto()));

        mockMvc.perform(get("/service/v1/colleges/"+ testCollegeId + "/cccid/"+testCccid+"/placements"))
            .andExpect(status().isOk());
        verify(placementService).getPlacementsForStudent(testCollegeId, testCccid);
    }

    @Test
    public void testPlacementAsStudent() throws Exception {
    	when(userContextService.getCurrentUserId()).thenReturn(testCccid);
    	when(userContextService.getCurrentAuthentication()).thenReturn(new TestingAuthenticationToken(mockAdminUser(), null));
    	when(userAccountService.getUserAccountByUsername(testCccid)).thenReturn(mockAdminUser());
        when( placementService.getStudentViewPlacementsForStudent(eq(testCollegeId), eq(testCccid))).thenReturn(Collections.singleton(new PlacementStudentViewDto()));
        mockMvc.perform(get("/service/v1/colleges/"+ testCollegeId + "/student-placements"))
            .andExpect(status().isOk());
        verify(placementService).getStudentViewPlacementsForStudent(testCollegeId, testCccid);
    }

    private UserAccountDto mockAdminUser() {
    	UserAccountDto admin = new UserAccountDto();
        admin.setCollegeIds(Collections.singleton(testCollegeId));
        admin.setUsername(testCccid);

        return admin;
    }

    private StudentViewDto mockStudentUser() {
    	StudentViewDto student = new StudentViewDto();
    	student.setCccid(testCccid);
    	Map<String,Integer> collegeStatuses = new HashMap();
    	collegeStatuses.put(testCollegeId, 1);
    	student.setCollegeStatuses(collegeStatuses);
        return student;
    }
}
