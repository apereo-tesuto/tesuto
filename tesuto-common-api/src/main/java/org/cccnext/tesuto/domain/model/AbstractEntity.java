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
package org.cccnext.tesuto.domain.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_updated_date")
    public Date lastUpdatedDate;

    private static final long serialVersionUID = 1l;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on_date")
    public Date createdOnDate;

    @PrePersist
    public void prePersist() {
        createdOnDate = Calendar.getInstance().getTime();
        lastUpdatedDate = Calendar.getInstance().getTime();
    }

    @PreUpdate
    public void preInsert() {
        lastUpdatedDate = Calendar.getInstance().getTime();
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Date getCreatedOnDate() {
        return createdOnDate;
    }

    public void setCreatedOnDate(Date createdOnDate) {
        this.createdOnDate = createdOnDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((createdOnDate == null) ? 0 : createdOnDate.hashCode());
        result = prime * result + ((lastUpdatedDate == null) ? 0 : lastUpdatedDate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractEntity other = (AbstractEntity) obj;
        if (createdOnDate == null) {
            if (other.createdOnDate != null)
                return false;
        } else if (!createdOnDate.equals(other.createdOnDate))
            return false;
        if (lastUpdatedDate == null) {
            if (other.lastUpdatedDate != null)
                return false;
        } else if (!lastUpdatedDate.equals(other.lastUpdatedDate))
            return false;
        return true;
    }

}
