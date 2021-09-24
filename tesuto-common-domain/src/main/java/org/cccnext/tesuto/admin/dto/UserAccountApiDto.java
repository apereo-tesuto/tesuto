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
package org.cccnext.tesuto.admin.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.cccnext.tesuto.domain.dto.Dto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class UserAccountApiDto implements Dto, UserDetails {
    private static final long serialVersionUID = 2l;

    private String userAccountApiId;

    private String username;

    @JsonIgnore
    private String password;

    private String displayName;

    private boolean enabled;

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

    private Set<GrantedAuthority> grantedAuthorities;

    private Set<SecurityGroupApiDto> securityGroupApiDtos;

    public String getUserAccountApiId() {
        return userAccountApiId;
    }

    public void setUserAccountApiId(String userAccountApiId) {
        this.userAccountApiId = userAccountApiId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !accountExpired;
    }

    public boolean isAccountExpired() {
        return accountExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO: Should we differentiate an expired password vs expired account?
        return !accountExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public Set<GrantedAuthority> getGrantedAuthorities() {
        return grantedAuthorities;
    }

    public void setGrantedAuthorities(Set<GrantedAuthority> grantedAuthorities) {
        this.grantedAuthorities = grantedAuthorities;
    }

    public Set<SecurityGroupApiDto> getSecurityGroupApiDtos() {
        return securityGroupApiDtos;
    }

    public void setSecurityGroupApiDtos(Set<SecurityGroupApiDto> securityGroupApiDtos) {
        this.securityGroupApiDtos = securityGroupApiDtos;
    }
    public boolean hasPermission(String permission) {
        return grantedAuthorities.stream().anyMatch(ga -> ga.getAuthority().equals(permission));
    }

    @Override
    public String toString() {
        return "UserAccountApiDto{" +
                "userAccountApiId='" + userAccountApiId + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", displayName='" + displayName + '\'' +
                ", enabled=" + enabled +
                ", accountExpired=" + accountExpired +
                ", lastLoginDate=" + lastLoginDate +
                ", accountLocked=" + accountLocked +
                ", failedLogins=" + failedLogins +
                ", createdOnDate=" + createdOnDate +
                ", lastUpdatedDate=" + lastUpdatedDate +
                ", grantedAuthorities=" + grantedAuthorities +
                ", securityGroupApiDtos=" + securityGroupApiDtos +
                '}';
    }
}
