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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.cccnext.tesuto.admin.dto.CollegeDto;
import org.cccnext.tesuto.admin.dto.DistrictDto;
import org.cccnext.tesuto.admin.dto.SecurityGroupDto;
import org.cccnext.tesuto.admin.dto.TestLocationDto;
import org.cccnext.tesuto.domain.dto.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserAccountReseedDto implements Dto {
    private static final long serialVersionUID = 2l;

    private String userAccountId;

    private String username;

    private String emailAddress;

    private String password;

    private String displayName;

    private String firstName;

    private String middleInitial;

    private String lastName;

    private String phoneNumber;

    private String extension;

    private boolean enabled;
    
    @JsonIgnore
    private String unencryptedPassword;

    @JsonIgnore
    private boolean accountExpired ;

    @JsonIgnore
    private Date lastLoginDate;

    @JsonIgnore
    private boolean accountLocked;

    @JsonIgnore
    private Integer failedLogins;

    @JsonIgnore
    private Date createdOnDate;

    @JsonIgnore
    private Date lastUpdatedDate;

    private Set<SecurityGroupDto> securityGroupDtos;

    private String namespace;

    private Set<CollegeDto> colleges;

    private Set<String> collegeIds;

    private Set<DistrictDto> districts;

    private String primaryCollegeId;

    public String getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(String userAccountId) {
        this.userAccountId = userAccountId;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    @JsonIgnore
    public boolean isAccountNonExpired() {
        return !accountExpired;
    }

    public boolean isAccountExpired() {
        return accountExpired;
    }

    @JsonIgnore
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return !accountExpired;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleInitial() {
        return middleInitial;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setAccountExpired(boolean accountExpired) {
        this.accountExpired = accountExpired;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Integer getFailedLogins() {
        return failedLogins;
    }

    public void setFailedLogins(Integer failedLogins) {
        this.failedLogins = failedLogins;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public Date getCreatedOnDate() {
        return createdOnDate;
    }

    public void setCreatedOnDate(Date createdOnDate) {
        this.createdOnDate = createdOnDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }


    public Set<SecurityGroupDto> getSecurityGroupDtos() {
        return securityGroupDtos;
    }

    public void setSecurityGroupDtos(Set<SecurityGroupDto> securityGroupDtos) {
        this.securityGroupDtos = securityGroupDtos;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public Set<TestLocationDto> getTestLocations() {
        Set<TestLocationDto> testLocations = new HashSet<>();
        for (CollegeDto collegeDto : getColleges()) {
            testLocations.addAll(collegeDto.getTestLocations());
        }
        return testLocations;
    }


    public Set<CollegeDto> getColleges() {
        return colleges;
    }

    public void setColleges(Set<CollegeDto> colleges) {
        this.colleges = colleges;
    }

    public Set<String> getCollegeIds() {
        return collegeIds;
    }

    public void setCollegeIds(Set<String> collegeIds) {
        this.collegeIds = collegeIds;
    }

    public Set<DistrictDto> getDistricts() {
        return districts;
    }

    public void setDistricts(Set<DistrictDto> districts) {
        this.districts = districts;
    }

    public String getPrimaryCollegeId() {
        return primaryCollegeId;
    }

    public void setPrimaryCollegeId(String primaryCollegeId) {
        this.primaryCollegeId = primaryCollegeId;
    }
    @JsonIgnore
    public boolean isStudent() {
        return securityGroupDtos.stream().anyMatch(g -> g.getGroupName().equals("STUDENT"));
    }

    public String getUnencryptedPassword() {
        return unencryptedPassword;
    }

    public void setUnencryptedPassword(String unencryptedPassword) {
        this.unencryptedPassword = unencryptedPassword;
    }

    @Override
    public String toString() {
        return "UserAccountDto{" +
                "userAccountId='" + userAccountId + '\'' +
                ", username='" + username + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", password='" + password + '\'' +
                ", displayName='" + displayName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", middleInitial='" + middleInitial + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", extension='" + extension + '\'' +
                ", enabled=" + enabled +
                ", accountExpired=" + accountExpired +
                ", lastLoginDate=" + lastLoginDate +
                ", accountLocked=" + accountLocked +
                ", failedLogins=" + failedLogins +
                ", createdOnDate=" + createdOnDate +
                ", lastUpdatedDate=" + lastUpdatedDate +                ", securityGroupDtos=" + securityGroupDtos +
                ", namespace='" + namespace + '\'' +
                ", colleges=" + colleges +
                ", districts=" + districts +
                '}';
    }
}
