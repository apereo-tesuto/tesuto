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
package org.cccnext.tesuto.delivery.test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.dto.UserPasswordResetDto;
import org.cccnext.tesuto.admin.exception.InvalidTestLocationException;
import org.cccnext.tesuto.admin.form.SearchParameters;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.exception.ValidationException;
import org.cccnext.tesuto.user.service.UserAccountExistsException;
import org.cccnext.tesuto.user.service.UserAccountService;
import org.spockframework.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class UserAccountServiceStub implements UserAccountService {

    @Override
    public List<UserAccountDto> getAllUserAccounts() {
        //Auto-generated method stub
        return null;
    }

    @Override
    public UserAccountDto getUserAccount(String id) {
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setUserAccountId(id);
        userAccountDto.setUsername(id);
        userAccountDto.setFirstName("firstName");
        userAccountDto.setSecurityGroupDtos(new HashSet<>());
        return userAccountDto;
    }

    @Override
    public String create(UserAccountDto userAccountDto, Set<Integer> roleIds, Set<String> collegeIds,
            Set<String> testLocationIds) throws UserAccountExistsException, InvalidTestLocationException {
        //Auto-generated method stub
        return null;
    }

    @Override
    public void update(UserAccountDto userAccountDto, Set<Integer> roleIds, Set<String> collegeIds,
            Set<String> testLocationIds) throws NotFoundException, UserAccountExistsException {
        //Auto-generated method stub

    }

    @Override
    public void delete(UserAccountDto userAccountDto) {
        //Auto-generated method stub
    }

    @Override
    public UserAccountDto upsert(UserAccountDto userAccountDto) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public UserAccountDto getUserAccountByUsername(String username) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public UserAccountDto setUserAccountCredential(String username, String credential) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public UserAccountDto setUserAccountCredential(UserAccountDto userAccountDto, String password) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public UserPasswordResetDto resetPassword(UserPasswordResetDto userPasswordResetDto) throws InterruptedException {
        //Auto-generated method stub
        return null;
    }

    @Override
    public UserAccountDto getUserAccountFromAuthenticationByUsername(String username) throws Exception {
        //Auto-generated method stub
        return null;
    }

    @Override
    public UserAccountDto getUserAccountByUserAccountId(String userAccountId) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public List<UserAccountDto> search(SearchParameters parameters) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public void initializeNewUserAccount(UserAccountDto userAccount) {
        //Auto-generated method stub

    }

    @Override
    public List<String> validateUserAccount(UserAccountDto userAccount, Set<String> collegeIds) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public List<String> validateEppnCode(String collegeEppnCode, Set<String> collegeIds) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public void updateEppnWithSuffix(UserAccountDto user) throws ValidationException {
        //Auto-generated method stub

    }

    @Override
    public void removeEppnSuffix(UserAccountDto user) {
        //Auto-generated method stub

    }

    @Override
    public Pair<Set<String>, Set<String>> getCollegesAndLocations(UserAccountDto adminUser, UserAccountDto user,
            Set<String> collegeIds, Set<String> testLocationIds) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public Set<UserAccountDto> getUsersByCollege(String collegeId) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public Set<UserAccountDto> getUsersByTestLocation(String testLocationId) {
        //Auto-generated method stub
        return null;
    }

    @Override
    public void addUserAttributes(UserAccountDto userToBeUpdated, Set<Integer> roleIds, Set<String> collegeIds,
            Set<String> testLocationIds) throws NotFoundException, UserAccountExistsException {
        //Auto-generated method stub
        
    }

    @Override
    public List<UserAccountDto> findByCccids(List<String> userAccountIdList) {
        return null;
    }

	@Override
	public void failedLogin(String userAccountId) {
		//Auto-generated method stub
		
	}

	@Override
	public void clearFailedLogins(String userAccountId) {
		//Auto-generated method stub
		
	}

}
