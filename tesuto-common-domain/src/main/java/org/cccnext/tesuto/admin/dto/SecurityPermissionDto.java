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

import java.util.Date;

import org.cccnext.tesuto.domain.dto.Dto;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class SecurityPermissionDto implements GrantedAuthority, Dto {
    private static final long serialVersionUID = 1l;

    private String securityPermissionId;

    private String description;

    private Date createdOnDate;

    private Date lastUpdatedDate;

    public String getSecurityPermissionId() {
        return securityPermissionId;
    }

    public void setSecurityPermissionId(String securityPermissionId) {
        this.securityPermissionId = securityPermissionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public String toString() {
        return "SecurityPermissionDto{" +
                "securityPermissionId='" + securityPermissionId + '\'' +
                ", description='" + description + '\'' +
                ", createdOnDate=" + createdOnDate +
                ", lastUpdatedDate=" + lastUpdatedDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecurityPermissionDto)) return false;

        SecurityPermissionDto that = (SecurityPermissionDto) o;

        if (securityPermissionId != null ? !securityPermissionId.equals(that.securityPermissionId) : that.securityPermissionId != null)
            return false;
        return description != null ? description.equals(that.description) : that.description == null;

    }

    @Override
    public int hashCode() {
        int result = securityPermissionId != null ? securityPermissionId.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @JsonIgnore
	@Override
	public String getAuthority() {
		return securityPermissionId;
	}
}
