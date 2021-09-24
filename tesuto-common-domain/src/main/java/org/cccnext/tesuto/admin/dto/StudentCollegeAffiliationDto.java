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

import java.util.Arrays;
import java.util.Date;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * @author Bill Smith (wsmith@unicon.net)
 */
public class StudentCollegeAffiliationDto {

    private String eppn;
    private String studentCccId;
    private String misCode;
    private Date loggedDate;
    private String authSource;

    public String getEppn() {
        return eppn;
    }

    public void setEppn(String eppn) {
        this.eppn = eppn;
    }

    public String getStudentCccId() {
        return studentCccId;
    }

    public void setStudentCccId(String studentCccId) {
        this.studentCccId = studentCccId;
    }

    public String getMisCode() {
        return misCode;
    }

    public void setMisCode(String misCode) {
        this.misCode = misCode;
    }

    public Date getLoggedDate() {
        return loggedDate;
    }

    public void setLoggedDate(Date loggedDate) {
        this.loggedDate = loggedDate;
    }

    public String getAuthSource() {
        return authSource;
    }

    public void setAuthSource(String authSource) {
        this.authSource = authSource;
    }
	
    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, Arrays.asList("loggedDate"));
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, Arrays.asList("loggedDate"));
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
