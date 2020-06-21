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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.cccnext.tesuto.admin.assembler.CollegeDtoAssembler;
import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.exception.InvalidTestLocationException;
import org.cccnext.tesuto.admin.model.College;
import org.cccnext.tesuto.admin.model.TestLocation;
import org.cccnext.tesuto.admin.repository.CollegeRepository;
import org.cccnext.tesuto.admin.repository.DistrictRepository;
import org.cccnext.tesuto.admin.service.TestLocationService;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.user.model.AuthorNamespace;
import org.cccnext.tesuto.user.repository.AuthorNamespaceRepository;
import org.cccnext.tesuto.user.service.UserAccountExistsException;
import org.cccnext.tesuto.user.service.UserAccountService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James T Stanley <jstanley@unicon.net>
 */
@Slf4j
@Service
public class UserGenerator {
    /* Student user names are expected to adhere to all caps and numbers
     * If at a later time restrictions are relaxed user names for students
     * additional testing will be required and Student search may need to be
     * changed.
     */
    public static String STUDENT_USER_NAME_1 = "AAA1234";
    public static String STUDENT_USER_NAME_2 = "AAA1235";
    public static String STUDENT_USER_NAME_3 = "AAA1236";
    public static String STUDENT_EMAIL_1 = "student1@unicon.net";
    public static String STUDENT_EMAIL_2 = "student2@unicon.net";
    public static String STUDENT_EMAIL_3 = "deletedstudent@unicon.net";
    public static String STUDENT_FIRST_NAME_1 = "Student";
    public static String STUDENT_LAST_NAME_1 = "One";
    public static String STUDENT_FIRST_NAME_2 = "Student";
    public static String STUDENT_LAST_NAME_2 = "Two";
    public static String STUDENT_FIRST_NAME_3 = STUDENT_FIRST_NAME_1;
    public static String STUDENT_LAST_NAME_3 = STUDENT_LAST_NAME_1;
    public static String PROCTOR_USER_NAME = "proctor@unicon.net";
    @Autowired
    UserAccountService userAccountService;
    
    @Autowired
    TestLocationService testLocationService;
    
    @Autowired
    CollegeRepository collegeRepository;
    
    @Autowired
    CollegeDtoAssembler collegeAssembler;
    
    @Autowired
    DistrictRepository districtRepository;
    
	@Autowired
	AuthorNamespaceRepository authorNamespaceRepository;
    
    @Autowired
    EntityManagerFactory emf;
        
    public String createStudent1UserAccount() {
        try {
            Set<String> collegeIds = new HashSet<>(Arrays.asList(new String[]{"ZZ2", "ZZ1"}));
            Set<Integer> roleIds = new HashSet<>(Arrays.asList(new Integer[]{1}));
            Set<String> testLocationIds = new HashSet<>();
            return upsertAccount(buildAccount(STUDENT_FIRST_NAME_1, STUDENT_LAST_NAME_1, null, STUDENT_USER_NAME_1, STUDENT_EMAIL_1, "ZZ1", collegeIds), roleIds, collegeIds, testLocationIds);
        } catch (UserAccountExistsException | InvalidTestLocationException exception) {
            log.error("Unable to create Student One Account", exception);
        }
        return "";
    }
    
    public String createStudent2UserAccount() {
        try {
            Set<String> collegeIds = new HashSet<>(Arrays.asList(new String[]{"ZZ2", "ZZ1"}));
            Set<Integer> roleIds = new HashSet<>(Arrays.asList(new Integer[]{1}));
            Set<String> testLocationIds = new HashSet<>();
            return upsertAccount(buildAccount(STUDENT_FIRST_NAME_2, STUDENT_FIRST_NAME_2, null, STUDENT_USER_NAME_2, STUDENT_EMAIL_2, "ZZ1", collegeIds), roleIds, collegeIds, testLocationIds);
        } catch (UserAccountExistsException | InvalidTestLocationException exception) {
            log.error("Unable to create Student Two Account", exception);
        }
        return "";
    }

    public String createDeletedStudentUserAccount() {
        try {
            Set<String> collegeIds = new HashSet<>(Arrays.asList(new String[]{"ZZ2", "ZZ1"}));
            Set<Integer> roleIds = new HashSet<>(Arrays.asList(new Integer[]{1}));
            Set<String> testLocationIds = new HashSet<>();
            UserAccountDto deletedAccount = buildAccount(STUDENT_FIRST_NAME_3, STUDENT_LAST_NAME_3, null, STUDENT_USER_NAME_3, STUDENT_EMAIL_3, "ZZ1", collegeIds);
            deletedAccount.setDeleted(true);
            return upsertAccount(deletedAccount, roleIds, collegeIds, testLocationIds);
        } catch (UserAccountExistsException | InvalidTestLocationException exception) {
            log.error("Unable to create Deleted Student Account", exception);
        }
        return "";
    }
    
    
    public String createProctorUserAccount(String authorNamespace) {
        try {
            Set<String> collegeIds = new HashSet<>(Arrays.asList(new String[]{"ZZ2", "ZZ1"}));
            Set<Integer> roleIds = new HashSet<>(Arrays.asList(new Integer[]{2}));
            Set<String> testLocationIds = new HashSet<>(Arrays.asList(new String[]{createTestLocation("Test College", "ZZ1", "ZZ1"), createTestLocation("Test College", "ZZ2", "ZZ2")}));
            
            String userAccountId =  upsertAccount(buildAccount("Complex", "User", authorNamespace, PROCTOR_USER_NAME, PROCTOR_USER_NAME, "ZZ1", collegeIds), roleIds, collegeIds, testLocationIds);
            EntityManager manager = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
            manager.flush();
            manager.clear();
            return userAccountId;
        } catch (UserAccountExistsException | InvalidTestLocationException exception) {
            log.error("Unable to create complex user Account", exception);
        }
        return "";
    }

    public String createDeletedProctorUserAccount(String authorNamespace) {
        try {
            Set<String> collegeIds = new HashSet<>(Arrays.asList(new String[]{"ZZ2", "ZZ1"}));
            Set<Integer> roleIds = new HashSet<>(Arrays.asList(new Integer[]{2}));
            Set<String> testLocationIds = new HashSet<>(Arrays.asList(new String[]{createTestLocation("Test College", "ZZ1", "ZZ1"), createTestLocation("Test College", "ZZ2", "ZZ2")}));

            UserAccountDto deletedUserAccount = buildAccount("DeletedComplex", "User", authorNamespace, "deleted"+PROCTOR_USER_NAME, "deleted"+PROCTOR_USER_NAME, "ZZ1", collegeIds);
            deletedUserAccount.setDeleted(true);
            String userAccountId =  upsertAccount(deletedUserAccount, roleIds, collegeIds, testLocationIds);
            EntityManager manager = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
            manager.flush();
            manager.clear();
            return userAccountId;
        } catch (UserAccountExistsException | InvalidTestLocationException exception) {
            log.error("Unable to create complex user Account", exception);
        }
        return "";
    }

    public String createTestLocation(String name, String id, String collegeId) {
        TestLocationDto testLocationDto = new TestLocationDto();
        testLocationDto.setCity("City " + name);
        testLocationDto.setCollegeId(collegeId);
        testLocationDto.setId(id);
        testLocationDto.setName(name  + " Test Location");
        testLocationDto.setCollegeName(name);
        testLocationDto.setLocationStatus("ACTIVE");
        testLocationDto.setLocationType("ON_SITE");
        testLocationDto.setEnabled(true);
        String updatedId = testLocationService.upsert(testLocationDto).getId();
        EntityManager manager = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
        manager.flush();
        manager.clear();
        return updatedId;
    }
    
    public String upsertAccount(UserAccountDto userAccountToUpsert, Set<Integer> roleIds, Set<String> collegeIds, Set<String> testLocationIds) throws NotFoundException, UserAccountExistsException, InvalidTestLocationException {
        UserAccountDto userAccount =userAccountService.getUserAccountByUsername(userAccountToUpsert.getUsername());
        
        if(userAccount == null) {
            return userAccountService.create(userAccountToUpsert, roleIds, collegeIds, testLocationIds);
        } else {
            userAccountToUpsert.setUserAccountId(userAccount.getUserAccountId());
            userAccountService.update(userAccountToUpsert, roleIds, collegeIds, testLocationIds);
        }
        return userAccountToUpsert.getUserAccountId();
    }
    
    public static TestLocation makeTestLocation(String id, College college) {
        TestLocation testLocation = new TestLocation();
        testLocation.setCapacity(100);
        testLocation.setCity("city");
        testLocation.setCreatedOnDate(new Date());
        testLocation.setId(id);
        testLocation.setLastUpdatedDate(new Date());
        testLocation.setLocationStatus("ACTIVE");
        testLocation.setLocationType("ON_SITE");
        testLocation.setName("name@" + id);
        testLocation.setPostalCode("postalCode");
        testLocation.setStreetAddress1("streetAddress1");
        testLocation.setStreetAddress1("setStreetAddress2");
        testLocation.setCollege(college);
        return testLocation;
    }
    
    public UserAccountDto buildAccount(String firstName, String lastName, String namespace, String username, String emailaddress, String primaryCollegeId, Set<String> collegeIds) {
        UserAccountDto userAccount = new UserAccountDto();
        userAccount.setAccountLocked(true);
        userAccount.setDisplayName(firstName + " " + lastName);
        userAccount.setNamespace(namespace);
        userAccount.setCreatedOnDate(new Date());
        userAccount.setEmailAddress(emailaddress);
        userAccount.setEnabled(true);
        userAccount.setAccountExpired(true);
        userAccount.setFailedLogins(10);
        userAccount.setFirstName(firstName);
        userAccount.setLastName(lastName);
        userAccount.setLastLoginDate(new Date());
        userAccount.setLastUpdatedDate(new Date());
        userAccount.setPassword("password");
        userAccount.setUsername(username);
        userAccount.setPrimaryCollegeId(primaryCollegeId);
        Set<CollegeDto> colleges = new HashSet<CollegeDto>(collegeAssembler.assembleDto(collegeRepository.findAll(collegeIds)));
        userAccount.setColleges(colleges);
        
        return userAccount;
    }
    
    public void createTestColleges() {
        Set<College> colleges = new HashSet<>();
        colleges.add(buildCollege("ZZ6","College of the Desert","43-500 Monterey Ave","","Palm Desert","92260","www.collegeofthedesert.edu","ZZ0"));
        colleges.add(buildCollege("ZZ7","Mt. San Jacinto Community College District","1499 N State St","","San Jacinto","92583-2399","www.msjc.edu","ZZ0"));
        colleges.add(buildCollege("ZZ8","Palo Verde College","One College Drive","","Blythe","92225","www.paloverde.edu","ZZ0"));
        colleges.add(buildCollege("ZZ9","Riverside Community College","4800 Magnolia Avenue","","Riverside","92506","www.rcc.edu/","ZZ0"));
        colleges.add(buildCollege("ZZ10","Copper Mountain Community College","6162 Rotary Way","","Joshua Tree","92252","www.cmccd.edu","ZZ0"));
        collegeRepository.save(colleges);
    }
    
    private College buildCollege(String cccid, String name, String streetAddress1, String streetAddress2, String city, String postalCode, String url, String districtCccid) {
        College college = new College();
        college.setCccId(cccid);
        college.setName(name);
        college.setStreetAddress1(streetAddress1);
        college.setStreetAddress2(streetAddress2);
        college.setCity(city);
        college.setPostalCode(postalCode);
        college.setDistrict(districtRepository.getOne(districtCccid));
        return college;
    }
    
    public void deleteTestColleges()  {
        List<String> collegeIds = new ArrayList<>(Arrays.asList(new String[]{"ZZ6","ZZ7","ZZ8","ZZ9","ZZ10"}));
        collegeIds.forEach(id -> collegeRepository.delete(id));
    }
    
    
    public AuthorNamespace createAuthorNamespace(String name) {
    	AuthorNamespace namespace = authorNamespaceRepository.findByNamespace(name);
		if (namespace == null) {
			AuthorNamespace developer = new AuthorNamespace();
			developer.setCreatedOnDate(new Date());
			developer.setLastUpdatedDate(developer.getCreatedOnDate());
			developer.setNamespace(name);
			return authorNamespaceRepository.save(developer);
		}
		return namespace;
    }
    
    public void deleteAuthorNamespace(String name) {
    	AuthorNamespace namespace = authorNamespaceRepository.findByNamespace(name);
		if(namespace != null)
			authorNamespaceRepository.delete(namespace);
    }
    
    public void deleteUserById(String userId) {
    	userAccountService.delete(userAccountService.getUserAccountByUserAccountId(userId));
    }

}
