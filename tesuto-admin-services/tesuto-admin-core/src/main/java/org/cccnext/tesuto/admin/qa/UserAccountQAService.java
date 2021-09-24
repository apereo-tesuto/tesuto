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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.exception.InvalidTestLocationException;
import org.cccnext.tesuto.exception.NotFoundException;
import org.cccnext.tesuto.user.service.UserAccountExistsException;
import org.cccnext.tesuto.user.service.UserAccountServiceImpl;
import org.cccnext.tesuto.util.ParseJsonUtil;
import org.dozer.Mapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James T Stanley <jstanley@unicon.net>
 */
@Slf4j
@Service
public class UserAccountQAService extends UserAccountServiceImpl  {
    
    static String DEFAULT_PASSWORD = "password";
    
    static String  USER_URI = "classpath:qa/users.json";
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Value("${seed.data.password.use.default}")
    Boolean useDefaultPassword;
    
    
    PasswordGenerator passwordGenerator = new PasswordGenerator();

    @Autowired Mapper mapper;
    
    public List<UserAccountReseedDto> storeUserAccountsToFile(String[] collegeIds, Boolean updatePassword) throws JsonParseException, JsonMappingException, IOException {
        List<UserAccountReseedDto> userAccounts = new ArrayList<UserAccountReseedDto>();
        List<String> usernames = new ArrayList<String>();
        for(int i = 0; i < collegeIds.length; i++){
            Set<UserAccountDto> userAccountDtos = getUsersByCollege(collegeIds[i]);
            for(UserAccountDto userAccountDto:userAccountDtos) {
                if(userAccountDto != null) {
                    UserAccountReseedDto userAccount = mapper.map(userAccountDto, UserAccountReseedDto.class);
                    if(updatePassword) {
                        autoUpdatePassword(userAccount);
                    } else {
                    	userAccount.setPassword(userAccountDto.getPassword());
                    }

                    if(!usernames.contains(userAccount.getUsername())) {
                        userAccounts.add(userAccount);
                        usernames.add(userAccount.getUsername());
                    }
                } else {
                    log.error(String.format("User account:%sencodePassword not found", collegeIds[i] ));
                }
              }
           
        }
        ParseJsonUtil.writeSeedData(USER_URI, userAccounts.toArray());
        return userAccounts;
    }
    
    public void upsertUserAccount(UserAccountReseedDto userAccount) throws NotFoundException, UserAccountExistsException, InvalidTestLocationException {
        UserAccountDto userAccountDto = mapper.map(userAccount, UserAccountDto.class);
        Set<String> locationIds = userAccountDto.getTestLocations().stream()
                .map(l -> l.getId())
                .collect(Collectors.toSet());
        Set<Integer> roleIds = userAccountDto.getSecurityGroupDtos().stream()
        .map(sg -> sg.getSecurityGroupId())
        .collect(Collectors.toSet());
        if(this.getUserAccountByUsername(userAccount.getUsername()) != null) {
            userAccountDto.setUserAccountId(this.getUserAccountByUsername(userAccount.getUsername()).getUserAccountId());
            this.update(userAccountDto, roleIds, userAccountDto.getCollegeIds(), locationIds);
        } else {
            this.create(userAccountDto, roleIds, userAccountDto.getCollegeIds(), locationIds);
        }
    }
    
    public void autoUpdatePassword(UserAccountReseedDto userAccount) throws UnsupportedEncodingException{
       if( StringUtils.isBlank(userAccount.getPassword())) {
           userAccount.setUnencryptedPassword( null);
           userAccount.setPassword(null);
           return;
       }
        userAccount.setUnencryptedPassword( randomPassword() );
        userAccount.setPassword(passwordEncoder.encode(userAccount.getUnencryptedPassword()));
    }
    
    private String randomPassword() throws UnsupportedEncodingException {
        if(useDefaultPassword) {
            return DEFAULT_PASSWORD;
        }
        return new String(passwordGenerator.generatePassword());
    }
}
