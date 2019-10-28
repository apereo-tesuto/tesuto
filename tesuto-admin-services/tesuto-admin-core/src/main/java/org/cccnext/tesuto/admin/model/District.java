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
package org.cccnext.tesuto.admin.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.cccnext.tesuto.user.model.UserAccount;

/**
 *
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
@Entity
@Table(schema="public",name = "district")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = "District.findAll", query = "SELECT d FROM District d"),
	@NamedQuery(name = "District.findByCccId", query = "SELECT d FROM District d WHERE d.cccId = :cccId"),
	@NamedQuery(name = "District.findByName", query = "SELECT d FROM District d WHERE d.name = :name"),
	@NamedQuery(name = "District.findByStreetAddress", query = "SELECT d FROM District d WHERE d.streetAddress = :streetAddress"),
	@NamedQuery(name = "District.findByCity", query = "SELECT d FROM District d WHERE d.city = :city"),
	@NamedQuery(name = "District.findByPostalCode", query = "SELECT d FROM District d WHERE d.postalCode = :postalCode"),
	@NamedQuery(name = "District.findByUrl", query = "SELECT d FROM District d WHERE d.url = :url"),
	@NamedQuery(name = "District.findByCreatedDate", query = "SELECT d FROM District d WHERE d.createdDate = :createdDate"),
	@NamedQuery(name = "District.findByLastUpdatedDate", query = "SELECT d FROM District d WHERE d.lastUpdatedDate = :lastUpdatedDate")})
public class District implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "ccc_id", nullable = false, length = 100)
	private String cccId;
	@Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "name", nullable = false, length = 100)
	private String name;
	@Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "street_address", nullable = false, length = 100)
	private String streetAddress;
	@Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "city", nullable = false, length = 64)
	private String city;
	@Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 10)
    @Column(name = "postal_code", nullable = false, length = 10)
	private String postalCode;
	@Size(max = 100)
    @Column(name = "url", length = 100)
	private String url;
	@Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
	private Date createdDate;
	@Column(name = "last_updated_date")
    @Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdatedDate;
	@JoinTable(schema="public", name = "user_account_district", joinColumns = {
    	@JoinColumn(name = "ccc_id", referencedColumnName = "ccc_id", nullable = false)}, inverseJoinColumns = {
    	@JoinColumn(name = "user_account_id", referencedColumnName = "user_account_id", nullable = false)})
    @ManyToMany
	private Set<UserAccount> userAccountSet;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "district")
	private Set<College> collegeSet;

	public District() {
	}

	public District(String cccId) {
		this.cccId = cccId;
	}

	public District(String cccId, String name, String streetAddress, String city, String postalCode) {
		this.cccId = cccId;
		this.name = name;
		this.streetAddress = streetAddress;
		this.city = city;
		this.postalCode = postalCode;
	}

	public String getCccId() {
		return cccId;
	}

	public void setCccId(String cccId) {
		this.cccId = cccId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastUpdatedDate() {
		return lastUpdatedDate;
	}

	public void setLastUpdatedDate(Date lastUpdatedDate) {
		this.lastUpdatedDate = lastUpdatedDate;
	}

	@XmlTransient
	public Set<UserAccount> getUserAccountSet() {
		return userAccountSet;
	}

	public void setUserAccountSet(Set<UserAccount> userAccountSet) {
		this.userAccountSet = userAccountSet;
	}

	@XmlTransient
	public Set<College> getCollegeSet() {
		return collegeSet;
	}

	public void setCollegeSet(Set<College> collegeSet) {
		this.collegeSet = collegeSet;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		District district = (District) o;

		if (cccId != null ? !cccId.equals(district.cccId) : district.cccId != null) return false;
		if (name != null ? !name.equals(district.name) : district.name != null) return false;
		if (streetAddress != null ? !streetAddress.equals(district.streetAddress) : district.streetAddress != null)
			return false;
		if (city != null ? !city.equals(district.city) : district.city != null) return false;
		if (postalCode != null ? !postalCode.equals(district.postalCode) : district.postalCode != null) return false;
		if (url != null ? !url.equals(district.url) : district.url != null) return false;
		if (createdDate != null ? !createdDate.equals(district.createdDate) : district.createdDate != null)
			return false;
		if (lastUpdatedDate != null ? !lastUpdatedDate.equals(district.lastUpdatedDate) : district.lastUpdatedDate != null)
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "org.cccnext.tesuto.admin.model.District[ cccId=" + cccId + " ]";
	}
	
	public int hashCode() {
		int result = cccId != null ? cccId.hashCode() : 0;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (streetAddress != null ? streetAddress.hashCode() : 0);
		result = 31 * result + (city != null ? city.hashCode() : 0);
		result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
		result = 31 * result + (url != null ? url.hashCode() : 0);
		result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
		result = 31 * result + (lastUpdatedDate != null ? lastUpdatedDate.hashCode() : 0);
		return result;
	}
}
