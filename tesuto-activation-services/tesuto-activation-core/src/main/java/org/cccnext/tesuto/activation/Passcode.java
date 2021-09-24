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
package org.cccnext.tesuto.activation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Calendar;

/**
 * Created by bruce on 1/27/16.
 */
public class Passcode {

    public enum PasscodeType {
        PUBLIC, PRIVATE, REMOTE
    };

    private String value;
    private PasscodeType type;
    private String userId;
    private Calendar createDate;
    private Calendar expirationDate;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public PasscodeType getType() {
        return type;
    }

    public void setType(PasscodeType type) {
        this.type = type;
    }

    public boolean isPublic() {
        return type == PasscodeType.PUBLIC;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Calendar getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Calendar createDate) {
        this.createDate = createDate;
    }

    public Calendar getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Calendar expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, "expirationDate");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, "expirationDate");
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
