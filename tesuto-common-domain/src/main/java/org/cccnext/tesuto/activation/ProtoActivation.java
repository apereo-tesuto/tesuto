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

/*
  This class is for input to the Activation POST endpoint. The main
  reason it exists is so that swagger can correctly document the
  expected input fields.
*/

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.cccnext.tesuto.content.model.DeliveryType;
import org.cccnext.tesuto.domain.dto.ScopedIdentifierDto;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProtoActivation {

    private String activationId;
    private String userId;
    private ScopedIdentifierDto assessmentScopedIdentifier;
    private String locationId;
    private Date startDate;
    private Date endDate;
    private Map<String, String> attributes = new HashMap<>();
    private DeliveryType deliveryType;
    private boolean bulk = false;

    public String getActivationId() {
        return activationId;
    }

    public void setActivationId(String activationId) {
        this.activationId = activationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ScopedIdentifierDto getAssessmentScopedIdentifier() {
        return assessmentScopedIdentifier;
    }

    public void setAssessmentScopedIdentifier(ScopedIdentifierDto assessmentScopedIdentifier) {
        this.assessmentScopedIdentifier = assessmentScopedIdentifier;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

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
    public boolean equals(Object o) { return EqualsBuilder.reflectionEquals(this, o); }

    @Override
    public int hashCode() { return HashCodeBuilder.reflectionHashCode(this); }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
