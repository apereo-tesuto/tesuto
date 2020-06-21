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
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class SecurityGroupDto implements Dto {
    private static final long serialVersionUID = 1l;

    private Integer securityGroupId;

    private String groupName;

    private String description;

    @JsonIgnore
    private Date createdOnDate;

    @JsonIgnore
    private Date lastUpdatedDate;

    // Leaving this out for now because I don't want to see performance impacted
    // needlessly.
    // private Set<UserAccount> userAccounts = new HashSet<UserAccount>();

    private Set<SecurityPermissionDto> securityPermissionDtos = new HashSet<SecurityPermissionDto>();

    public Integer getSecurityGroupId() {
        return securityGroupId;
    }

    public void setSecurityGroupId(Integer securityGroupId) {
        this.securityGroupId = securityGroupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<SecurityPermissionDto> getSecurityPermissionDtos() {
        return securityPermissionDtos;
    }

    public void setSecurityPermissionDtos(Set<SecurityPermissionDto> securityPermissionDtos) {
        this.securityPermissionDtos = securityPermissionDtos;
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

    @JsonIgnore
    public Set<GrantedAuthority> getGrantedAuthorities() {
        Set<GrantedAuthority> grantedAuthoritySet = new HashSet<>();
        for (SecurityPermissionDto securityPermissionDto : securityPermissionDtos) {
            grantedAuthoritySet.add(new SimpleGrantedAuthority(securityPermissionDto.getSecurityPermissionId()));
        }
        return grantedAuthoritySet;
    }

    @Override
    public String toString() {
        return "SecurityGroupDto{" +
                "securityGroupId=" + securityGroupId +
                ", groupName='" + groupName + '\'' +
                ", description='" + description + '\'' +
                ", createdOnDate=" + createdOnDate +
                ", lastUpdatedDate=" + lastUpdatedDate +
                ", securityPermissionDtos=" + securityPermissionDtos +
                '}';
    }

    /**
     * These are efficient methods which have the potential to be called a lot. They should not be replaced with
     * reflection type implementations.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SecurityGroupDto that = (SecurityGroupDto) o;

        if (!securityGroupId.equals(that.securityGroupId)) return false;
        if (!groupName.equals(that.groupName)) return false;
        return description.equals(that.description);
    }

	/**
     * These are efficient methods which have the potential to be called a lot. They should not be replaced with
     * reflection type implementations.
     *
     * @return
     */
    @Override
    public int hashCode() {
        int result = securityGroupId.hashCode();
        result = 31 * result + groupName.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }
}
