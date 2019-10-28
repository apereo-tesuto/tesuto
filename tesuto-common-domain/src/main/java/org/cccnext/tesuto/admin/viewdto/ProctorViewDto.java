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
package org.cccnext.tesuto.admin.viewdto;

import java.util.List;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.cccnext.tesuto.domain.dto.ViewDto;

public class ProctorViewDto implements ViewDto {

    private static final long serialVersionUID = 1L;

    String displayName;

    String username;

    String userAccountId;

    boolean enabled;

    List<String> securityGroups;

    List<String> securityPermissions;

    Set<CollegeDto> colleges;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(String userId) {
        this.userAccountId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getSecurityGroups() {
        return securityGroups;
    }

    public void setSecurityGroups(List<String> securityGroups) {
        this.securityGroups = securityGroups;
    }

    public List<String> getSecurityPermissions() {
        return securityPermissions;
    }

    public void setSecurityPermissions(List<String> securityPermissions) {
        this.securityPermissions = securityPermissions;
    }

    public Set<CollegeDto> getColleges() {
        return colleges;
    }

    public void setColleges(Set<CollegeDto> colleges) {
        this.colleges = colleges;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
