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
package org.cccnext.tesuto.reports.stub;

import java.util.List;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.admin.exception.InvalidTestLocationException;
import org.cccnext.tesuto.user.assembler.UserAccountDtoAssembler;
import org.cccnext.tesuto.user.model.UserAccount;
import org.cccnext.tesuto.user.service.UserAccountExistsException;
import org.springframework.stereotype.Service;

@Service
public class UserAccountDtoAssemblerStub implements UserAccountDtoAssembler {


	@Override
	public String create(UserAccountDto userAccountDto, Set<Integer> roleIds, Set<String> collegeIds,
			Set<String> testLocationIds) throws UserAccountExistsException, InvalidTestLocationException {
		//Auto-generated method stub
		return null;
	}

	@Override
	public void delete(UserAccountDto userAccountDto) {
		//Auto-generated method stub

	}

	@Override
	public void update(UserAccountDto userAccountDto, Set<Integer> roleIds, Set<String> collegeIds,
			Set<String> testLocationIds) throws UserAccountExistsException {
		//Auto-generated method stub

	}

	@Override
	public UserAccountDto create(UserAccountDto userAccountDto) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public UserAccountDto update(UserAccountDto userAccountDto) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public UserAccountDto readById(String userAccountId) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public List<UserAccountDto> readAll() {
		//Auto-generated method stub
		return null;
	}

	@Override
	public UserAccountDto assembleDto(UserAccount userAccount) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public UserAccount disassembleDto(UserAccountDto userAccountDto) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public UserAccountDto readDtoByUsername(String username) {
		//Auto-generated method stub
		return null;
	}

}
