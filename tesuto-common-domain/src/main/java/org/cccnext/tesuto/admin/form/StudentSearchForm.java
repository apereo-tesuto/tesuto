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
package org.cccnext.tesuto.admin.form;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Richard Scott Smith <scott.smith@isostech.com>
 */
public class StudentSearchForm implements Serializable {
    private String middleName;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Integer age;
    
    Set<String> misCodes;

    private List<String> cccids; // Included in cache for 2 hrs by default.

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

    public List<String> getCccids() {
        return cccids;
    }

    @JsonIgnore
    public List<String> getNotNullCccids() {
        if (CollectionUtils.isNotEmpty(cccids)) {
            List<String> uppercase = new ArrayList<>();
            for(String cccid: cccids){
                if(StringUtils.isNotBlank(cccid)) {
                    uppercase.add(cccid.toUpperCase());
                }
            }
            return uppercase;
        }
        return cccids;
    }
    

    public void setCccids(List<String> cccids) {
        this.cccids = cccids;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Set<String> getMisCodes() {
        return misCodes;
    }

    public void setMisCodes(Set<String> misCodes) {
        this.misCodes = misCodes;
    }

    @JsonIgnore
    public boolean isValidForSearch() {
        boolean isValid = false;
        if (CollectionUtils.isNotEmpty(getNotNullCccids())) {
            isValid = true;
        }

        if (StringUtils.isNotBlank(getLastName())) {
            isValid = true;
        }
        return isValid;
    }

    @JsonIgnore
    public boolean isCccidsOnly() {
        boolean onlyCccids = true;
        if (isValidForSearch() && (StringUtils.isNotBlank(getLastName()))) {
            onlyCccids = false;
        }
        return onlyCccids;
    }
    
    @JsonIgnore
    public StudentSearchForm cleanForm() {
        if(this.age != null && this.age <= 0) {
            this.age = null;
        }
        if(StringUtils.isBlank(this.email)) {
            this.email = null;
        }
        
        if(StringUtils.isBlank(this.firstName)) {
            this.firstName = null;
        }
        
        if(StringUtils.isBlank(this.middleName)) {
            this.middleName = null;
        }
        
        if(StringUtils.isBlank(this.lastName)) {
            this.lastName = null;
        }
        
        if(StringUtils.isBlank(this.phone)) {
            this.phone = null;
        }
        cccids = getNotNullCccids();
        if(CollectionUtils.isEmpty(this.cccids)) {
            this.cccids = null;
        }
        return this;
        
    }
}
