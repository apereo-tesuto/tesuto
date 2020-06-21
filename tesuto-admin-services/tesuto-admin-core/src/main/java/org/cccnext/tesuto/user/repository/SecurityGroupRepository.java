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
package org.cccnext.tesuto.user.repository;

import org.cccnext.tesuto.user.model.SecurityGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface SecurityGroupRepository extends JpaRepository<SecurityGroup, Integer> {
	public SecurityGroup findByGroupName(String groupName);

	@Query("from SecurityGroup where securityGroupId in ?1")
	public Set<SecurityGroup> findSecurityGroupSet(Set<Integer> securityGroupIds);

	public void removeSecurityPermission(Integer securityGroupId, String securityPermissionId);

	public void addSecurityPermission(Integer securityGroupId, String securityPermissionId);
}
