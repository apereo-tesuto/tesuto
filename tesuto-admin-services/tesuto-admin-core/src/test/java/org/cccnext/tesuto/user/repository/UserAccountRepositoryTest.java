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
package org.cccnext.tesuto.user.repository;

import org.cccnext.tesuto.user.model.UserAccount;
import org.cccnext.tesuto.user.service.UserGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.cccnext.tesuto.user.service.UserGenerator.PROCTOR_USER_NAME;
import static org.cccnext.tesuto.user.service.UserGenerator.STUDENT_USER_NAME_1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Important! These are integration tests. They connect with the database and
 * they are expecting values to be in there! These will fail without those
 * values.
 *
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/userTestApplicationContext.xml" })
@Transactional // this makes all test methods transactional!!
public class UserAccountRepositoryTest {

    @Resource(name = "userAccountRepository")
    UserAccountRepository userAccountRepository;
    
    @Autowired
    UserGenerator generator;
    
    private String generatedUserAccountId;
    

    public UserAccountRepositoryTest() {
    }

    @Before
    public void setUp() {
        generatedUserAccountId = generator.createStudent1UserAccount();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of findByUsername method, of class UserAccountRepository.
     */
    @Test
    public void testFindByUsername() throws Exception {
        UserAccount result = userAccountRepository.findByUsernameIgnoreCase(STUDENT_USER_NAME_1);
        assertEquals(String.format("Expected: %s\n Actual: %s\n", STUDENT_USER_NAME_1, result.getUsername()), 
                STUDENT_USER_NAME_1.toLowerCase(), result.getUsername().toLowerCase());
        
    }

    @Test
    public void testFindByUsernameCaseInsensitive() throws Exception {
        UserAccount result = userAccountRepository.findByUsernameIgnoreCase(STUDENT_USER_NAME_1);
        assertEquals(String.format("Expected: %s\n Actual: %s\n", STUDENT_USER_NAME_1, result.getUsername()), 
                STUDENT_USER_NAME_1.toLowerCase(), result.getUsername().toLowerCase());
        
    }

    @Test
    public void testFindByStudentUsernameMixedCase() throws Exception {
        UserAccount result = userAccountRepository.findByUsernameIgnoreCase("aaa1234");
        assertEquals(String.format("Expected: %s\n Actual: %s\n", STUDENT_USER_NAME_1, result.getUsername()), 
                STUDENT_USER_NAME_1.toLowerCase(), result.getUsername().toLowerCase());
    }
    @Test
    public void testFindByProctorUsernameMixedCase() throws Exception {
        generator.createProctorUserAccount("UNIT_TEST_AUTHOR");
        String mixedCaseProctorName = "Proctor@unicon.NET";
        UserAccount result = userAccountRepository.findByUsernameIgnoreCase(mixedCaseProctorName);
        assertEquals(String.format("Expected: %s\n Actual: %s\n", mixedCaseProctorName, result.getUsername()), 
                mixedCaseProctorName.toLowerCase(), result.getUsername().toLowerCase());
        
    }

    @Test
    public void testFindByUserAccountID() throws Exception {
        UserAccount result = userAccountRepository.findByUserAccountId(generatedUserAccountId);
        assertEquals("Get User by UserAccountID", STUDENT_USER_NAME_1.toLowerCase(), result.getUsername());

    }

    @Test
    public void testFindByUserAccountIDValidateLocations() throws Exception {
        String generatedProctorAccountId = generator.createProctorUserAccount("UNIT_TEST_AUTHOR");
        UserAccount result = userAccountRepository.findByUserAccountId(generatedProctorAccountId);
        assertEquals(String.format("Get User by UserAccountID expected username: %s\n actual: %s", PROCTOR_USER_NAME, result.getUsername()), PROCTOR_USER_NAME, result.getUsername());
        assertNotNull("Test Locations for User:proctor should not be null", result.getTestLocations());
        assertTrue(String.format("Test Locations for User: proctor expected 2 actual: %s", result.getTestLocations().size()), result.getTestLocations().size() == 2);
    }

    @Test
    public void testFindByUserAccountIdIn() throws Exception {
        /* This userAccountId is a valid username but not userAccountId -scott smith
        System.out.println("findByUserAccountIdIn");
        String userAccountId = "A123456";
        List<String> userStringList = new LinkedList<>();
        userStringList.add(userAccountId);
        List<UserAccount> result = userAccountRepository.findByUserAccountIdIn(userStringList);
        System.out.println(result.get(0).getUserAccountId());
        */
    }
}
