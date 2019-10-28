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
package org.cccnext.tesuto.admin.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class UrlUtils {

	@Value("${tesuto.admin.landing.page}")
	private String adminUrl;

	@Value("${tesuto.student.landing.page}")
	private String studentUrl;

	@Value("${tesuto.unauthorized.landing.page}")
	private String unauthorizedUrl;

	@Value("${tesuto.allowed.admin.roles}")
	private List<String> adminRoles;

	/**
	 * Builds the target URL according to the logic defined in the main class
	 * Javadoc.
	 */

	public String determineTargetUrl(Set<SecurityGroupDto> securityGroupDtos) {
		Boolean isStudent = securityGroupDtos.stream().anyMatch(sg -> sg.getGroupName().equals("STUDENT"));
		if(isStudent) {
			return studentUrl;
		}
		// if has securityGroupDtos is already authorized
		return adminUrl;
	}

	public String determineTargetUrl(Collection<? extends GrantedAuthority> authorities) {
		boolean isStudent = false;
		boolean isAdmin = false;
		for (GrantedAuthority grantedAuthority : authorities) {
			if (grantedAuthority.getAuthority().equals("STUDENT")) {
				isStudent = true;
				break;
			} else if (adminRoles.contains(grantedAuthority.getAuthority())) {
				isAdmin = true;
			}
		}

		if (isStudent) {
			return studentUrl;
		} else if (isAdmin) {
			return adminUrl;
		}
		return unauthorizedUrl;
	}

	public void setAdminUrl(String adminUrl) {
		this.adminUrl = adminUrl;
	}

	public List<String> getAdminRoles() {
		return adminRoles;
	}

	public void setAdminRoles(List<String> adminRoles) {
		this.adminRoles = adminRoles;
	}

	public String getAdminUrl() {
		return adminUrl;
	}

	public String getStudentUrl() {
		return studentUrl;
	}

	public void setStudentUrl(String studentUrl) {
		this.studentUrl = studentUrl;
	}

	public void setAdminRoles(String[] adminRoles) {
		this.adminRoles = Arrays.asList(adminRoles);
	}

	public String getUnauthorizedUrl() {
		return unauthorizedUrl;
	}

	public void setUnauthorizedUrl(String unauthorizedUrl) {
		this.unauthorizedUrl = unauthorizedUrl;
	}
}
