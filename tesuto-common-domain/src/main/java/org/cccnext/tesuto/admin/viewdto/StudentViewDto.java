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
package org.cccnext.tesuto.admin.viewdto;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.domain.dto.ViewDto;

public class StudentViewDto implements ViewDto {

    private static final long serialVersionUID = 1L;

    public enum Status {
        STATUS_ASSOCIATED, STATUS_APPLIED, STATUS_ENROLLED
    };

    private String middleName;
    private String firstName;
    private String lastName;
    private String displayName;
    private String phone;
    private String email;
    private int age;

    private String cccid;

    private String cccId;

    private Map<String,Integer> collegeStatuses = new HashMap<String, Integer>();

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getCccid() {
        return cccid;
    }

    public void setCccid(String cccid) {
        this.cccid = cccid;
    }

    public String getCccId() {
        return cccId;
    }

    public void setCccId(String cccId) {
        this.cccId = cccId;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<String,Integer> getCollegeStatuses() {
        return collegeStatuses;
    }

    public void setCollegeStatuses(Map<String,Integer> collegeStatuses) {
        this.collegeStatuses = collegeStatuses;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object obj) {

        return EqualsBuilder.reflectionEquals(this, obj);
    }

    public void cleanFields() {
        if (getMiddleName() == null) {
            setMiddleName("");
        }

        if (getFirstName() == null) {
            setFirstName("");
        }

        if (getPhone() == null) {
            setPhone("");
        }

        if (getEmail() == null) {
            setEmail("");
        }
        // to support current ui
        if (getCccid() != null) {
            setCccId(getCccid());
        } else {
            setCccid(getCccId());
        }

        if(collegeStatuses == null) {
            collegeStatuses = new HashMap<String,Integer>();
        }

        if (StringUtils.isBlank(getDisplayName())) {
            setDisplayName(getFirstName() + " " + getMiddleName() + " " + getLastName());
        }
    }

}
