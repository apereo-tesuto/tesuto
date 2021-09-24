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
package org.cccnext.tesuto.admin.controller;

import java.util.Set;

import org.cccnext.tesuto.admin.dto.UserAccountDto;

/**
 * Created by bruce on 7/6/16.
 */
public class UserAccountFormData {
    UserAccountDto user;
    Set<Integer> roleIds;
    Set<String> collegeIds;
    String collegeEppnCode;
    Set<String> testLocationIds;

    public UserAccountDto getUser() {
        return user;
    }

    public void setUser(UserAccountDto user) {
        this.user = user;
    }

    public Set<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<Integer> roleIds) {
        this.roleIds = roleIds;
    }

    public Set<String> getCollegeIds() {
        return collegeIds;
    }

    public void setCollegeIds(Set<String> collegeIds) {
        this.collegeIds = collegeIds;
    }
    
    public String getCollegeEppnCode() {
        return collegeEppnCode;
    }

    public void setCollegeEppnCode(String collegeEppnCode) {
        this.collegeEppnCode = collegeEppnCode;
    }

    public Set<String> getTestLocationIds() {
        return testLocationIds;
    }

    public void setTestLocationIds(Set<String> testLocationIds) {
        this.testLocationIds = testLocationIds;
    }

    @Override
    public String toString() {
        return "UserAccountFormData{" +
                "user=" + user +
                ", roleIds=" + roleIds +
                ", collegeIds=" + collegeIds +
                ", collegeEppnCode=" + collegeEppnCode +
                ", testLocationIds=" + testLocationIds +
                '}';
    }
}
