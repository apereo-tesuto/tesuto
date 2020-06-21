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
package org.cccnext.tesuto.web.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Entity
@Table(schema="public", name="student_college_affiliation")
@IdClass(StudentCollegeAffiliationId.class)
public class StudentCollegeAffiliation implements Serializable {

    private static final long serialVersionUID = 2l;

    @Id
    @Column(name = "eppn")
    private String eppn;

    @Id
    @Column(name = "student_ccc_id")
    private String studentCccId;

    @Id
    @Column(name = "mis_code")
    private String misCode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "logged_date")
    private Date loggedDate;

    @Column(name = "auth_source")
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StudentCollegeAffiliation that = (StudentCollegeAffiliation) o;

        if (!eppn.equals(that.eppn)) return false;
        if (!studentCccId.equals(that.studentCccId)) return false;
        if (!misCode.equals(that.misCode)) return false;
        return loggedDate.equals(that.loggedDate);

    }

    @Override
    public int hashCode() {
        int result = eppn.hashCode();
        result = 31 * result + studentCccId.hashCode();
        result = 31 * result + misCode.hashCode();
        result = 31 * result + loggedDate.hashCode();
        return result;
    }
}
