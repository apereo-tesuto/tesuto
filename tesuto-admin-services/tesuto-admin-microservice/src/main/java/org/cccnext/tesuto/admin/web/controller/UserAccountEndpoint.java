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
package org.cccnext.tesuto.admin.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.admin.controller.UserAccountController;
import org.cccnext.tesuto.admin.controller.UserAccountFormData;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.dto.UserAccountViews;
import org.cccnext.tesuto.admin.exception.InvalidTestLocationException;
import org.cccnext.tesuto.admin.service.TestLocationService;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.exception.ValidationException;
import org.cccnext.tesuto.springboot.web.BaseController;
import org.cccnext.tesuto.user.service.UserAccountExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.annotation.JsonView;

@Slf4j
@Controller
@RequestMapping(value = "service/v1/user")
public class UserAccountEndpoint extends BaseController {

	@Autowired
	UserAccountController controller;
	@Autowired
	TestLocationService testLocationService;


	@ExceptionHandler(UserAccountExistsException.class)
	public ResponseEntity<?> userAccountExistsExceptionHandler(
			UserAccountExistsException ex) {
		return new ResponseEntity<>(error(ex.getMessage()),
				HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidTestLocationException.class)
	public ResponseEntity<?> InvalidTestLocationExceptionHandler(
			InvalidTestLocationException ex) {
		return new ResponseEntity<>(error(ex.getMessage()),
				HttpStatus.BAD_REQUEST);
	}

	@PreAuthorize("hasAuthority('VIEW_TEST_LOCATIONS_BY_USER')")
	@RequestMapping(value = "{userAccountId:.+}/locations", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> getUserLocations(@PathVariable String userAccountId) {
		log.info(String.format("Reading locations for userAccountId: %s",
				userAccountId));
		if (StringUtils.isBlank(userAccountId)) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}

		return controller.getUserLocations(userAccountId);
	}

	@PreAuthorize("hasAnyAuthority('API','VIEW_USER')")
	@RequestMapping(value = "username", method = RequestMethod.GET, produces = "application/json")
	@JsonView(UserAccountViews.UserColleges.class)
	public ResponseEntity<?> geUserAccountByUserName(
			@RequestParam("value") String username)
			throws UnsupportedEncodingException {
		log.info(String.format("Getting useraccount for username: %s",
				username));
		if (StringUtils.isBlank(username)) {
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
		}

		return controller.geUserAccountByUserName(username);
	}

	// TODO: Review the security implications. Do we need to have another
	// permission which overrides the association?
	// A super admin if you will?
	@PreAuthorize("hasAnyAuthority('USER_SEARCH', 'API')")
	@RequestMapping(value = "search", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> search(
			@RequestParam(value = "firstName", required = false) String firstName,
			@RequestParam(value = "lastName", required = false) String lastName,
			@RequestParam(value = "collegeId", required = false) Set<String> collegeIds,
			@RequestParam(value = "projection", required = false) String projection)
			throws UnsupportedEncodingException {

		return controller.search(getUser().getCollegeIds(), firstName,
				lastName, collegeIds, projection);
	}

	@PreAuthorize("hasAuthority('VIEW_USERS_BY_COLLEGE')")
	@RequestMapping(value = "college/{collegeId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> getUsersByCollege(@PathVariable String collegeId) {
		return controller.getUsersByCollege(getUser().getCollegeIds(),
				collegeId);
	}

	@PreAuthorize("hasAuthority('VIEW_USERS_BY_TEST_LOCATION')")
	@RequestMapping(value = "test-location/{testLocationId}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<?> getUsersByTestLocation(
			@PathVariable String testLocationId) {
		UserAccountDto adminUser = getUser();
		return controller.getUsersByTestLocation(adminUser, testLocationId);
	}

	@PreAuthorize("hasAuthority('USER_SEARCH')")
	@RequestMapping(value = "{userId:.+}", method = RequestMethod.GET)
	public ResponseEntity<?> getUser(@PathVariable String userId) {
		return controller.getUser(userId, getUser().getCollegeIds());
	}

	@PreAuthorize("hasAuthority('CREATE_USER')")
	@RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<?> create(@RequestBody UserAccountFormData formData)
			throws ValidationException, UserAccountExistsException,
			InvalidTestLocationException {
		UserAccountDto adminUserAccount = getUser();
		if (formData.getCollegeIds() == null || formData.getRoleIds() == null
				|| formData.getUser() == null) {
			return new ResponseEntity<>(error("Missing form data"),
					HttpStatus.BAD_REQUEST);
		}
		if (!getUser().getCollegeIds().containsAll(formData.getCollegeIds())) {
			return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
		}

		return controller.create(formData, adminUserAccount.getUserAccountId(),
				adminUserAccount.getCollegeIds());
	}

	@PreAuthorize("hasAuthority('DELETE_USER')")
	@RequestMapping(value = "{userId:.+}", method = RequestMethod.DELETE)
	public ResponseEntity<?> delete(@PathVariable String userId)
			throws NotFoundException {

		return controller.delete(userId);
	}

	@PreAuthorize("hasAuthority('EDIT_USER')")
	@RequestMapping(value = "{userId:.+}", method = RequestMethod.PUT)
	public ResponseEntity<?> edit(@PathVariable String userId,
			@RequestBody UserAccountFormData formData)
			throws ValidationException, NotFoundException,
			UserAccountExistsException {
		if (formData.getCollegeIds() == null || formData.getRoleIds() == null
				|| formData.getUser() == null) {
			return new ResponseEntity<>(error("Missing form data"),
					HttpStatus.BAD_REQUEST);
		}

		return controller.edit(getCurrentUserId(), userId, formData);
	}

}
