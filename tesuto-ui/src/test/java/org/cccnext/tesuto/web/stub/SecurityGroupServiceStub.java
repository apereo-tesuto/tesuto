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

import java.util.List;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.cccnext.tesuto.user.service.SecurityGroupService;
import org.springframework.stereotype.Service;

@Service
public class SecurityGroupServiceStub implements SecurityGroupService {

	@Override
	public void addSecurityPermissionToSecurityGroup(Integer securityGroupId, String securityPermissionId) {
		//Auto-generated method stub

	}

	@Override
	public void removeSecurityPermissionFromSecurityGroup(Integer securityGroupId, String securityPermissionId) {
		//Auto-generated method stub

	}

	@Override
	public List<SecurityGroupDto> getAllSecurityGroups() {
		//Auto-generated method stub
		return null;
	}

	@Override
	public SecurityGroupDto getSecurityGroup(Integer id) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public SecurityGroupDto upsert(SecurityGroupDto securityGroupDto) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public SecurityGroupDto getSecurityGroupByGroupName(String groupName) {
		//Auto-generated method stub
		return null;
	}

	@Override
	public Set<SecurityGroupDto> getSecurityGroupsByUserAccountId(String userAccountId) {
		//Auto-generated method stub
		return null;
	}

}
