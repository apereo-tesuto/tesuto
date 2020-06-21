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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.admin.assembler.CollegeDtoAssembler;
import org.cccnext.tesuto.admin.assembler.TestLocationDtoAssembler;
import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.exception.InvalidTestLocationException;
import org.cccnext.tesuto.admin.model.College;
import org.cccnext.tesuto.admin.model.TestLocation;
import org.cccnext.tesuto.admin.repository.CollegeRepository;
import org.cccnext.tesuto.admin.repository.TestLocationRepository;
import org.cccnext.tesuto.admin.service.CollegeService;
import org.cccnext.tesuto.admin.service.TestLocationService;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.user.model.AuthorNamespace;
import org.cccnext.tesuto.user.model.SecurityGroup;
import org.cccnext.tesuto.user.model.UserAccount;
import org.cccnext.tesuto.user.model.UserAccountCollege;
import org.cccnext.tesuto.user.repository.AuthorNamespaceRepository;
import org.cccnext.tesuto.user.repository.SecurityGroupRepository;
import org.cccnext.tesuto.user.repository.UserAccountCollegeRepository;
import org.cccnext.tesuto.user.repository.UserAccountRepository;
import org.cccnext.tesuto.user.service.UserAccountExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James T Stanley <jstanley@unicon.net>
 */
@Slf4j
@Transactional
@Component(value = "userAccountDtoAssembler")
public class UserAccountDtoAssemblerImpl implements UserAccountDtoAssembler {

	@Autowired
	UserAccountRepository userAccountRepository;
	@Autowired
	UserAccountCollegeRepository userAccountCollegeRepository;
	@Autowired
	AuthorNamespaceRepository authorNamespaceRepository;
	@Autowired
	CollegeDtoAssembler collegeDtoAssembler;
	@Autowired
	SecurityGroupDtoAssembler securityGroupDtoAssembler;
	@Autowired
	TestLocationDtoAssembler testLocationDtoAssembler;
	@Autowired
	SecurityGroupRepository securityGroupRepository;
	@Autowired
	CollegeService collegeService;
	@Autowired
	TestLocationService testLocationService;
	@Autowired
	CollegeRepository collegeRepository;
	@Autowired
	TestLocationRepository testLocationRepository;

	@Transactional("transactionManagerAdmin")
	public UserAccount create(UserAccount userAccount, Set<Integer> roleIds)
			throws UserAccountExistsException, InvalidTestLocationException {

		if (userAccount.getUsername() != null
				&& userAccountRepository.findByUsernameIgnoreCase(userAccount.getUsername()) != null) {
			throw new UserAccountExistsException(userAccount.getUsername());
		}

		if (!CollectionUtils.isEmpty(roleIds)) {
			userAccount.setSecurityGroups(securityGroupRepository.findSecurityGroupSet(roleIds));
		}
		return userAccountRepository.save(userAccount);
	}

	@Transactional("transactionManagerAdmin")
	public void updateTestLocationsColleges(UserAccount userAccount, UserAccountCollege primaryUserCollege,
			Set<String> collegeIds, Set<String> testLocationIds) throws InvalidTestLocationException {

		Set<String> collegeTestLocationIds = getCollegeTestLocationIdsForColleges(collegeIds);
		Set<TestLocation> userTestLocations = filterTestLocationIdsByCollegeTestLocations(testLocationIds,
				collegeTestLocationIds);
		userAccount.setTestLocations(userTestLocations);
		if (collegeIds != null && collegeIds.isEmpty() == false) {
			Set<UserAccountCollege> colleges = new HashSet<>();
			for (String collegeId : collegeIds) {
				colleges.add(new UserAccountCollege(userAccount.getUserAccountId(), collegeId));
			}
			userAccount.setUserAccountColleges(colleges);
			userAccountCollegeRepository.saveAll(colleges);
			if (primaryUserCollege != null) {
				primaryUserCollege.setUserAccountId(userAccount.getUserAccountId());
				userAccount.setPrimaryCollege(primaryUserCollege);
			}
		} else {
			userAccount.setUserAccountColleges(null);
		}
		
	}
	

	@Override
	public void delete(UserAccountDto userAccountDto) {
		String userId = userAccountDto.getUserAccountId();
		UserAccount userAccount = disassembleDto(userAccountDto);

		userAccount.setDeleted(true);

		userAccountRepository.save(userAccount);
	}

	@Caching(evict = { @CacheEvict(value = "UserDtoAssembler", key = "#userAccountDto.userAccountId"),
			@CacheEvict(value = "UserDtoAssembler", key = "#userAccountDto.username") })
	public void update(UserAccountDto userAccountDto, Set<Integer> roleIds, Set<String> collegeIds,
			Set<String> testLocationIds) throws UserAccountExistsException {
		String userId = userAccountDto.getUserAccountId();
		UserAccount userAccount = disassembleDto(userAccountDto);

		// Repopulate values that do not come in from the user form so they are not
		// lost.
		UserAccount oldUserAccount = userAccountRepository.findByUserAccountId(userId);
		if (userId == null || oldUserAccount == null) {
			throw new NotFoundException("A user with id " + userId + " was expected to be found but was not.");
		}

		if (StringUtils.isBlank(userAccount.getPassword())) {
			userAccount.setPassword(oldUserAccount.getPassword());
		}

		if (userAccount.getUsername() != null) {
			UserAccount duplicateUser = userAccountRepository.findByUsernameIgnoreCase(userAccount.getUsername());
			if (duplicateUser != null && !duplicateUser.getUserAccountId().equals(userAccountDto.getUserAccountId())) {
				throw new UserAccountExistsException(userAccount.getUsername());
			}
		}

		if (!CollectionUtils.isEmpty(roleIds)) {
			userAccount.setSecurityGroups(securityGroupRepository.findSecurityGroupSet(roleIds));
		}

		Set<TestLocation> testLocations = null;
		if (testLocationIds.size() > 0) {
			testLocations = testLocationRepository.getTestLocationsByIds(testLocationIds);
			for (TestLocation testLocation : testLocations) {
				if (!collegeIds.contains(testLocation.getCollegeId())) {
					throw new AccessDeniedException("Expected test location id " + testLocation.getId()
							+ " to be present with college id " + testLocation.getCollegeId());
				}
			}
			userAccount.setTestLocations(testLocations);
		}
		userAccount.setTestLocations(testLocations);

		Set<UserAccountCollege> currentColleges = oldUserAccount.getUserAccountColleges();
		Set<String> currentCollegeIds = new HashSet<>();
		Set<String> toBeDeletedCollegeIds = new HashSet<>();
		if (currentColleges != null) {
			for (UserAccountCollege college : currentColleges) {
				currentCollegeIds.add(college.getCollegeId());
				if (!collegeIds.contains(college.getCollegeId())) {
					toBeDeletedCollegeIds.add(college.getCollegeId());
				}
			}
		}
		if (toBeDeletedCollegeIds.size() > 0) {
			userAccountCollegeRepository.deleteFromUserAccountCollegeByUserAccountIdAndCollegeIds(userId,
					toBeDeletedCollegeIds);
		}
		Set<UserAccountCollege> collegesToSave = new HashSet<>();
		Set<UserAccountCollege> colleges = new HashSet<>();
		for (String collegeId : collegeIds) {
			UserAccountCollege userCollegeAccount = new UserAccountCollege(userId, collegeId);
			if (!currentCollegeIds.contains(collegeId)) {
				collegesToSave.add(userCollegeAccount);
			}
			colleges.add(userCollegeAccount);
		}
		userAccountCollegeRepository.saveAll(collegesToSave);
		userAccount.setUserAccountColleges(colleges);

		userAccountRepository.save(userAccount);
	}

	/**
	 * For a set of college IDs, generate a set of all possible test locations for
	 * those colleges
	 * 
	 * @param collegeIds
	 * @return
	 */
	public Set<String> getCollegeTestLocationIdsForColleges(Set<String> collegeIds) {
		List<CollegeDto> collegeDtos = collegeService.read(new ArrayList<String>(collegeIds));
		Set<String> collegeTestLocationIds = new HashSet<>();
		for (CollegeDto college : collegeDtos) {
			Set<TestLocationDto> testLocationDtos = college.getTestLocations();
			for (TestLocationDto testLocation : testLocationDtos)
				collegeTestLocationIds.add(testLocation.getId());
		}
		return collegeTestLocationIds;
	}

	/**
	 * Filter a set of test location ids by another set of ids. Throws an
	 * InvalidTestLocationException if a test location does not exist in the
	 * filtered-by set.
	 * 
	 * @param testLocationIds        the set of ids to test
	 * @param collegeTestLocationIds the set of ids to filter by
	 * @return
	 * @throws InvalidTestLocationException
	 */
	public Set<TestLocation> filterTestLocationIdsByCollegeTestLocations(Set<String> testLocationIds,
			Set<String> collegeTestLocationIds) throws InvalidTestLocationException {
		Set<String> userTestLocationIds = new HashSet<>();
		if (testLocationIds == null || testLocationIds.isEmpty()) {
			return null;
		}
		for (String testLocationId : testLocationIds) {
			if (collegeTestLocationIds.contains(testLocationId)) {
				userTestLocationIds.add(testLocationId);
			} else {
				throw new InvalidTestLocationException(testLocationId);
			}
		}
		return testLocationService.readModels(userTestLocationIds);
	}

	@Override
	public UserAccountDto create(UserAccountDto userAccountDto) {
		UserAccount userAccount = disassembleDto(userAccountDto);

		// Protect if there is no user returned.
		if (userAccount != null) {
			Date currentTimeStamp = new Date(System.currentTimeMillis());
			userAccount.setLastUpdatedDate(currentTimeStamp);
			userAccount.setCreatedOnDate(currentTimeStamp);
		}

		userAccount = userAccountRepository.save(userAccount);
		return assembleDto(userAccount);
	}

	@Override
	/*
	 * @Caching(evict = {
	 * 
	 * @CacheEvict(value = "UserDtoAssembler", key="#userAccountDto.userAccountId"),
	 * 
	 * @CacheEvict(value = "UserDtoAssembler", key="#userAccountDto.username") })
	 */
	public UserAccountDto update(UserAccountDto userAccountDto) {
		UserAccount oldUser = userAccountRepository.getOne(userAccountDto.getUserAccountId());
		if (oldUser == null) {
			throw new NotFoundException(userAccountDto.getUserAccountId());
		}
		userAccountDto.setUserAccountId(oldUser.getUserAccountId());
		UserAccount userAccount = disassembleDto(userAccountDto);

		Date currentTimeStamp = new Date(System.currentTimeMillis());
		userAccount.setLastLoginDate(currentTimeStamp);

		userAccount = userAccountRepository.save(userAccount);
		return assembleDto(userAccount);
	}

	@Override
	// TODO reestablish cache @Cacheable(cacheNames = "UserDtoAssembler", key =
	// "#username")
	public UserAccountDto readDtoByUsername(String username) {
		UserAccount userAccount = userAccountRepository.findByUsernameIgnoreCase(username);
		return assembleDto(userAccount);
	}

	@Override
	// TODO reestablish cache @Cacheable(cacheNames = "UserDtoAssembler",
	// key="#userAccountId")
	public UserAccountDto readById(String userAccountId) {
		UserAccount userAccount = userAccountRepository.findWithColleges(userAccountId);
		return assembleDto(userAccount);
	}

	@Override
	public List<UserAccountDto> readAll() {
		List<UserAccount> userAccountList = userAccountRepository.findAllNotDeleted();
		List<UserAccountDto> userAccounts = new ArrayList<UserAccountDto>();
		for (UserAccount userAccount : userAccountList) {
			userAccounts.add(assembleDto(userAccount));
		}
		return userAccounts;
	}

	@Override
	public UserAccountDto assembleDto(UserAccount userAccount) {
		// Drop out of here immediately if there is nothing to assemble.
		if (userAccount == null) {
			return null;
		}

		UserAccountDto userAccountDto = new UserAccountDto();
		userAccountDto.setCreatedOnDate(userAccount.getCreatedOnDate());
		userAccountDto.setLastUpdatedDate(userAccount.getLastUpdatedDate());
		userAccountDto.setUserAccountId(userAccount.getUserAccountId());
		userAccountDto.setEmailAddress(userAccount.getEmailAddress());
		userAccountDto.setUsername(userAccount.getUsername());
		userAccountDto.setPassword(userAccount.getPassword());
		userAccountDto.setDisplayName(userAccount.getDisplayName());
		userAccountDto.setFirstName(userAccount.getFirstName());
		userAccountDto.setMiddleInitial(userAccount.getMiddleInitial());
		userAccountDto.setLastName(userAccount.getLastName());
		userAccountDto.setPhoneNumber(userAccount.getPhoneNumber());
		userAccountDto.setExtension(userAccount.getExtension());

		userAccountDto.setEnabled(userAccount.isEnabled());
		userAccountDto.setAccountExpired(userAccount.isAccountExpired());
		userAccountDto.setLastLoginDate(userAccount.getLastLoginDate());
		userAccountDto.setAccountLocked(userAccount.isAccountLocked());
		userAccountDto.setFailedLogins(userAccount.getFailedLogins());
		userAccountDto.setDeleted(userAccount.isDeleted());

		if (userAccount.getPrimaryCollege() != null) {
			userAccountDto.setPrimaryCollegeId(userAccount.getPrimaryCollege().getCollegeId());
		}

		Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
		for (SecurityGroup securityGroup : userAccount.getSecurityGroups()) {
			SecurityGroupDto securityGroupDto = securityGroupDtoAssembler.assembleDto(securityGroup);
			grantedAuthorities.addAll(securityGroupDto.getSecurityPermissionDtos());
		}
		userAccountDto.setGrantedAuthorities(grantedAuthorities);

		Set<SecurityGroupDto> securityGroupDtos = new HashSet<SecurityGroupDto>();
		for (SecurityGroup securityGroup : userAccount.getSecurityGroups()) {
			SecurityGroupDto securityGroupDto = securityGroupDtoAssembler.assembleDto(securityGroup);
			securityGroupDtos.add(securityGroupDto);
		}
		userAccountDto.setSecurityGroupDtos(securityGroupDtos);

		if (userAccount.getAuthorNamespace() != null) {
			userAccountDto.setNamespace(userAccount.getAuthorNamespace().getNamespace());
		}

		if (userAccount.getUserAccountColleges() != null) {
			userAccountDto.setColleges(collegeDtoAssembler.assembleDto(userAccount.getColleges()));
			userAccountDto.setCollegeIds(userAccount.getUserAccountColleges().stream().map(c -> c.getCollegeId())
					.collect(Collectors.toSet()));
		}

		Set<TestLocation> userTestLocations = userAccount.getTestLocations();
		Set<String> testLocationIds = new HashSet<>();
		if (userTestLocations != null) {
			for (TestLocation testLocation : userTestLocations) {
				testLocationIds.add(testLocation.getId());
			}
			for (CollegeDto collegeDto : userAccountDto.getColleges()) {
				Set<TestLocationDto> testLocationDtos = new HashSet<>();
				if (collegeDto.getTestLocations() != null) {
					for (TestLocationDto testLocationDto : collegeDto.getTestLocations()) {
						if (testLocationIds.contains(testLocationDto.getId())) {
							testLocationDtos.add(testLocationDto);
						}
					}
				}
				collegeDto.setTestLocations(testLocationDtos);
			}
		}
		return userAccountDto;
	}

	@Override
	public UserAccount disassembleDto(UserAccountDto userAccountDto) {
		// If there is nothing to disassemble, just return null
		if (userAccountDto == null) {
			return null;
		}
		UserAccount userAccount = new UserAccount();
		userAccount.setCreatedOnDate(userAccountDto.getCreatedOnDate());
		userAccount.setLastUpdatedDate(userAccountDto.getLastUpdatedDate());
		userAccount.setUserAccountId(userAccountDto.getUserAccountId());
		userAccount.setEmailAddress(userAccountDto.getEmailAddress());
		// transform the username to lower case if a non-student
		if (userAccountDto.isStudent()) {
			userAccount.setUsername(userAccountDto.getUsername());
		} else {
			userAccount.setUsername(userAccountDto.getUsername().toLowerCase());
		}
		userAccount.setPassword(userAccountDto.getPassword());
		userAccount.setDisplayName(userAccountDto.getDisplayName());
		userAccount.setFirstName(userAccountDto.getFirstName());
		userAccount.setMiddleInitial(userAccountDto.getMiddleInitial());
		userAccount.setLastName(userAccountDto.getLastName());
		userAccount.setPhoneNumber(userAccountDto.getPhoneNumber());
		userAccount.setExtension(userAccountDto.getExtension());

		userAccount.setEnabled(userAccountDto.isEnabled());
		userAccount.setExpired(userAccountDto.isAccountExpired());
		userAccount.setLastLoginDate(userAccountDto.getLastLoginDate());
		userAccount.setAccountLocked(userAccountDto.isAccountLocked());
		userAccount.setFailedLogins(userAccountDto.getFailedLogins());
		userAccount.setDeleted(userAccountDto.isDeleted());

		// TODO: Perhaps the null object pattern is in order here.
		if (userAccountDto.getSecurityGroupDtos() != null) {
			Set<SecurityGroup> securityGroups = new HashSet<SecurityGroup>();
			for (SecurityGroupDto securityGroupDto : userAccountDto.getSecurityGroupDtos()) {
				SecurityGroup securityGroup = securityGroupDtoAssembler.disassembleDto(securityGroupDto);
				securityGroups.add(securityGroup);
			}
			userAccount.setSecurityGroups(securityGroups);
		}
		AuthorNamespace authorNamespace = authorNamespaceRepository.findByNamespace(userAccountDto.getNamespace());
		userAccount.setAuthorNamespace(authorNamespace);

		UserAccountCollege primaryUserAccountCollege = null;
		if (userAccountDto.getPrimaryCollegeId() != null) {
			primaryUserAccountCollege = new UserAccountCollege(userAccountDto.getUserAccountId(),
					userAccountDto.getPrimaryCollegeId());
			userAccount.setPrimaryCollege(primaryUserAccountCollege);
		}

		if (userAccountDto.getColleges() != null) {
			userAccount.setUserAccountColleges(userAccountDto.getColleges().stream().map(collegeDto -> {
				College college = collegeDtoAssembler.disassembleDto(collegeDto);
				return new UserAccountCollege(userAccount, college);
			}).collect(Collectors.toSet()));
		}

		if (userAccount.getUserAccountColleges() != null && primaryUserAccountCollege != null) {
			userAccount.getUserAccountColleges().add(primaryUserAccountCollege);
		}

		if (userAccountDto.getColleges() != null && userAccountDto.getTestLocations() != null) {
			userAccount.setTestLocations(testLocationDtoAssembler.disassembleDto(userAccountDto.getTestLocations()));
		}

		return userAccount;
	}

}
