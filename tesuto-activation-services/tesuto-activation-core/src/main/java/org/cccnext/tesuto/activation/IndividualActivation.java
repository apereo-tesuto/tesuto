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

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.content.model.DeliveryType;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class IndividualActivation extends Activation {

    private String locationId;
    private Date startDate;
    private Date endDate;
    private DeliveryType deliveryType;
    private  boolean bulk;


    @JsonIgnore
    public boolean isAlmostWellFormed() {
        return getUserId() != null && getAssessmentScopedIdentifier() != null && getLocationId() != null && getStartDate() != null && getEndDate() != null
                && !getEndDate().before(startDate) && getAttributes() != null && getStatusChangeHistory() != null
                && getAssessmentSessionIds() != null;
    }

    @JsonIgnore
    public boolean isWellFormed() {
        return getActivationId() != null && isAlmostWellFormed();
    }

    @Override
    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    @Override
    public Date getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Override
    public Date getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    public boolean isBulk() {
        return bulk;
    }

    public void setBulk(boolean bulk) {
        this.bulk = bulk;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals( o, this, "statusChangeHistory");
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
