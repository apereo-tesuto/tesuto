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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.domain.dto.Dto;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;

/**
 * Created by bruce on 5/24/16.
 */
public class CollegeDto implements Dto {

    private String cccId;
    private String districtCCCId;
    private String name;
    private String streetAddress1;
    private String streetAddress2;
    private String city;
    private String postalCode;
    private String url;
    private String eppnSuffix;
    private Date createdDate;
    private Date lastUpdatedDate;
    private Set<TestLocationDto> testLocations;
    private CollegeAttributeDto collegeAttributeDto;

    public String getCccId() {
        return cccId;
    }

    public void setCccId(String cccId) {
        this.cccId = cccId;
    }

    public String getDistrictCCCId() {
        return districtCCCId;
    }

    public void setDistrictCCCId(String districtCCCId) {
        this.districtCCCId = districtCCCId;
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

    public Set<TestLocationDto> getTestLocations() {
        return testLocations;
    }

    public void setTestLocations(Set<TestLocationDto> testLocations) {
        this.testLocations = testLocations;
    }

    public CollegeAttributeDto getCollegeAttributeDto() {
        return collegeAttributeDto;
    }

    public void setCollegeAttributeDto(CollegeAttributeDto collegeAttributeDto) {
        this.collegeAttributeDto = collegeAttributeDto;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, Arrays.asList("district", "userAccountCollegeSet"));
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Arrays.asList("district", "userAccountCollegeSet"));
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
