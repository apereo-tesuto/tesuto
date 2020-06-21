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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

import org.cccnext.tesuto.admin.model.College;
import org.cccnext.tesuto.admin.model.District;
import org.cccnext.tesuto.admin.model.TestLocation;
import org.cccnext.tesuto.domain.model.AbstractEntity;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@Entity
@Table(schema="public",name = "user_account")
//@Cache(region = "userAccount", usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserAccount extends AbstractEntity {

    private static final long serialVersionUID = 3l;

    @Id
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "user_account_id", nullable = false, length = 256)
    private String userAccountId;

    @Column(name = "account_locked")
    private boolean accountLocked;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "display_name", nullable = false, length = 255)
    private String displayName;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @NotBlank(message = "{UserAccount.emailAddress.NotEmpty}")
    @Email(message = "{UserAccount.emailAddress.Email}")
    @Column(name = "email_address", nullable = false, length = 255)
    private String emailAddress;

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

    @Size(max = 255)
    @Column(name = "first_name", length = 255)
    private String firstName;

    @Size(max = 100)
    @Column(name = "last_name", length = 100)
    private String lastName;

    @Size(max = 255)
    @Column(name = "middle_initial", length = 255)
    private String middleInitial;

    @Size(max = 255)
    @Column(name = "phone_number", length = 255)
    private String phoneNumber;

    @Size(max = 10)
    @Column(name = "extension", length = 10)
    private String extension;

    @ManyToMany(mappedBy = "userAccountSet")
    private Set<District> districtSet;

    @ManyToMany(fetch= FetchType.EAGER)
    @JoinTable(schema="public", name = "user_account_security_group", joinColumns = {
            @JoinColumn(name = "user_account_id") }, inverseJoinColumns = { @JoinColumn(name = "security_group_id") })
    private Set<SecurityGroup> securityGroups = new HashSet<SecurityGroup>();

    @JoinColumn(name = "author_namespace_id", referencedColumnName = "namespace_id", nullable = true)
    @ManyToOne
    private AuthorNamespace authorNamespace;

    @ManyToMany(fetch= FetchType.EAGER)
    @JoinTable(schema="public", name = "user_account_test_location", joinColumns = {
            @JoinColumn(name = "user_account_id") }, inverseJoinColumns = { @JoinColumn(name = "test_location_id") })
    private Set<TestLocation> testLocations;

    @OneToMany(fetch= FetchType.EAGER, mappedBy = "userAccount")
    private Set<UserAccountCollege> userAccountColleges;

    @ManyToMany
    @JoinTable(schema="public", name = "user_account_district", joinColumns = {
            @JoinColumn(name = "user_account_id") }, inverseJoinColumns = { @JoinColumn(name = "ccc_id") })
    private Set<District> districts = new HashSet<District>();

    @Column(name="primary_college_id")
    private String primaryCollegeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "user_account_id", referencedColumnName = "user_account_id", insertable = false, updatable = false),
            @JoinColumn(name = "primary_college_id", referencedColumnName = "college_ccc_id", insertable = false, updatable = false) })
    private UserAccountCollege primaryCollege;

    @Column(name = "deleted")
    private boolean deleted;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.security.core.userdetails.UserDetails#getAuthorities(
     * )
     */
    // @JsonIgnore
    //@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        for (SecurityGroup group : this.getSecurityGroups()) {
            authorities.addAll(group.getPermissions());
        }
        return authorities;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.security.core.userdetails.UserDetails#getPassword()
     */
    // @JsonIgnore
    //@Override
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.security.core.userdetails.UserDetails#getUsername()
     */
    //@Override
    public String getUsername() {
        return this.username;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.core.userdetails.UserDetails#
     * isAccountNonExpired()
     */
    //@Override
    public boolean isAccountNonExpired() {
        return !this.expired;
    }

    public boolean isAccountExpired() {
        return this.expired;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.core.userdetails.UserDetails#
     * isAccountNonLocked()
     */
    //@Override
    public boolean isAccountNonLocked() {
        return !this.accountLocked;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.core.userdetails.UserDetails#
     * isCredentialsNonExpired()
     */
    //@Override
    public boolean isCredentialsNonExpired() {
        return !this.expired;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.security.core.userdetails.UserDetails#isEnabled()
     */
    //@Override
    public boolean isEnabled() {
        return this.enabled;
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

    public void setUserAccountId(String userAccountId) {
        this.userAccountId = userAccountId;
    }

    public String getUserAccountId() {
        return userAccountId;
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

    public Set<SecurityGroup> getSecurityGroups() {
        return securityGroups;
    }

    public void setSecurityGroups(Set<SecurityGroup> securityGroups) {
        this.securityGroups = securityGroups;
    }

    public void addSecurityGroup(SecurityGroup group) {
        this.getSecurityGroups().add(group);
    }

    public AuthorNamespace getAuthorNamespace() {
        return authorNamespace;
    }

    public void setAuthorNamespace(AuthorNamespace authorNamespace) {
        this.authorNamespace = authorNamespace;
    }

    public Set<TestLocation> getTestLocations() {
        return testLocations;
    }

    public void setTestLocations(Set<TestLocation> testLocations) {
        this.testLocations = testLocations;
    }

    public Set<UserAccountCollege> getUserAccountColleges() {
        return userAccountColleges;
    }

    public void setUserAccountColleges(Set<UserAccountCollege> userAccountColleges) {
        this.userAccountColleges = userAccountColleges;
    }

    public Set<String> getCollegeIds() {
        return getUserAccountColleges().stream().map(uc -> uc.getCollegeId()).collect(Collectors.toSet());
    }

    public Set<College> getColleges() {
        if (userAccountColleges == null) {
            return null;
        } else {
            return userAccountColleges.stream().map(uc -> uc.getCollege()).filter(c -> c != null).collect(Collectors.toSet());
        }
    }

    public UserAccountCollege getPrimaryCollege() {
        return primaryCollege;
    }

    public void setPrimaryCollege(UserAccountCollege primaryCollege) {
        this.primaryCollege = primaryCollege;
        if(primaryCollege != null) {
            this.primaryCollegeId = primaryCollege.getCollegeId();
        } else {
            this.primaryCollegeId = null;
        }
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    //This method is used for unit testing
    public boolean isEqualTo(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount)) return false;
        if (!super.equals(o)) return false;

        UserAccount that = (UserAccount) o;

        if (isEnabled() != that.isEnabled()) return false;
        if (getExpired() != that.getExpired()) return false;
        if (isAccountLocked() != that.isAccountLocked()) return false;
        if (getUserAccountId() != null ? !getUserAccountId().equals(that.getUserAccountId()) : that.getUserAccountId() != null)
            return false;
        if (getUsername() != null ? !getUsername().equals(that.getUsername()) : that.getUsername() != null)
            return false;
        if (getEmailAddress() != null ? !getEmailAddress().equals(that.getEmailAddress()) : that.getEmailAddress() != null)
            return false;
        if (getPassword() != null ? !getPassword().equals(that.getPassword()) : that.getPassword() != null)
            return false;
        if (getDisplayName() != null ? !getDisplayName().equals(that.getDisplayName()) : that.getDisplayName() != null)
            return false;
        if (getFirstName() != null ? !getFirstName().equals(that.getFirstName()) : that.getFirstName() != null)
            return false;
        if (getMiddleInitial() != null ? !getMiddleInitial().equals(that.getMiddleInitial()) : that.getMiddleInitial() != null)
            return false;
        if (getLastName() != null ? !getLastName().equals(that.getLastName()) : that.getLastName() != null)
            return false;
        if (getPhoneNumber() != null ? !getPhoneNumber().equals(that.getPhoneNumber()) : that.getPhoneNumber() != null)
            return false;
        if (getExtension() != null ? !getExtension().equals(that.getExtension()) : that.getExtension() != null)
            return false;
        if (getLastLoginDate() != null ? !getLastLoginDate().equals(that.getLastLoginDate()) : that.getLastLoginDate() != null)
            return false;
        if (getFailedLogins() != null ? !getFailedLogins().equals(that.getFailedLogins()) : that.getFailedLogins() != null)
            return false;
        if (getSecurityGroups() != null ? !getSecurityGroups().equals(that.getSecurityGroups()) : that.getSecurityGroups() != null)
            return false;
        if (getAuthorNamespace() != null ? !getAuthorNamespace().equals(that.getAuthorNamespace()) : that.getAuthorNamespace() != null)
            return false;
        if (getTestLocations() != null ? !getTestLocations().equals(that.getTestLocations()) : that.getTestLocations() != null)
            return false;
        if (getUserAccountColleges() != null ? !getUserAccountColleges().equals(that.getUserAccountColleges()) : that.getUserAccountColleges() != null)
            return false;
        return isDeleted() == that.isDeleted();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount)) return false;
        if (!super.equals(o)) return false;

        UserAccount that = (UserAccount) o;

        if (isEnabled() != that.isEnabled()) return false;
        if (getExpired() != that.getExpired()) return false;
        if (isAccountLocked() != that.isAccountLocked()) return false;
        if (getUserAccountId() != null ? !getUserAccountId().equals(that.getUserAccountId()) : that.getUserAccountId() != null)
            return false;
        if (getUsername() != null ? !getUsername().equals(that.getUsername()) : that.getUsername() != null)
            return false;
        if (getEmailAddress() != null ? !getEmailAddress().equals(that.getEmailAddress()) : that.getEmailAddress() != null)
            return false;
        if (getPassword() != null ? !getPassword().equals(that.getPassword()) : that.getPassword() != null)
            return false;
        if (getDisplayName() != null ? !getDisplayName().equals(that.getDisplayName()) : that.getDisplayName() != null)
            return false;
        if (getFirstName() != null ? !getFirstName().equals(that.getFirstName()) : that.getFirstName() != null)
            return false;
        if (getMiddleInitial() != null ? !getMiddleInitial().equals(that.getMiddleInitial()) : that.getMiddleInitial() != null)
            return false;
        if (getLastName() != null ? !getLastName().equals(that.getLastName()) : that.getLastName() != null)
            return false;
        if (getPhoneNumber() != null ? !getPhoneNumber().equals(that.getPhoneNumber()) : that.getPhoneNumber() != null)
            return false;
        if (getExtension() != null ? !getExtension().equals(that.getExtension()) : that.getExtension() != null)
            return false;
        if (getLastLoginDate() != null ? !getLastLoginDate().equals(that.getLastLoginDate()) : that.getLastLoginDate() != null)
            return false;
        if (getFailedLogins() != null ? !getFailedLogins().equals(that.getFailedLogins()) : that.getFailedLogins() != null)
            return false;
        if (isDeleted() != that.isDeleted()) return false;
        return true;
    }



    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getUserAccountId() != null ? getUserAccountId().hashCode() : 0);
        result = 31 * result + (getUsername() != null ? getUsername().hashCode() : 0);
        result = 31 * result + (getEmailAddress() != null ? getEmailAddress().hashCode() : 0);
        result = 31 * result + (getPassword() != null ? getPassword().hashCode() : 0);
        result = 31 * result + (getDisplayName() != null ? getDisplayName().hashCode() : 0);
        result = 31 * result + (getFirstName() != null ? getFirstName().hashCode() : 0);
        result = 31 * result + (getMiddleInitial() != null ? getMiddleInitial().hashCode() : 0);
        result = 31 * result + (getLastName() != null ? getLastName().hashCode() : 0);
        result = 31 * result + (getPhoneNumber() != null ? getPhoneNumber().hashCode() : 0);
        result = 31 * result + (getExtension() != null ? getExtension().hashCode() : 0);
        result = 31 * result + (isEnabled() ? 1 : 0);
        result = 31 * result + (getExpired() ? 1 : 0);
        result = 31 * result + (getLastLoginDate() != null ? getLastLoginDate().hashCode() : 0);
        result = 31 * result + (isAccountLocked() ? 1 : 0);
        result = 31 * result + (getFailedLogins() != null ? getFailedLogins().hashCode() : 0);
        result = 31 * result + (isDeleted() ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserAccount{" +
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
                ", expired=" + expired +
                ", lastLoginDate=" + lastLoginDate +
                ", accountLocked=" + accountLocked +
                ", failedLogins=" + failedLogins +
                ", securityGroups=" + securityGroups +
                ", authorNamespace=" + authorNamespace +
                ", testLocations=" + testLocations +
                ", userAccountColleges=" + userAccountColleges +
                ", colleges=" + getColleges() +
                ", deleted=" + deleted +
                '}';
    }
}
