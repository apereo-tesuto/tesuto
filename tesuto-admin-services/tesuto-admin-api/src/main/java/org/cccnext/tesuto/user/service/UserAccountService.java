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

import java.util.List;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.dto.UserPasswordResetDto;
import org.cccnext.tesuto.admin.exception.InvalidTestLocationException;
import org.cccnext.tesuto.admin.form.SearchParameters;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.exception.ValidationException;
import org.spockframework.util.Pair;
import org.springframework.security.core.GrantedAuthority;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public interface UserAccountService extends UserAccountReader {

    public static final String USER_ACCOUNT_ID_NULL = "userAccountId is missing.";
    public static final String USERNAME_NULL = "username is missing.";
    public static final String FIRST_NAME_MISSING = "First name is missing.";
    public static final String LAST_NAME_MISSING = "Last name is missing.";
    public static final String EMAIL_ADDRESS_IS_MISSING = "Email address is missing.";
    public static final String INVALID_EMAIL_ADDRESS = "Invalid email address: ";
    public static final String INVALID_EPPN_COLLEGE_CODE = "Eppn College Code does not return suffix, CODE:";
    public static final String INVALID_PRIMARY_COLLEGE = "The user does not have access to their primary college: ";
    public static final String INVALID_USERNAME = "The username already contains at least one '@': ";
    public static final String EPPN_COLLEGE_CODE_NOT_IN_USER_COLLEGE_IDS = "Eppn College Id can not be found in college ids";
    public static final String USER_ACCOUNT_IDS_DO_NOT_MATCH = "Url parameter user id does not match form data user id.";

    public static final String VIEW_COLLEGES_WITH_ALL_LOCATIONS_PERMISSION = "VIEW_COLLEGES_WITH_ALL_LOCATIONS";

    List<UserAccountDto> getAllUserAccounts();

    String create(UserAccountDto userAccountDto, Set<Integer> roleIds, Set<String> collegeIds, Set<String> testLocationIds) throws UserAccountExistsException, InvalidTestLocationException;

    void update(UserAccountDto userAccountDto, Set<Integer> roleIds, Set<String> collegeIds, Set<String> testLocationIds) throws NotFoundException, UserAccountExistsException;

    void delete(UserAccountDto userAccountDto);

    UserAccountDto upsert(UserAccountDto userAccountDto);

    UserAccountDto setUserAccountCredential(String username, String credential);

    UserAccountDto setUserAccountCredential(UserAccountDto userAccountDto, String password);

    UserPasswordResetDto resetPassword(UserPasswordResetDto userPasswordResetDto) throws InterruptedException;

    UserAccountDto getUserAccountFromAuthenticationByUsername(String username) throws Exception;

    UserAccountDto getUserAccountByUserAccountId(String userAccountId);

    List<UserAccountDto> search(SearchParameters parameters);

    void initializeNewUserAccount(UserAccountDto userAccount);

    List<String> validateUserAccount(UserAccountDto userAccount, Set<String> collegeIds);

    List<String> validateEppnCode(String collegeEppnCode, Set<String> collegeIds);

    void updateEppnWithSuffix(UserAccountDto user) throws ValidationException;

    static boolean doesUserHavePermission(UserAccountDto user, String permission) {
        boolean hasPermission = false;
        Set<GrantedAuthority> grantedAuthorities = user.getGrantedAuthorities();
        for (GrantedAuthority grantedAuthority : grantedAuthorities) {
            if (grantedAuthority.getAuthority().equals(permission)) {
                hasPermission = true;
                break;
            }
        }
        return hasPermission;
    }

    void removeEppnSuffix(UserAccountDto user);

    Pair<Set<String>,Set<String>> getCollegesAndLocations(UserAccountDto adminUser, UserAccountDto user, Set<String> collegeIds, Set<String> testLocationIds);

    Set<UserAccountDto> getUsersByCollege(String collegeId);

    Set<UserAccountDto> getUsersByTestLocation(String testLocationId);

    void addUserAttributes(UserAccountDto userToBeUpdated, Set<Integer> roleIds, Set<String> collegeIds,
            Set<String> testLocationIds) throws NotFoundException, UserAccountExistsException;

    List<UserAccountDto> findByCccids(List<String> userAccountIdList);
    
}
