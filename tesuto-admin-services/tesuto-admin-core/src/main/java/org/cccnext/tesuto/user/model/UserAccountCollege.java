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

import org.cccnext.tesuto.admin.model.College;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by bruce on 6/15/16.
 */
@Entity
@Table(schema="public",name="user_account_college")
@IdClass(UserAccountCollegeId.class)
public class UserAccountCollege {

    @Id
    @Column(name="user_account_id", nullable = false, insertable = false, updatable = false)
    private String userAccountId;

    @Id
    @Column(name="college_ccc_id", nullable = false, insertable = false, updatable = false)
    private String collegeId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_account_id", referencedColumnName = "user_account_id", nullable = false, insertable = false, updatable = false)
    private UserAccount userAccount;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "college_ccc_id", referencedColumnName = "ccc_id", nullable = false, insertable = false, updatable = false)
    private College college;

    public UserAccountCollege() {
    }

    public UserAccountCollege(String userAccountId, String collegeId) {
        this.userAccountId = userAccountId;
        this.collegeId = collegeId;
    }

    public UserAccountCollege(UserAccount userAccount, College college) {
        this.userAccount = userAccount;
        this.userAccountId = userAccount.getUserAccountId();
        this.college = college;
        this.collegeId = college.getCccId();
    }

    public String getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(String userAccountId) {
        this.userAccountId = userAccountId;
    }

    public String getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(String collegeId) {
        this.collegeId = collegeId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public College getCollege() {
        return college;
    }

    public void setCollege(College college) {
        this.college = college;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccountCollege)) return false;

        UserAccountCollege that = (UserAccountCollege) o;

        if (getUserAccountId() != null ? !getUserAccountId().equals(that.getUserAccountId()) : that.getUserAccountId() != null)
            return false;
        return getCollegeId() != null ? getCollegeId().equals(that.getCollegeId()) : that.getCollegeId() == null;

    }

    @Override
    public int hashCode() {
        int result = getUserAccountId() != null ? getUserAccountId().hashCode() : 0;
        result = 31 * result + (getCollegeId() != null ? getCollegeId().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserAccountCollege{" +
                "userAccountId='" + userAccountId + '\'' +
                ", collegeId='" + collegeId + '\'' +
                '}';
    }
}
