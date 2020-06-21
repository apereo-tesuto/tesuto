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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by bruce on 6/15/16.
 */
@Embeddable
public class UserAccountCollegeId implements Serializable {

    private static final long serialVersionUID = 1l;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 256)
    @Column(name = "user_account_id", nullable = false, length = 256)
    private String userAccountId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "college_ccc_id", nullable = false, length = 100)
    private String collegeId;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccountCollegeId)) return false;

        UserAccountCollegeId that = (UserAccountCollegeId) o;

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
        return "UserAccountCollegeId{" +
                "userAccountId='" + userAccountId + '\'' +
                ", collegeId='" + collegeId + '\'' +
                '}';
    }
}
