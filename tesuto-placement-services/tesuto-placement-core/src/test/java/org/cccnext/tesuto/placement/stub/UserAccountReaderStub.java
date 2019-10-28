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
package org.cccnext.tesuto.placement.stub;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.user.service.UserAccountReader;

public class UserAccountReaderStub implements UserAccountReader {

    Map<String,UserAccountDto> userAccounts = new HashMap<>();

    public void addUserAccountWithColleges(String id, Set<String>  collegeMisCodes) {
        UserAccountDto userAccountDto = new UserAccountDto();
        userAccountDto.setUserAccountId(id);
        userAccountDto.setUsername("username");
        userAccountDto.setCollegeIds(collegeMisCodes);
        userAccounts.put(id,userAccountDto);
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
	public void failedLogin(String userAccountId) {
		//Auto-generated method stub
		
	}



	@Override
	public UserAccountDto getUserAccountByUsername(String username) {
		//Auto-generated method stub
		return null;
	}



	@Override
	public void clearFailedLogins(String userAccountId) {
		//Auto-generated method stub
		
	}
}
