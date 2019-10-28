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
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.dto.UserPasswordResetDto;
import org.cccnext.tesuto.admin.exception.InvalidTestLocationException;
import org.cccnext.tesuto.admin.form.SearchParameters;
import org.cccnext.tesuto.admin.service.CollegeService;
import org.cccnext.tesuto.domain.util.RandomGenerator;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.exception.ValidationException;
import org.cccnext.tesuto.user.assembler.UserAccountDtoAssembler;
import org.cccnext.tesuto.user.model.UserAccount;
import org.cccnext.tesuto.user.model.UserAccountCollege;
import org.cccnext.tesuto.user.repository.UserAccountRepository;
import org.cccnext.tesuto.util.TesutoUtils;
import org.spockframework.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James T Stanley <jstanley@unicon.net>
 */
@Slf4j
@Service(value = "userAccountService")
@Primary
public class UserAccountServiceImpl implements UserAccountService {

	@Autowired
	private UserAccountDtoAssembler userAccountDtoAssembler;

	@Autowired
	private RandomGenerator randomGenerator;
	@Autowired
	private UserContextService userContextService;
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // TODO: Upgrade PasswordEncoder
	@Autowired
	private UserAccountSearchService searchService;
	@Autowired
	private CollegeService collegeService;
	@Autowired
	private UserAccountRepository userAccountRepository;;

	@Override
	public String create(UserAccountDto userAccountDto, Set<Integer> roleIds, Set<String> collegeIds,
			Set<String> testLocationIds) throws UserAccountExistsException, InvalidTestLocationException {
		String userId = userAccountDto.getUserAccountId();
		if (userId != null && userAccountRepository.existsById(userId)) {
			throw new UserAccountExistsException(userId);
		}
		
		if(StringUtils.isBlank(userAccountDto.getUserAccountId())) {
			userAccountDto.setUserAccountId(TesutoUtils.newId());
		}
		UserAccount userAccount = userAccountDtoAssembler.disassembleDto(userAccountDto);
		UserAccountCollege primaryCollege = userAccount.getPrimaryCollege();
		userAccount.setUserAccountColleges(null);
		userAccount.setPrimaryCollege(null);
		userAccount = userAccountDtoAssembler.create(userAccount, roleIds);
	    userAccountDtoAssembler.updateTestLocationsColleges(userAccount, primaryCollege, collegeIds,
				testLocationIds);
	    
	    return save(userAccount);
	}
	
	@Transactional("transactionManagerAdmin")
	public String save(UserAccount userAccount) {
		return userAccountRepository.save(userAccount).getUserAccountId();
	}

	@Override
	@Transactional("transactionManagerAdmin")
	public void update(UserAccountDto userToBeUpdated, Set<Integer> roleIds, Set<String> collegeIds,
			Set<String> testLocationIds) throws NotFoundException, UserAccountExistsException {
		if (userToBeUpdated.getDisplayName() == null) {
			userToBeUpdated.setDisplayName(userToBeUpdated.getFirstName());
		}
		userAccountDtoAssembler.update(userToBeUpdated, roleIds, collegeIds, testLocationIds);
	}

	@Override
	@Transactional("transactionManagerAdmin")
	public void delete(UserAccountDto userToBeDeleted) {
		userAccountDtoAssembler.delete(userToBeDeleted);
	}

	@Override
	@Transactional("transactionManagerAdmin")
	public void addUserAttributes(UserAccountDto userToBeUpdated, Set<Integer> roleIdsToAdd,
			Set<String> collegeIdsToAdd, Set<String> testLocationIdsToAdd)
			throws NotFoundException, UserAccountExistsException {

		Set<Integer> hasRoles = userToBeUpdated.getSecurityGroupDtos().stream().map(sg -> sg.getSecurityGroupId())
				.collect(Collectors.toSet());
		Set<Integer> allRoleIds = new HashSet<Integer>(hasRoles);
		if (CollectionUtils.isNotEmpty(roleIdsToAdd)) {
			allRoleIds.addAll(roleIdsToAdd);
		}

		Set<String> allLocations = new HashSet<>();
		if (userToBeUpdated.getTestLocations() != null) {
			allLocations.addAll(
					userToBeUpdated.getTestLocations().stream().map(tl -> tl.getId()).collect(Collectors.toSet()));
		}
		if (CollectionUtils.isNotEmpty(testLocationIdsToAdd)) {
			allLocations.addAll(testLocationIdsToAdd);
		}

		Set<String> allColleges = new HashSet<>();
		if (userToBeUpdated.getCollegeIds() != null) {
			allColleges.addAll(userToBeUpdated.getCollegeIds());
		}
		if (collegeIdsToAdd != null && !collegeIdsToAdd.isEmpty()) {
			if (collegeIdsToAdd != null && !collegeIdsToAdd.isEmpty()) {
				allColleges.addAll(collegeIdsToAdd);
			}
		}
		userAccountDtoAssembler.update(userToBeUpdated, allRoleIds, allColleges, allLocations);
	}

	@Override
	@Transactional(value = "transactionManagerAdmin", readOnly = true)
	public List<UserAccountDto> getAllUserAccounts() {
		return userAccountDtoAssembler.readAll();
	}

	@Override
	@Transactional(value = "transactionManagerAdmin", readOnly = true)
	public UserAccountDto getUserAccount(String id) {
		UserAccountDto userAccountDto = userAccountDtoAssembler.readById(id);
		if (userAccountDto == null) {
			userAccountDto = userAccountDtoAssembler.readDtoByUsername(id);
		}
		return userAccountDto;
	}

	@Override
	@Transactional(value = "transactionManagerAdmin", readOnly = true)
	public UserAccountDto getUserAccountByUserAccountId(String userAccountId) {
		UserAccountDto userAccountDto = userAccountDtoAssembler.readById(userAccountId);
		return userAccountDto;
	}

	@Override
	@Transactional("transactionManagerAdmin")
	public UserAccountDto upsert(UserAccountDto userAccountDto) {
		UserAccountDto oldUserAccountDto = userAccountDtoAssembler.readDtoByUsername(userAccountDto.getUsername());

		Calendar calendar = Calendar.getInstance();

		if (oldUserAccountDto == null) {
			// Passwords are only set on new accounts. Otherwise a user should
			// reset their own.
			userAccountDto.setPassword(passwordEncoder.encode(userAccountDto.getPassword()));
			// TODO: Check that these are automated now.
			// userAccountDto.setCreatedOnDate(calendar.getTime());
			// userAccountDto.setLastUpdatedDate(calendar.getTime());
			userAccountDto = userAccountDtoAssembler.create(userAccountDto);
		} else {
			// TODO: What the foo is this, do we need it?
			// Guarantee a created on date is set in case it is null at some
			// point in the future.
			if (userAccountDto.getCreatedOnDate() != null) {
				oldUserAccountDto.setCreatedOnDate(userAccountDto.getCreatedOnDate());
			} else {
				oldUserAccountDto.setCreatedOnDate(calendar.getTime());
			}
			// TODO: Review these.
			oldUserAccountDto.setDisplayName(userAccountDto.getDisplayName());
			oldUserAccountDto.setEnabled(userAccountDto.isEnabled());
			oldUserAccountDto.setAccountExpired(userAccountDto.isAccountExpired());
			oldUserAccountDto.setFailedLogins(userAccountDto.getFailedLogins());
			oldUserAccountDto.setLastUpdatedDate(calendar.getTime());
			oldUserAccountDto.setAccountLocked(userAccountDto.isAccountLocked());
			oldUserAccountDto.setUsername(userAccountDto.getUsername());
			oldUserAccountDto.setNamespace(userAccountDto.getNamespace());
			userAccountDto = userAccountDtoAssembler.update(oldUserAccountDto);
		}
		return userAccountDto;
	}

	@Override
	@Transactional(value = "transactionManagerAdmin", readOnly = true)
	public UserAccountDto getUserAccountByUsername(String username) {
		UserAccountDto userAccountDto = userAccountDtoAssembler.readDtoByUsername(username);
		if (userAccountDto == null) {
			return null;
		}
		return userAccountDto;
	}

	@Override
	@Transactional("transactionManagerAdmin")
	public UserAccountDto setUserAccountCredential(String username, String credential) {
		UserAccountDto userAccountDto = setUserAccountCredential(getUserAccountByUsername(username), credential);
		return userAccountDto;
	}

	@Override
	@Transactional("transactionManagerAdmin")
	public UserAccountDto setUserAccountCredential(UserAccountDto userAccountDto, String password) {
		if (null != userAccountDto) {
			userAccountDto.setPassword(passwordEncoder.encode(password));
			userAccountDtoAssembler.update(userAccountDto);
		}
		return userAccountDto;
	}

	@Override
	@Transactional("transactionManagerAdmin")
	public UserPasswordResetDto resetPassword(UserPasswordResetDto userPasswordResetDto) throws InterruptedException {
		// Get a UserAccount
		UserAccountDto userAccountDto = getUserAccountByUsername(userPasswordResetDto.getUsername());

		// Populate missing shiz from the database.
		userPasswordResetDto.setCredential(randomGenerator.getRandomString(6));
		userPasswordResetDto.setDisplayName(userAccountDto.getDisplayName());
		userPasswordResetDto.setUsername(userAccountDto.getUsername());
		String encodedPassword = passwordEncoder.encode(userPasswordResetDto.getCredential());
		setUserAccountCredential(userAccountDto, encodedPassword);

		return userPasswordResetDto;
	}

	/**
	 * If we update user information we may want to update the authentication as
	 * well.
	 *
	 * @param username
	 * @return
	 */
	@Override
	@Transactional(value = "transactionManagerAdmin", readOnly = true)
	public UserAccountDto getUserAccountFromAuthenticationByUsername(String username) throws Exception {
		if (username == null) {
			throw new Exception(
					"UserAccountService.getUserAccountFromAuthenticationByUsername() called with null username, username cannot be null");
		}
		UserAccount userAccountFromSession = (UserAccount) userContextService.getCurrentAuthentication().getPrincipal();
		if (userAccountFromSession.getUsername() != null && username.compareTo(username) == 0) {
			UserAccountDto userAccountDtoFromSession = userAccountDtoAssembler.assembleDto(userAccountFromSession);
			return userAccountDtoFromSession;
		} else {
			UserDetails userDetails = userAccountDtoAssembler.readDtoByUsername(username);
			UserAccount userAccountFromDatabase = (UserAccount) userDetails;
			UserAccountDto userAccountDtoFromDatabase = userAccountDtoAssembler.assembleDto(userAccountFromDatabase);
			return userAccountDtoFromDatabase;
		}
	}

	@Override
	@Transactional(value = "transactionManagerAdmin", readOnly = true)
	public List<UserAccountDto> search(SearchParameters parameters) {
		return searchService.search(parameters).stream().map(user -> userAccountDtoAssembler.assembleDto(user))
				.collect(Collectors.toList());
	}

	@Override
	public void initializeNewUserAccount(UserAccountDto userAccount) {
		userAccount.setAccountExpired(false);
		userAccount.setAccountLocked(false);
		userAccount.setEnabled(true);
		userAccount.setFailedLogins(0);
		userAccount.setColleges(new HashSet<>(0));
		userAccount.setDistricts(new HashSet<>(0));
		userAccount.setSecurityGroupDtos(new HashSet<>(0));
		if (userAccount.getDisplayName() == null) {
			userAccount.setDisplayName(userAccount.getFirstName());
		}
	}

	@Override
	public List<String> validateUserAccount(UserAccountDto userAccount, Set<String> collegeIds) {
		List<String> messages = new ArrayList<>();
		if (StringUtils.isBlank((userAccount.getUsername()))) {
			messages.add(USERNAME_NULL);
		}
		if (StringUtils.isBlank((userAccount.getFirstName()))) {
			messages.add(FIRST_NAME_MISSING);
		}
		if (StringUtils.isBlank((userAccount.getLastName()))) {
			messages.add(LAST_NAME_MISSING);
		}
		if (StringUtils.isBlank((userAccount.getEmailAddress()))) {
			messages.add(EMAIL_ADDRESS_IS_MISSING);
		} else {
			try {
				InternetAddress address = new InternetAddress(userAccount.getEmailAddress());
				address.validate();
			} catch (AddressException e) {
				messages.add(INVALID_EMAIL_ADDRESS + e.getMessage());
			}
		}
		if (userAccount.getPrimaryCollegeId() != null && !collegeIds.contains(userAccount.getPrimaryCollegeId())) {
			messages.add(INVALID_PRIMARY_COLLEGE + userAccount.getPrimaryCollegeId());
		}
		return messages;
	}

	@Override
	public List<String> validateEppnCode(String collegeEppnCode, Set<String> collegeIds) {
		List<String> messages = new ArrayList<>();
		if (collegeEppnCode == null) {
			messages.add("EppnSuffixCode is null");
		}

		if (collegeEppnCode != null && CollectionUtils.isEmpty(collegeIds) || !collegeIds.contains(collegeEppnCode)) {
			messages.add(EPPN_COLLEGE_CODE_NOT_IN_USER_COLLEGE_IDS);
		}
		return messages;
	}

	@Override
	public void updateEppnWithSuffix(UserAccountDto user) throws ValidationException {
		String collegeEppnCode = user.getPrimaryCollegeId();
		if (StringUtils.isNotBlank(collegeEppnCode)) {
			List<String> messages = new ArrayList<>();
			String eppnSuffix = collegeService.getEppnSuffix(collegeEppnCode);

			if (eppnSuffix == null) {
				String message = String.format(INVALID_EPPN_COLLEGE_CODE, collegeEppnCode);
				messages.add(message);
			}

			String username = user.getUsername();
			if (username != null && username.contains("@")) {
				String message = String.format(INVALID_USERNAME, username);
				messages.add(message);
			}

			if (messages.size() > 0) {
				throw new ValidationException(messages);
			}
			user.setUsername(username + "@" + eppnSuffix);
		}
	}

	@Override
	public void removeEppnSuffix(UserAccountDto user) {
		String username = user.getUsername();
		if (username != null) {
			username = username.lastIndexOf('@') >= 0 ? username.substring(0, username.lastIndexOf('@')) : username;
		}
		user.setUsername(username);
	}

	@Override
	public Pair<Set<String>, Set<String>> getCollegesAndLocations(UserAccountDto adminUser, UserAccountDto user,
			Set<String> collegeIds, Set<String> testLocationIds) {
		boolean adminHasPermissionsToAllLocations = UserAccountService.doesUserHavePermission(adminUser,
				VIEW_COLLEGES_WITH_ALL_LOCATIONS_PERMISSION);

		// build admin user's id sets - what the admin has permission to change
		Set<String> adminUserCollegeIds = adminUser.getCollegeIds();
		Set<String> adminUserTestLocationIds = adminHasPermissionsToAllLocations ? new HashSet<>()
				: getAdminUserTestLocationIds(adminUser);

		Set<String> filteredCollegeIds = new HashSet<>();
		Set<String> filteredTestLocationIds = new HashSet<>();

		// build to-be-edited user's id sets
		if (user != null) {
			Pair<Set<String>, Set<String>> filteredCollegesAndTestLocationIds = filterUserCurrentCollegesAndLocationsByAdminPermission(
					user, adminHasPermissionsToAllLocations, adminUserCollegeIds, adminUserTestLocationIds);
			filteredCollegeIds = filteredCollegesAndTestLocationIds.first();
			filteredTestLocationIds = filteredCollegesAndTestLocationIds.second();
		}
		for (String newCollegeId : collegeIds) {
			if (adminUserCollegeIds.contains(newCollegeId)) { // if the admin user has permission to that college
				filteredCollegeIds.add(newCollegeId);
			}
		}
		for (String newTestLocationId : testLocationIds) {
			if (adminHasPermissionsToAllLocations || adminUserTestLocationIds.contains(newTestLocationId)) { // if the
																												// admin
																												// user
																												// has
																												// permission
																												// to
																												// that
																												// test
																												// location
				filteredTestLocationIds.add(newTestLocationId);
			}
		}

		return Pair.of(filteredCollegeIds, filteredTestLocationIds);
	}

	@Override
	public Set<UserAccountDto> getUsersByCollege(String collegeId) {
		return userAccountDtoAssembler.assembleDto(userAccountRepository.findByCollegeId(collegeId));
	}

	@Override
	public Set<UserAccountDto> getUsersByTestLocation(String testLocationId) {
		return userAccountDtoAssembler.assembleDto(userAccountRepository.findByTestLocationId(testLocationId));
	}

	@Override
	public List<UserAccountDto> findByCccids(List<String> userAccountIdList) {
		return userAccountDtoAssembler.assembleDto(userAccountRepository.findByUserAccountIdIn(userAccountIdList));
	}

	private Pair<Set<String>, Set<String>> filterUserCurrentCollegesAndLocationsByAdminPermission(UserAccountDto user,
			boolean adminHasPermissionsToAllLocations, Set<String> adminUserCollegeIds,
			Set<String> adminUserTestLocationIds) {
		Set<String> filteredCollegeIds = new HashSet<>();
		Set<String> filteredTestLocationIds = new HashSet<>();

		Set<CollegeDto> userColleges = user.getColleges();
		for (CollegeDto college : userColleges) {
			if (!adminUserCollegeIds.contains(college.getCccId())) { // if the admin user doesn't have permission to
																		// edit this college id
				filteredCollegeIds.add(college.getCccId());
			}
			if (!adminHasPermissionsToAllLocations) { // if the admin doesn't have permission to administer all test
														// locations
				for (TestLocationDto testLocation : college.getTestLocations()) {
					if (!adminUserTestLocationIds.contains(testLocation.getId())) { // if the admin user doesn't have
																					// permission to edit this test
																					// location id
						filteredTestLocationIds.add(testLocation.getId());
					}
				}
			}
		}
		return Pair.of(filteredCollegeIds, filteredTestLocationIds);
	}

	private Set<String> getAdminUserTestLocationIds(UserAccountDto adminUser) {
		Set<String> adminUserTestLocationIds = new HashSet<>();
		Set<CollegeDto> adminUserColleges = adminUser.getColleges();
		for (CollegeDto college : adminUserColleges) {
			for (TestLocationDto testLocation : college.getTestLocations()) {
				adminUserTestLocationIds.add(testLocation.getId());
			}
		}
		return adminUserTestLocationIds;
	}

	protected UserAccountRepository getRepository() {
		return userAccountRepository;
	}

	@Override
	public void failedLogin(String userAccountId) {
		UserAccountDto userAccountDto = this.getUserAccountByUserAccountId(userAccountId);
		if (userAccountDto == null) {
			return;
		}
		int loginAttempts = 0;
		if (userAccountDto.getFailedLogins() != null) {
			loginAttempts = userAccountDto.getFailedLogins();
		}
		userAccountDto.setFailedLogins(++loginAttempts);
		// Login attempts cause a lockout at the hard coded number of 15
		// tries
		// The 16th try causes a lockout.
		if (loginAttempts > 15) {
			// Uncomment the following line to enable account locking
			// after 15 login attempts :-)
			userAccountDto.setAccountLocked(true);
		}
		Calendar calendar = Calendar.getInstance();

		userAccountDto.setFailedLogins(loginAttempts);
		userAccountDto.setLastLoginDate(calendar.getTime());
		upsert(userAccountDto);

	}

	@Override
	public void clearFailedLogins(String userAccountId) {
		UserAccountDto userAccountDto = this.getUserAccountByUserAccountId(userAccountId);
		if (userAccountDto == null) {
			return;
		}
		userAccountDto.setFailedLogins(0);
		Calendar calendar = Calendar.getInstance();
		userAccountDto.setLastLoginDate(calendar.getTime());
		upsert(userAccountDto);
	}
}
