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

import org.cccnext.tesuto.domain.model.AbstractEntity;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(schema="public",name = "security_permission")
public class SecurityPermission extends AbstractEntity implements GrantedAuthority, Comparable<SecurityPermission> {
    private static final long serialVersionUID = 1l;

    @Id
    @Size(max = 255)
    @Column(name = "security_permission_id", length = 255)
    private String securityPermissionId;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

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

    @Override
    public String getAuthority() {
        return securityPermissionId;
    }

    @Override
    public int compareTo(SecurityPermission arg0) {
        return this.getSecurityPermissionId().compareTo(arg0.getSecurityPermissionId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        // Not calling super here because we don't need to include the lastUpdatedDate and createdOnDate from AbstractEntity.
        //if (!super.equals(o)) return false;

        SecurityPermission that = (SecurityPermission) o;

        if (!securityPermissionId.equals(that.securityPermissionId)) return false;
        return description != null ? description.equals(that.description) : that.description == null;

    }

    @Override
    public int hashCode() {
        // Not calling super here because we don't need to include the lastUpdatedDate and createdOnDate from AbstractEntity.
        //int result = super.hashCode();
        int result = 37;
        result = 31 * result + securityPermissionId.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SecurityPermission{" +
                "securityPermissionId='" + securityPermissionId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
