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

import static org.cccnext.tesuto.user.service.UserAccountService.EMAIL_ADDRESS_IS_MISSING;
import static org.cccnext.tesuto.user.service.UserAccountService.EPPN_COLLEGE_CODE_NOT_IN_USER_COLLEGE_IDS;
import static org.cccnext.tesuto.user.service.UserAccountService.FIRST_NAME_MISSING;
import static org.cccnext.tesuto.user.service.UserAccountService.INVALID_EMAIL_ADDRESS;
import static org.cccnext.tesuto.user.service.UserAccountService.INVALID_PRIMARY_COLLEGE;
import static org.cccnext.tesuto.user.service.UserAccountService.LAST_NAME_MISSING;
import static org.cccnext.tesuto.user.service.UserGenerator.STUDENT_USER_NAME_1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;

import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.dto.UserPasswordResetDto;
import org.cccnext.tesuto.admin.service.CollegeService;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.exception.ValidationException;
import org.cccnext.tesuto.user.repository.UserAccountRepository;
import org.cccnext.tesuto.user.service.SecurityGroupService;
import org.cccnext.tesuto.user.service.UserAccountExistsException;
import org.cccnext.tesuto.user.service.UserAccountService;
import org.cccnext.tesuto.util.TesutoUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.spockframework.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/userTestApplicationContext.xml" })
@Transactional
public class UserAccountServiceImplTest {

    @Autowired
    EntityManagerFactory emf;

    @Resource(name = "userAccountService")
    UserAccountService userAccountService;
    @Resource(name = "securityGroupService")
    SecurityGroupService securityGroupService;
    @Resource(name = "collegeService")
    CollegeService collegeService;
    
    @Autowired
    UserGenerator generator;

    @Autowired
    UserAccountRepository repo;
    
    private String studentUserAccountId;
    
    private String proctorUserAccountId;

    public UserAccountServiceImplTest() {
    }

    @Before
    public void setUp() throws Exception {
        EntityManager manager = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
        studentUserAccountId = generator.createStudent1UserAccount();
        proctorUserAccountId = generator.createProctorUserAccount("UNIT_TEST_AUTHOR");
        generator.createDeletedStudentUserAccount();
        generator.createDeletedProctorUserAccount("UNIT_TEST_AUTHOR");
        manager.flush();
        manager.clear();
    }

    @After
    public void tearDown() throws Exception {

    }

    private UserAccountDto makeUser() {
        String id = TesutoUtils.newId();
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setUserAccountId(id);
        userAccountDto.setDisplayName("Tester McTestyface");
        userAccountDto.setEmailAddress("test@nowhere.com");
        userAccountDto.setUsername("tface");
        userAccountDto.setPassword("tpassword");
        return userAccountDto;
    }

    @Test
    public void findByTestLocationIdDoesNotReturnDeletedUsers() {
        Set<UserAccountDto> users = userAccountService.getUsersByTestLocation("ZZ2");
        assertTrue("Every returned user should not be deleted", users.stream().allMatch(u -> !u.isDeleted()));
    }

    @Test
    public void getUsersByCollegeDoesNotReturnDeletedUsers() {
        Set<UserAccountDto> users = userAccountService.getUsersByCollege("ZZ1");
        assertTrue("Every returned user should not be deleted", users.stream().allMatch(u -> !u.isDeleted()));
    }

    @Test
    public void testCreate() throws Exception {
        EntityManager manager = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
        UserAccountDto userAccountDto = makeUser();
        
        generator.createTestLocation("963 College:", "963", "963");
        generator.createTestLocation("611 College:", "611", "611");
        Set<String> collegeIds = new HashSet<>();
        collegeIds.add("963");
        collegeIds.add("611");
        Set<String> testLocationIds = new HashSet<>();
        testLocationIds.add("963");
        testLocationIds.add("611");
        Set<Integer> roleIds = new HashSet<>();
        roleIds.add(1);
        roleIds.add(2);
        String userId = userAccountService.create(userAccountDto, roleIds, collegeIds, testLocationIds);
        manager.flush();
        manager.clear();
        UserAccountDto result = userAccountService.getUserAccount(userId);
        for (CollegeDto collegeDto : result.getColleges()) {
            for (TestLocationDto testLocationDto : collegeDto.getTestLocations()) {
                assertTrue(testLocationIds.contains(testLocationDto.getId()));
            }
        }
        assertEquals(result.getUserAccountId(), userId);
        assertEquals(collegeIds, result.getCollegeIds());
        Set<Integer> resultRoleIds = result.getSecurityGroupDtos().stream().map(g -> g.getSecurityGroupId())
                .collect(Collectors.toSet());
        assertEquals(roleIds, resultRoleIds);
    }

    @Test
    public void testCreateForceLowercaseUsername() throws Exception {
        EntityManager manager = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
        UserAccountDto userAccountDto = makeUser();
        userAccountDto.setUsername("TFACE1");
        generator.createTestLocation("963 College:", "963", "963");
        generator.createTestLocation("611 College:", "611", "611");
        Set<String> collegeIds = new HashSet<>();
        collegeIds.add("963");
        collegeIds.add("611");
        Set<String> testLocationIds = new HashSet<>();
        testLocationIds.add("963");
        testLocationIds.add("611");
        Set<Integer> roleIds = new HashSet<>();
        roleIds.add(1);
        roleIds.add(2);
        String userId = userAccountService.create(userAccountDto, roleIds, collegeIds, testLocationIds);
        manager.flush();
        manager.clear();
        UserAccountDto result = userAccountService.getUserAccount(userId);
        assertEquals(result.getUserAccountId(), userId);
        assertEquals(userAccountDto.getUsername().toLowerCase(), result.getUsername());
    }

    @Test(expected = UserAccountExistsException.class)
    public void testCreateExistingUserThrowsException() throws Exception {
        UserAccountDto userAccountDto = makeUser();
        userAccountDto.setUserAccountId(studentUserAccountId);
        userAccountDto.setPrimaryCollegeId("ZZ1");
        userAccountDto.setUsername(STUDENT_USER_NAME_1);
        userAccountService.create(userAccountDto, new HashSet<Integer>(0), new HashSet<String>(0), new HashSet<String>(0));
    }

    @Test
    public void testGetUserAccount() throws Exception {
        System.out.println("UserAccountServiceImpl.getUserAccount()");
        String id = studentUserAccountId;
        String expResult = STUDENT_USER_NAME_1.toLowerCase();
        UserAccountDto result = userAccountService.getUserAccount(id);
        assertEquals(expResult, result.getUsername());
    }

    @Test
    public void testGetUserAccountByUserAccountId() throws Exception {
        System.out.println("UserAccountServiceImpl.getUserAccountByUserAccountId()");
        String id = studentUserAccountId;
        String expResult = STUDENT_USER_NAME_1.toLowerCase();
        UserAccountDto result = userAccountService.getUserAccountByUserAccountId(id);
        assertEquals(expResult, result.getUsername());
    }

    @Test
    public void testUpsert() throws Exception {
        System.out.println("UserAccountServiceImpl.upsert()");
        UserAccountDto expectedUserAccountDto = new UserAccountDto();
        expectedUserAccountDto.setUserAccountId("1234");
        expectedUserAccountDto.setDisplayName("Scott Smith");
        expectedUserAccountDto.setEmailAddress("test@unicon.net");
        expectedUserAccountDto.setUsername("ssmith");
        expectedUserAccountDto.setPassword("password1");
        UserAccountDto returnedUserAccountDto = userAccountService.upsert(expectedUserAccountDto);

        // Check it
        UserAccountDto returnedUserAccountDto1 = userAccountService
                .getUserAccount(returnedUserAccountDto.getUserAccountId());
        assertEquals(expectedUserAccountDto.getDisplayName(), returnedUserAccountDto.getDisplayName());
        assertEquals(expectedUserAccountDto.getDisplayName(), returnedUserAccountDto1.getDisplayName());
    }

    @Test
    public void testGetUserAccountByUsername() throws Exception {
        System.out.println("UserAccountServiceImpl.getUserAccountByUsername()");
        String username = STUDENT_USER_NAME_1;
        UserAccountDto result = userAccountService.getUserAccountByUsername(username);
        assertEquals(username.toLowerCase(), result.getUsername());
    }

    /**
     * Does not need the user account to exist in the database.
     * 
     * @throws Exception
     */
    @Test
    public void testSetUserAccountCredential() throws Exception {
        UserAccountDto userAccountDto = userAccountService.getUserAccountByUsername(STUDENT_USER_NAME_1);
        String expResult = STUDENT_USER_NAME_1.toLowerCase();
        String oldPassword = userAccountDto.getPassword();
        UserAccountDto result = userAccountService.setUserAccountCredential(userAccountDto, "another-password");
        assertEquals(expResult, result.getUsername());
        assertNotEquals(oldPassword, userAccountDto.getPassword());
    }

    /**
     * Expects the user account to exist in the database.
     *
     * @throws Exception
     */
    @Test
    public void testSetUserAccountCredential1() throws Exception {
        UserAccountDto userAccountDto = userAccountService.getUserAccountByUsername(STUDENT_USER_NAME_1);

        String oldPassword = userAccountDto.getPassword();
        UserAccountDto result = userAccountService.setUserAccountCredential(STUDENT_USER_NAME_1, "another-password");
        assertNotEquals(oldPassword, result.getPassword());
    }

    @Test
    public void testResetPassword() throws Exception {
        UserPasswordResetDto userPasswordResetDto = new UserPasswordResetDto();
        userPasswordResetDto.setUsername(STUDENT_USER_NAME_1);
        userPasswordResetDto.setDisplayName("Superman");
        userPasswordResetDto.setCredential("bogus");
        userAccountService.resetPassword(userPasswordResetDto);
        assertNotEquals("bogus", userPasswordResetDto.getCredential());
    }

    /**
     * Not a complete test because the Authentication in the web session
     * scenario should be mocked too. There are 2 possible outcomes and this
     * only tests the one pulled from the database where the web session does
     * not exist. The test is disabled in the absence of a session for now.
     * 
     * @throws Exception
     */
    @Test
    @Ignore
    public void testGetUserAccountFromAuthenticationByUsername() throws Exception {
        System.out.println("UserAccountServiceImpl.getUserAccountFromAuthenticationByUsername()");
        String expResult = "scott";

        UserAccountDto userAccountDto1 = userAccountService.getUserAccountFromAuthenticationByUsername(STUDENT_USER_NAME_1);
        assertEquals(expResult, userAccountDto1.getUsername());
    }



    private UserAccountDto getUser() throws Exception {
        return userAccountService.getUserAccount(studentUserAccountId);
    }

    @Test
    public void validateUsersAreValid() throws Exception {
        userAccountService.validateUserAccount(getUser(), buildCollegeIds());
    }

    @Test
    public void validationExceptionFirstName() throws Exception {
        UserAccountDto dto = getUser();
        dto.setFirstName(null);
        List<String> messages = userAccountService.validateUserAccount(dto, buildCollegeIds());
        assertEquals(1, messages.size());
        assertEquals(messages.get(0), FIRST_NAME_MISSING);
    }

    @Test
    public void validationExceptionLastName() throws Exception {
        UserAccountDto dto = getUser();
        dto.setLastName(null);
        List<String> messages = userAccountService.validateUserAccount(dto, buildCollegeIds());
        assertEquals(1, messages.size());
        assertEquals(messages.get(0), LAST_NAME_MISSING);
    }

    @Test
    public void validationExceptionNullEmail() throws Exception {
        UserAccountDto dto = getUser();
        dto.setEmailAddress(null);
        List<String> messages = userAccountService.validateUserAccount(dto, buildCollegeIds());
        assertEquals(1, messages.size());
        assertEquals(messages.get(0), EMAIL_ADDRESS_IS_MISSING);
    }

    @Test
    public void validationExceptionBadEmail() throws Exception {
        UserAccountDto dto = getUser();
        dto.setEmailAddress("This is not an email address");
        List<String> messages = userAccountService.validateUserAccount(dto, buildCollegeIds());
        assertEquals(1, messages.size());
        assertTrue(messages.get(0).startsWith(INVALID_EMAIL_ADDRESS));
    }

    @Test
    public void validateExceptionBadPrimaryCollege() throws Exception {
        UserAccountDto dto = getUser();
        dto.setPrimaryCollegeId("Bad college id");
        List<String> messages = userAccountService.validateUserAccount(dto, buildCollegeIds());
        assertEquals(1, messages.size());
        assertEquals(messages.get(0), INVALID_PRIMARY_COLLEGE + dto.getPrimaryCollegeId());
    }

    @Test
    public void formDataIsValidAndUserAccountIdIsValid() throws Exception {
        UserAccountDto dto = getUser();
        dto.setUsername("userAccountId");
        dto.setPrimaryCollegeId("ZZ1");
        userAccountService.updateEppnWithSuffix(dto);
        assertEquals("userAccountId@democollege.edu", dto.getUsername());
    }

    @Test(expected = ValidationException.class)
    public void eppnCodeDataIsValidAndUserAccountIdSuffixRemovedReplaced() throws Exception {
        UserAccountDto dto = getUser();
        dto.setPrimaryCollegeId("ZZ1");
        dto.setUsername("userAccountId@thisistoberemoved@thisaswell.com");
        userAccountService.validateEppnCode("ZZ1", buildCollegeIds());
        userAccountService.updateEppnWithSuffix(dto);
    }

    @Test
    public void eppnCodeIsNotInCollegeIds() throws ValidationException {
        String collegeEppnCode = "eppnCode";
        List<String> messages = userAccountService.validateEppnCode(collegeEppnCode, buildCollegeIds());
        assertEquals(1, messages.size());
        assertTrue(messages.get(0).startsWith(EPPN_COLLEGE_CODE_NOT_IN_USER_COLLEGE_IDS));
    }

    @Test(expected = EntityNotFoundException.class)
    public void eppnCodeIsNotValidCollege() throws Exception {
        UserAccountDto dto = getUser();
        String collegeEppnCode = "eppnCode";
        dto.setPrimaryCollegeId(collegeEppnCode);
        userAccountService.updateEppnWithSuffix(dto);
    }

    @Test
    public void eppnCanBeSuccessfullyRemoved() throws Exception {
        UserAccountDto dto = getUser();
        dto.setUsername("test@username");
        userAccountService.removeEppnSuffix(dto);
        assertEquals("test", dto.getUsername());
    }

    private Set<String> buildCollegeIds() {
        Set<String> collegeIds = new HashSet<String>();
        collegeIds.add("ZZ1");
        return collegeIds;
    }

    @Test
    public void getCollegesAndLocationsReturnsProperlyForBasicCreateFlow() {
        UserAccountDto admin = userAccountService.getUserAccount(proctorUserAccountId);

        // The values here were chosen based on the colleges available to our admin user.
        // Our admin, user id 8, has permission to edit 71, 72, and 73, but not 50.
        Set<String> collegeIds = new HashSet<>(Arrays.asList("ZZ1","ZZ2"));
        Set<String> testLocationIds = new HashSet<>(Arrays.asList("51", "ZZ1", "ZZ2"));
        
        generator.createTestLocation("51 College:", "51", "51");
        generator.createTestLocation("ZZ1 College:", "ZZ1", "ZZ1");
        generator.createTestLocation("ZZ2 College:", "ZZ2", "ZZ2");

        Pair<Set<String>, Set<String>> collegesAndLocations = userAccountService.getCollegesAndLocations(admin, null, collegeIds, testLocationIds);
        assertTrue(collegesAndLocations.first().contains("ZZ1"));
       assertTrue(collegesAndLocations.first().contains("ZZ2"));
        

        assertFalse(collegesAndLocations.second().contains("50"));
        assertTrue(collegesAndLocations.second().contains("ZZ1"));
        assertTrue(collegesAndLocations.second().contains("ZZ2"));
       
    }

    @Test
    public void getCollegesAndLocationsReturnsProperlyForBasicEditFlow() {
        UserAccountDto admin = userAccountService.getUserAccount(proctorUserAccountId);

        UserAccountDto user = makeUser();

        Set<CollegeDto> colleges = new HashSet<>();
        colleges.add(collegeService.read("ZZ1"));
        user.setColleges(colleges);

        Set<String> collegeIds = new HashSet<>(Arrays.asList("ZZ2"));
        Set<String> testLocationIds = new HashSet<>(Arrays.asList("ZZ1", "ZZ2", "72"));
        generator.createTestLocation("ZZ1 College:", "ZZ1", "ZZ1");
        generator.createTestLocation("ZZ2 College:", "ZZ2", "ZZ2");
        generator.createTestLocation("72 College:", "72", "ZZ2");

        Pair<Set<String>, Set<String>> collegesAndLocations = userAccountService.getCollegesAndLocations(admin, user, collegeIds, testLocationIds);

        assertTrue(collegesAndLocations.first().size() == 1);
        assertTrue(collegesAndLocations.first().contains("ZZ2"));

        assertTrue(collegesAndLocations.second().contains("ZZ2"));
        assertTrue(collegesAndLocations.second().contains("ZZ1"));
        assertTrue(collegesAndLocations.second().contains("72"));
    }
}
