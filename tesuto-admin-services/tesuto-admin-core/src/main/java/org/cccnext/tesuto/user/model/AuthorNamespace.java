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

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@Entity
@Table(schema="public", name = "author_namespace", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "namespace_id", "namespace" }) })
public class AuthorNamespace {

    @Id
    // @GeneratedValue(strategy = GenerationType.IDENTITY) // For MySQL
    @GenericGenerator(name = "hibernate-generator", strategy = "increment") // For
                                                                            // Postgres
    @GeneratedValue(generator = "hibernate-generator") // For Postgres
    @Basic(optional = false)
    @Column(name = "namespace_id", nullable = false)
    private Long namespaceId;
    @Basic(optional = false)
    @Size(max = 255)
    @Column(name = "namespace", nullable = false)
    private String namespace;

	@Column(name = "created_on_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOnDate;
	@Column(name = "last_updated_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdatedDate;

    @OneToMany(cascade = CascadeType.DETACH, mappedBy = "authorNamespace")
    private List<UserAccount> userAccountList;

    public Long getNamespaceId() {
        return namespaceId;
    }

    public void setNamespaceId(Long namespaceId) {
        this.namespaceId = namespaceId;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public List<UserAccount> getUserAccountList() {
        return userAccountList;
    }

    public void setUserAccountList(List<UserAccount> userAccountList) {
        this.userAccountList = userAccountList;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
        result = prime * result + ((namespaceId == null) ? 0 : namespaceId.hashCode());
        result = prime * result + ((userAccountList == null) ? 0 : userAccountList.hashCode());
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
        AuthorNamespace other = (AuthorNamespace) obj;
        if (namespace == null) {
            if (other.namespace != null)
                return false;
        } else if (!namespace.equals(other.namespace))
            return false;
        if (namespaceId == null) {
            if (other.namespaceId != null)
                return false;
        } else if (!namespaceId.equals(other.namespaceId))
            return false;

        return true;
    }

	public AuthorNamespace() {
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
}
