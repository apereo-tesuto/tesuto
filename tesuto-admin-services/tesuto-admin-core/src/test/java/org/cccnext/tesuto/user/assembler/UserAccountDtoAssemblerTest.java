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

import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.exception.InvalidTestLocationException;
import org.cccnext.tesuto.admin.model.College;
import org.cccnext.tesuto.admin.model.TestLocation;
import org.cccnext.tesuto.user.model.AuthorNamespace;
import org.cccnext.tesuto.user.model.UserAccount;
import org.cccnext.tesuto.user.model.UserAccountCollege;
import org.cccnext.tesuto.user.repository.AuthorNamespaceRepository;
import org.cccnext.tesuto.user.repository.UserAccountRepository;
import org.cccnext.tesuto.user.service.UserAccountExistsException;
import org.cccnext.tesuto.user.service.UserGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/userTestApplicationContext.xml" })
@Transactional
public class UserAccountDtoAssemblerTest {

	@Autowired
	EntityManagerFactory emf;
	@Autowired
	UserAccountDtoAssemblerImpl userAccountDtoAssembler;
	
	@Autowired
	AuthorNamespaceRepository authorNamespaceRepository;

	@Autowired
	UserAccountRepository userAccountRepository;

	@Autowired
	UserGenerator generator;

	private String proctorUserAccountId;

	@Before
	public void setup() {
		
		generator.createAuthorNamespace("USER_UNIT_TEST");
		proctorUserAccountId = generator.createProctorUserAccount("USER_UNIT_TEST");
		
		generator.createDeletedProctorUserAccount("USER_UNIT_TEST");
		generator.createDeletedStudentUserAccount();
		
	}
	
	@After 
	public void teardown() {
		generator.deleteAuthorNamespace("USER_UNIT_TEST");
	}

	public static UserAccount makeUserAccount() {
		String userId = "userAccountId";

		UserAccount userAccount = new UserAccount();
		userAccount.setAccountLocked(true);
		userAccount.setDisplayName("displayName");

		userAccount.setCreatedOnDate(new Date());
		userAccount.setEmailAddress("emailAddress@gmail.net");
		userAccount.setEnabled(true);
		userAccount.setExpired(true);
		userAccount.setFailedLogins(10);
		userAccount.setFirstName("firstName:");
		userAccount.setMiddleInitial("A");
		userAccount.setLastName("lastName");
		userAccount.setPhoneNumber("555-555-5555");
		userAccount.setExtension("123");
		userAccount.setLastLoginDate(new Date());
		userAccount.setLastUpdatedDate(new Date());
		userAccount.setPassword("password");
		userAccount.setUserAccountId(userId);
		userAccount.setUsername("username");

		userAccount.setPrimaryCollege(new UserAccountCollege(userId, "61"));

		userAccount.setSecurityGroups(SecurityGroupDtoAssemblerTest.getSecurityGroups());

		return userAccount;
	}

	public static College makeCollege(String cccId) {
		College college = new College();
		college.setCccId(cccId);
		college.setStreetAddress1("Here " + cccId);
		college.setCity("There " + cccId);
		college.setName("U of Test " + cccId);
		return college;
	}

	private UserAccount getUserAccount() {
		UserAccount userAccount = userAccountRepository.findByUserAccountId(proctorUserAccountId);
		return userAccount;
	}

	@Test
	public void testReadAllDoesNotReturnDeletedUsers() {
		List<UserAccountDto> users = userAccountDtoAssembler.readAll();
		System.out.println("WOO! Size: " + users.size());
		assertTrue("Every returned user should not be deleted", users.stream().allMatch(u -> !u.isDeleted()));
	}

	@Test
	public void testAssembler() {
		AuthorNamespace authorNamespace = authorNamespaceRepository.findByNamespace("USER_UNIT_TEST");
		UserAccount userAccount = getUserAccount();
		userAccount.setAuthorNamespace(authorNamespace);
		Set<TestLocation> testLocations = userAccount.getTestLocations();
		testLocations.clear();
		College college = userAccount.getColleges().stream().findFirst().get();
		testLocations.add(college.getTestLocations().stream().findFirst().get());
		UserAccountDto userAccountDto = userAccountDtoAssembler.assembleDto(userAccount);
		UserAccount userAccountAssembled = userAccountDtoAssembler.disassembleDto(userAccountDto);

		userAccountDto = userAccountDtoAssembler.assembleDto(userAccountAssembled);
		UserAccount finalUserAccount = userAccountDtoAssembler.disassembleDto(userAccountDto);

		assertEquals(1, userAccountAssembled.getTestLocations().size());
		assertTrue("Reassembled User Account should be the same", userAccountAssembled.equals(finalUserAccount));
	}

	@Test
	public void testCreateAndUpdate() throws Exception {
		try {
			EntityManager manager = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
			UserAccountDto userAccount = userAccountDtoAssembler.assembleDto(makeUserAccount());
			Set<Integer> roleIds = new HashSet<>();
			Set<String> collegeIds = new HashSet<>(Arrays.asList("61", "71", "72", "73"));
			Set<String> testLocationIds = new HashSet<>(Arrays.asList("61", "71", "73"));

			generator.createTestLocation("61 College:", "61", "61");
			generator.createTestLocation("71 College:", "71", "71");
			generator.createTestLocation("73 College:", "73", "73");
			generator.createTestLocation("72 College:", "72", "72");
			String userId = userAccountDtoAssembler.create(userAccount, roleIds, collegeIds, testLocationIds);
			manager.flush();
			manager.clear();

			UserAccount createResult = userAccountRepository.findByUserAccountId(userId);

			assertEquals(3, createResult.getTestLocations().size());
			assertEquals(4, createResult.getUserAccountColleges().size());
			assertNotEquals("Modified", createResult.getFirstName());

			userAccount.setUserAccountId(userId);
			userAccount.setFirstName("Modified");
			testLocationIds.add("72");

			userAccountDtoAssembler.update(userAccount, roleIds, collegeIds, testLocationIds);
			manager.flush();
			manager.clear();
			UserAccount updateResult = userAccountRepository.findByUserAccountId(userId);

			assertEquals(4, updateResult.getTestLocations().size());
			assertEquals("Modified", updateResult.getFirstName());
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void testCreateSetsPrimaryCollegeProperly() throws InvalidTestLocationException, UserAccountExistsException {
		EntityManager manager = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
		UserAccountDto userAccount = userAccountDtoAssembler.assembleDto(makeUserAccount()); // primary college is "61"
		Set<Integer> roleIds = new HashSet<>();
		Set<String> collegeIds = new HashSet<>(Arrays.asList("61", "71", "72", "73"));
		generator.createTestLocation("61 College:", "61", "61");
		generator.createTestLocation("71 College:", "71", "71");
		generator.createTestLocation("73 College:", "73", "73");
		Set<String> testLocationIds = new HashSet<>(Arrays.asList("61", "71", "73"));

		String userId = userAccountDtoAssembler.create(userAccount, roleIds, collegeIds, testLocationIds);
		manager.flush();
		manager.clear();
		UserAccount createResult = userAccountRepository.findByUserAccountId(userId);

		assertNotNull(createResult.getPrimaryCollege());
		assertEquals("61", createResult.getPrimaryCollege().getCollegeId());
	}

	@Test
	public void testUpdateCorrectlyAddsAndDeletesUserAccountColleges()
			throws InvalidTestLocationException, UserAccountExistsException {
		EntityManager manager = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
		UserAccountDto userAccount = userAccountDtoAssembler.assembleDto(makeUserAccount()); // primary college is "61"
		Set<Integer> roleIds = new HashSet<>();
		Set<String> collegeIds = new HashSet<>(Arrays.asList("61", "71", "72", "73"));
		Set<String> testLocationIds = new HashSet<>(Arrays.asList("61", "71"));

		generator.createTestLocation("61 College:", "61", "61");
		generator.createTestLocation("71 College:", "71", "71");

		String userId = userAccountDtoAssembler.create(userAccount, roleIds, collegeIds, testLocationIds);
		manager.flush();
		manager.clear();

		Set<String> updatedCollegeIds = new HashSet<>(Arrays.asList("61", "71", "91"));
		UserAccount createResult = userAccountRepository.findByUserAccountId(userId);
		userAccount.setUserAccountId(createResult.getUserAccountId());
		userAccountDtoAssembler.update(userAccount, roleIds, updatedCollegeIds, testLocationIds);
		manager.flush();
		manager.clear();

		UserAccount updateResult = userAccountRepository.findByUserAccountId(userId);
		assertEquals(updatedCollegeIds.size(), updateResult.getUserAccountColleges().size());
		for (UserAccountCollege uac : updateResult.getUserAccountColleges()) {
			assertTrue(updatedCollegeIds.contains(uac.getCollegeId()));
		}
	}

	@Test
	public void testUpdateUpdatesPrimaryCollegeIdProperly()
			throws InvalidTestLocationException, UserAccountExistsException {
		EntityManager manager = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
		UserAccountDto userAccount = userAccountDtoAssembler.assembleDto(makeUserAccount()); // primary college is "61"
		Set<Integer> roleIds = new HashSet<>();
		Set<String> collegeIds = new HashSet<>(Arrays.asList("61", "71", "72", "73"));
		Set<String> testLocationIds = new HashSet<>(Arrays.asList("61", "71"));
		generator.createTestLocation("61 College:", "61", "61");
		generator.createTestLocation("71 College:", "71", "71");

		String userId = userAccountDtoAssembler.create(userAccount, roleIds, collegeIds, testLocationIds);
		manager.flush();
		manager.clear();

		UserAccount createResult = userAccountRepository.findByUserAccountId(userId);
		createResult.setPrimaryCollege(new UserAccountCollege(createResult.getUserAccountId(), "71"));
		userAccountDtoAssembler.update(userAccountDtoAssembler.assembleDto(createResult), roleIds, collegeIds,
				testLocationIds);
		manager.flush();
		manager.clear();

		UserAccount updateResult = userAccountRepository.findByUserAccountId(userId);
		assertEquals("71", updateResult.getPrimaryCollege().getCollegeId());
	}

	@Test
	public void testUpdateHandlesChangingAndDeletingPrimaryCollegeInOneOperation()
			throws InvalidTestLocationException, UserAccountExistsException {
		EntityManager manager = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
		UserAccountDto userAccount = userAccountDtoAssembler.assembleDto(makeUserAccount()); // primary college is "61"
		Set<Integer> roleIds = new HashSet<>();
		Set<String> collegeIds = new HashSet<>(Arrays.asList("61", "71"));
		Set<String> testLocationIds = new HashSet<>(Arrays.asList("61", "71"));
		generator.createTestLocation("61 College:", "61", "61");
		generator.createTestLocation("71 College:", "71", "71");

		String userId = userAccountDtoAssembler.create(userAccount, roleIds, collegeIds, testLocationIds);
		manager.flush();
		manager.clear();

		Set<String> updatedCollegeIds = new HashSet<>(Arrays.asList("72"));
		Set<String> updatedTestLocationIds = new HashSet<>(Arrays.asList("72"));
		generator.createTestLocation("72 College:", "72", "72");
		userAccount.setPrimaryCollegeId("72");
		userAccount.setUserAccountId(userId);
		userAccountDtoAssembler.update(userAccount, roleIds, updatedCollegeIds, updatedTestLocationIds);
		manager.flush();
		manager.clear();

		UserAccount updateResult = userAccountRepository.findByUserAccountId(userId);
		assertEquals("72", updateResult.getPrimaryCollege().getCollegeId());
	}

	@Test
	public void testUpdateHandlesEmptyTestLocations() throws InvalidTestLocationException, UserAccountExistsException {
		EntityManager manager = EntityManagerFactoryUtils.getTransactionalEntityManager(emf);
		UserAccountDto userAccount = userAccountDtoAssembler.assembleDto(makeUserAccount()); // primary college is "61"
		Set<Integer> roleIds = new HashSet<>();
		Set<String> collegeIds = new HashSet<>(Arrays.asList("61", "71", "72", "73"));
		Set<String> testLocationIds = new HashSet<>();

		String userId = userAccountDtoAssembler.create(userAccount, roleIds, collegeIds, testLocationIds);
		manager.flush();
		manager.clear();

		UserAccount createResult = userAccountRepository.findByUserAccountId(userId);
		assertEquals(0, createResult.getTestLocations().size());
	}
}
