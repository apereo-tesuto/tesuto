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
package org.cccnext.tesuto.admin.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.exception.InvalidTestLocationException;
import org.cccnext.tesuto.admin.form.SearchParameters;
import org.cccnext.tesuto.admin.service.TestLocationService;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.exception.ValidationException;
import org.cccnext.tesuto.user.service.UserAccountExistsException;
import org.cccnext.tesuto.user.service.UserAccountService;
import org.spockframework.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Service
public class UserAccountController {

    @Autowired
    UserAccountService service;
    @Autowired
    TestLocationService testLocationService;

    ProjectionFactory projectionFactory = new SpelAwareProxyProjectionFactory();


    public ResponseEntity<?> getUserLocations(String userAccountId) {
        if (StringUtils.isBlank(userAccountId)) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }

        UserAccountDto userAccountDto = service.getUserAccountByUserAccountId(userAccountId);
        if (userAccountDto != null) {
            Set<TestLocationDto> testLocations = userAccountDto.getTestLocations();
            return new ResponseEntity<Set<TestLocationDto>>(testLocations, HttpStatus.OK);
        }
        return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> geUserAccountByUserName(String username)
            throws UnsupportedEncodingException {
        if (StringUtils.isBlank(username)) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }

        UserAccountDto userAccountDto = service.getUserAccountByUsername(URLDecoder.decode(username, "UTF-8"));
        if (userAccountDto != null) {
            return new ResponseEntity<UserAccountDto>(userAccountDto, HttpStatus.OK);
        }
        return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> search(Set<String> userCollegeIds, String firstName,
            String lastName,
            Set<String> collegeIds,
            String projection) throws UnsupportedEncodingException {
        
        SearchParameters p = new SearchParameters();
        p.setFirstName(firstName);
        p.setLastName(lastName);
        if (collegeIds != null) {
            // restrict colleges to those the user has access to
            collegeIds.retainAll(userCollegeIds);
        } else {
            collegeIds = userCollegeIds;
        }
        p.setCollegeIds(collegeIds);
        // TODO: Restrict by District IDs and associations at some point?
        List<UserAccountDto> users = service.search(p);
        users.forEach(u -> {
            u.setPassword(null);
            service.removeEppnSuffix(u);
        });

        if (StringUtils.equals(projection, "slim")) {
            return new ResponseEntity<>(users.stream()
                    .map(u -> projectionFactory.createProjection(UserAccountSlimProjection.class, u))
                    .collect(Collectors.toList()), HttpStatus.OK);
        }
        return new ResponseEntity<List<UserAccountDto>>(users, HttpStatus.OK);
    }

    public ResponseEntity<?> getUsersByCollege(Set<String> userCollegeIds, String collegeId) {
        if (StringUtils.isBlank(collegeId)) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }
        if (!userCollegeIds.contains(collegeId)) {
            return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        }
        Set<UserAccountDto> users = service.getUsersByCollege(collegeId);
        users.forEach(u -> {
            u.setPassword(null);
            service.removeEppnSuffix(u);
        });
        return new ResponseEntity<Set<UserAccountDto>>(users, HttpStatus.OK);
    }

    public ResponseEntity<?> getUsersByTestLocation(UserAccountDto adminUser, String testLocationId) {
        if (testLocationId == null) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        }

        if (!UserAccountService.doesUserHavePermission(adminUser,
                UserAccountService.VIEW_COLLEGES_WITH_ALL_LOCATIONS_PERMISSION)
                && !testLocationService.isUserAssociatedWithTestLocation(adminUser, testLocationId)) {
            return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        }
        Set<UserAccountDto> users = service.getUsersByTestLocation(testLocationId);
        users.forEach(u -> {
            u.setPassword(null);
            service.removeEppnSuffix(u);
        });
        return new ResponseEntity<Set<UserAccountDto>>(users, HttpStatus.OK);
    }

    public ResponseEntity<?> getUser(String userId, Set<String> adminCollegeIds) {
        UserAccountDto user = service.getUserAccount(userId);
        if (user == null) {
            throw new NotFoundException("A user with id " + userId + " was not found in the system.");
        }
        // the current user needs access to at one of the colleges for the user
        // he is search for
        if (CollectionUtils.intersection(adminCollegeIds, user.getCollegeIds()).isEmpty()) {
            return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        }
        user.setPassword(null);
        service.removeEppnSuffix(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public ResponseEntity<?> create(UserAccountFormData formData,  String adminUserId, Set<String> adminCollegeIds) throws ValidationException,
            UserAccountExistsException, InvalidTestLocationException {
    	
        if (!adminCollegeIds.containsAll(formData.getCollegeIds())) {
            return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        }

        Pair<Set<String>, Set<String>> collegesAndLocations = getCollegesAndLocations(null, adminUserId, formData);

        if (CollectionUtils.isEmpty(collegesAndLocations.first())) {
            return new ResponseEntity<Void>(HttpStatus.FORBIDDEN);
        }

        List<String> messages = service.validateUserAccount(formData.getUser(), formData.getCollegeIds());

        service.validateEppnCode(formData.getUser().getPrimaryCollegeId(), formData.getCollegeIds());

        if (messages.size() > 0) {
            throw new ValidationException(messages);
        }
        
        // Note may need to be removed if passwords are ever allowed through the ui
        formData.getUser().setPassword(null);
        service.updateEppnWithSuffix(formData.getUser());

        service.initializeNewUserAccount(formData.getUser());
        String userId = service.create(formData.getUser(), formData.getRoleIds(), formData.getCollegeIds(),
                formData.getTestLocationIds());
        List<String> retval = new ArrayList<>();
        retval.add(userId);
        return new ResponseEntity<List>(retval, HttpStatus.CREATED);
    }

    public ResponseEntity<?> delete(String userId) throws NotFoundException {
        UserAccountDto user = service.getUserAccount(userId);
        if (user == null) {
            throw new NotFoundException("A user with id " + userId + " was not found in the system.");
        }

        service.delete(user);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<?> edit(String adminUserId, String userId, UserAccountFormData formData)
            throws ValidationException, NotFoundException, UserAccountExistsException {

        UserAccountDto user = service.getUserAccount(userId);
        if (user == null) {
            throw new NotFoundException("A user with id " + userId + " was not found in the system.");
        }
        UserAccountDto userToBeUpdated = formData.getUser();
        // Note may need to be removed if passwords are ever allowed through the ui
        userToBeUpdated.setPassword(null);
        List<String> messages = service.validateUserAccount(userToBeUpdated, formData.getCollegeIds());
        if (!userId.equals(userToBeUpdated.getUserAccountId())) {
            messages.add(service.USER_ACCOUNT_IDS_DO_NOT_MATCH);
        }
        service.updateEppnWithSuffix(userToBeUpdated);
        if (messages.size() > 0) {
            throw new ValidationException(messages);
        }

        Pair<Set<String>, Set<String>> collegeLocationIds = getCollegesAndLocations(user, adminUserId, formData);
        service.update(userToBeUpdated, formData.getRoleIds(), collegeLocationIds.first(), collegeLocationIds.second());

        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }

    private Pair<Set<String>, Set<String>> getCollegesAndLocations(UserAccountDto user, String adminUserId, UserAccountFormData formData) {
        UserAccountDto adminUser = service.getUserAccount(adminUserId);
        return service
                .getCollegesAndLocations(adminUser, user, formData.getCollegeIds(), formData.getTestLocationIds());
    }

    public Set<String> getCollegeIdSetFromDtoSet(Set<CollegeDto> collegeDtoSet) {
        Set<String> collegeIds = new HashSet<>();
        for (CollegeDto college : collegeDtoSet) {
            collegeIds.add(college.getCccId());
        }
        return collegeIds;
    }
}
