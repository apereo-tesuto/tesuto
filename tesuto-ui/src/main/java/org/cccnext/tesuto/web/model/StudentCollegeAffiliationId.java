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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author Bill Smith (wsmith@unicon.net)
 */
@Embeddable
public class StudentCollegeAffiliationId implements Serializable {

    private static final long serialVersionUID = 1l;

    @Basic
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "eppn", nullable = false, length = 255)
    private String eppn;

    @Basic
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "student_ccc_id", nullable = false, length = 100)
    private String studentCccId;

    @Basic
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "mis_code", nullable = false, length = 100)
    private String misCode;

    public StudentCollegeAffiliationId() {
    }

    public StudentCollegeAffiliationId(String eppn, String studentCccId, String misCode) {
        this.eppn = eppn;
        this.studentCccId = studentCccId;
        this.misCode = misCode;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StudentCollegeAffiliationId that = (StudentCollegeAffiliationId) o;

        if (!eppn.equals(that.eppn)) return false;
        if (!studentCccId.equals(that.studentCccId)) return false;
        return misCode.equals(that.misCode);
    }

    @Override
    public int hashCode() {
        int result = eppn.hashCode();
        result = 31 * result + studentCccId.hashCode();
        result = 31 * result + misCode.hashCode();
        return result;
    }
}
