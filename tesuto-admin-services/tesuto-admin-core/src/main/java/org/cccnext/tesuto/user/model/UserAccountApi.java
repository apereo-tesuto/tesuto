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
package org.cccnext.tesuto.user.model;

import org.cccnext.tesuto.admin.model.College;
import org.cccnext.tesuto.admin.model.District;
import org.cccnext.tesuto.admin.model.TestLocation;
import org.cccnext.tesuto.domain.model.AbstractEntity;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@Entity
@Table(schema="public",name = "user_account_api")
public class UserAccountApi extends AbstractEntity implements UserDetails { // TODO: Determine whether we need to implement UserDetails.

    private static final long serialVersionUID = 1l;

    @Id
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "user_account_api_id", nullable = false, length = 256)
    private String userAccountApiId;

    @Column(name = "account_locked")
    private boolean accountLocked;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "display_name", nullable = false, length = 255)
    private String displayName;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "expired")
    private boolean expired;

    @Column(name = "failed_logins")
    private Integer failedLogins;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_login_date")
    private Date lastLoginDate;

    @Size(max = 255)
    @Column(name = "password", length = 255)
    private String password;

    @Size(max = 255)
    @Column(name = "username", length = 255, nullable = false)
    @NotNull
    private String username;

    @ManyToMany
    @JoinTable(schema="public", name = "user_account_api_security_group_api", joinColumns = {
            @JoinColumn(name = "user_account_api_id") }, inverseJoinColumns = { @JoinColumn(name = "security_group_api_id") })
    private Set<SecurityGroupApi> securityGroupApis = new HashSet<SecurityGroupApi>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        for (SecurityGroupApi securityGroupApi : this.securityGroupApis) {
            authorities.addAll(securityGroupApi.getPermissions());
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.expired;
    }

    public boolean isAccountExpired() {
        return this.expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.expired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public void setUserAccountApiId(String userAccountApiId) {
        this.userAccountApiId = userAccountApiId;
    }

    public String getUserAccountApiId() {
        return userAccountApiId;
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

    public boolean getExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public Integer getFailedLogins() {
        return failedLogins;
    }

    public void setFailedLogins(Integer failedLogins) {
        this.failedLogins = failedLogins;
    }

    @Override
    public Date getCreatedOnDate() {
        return createdOnDate;
    }

    @Override
    public void setCreatedOnDate(Date createdOnDate) {
        this.createdOnDate = createdOnDate;
    }

    @Override
    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    @Override
    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Set<SecurityGroupApi> getSecurityGroupApis() {
        return securityGroupApis;
    }

    public void setSecurityGroupApis(Set<SecurityGroupApi> securityGroupApis) {
        this.securityGroupApis = securityGroupApis;
    }

    public void addSecurityGroupApi(SecurityGroupApi securityGroupApi) {
        this.getSecurityGroupApis().add(securityGroupApi);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccountApi)) return false;
        if (!super.equals(o)) return false;

        UserAccountApi that = (UserAccountApi) o;

        if (isEnabled() != that.isEnabled()) return false;
        if (getExpired() != that.getExpired()) return false;
        if (isAccountLocked() != that.isAccountLocked()) return false;
        if (getUserAccountApiId() != null ? !getUserAccountApiId().equals(that.getUserAccountApiId()) : that.getUserAccountApiId() != null)
            return false;
        if (getUsername() != null ? !getUsername().equals(that.getUsername()) : that.getUsername() != null)
            return false;
        if (getPassword() != null ? !getPassword().equals(that.getPassword()) : that.getPassword() != null)
            return false;
        if (getDisplayName() != null ? !getDisplayName().equals(that.getDisplayName()) : that.getDisplayName() != null)
            return false;
        if (getLastLoginDate() != null ? !getLastLoginDate().equals(that.getLastLoginDate()) : that.getLastLoginDate() != null)
            return false;
        if (getFailedLogins() != null ? !getFailedLogins().equals(that.getFailedLogins()) : that.getFailedLogins() != null)
            return false;
        return true;
    }



    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getUserAccountApiId() != null ? getUserAccountApiId().hashCode() : 0);
        result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (getDisplayName() != null ? getDisplayName().hashCode() : 0);
        result = 31 * result + (isEnabled() ? 1 : 0);
        result = 31 * result + (getExpired() ? 1 : 0);
        result = 31 * result + (getLastLoginDate() != null ? getLastLoginDate().hashCode() : 0);
        result = 31 * result + (isAccountLocked() ? 1 : 0);
        result = 31 * result + (getFailedLogins() != null ? getFailedLogins().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserAccountApi{" +
                "userAccountApiId='" + userAccountApiId + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", displayName='" + displayName + '\'' +
                ", enabled=" + enabled +
                ", expired=" + expired +
                ", lastLoginDate=" + lastLoginDate +
                ", accountLocked=" + accountLocked +
                ", failedLogins=" + failedLogins +
                ", securityGroupApis=" + securityGroupApis +
                '}';
    }
}
