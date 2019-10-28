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
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by bruce on 5/24/16.
 */

@Entity
@Table(schema="public",name = "college")
@XmlRootElement
public class College implements Serializable {

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
    @Column(name = "street_address_1", nullable = false, length = 100)
    private String streetAddress1;

    @Size(max = 100)
    @Column(name = "street_address_2", length = 100)
    private String streetAddress2;

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

    @Size(max = 100)
    @Column(name = "eppn_suffix", length = 100)
    private String eppnSuffix;

    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name = "last_updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdatedDate;

    @JoinColumn(name = "district_ccc_id", referencedColumnName = "ccc_id", nullable = false)
    @ManyToOne(optional = false)
    private District district;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "college", fetch = FetchType.EAGER)
    private Set<TestLocation> testLocations;

    @JoinColumn(name = "ccc_id", referencedColumnName = "college_id", nullable = false)
    @OneToOne(cascade = CascadeType.ALL)
    private CollegeAttribute collegeAttribute;

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

    public String getStreetAddress1() {
        return streetAddress1;
    }

    public void setStreetAddress1(String streetAddress1) {
        this.streetAddress1 = streetAddress1;
    }

    public String getStreetAddress2() {
        return streetAddress2;
    }

    public void setStreetAddress2(String streetAddress2) {
        this.streetAddress2 = streetAddress2;
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

    public String getEppnSuffix() {
        return eppnSuffix;
    }

    public void setEppnSuffix(String eppnSuffix) {
        this.eppnSuffix = eppnSuffix;
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

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public Set<TestLocation> getTestLocations() {
        return testLocations;
    }

    public void setTestLocations(Set<TestLocation> testLocations) {
        this.testLocations = testLocations;
    }

    public CollegeAttribute getCollegeAttribute() {
        return collegeAttribute;
    }

    public void setCollegeAttribute(CollegeAttribute collegeAttribute) {
        this.collegeAttribute = collegeAttribute;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, Arrays.asList("district", "userAccountCollegeSet", "testLocations", "collegeAttribute"));
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Arrays.asList("district", "userAccountCollegeSet", "testLocations", "collegeAttribute"));
    }

    @Override
    public String toString() {
        return "College{" +
                "cccId='" + cccId + '\'' +
                ", name='" + name + '\'' +
                ", streetAddress1='" + streetAddress1 + '\'' +
                ", streetAddress2='" + streetAddress2 + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", url='" + url + '\'' +
                ", eppnSuffix='" + eppnSuffix + '\'' +
                ", createdDate=" + createdDate +
                ", lastUpdatedDate=" + lastUpdatedDate +
                '}';
    }
}
