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
package org.cccnext.tesuto.web.stub;

import java.util.Set;

import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.user.service.UserAccountReader;
import org.springframework.stereotype.Service;

@Service
public class UserAccountReaderStub implements UserAccountReader {

	@Override
	public UserAccountDto getUserAccountByUsername(String username) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public UserAccountDto getUserAccount(String id) {
		//Auto-generated method stub
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
