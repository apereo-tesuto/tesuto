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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(schema="public", name = "security_group_api", indexes = {@Index(name = "group_name_idx", columnList = "group_name")})
@NamedNativeQueries({
        @NamedNativeQuery(name = "SecurityGroupApi.removeSecurityPermission",
                query = "delete from security_group_security_permission sgsp " +
                        "where sgsp.security_group_id = ?1 " +
                        "and sgsp.security_permission_id = ?2"),
        @NamedNativeQuery(name = "SecurityGroupApi.addSecurityPermission",
        query = "insert into security_group_security_permission " +
                "(security_group_id, security_permission_id) " +
                "values (?1, ?2)")
})
public class SecurityGroupApi extends AbstractEntity {
    private static final long serialVersionUID = 1l;

    @Id
    @GeneratedValue
    @Column(name = "security_group_api_id")
    private Integer securityGroupApiId;

    @Size(max = 255)
    @Column(name = "group_name", length = 255)
    private String groupName;

    @Size(max = 255)
    @Column(name = "description", length = 255)
    private String description;

    @ManyToMany
    @JoinTable(schema="public", name = "security_group_api_security_permission", joinColumns = {
            @JoinColumn(name = "security_group_api_id") }, inverseJoinColumns = {
                    @JoinColumn(name = "security_permission_id") })
    private Set<SecurityPermission> securityPermissions = new HashSet<SecurityPermission>();

    public Integer getSecurityGroupApiId() {
        return securityGroupApiId;
    }

    public void setSecurityGroupApiId(Integer securityGroupApiId) {
        this.securityGroupApiId = securityGroupApiId;
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

    public Set<SecurityPermission> getPermissions() {
        return securityPermissions;
    }

    public void setPermissions(Set<SecurityPermission> permissions) {
        this.securityPermissions = permissions;
    }

    public void addPermission(SecurityPermission permission) {
        this.getPermissions().add(permission);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((groupName == null) ? 0 : groupName.hashCode());
        result = prime * result + ((securityGroupApiId == null) ? 0 : securityGroupApiId.hashCode());
        result = prime * result + ((securityPermissions == null) ? 0 : securityPermissions.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SecurityGroupApi other = (SecurityGroupApi) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (groupName == null) {
            if (other.groupName != null)
                return false;
        } else if (!groupName.equals(other.groupName))
            return false;
        if (securityGroupApiId == null) {
            if (other.securityGroupApiId != null)
                return false;
        } else if (!securityGroupApiId.equals(other.securityGroupApiId))
            return false;
        if (securityPermissions == null) {
            if (other.securityPermissions != null)
                return false;
        } else if (!securityPermissions.equals(other.securityPermissions))
            return false;
        return true;
    }

}
