/*******************************************************************************
 * Copyright Â© 2019 by California Community Colleges Chancellor's Office
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

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.cccnext.tesuto.domain.model.AbstractEntity;
import org.cccnext.tesuto.user.model.UserAccount;

@Entity
@Table(schema="public",name = "test_location")
//@Cache(region = "testLocation", usage = CacheConcurrencyStrategy.READ_WRITE)
public class TestLocation extends AbstractEntity {

    private static final long serialVersionUID = 4L;

    @Id
    @Basic(optional = false)

    @Column(name = "id", nullable = false, length = 256)
    String id;

    @Column(name = "name", nullable = false, length = 100)
    String name;

    @Column(name = "street_address_1", nullable = true, length = 100)
    String streetAddress1;

    @Column(name = "street_address_2", nullable = true, length = 100)
    String streetAddress2;

    @Column(name = "city", nullable = true, length = 64)
    String city;

    @Column(name = "postal_code", nullable = true, length = 10)
    String postalCode;

    @Column(name = "location_type", nullable = false, length = 20)
    String locationType;

    @Column(name = "location_status", nullable = false, length = 20)
    String locationStatus;

    @Column(name = "capacity", nullable = true)
    Integer capacity;

    @Column(name = "college_ccc_id")
    String collegeId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="college_ccc_id", insertable = false, updatable = false)
    College college;

    @Transient
    @JoinTable(schema="public", name = "user_account_test_location", joinColumns = {
            @JoinColumn(name = "test_location_id") }, inverseJoinColumns = { @JoinColumn(name = "user_account_id") })
    private Set<UserAccount> userAccounts;

    @Column(name = "enabled")
    boolean enabled;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }

    public String getLocationStatus() {
        return locationStatus;
    }

    public void setLocationStatus(String locationStatus) {
        this.locationStatus = locationStatus;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }
    

    public College getCollege() {
        return college;
    }

    public void setCollege(College college) {
        this.college = college;
    }


    public Set<UserAccount> getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(Set<UserAccount> userAccounts) {
        this.userAccounts = userAccounts;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, "college", "userAccounts");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "college", "userAccounts");
    }

    @Override
    public String toString() {
        return "TestLocation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", streetAddress1='" + streetAddress1 + '\'' +
                ", streetAddress2='" + streetAddress2 + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", locationType='" + locationType + '\'' +
                ", locationStatus='" + locationStatus + '\'' +
                ", capacity=" + capacity +
                ", collegeId='" + collegeId + '\'' +
                ", enabled='" + enabled + '\'' +
                '}';
    }
}