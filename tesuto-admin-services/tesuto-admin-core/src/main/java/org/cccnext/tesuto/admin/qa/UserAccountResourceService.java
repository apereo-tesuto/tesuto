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
package org.cccnext.tesuto.admin.qa;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.admin.assembler.TestLocationDtoAssembler;
import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.exception.InvalidTestLocationException;
import org.cccnext.tesuto.admin.repository.TestLocationRepository;
import org.cccnext.tesuto.domain.util.ZipFileCompressor;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.qa.QAService;
import org.cccnext.tesuto.user.model.AuthorNamespace;
import org.cccnext.tesuto.user.repository.AuthorNamespaceRepository;
import org.cccnext.tesuto.user.service.UserAccountExistsException;
import org.cccnext.tesuto.util.ParseJsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class UserAccountResourceService implements QAService<UserAccountReseedDto> {

	private static String TEST_LOCATION_URI = "classpath:/qa/test_locations.json";

	static String USER_URI = "classpath:qa/users/";

	static String PASSWORD_URI = "classpath:qa/password.txt";

	static String QA_FOLDER_URI = "classpath:qa";

	@Autowired
	EntityManagerFactory emf;

	@Autowired
	UserAccountQAService userAccountService;

	@Autowired
	ZipFileCompressor compressor;

	@Value("${seed.data.zip.password}")
	String zipPassword;

	@Autowired
	TestLocationRepository testLocationRepository;

	@Autowired
	TestLocationDtoAssembler testLocationAssembler;
	
	@Autowired
	TestLocationQAService testLocationQAService;
	
	@Autowired
	AuthorNamespaceRepository authorNamespaceRepo;
	
	@Autowired
    Environment environment;

	private static String[] collegeIds = new String[] { "ZZ1", "ZZ2", "ZZ3", "ZZ4", "ZZ5" };
	
	public void init() throws Exception {
		List<String> profiles = Arrays.asList(environment.getActiveProfiles());
		if(profiles.contains("ci") || profiles.contains("local") || profiles.contains("test")) {
			AuthorNamespace namespace = authorNamespaceRepo.findByNamespace("DEVELOPER");
			if(namespace == null) {
				AuthorNamespace developer = new AuthorNamespace();
				developer.setCreatedOnDate(new Date());
				developer.setLastUpdatedDate(developer.getCreatedOnDate());
				developer.setNamespace("DEVELOPER");
				authorNamespaceRepo.save(developer);
			}
			seedUserData("superuser");
		}
		
	}

	public void generateData(boolean seedDataGeneratePassword)
			throws JsonParseException, JsonMappingException, IOException {
		List<TestLocationDto> testLocations = testLocationAssembler.assembleDto(testLocationRepository.findAll());

		List<Object> objects = testLocations.stream().map(tl -> (Object) tl).collect(Collectors.toList());
		ParseJsonUtil.writeSeedData(TEST_LOCATION_URI, objects.toArray());

		List<UserAccountReseedDto> userAccounts = userAccountService.storeUserAccountsToFile(collegeIds,
				seedDataGeneratePassword);
		storePasswords(userAccounts);
	}

	public void seedData(boolean seedDataGeneratePassword) throws Exception {
		seedTestLocations();
		seedUsers(seedDataGeneratePassword);
	}

	public void seedUserData(String username) throws Exception {
		seedTestLocations();
		seedUser(username);
	}

	public void deleteUsersByCollegeIds(Set<String> collegeIds) {
		for (String collegeId : collegeIds) {
			Set<UserAccountDto> userAccountDtos = userAccountService.getUsersByCollege(collegeId);
			userAccountDtos.forEach(u -> userAccountService.delete(u));
		}
	}

	private void seedTestLocations() throws IOException {
		testLocationQAService.setDefaults();
	}

	private void seedUsers(boolean seedDataGeneratePassword) throws IOException {
		Set<UserAccountReseedDto> userAccounts = getResources(UserAccountReseedDto.class);
		if (seedDataGeneratePassword) {
			List<UserAccountReseedDto> castAccounts = new ArrayList<>();
			userAccounts.stream().forEach(ua -> {
				try {
					userAccountService.autoUpdatePassword((UserAccountReseedDto) ua);
				} catch (Exception e) {
					throw new RuntimeException("An error occurred while attempting to seed data!", e);
				}
				castAccounts.add((UserAccountReseedDto) ua);
			});
			userAccounts.forEach(u -> {
				try {
					ParseJsonUtil.writeSeedData(UserAccountResourceService.USER_URI + "/" + u.getUsername(), u);
				} catch (IOException e) {
				}
			});
			ParseJsonUtil.writeSeedData(UserAccountResourceService.USER_URI, userAccounts.toArray());
			storePasswords(castAccounts);
		}
		setDefaults();
	}

	private void seedUser(String username) throws IOException {
		for (UserAccountReseedDto userAccount : getResources(UserAccountReseedDto.class)) {
			if (userAccount.getUsername().equalsIgnoreCase(username)) {
				try {
					if (userAccountService.getUserAccountByUserAccountId(userAccount.getUserAccountId()) == null) {
						userAccountService.upsertUserAccount(userAccount);
					}
				} catch (NotFoundException e) {
					try {
						userAccountService.upsertUserAccount(userAccount);
					} catch (NotFoundException | UserAccountExistsException | InvalidTestLocationException e1) {
						throw new RuntimeException("An error occurred while attempting to seed data!", e);
					}
				} catch (UserAccountExistsException | InvalidTestLocationException exception) {
					throw new RuntimeException("An error occurred while attempting to seed data!", exception);
				}
				return;
			}
		}
	}

	private void storePasswords(List<UserAccountReseedDto> userAccounts) throws IOException {
		StringBuilder passwords = new StringBuilder();
		List<UserAccountReseedDto> filteredAccounts = userAccounts.stream()
				.filter(ua -> StringUtils.isNotBlank(ua.getPassword())).collect(Collectors.toList());
		filteredAccounts.stream().forEach(ua -> {
			try {
				storeNewPassword(passwords, (UserAccountReseedDto) ua);
			} catch (Exception e) {
				throw new RuntimeException("An error occurred while attempting to seed data!", e);
			}
		});
		File pwfile = new File(PASSWORD_URI);
		pwfile.deleteOnExit();
		FileWriter fw = new FileWriter(pwfile.getAbsoluteFile());
		fw.write(passwords.toString());
		fw.close();

		compressor.compressFiles(QA_FOLDER_URI, "passwords.zip", Arrays.asList(pwfile), zipPassword);
	}

	private void storeNewPassword(StringBuilder writer, UserAccountReseedDto userAccount) throws IOException {
		writer.append(userAccount.getUsername() + " password: " + userAccount.getUnencryptedPassword() + "\n");
	}

	@Override
	public String getDirectoryPath() {
		return "classpath:qa/users";
	}

	@Override
	public void setDefaults() throws IOException {
		Set<UserAccountReseedDto> userAccounts = getResources(UserAccountReseedDto.class);
		for (UserAccountReseedDto userAccount : userAccounts) {
			try {
			   userAccountService.upsertUserAccount(userAccount);
				
			} catch (NotFoundException e) {
				throw new RuntimeException("An error occurred while attempting to seed data!", e);
			} catch (UserAccountExistsException | InvalidTestLocationException exception) {
				throw new RuntimeException("An error occurred while attempting to seed data!", exception);
			}
		}
	}

}
