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

import org.apache.commons.lang.builder.ToStringBuilder;
import org.cccnext.tesuto.activation.model.Activation;
import org.cccnext.tesuto.content.model.DeliveryType;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Parameter Object for search queries
 */
public class SearchParameters implements Cloneable {

    // Null parameters are to be ignored

    private Set<String> userIds;
    private Set<String> locationIds;
    private Set<String> creatorIds;
    private Set<String> currentAssessmentSessionIds;
    private boolean includeCanceled;
    private Date minStartDate;
    private Date maxStartDate;
    private Date minEndDate;
    private Date maxEndDate;
    private Date minCreateDate;
    private Date maxCreateDate;
    private Date minStatusUpdateDate;
    private Date maxStatusUpdateDate;
    private Activation.Status currentStatus;
    private DeliveryType deliveryType;

    public boolean isValid() {
        return userIds != null || locationIds != null || creatorIds != null || currentStatus != null
                || deliveryType == DeliveryType.PAPER ||currentAssessmentSessionIds != null;
    }

    public Set<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<String> userIds) {
        this.userIds = userIds;
    }

    public void addUserId(String userId) {
        if (userIds == null) {
            userIds = new HashSet<>();
        }
        userIds.add(userId);
    }

    public Set<String> getLocationIds() {
        return locationIds;
    }

    public void setLocationIds(Set<String> locationIds) {
        this.locationIds = locationIds;
    }

    public Set<String> getCreatorIds() {
        return creatorIds;
    }

    public void setCreatorIds(Set<String> creatorIds) {
        this.creatorIds = creatorIds;
    }

    public Set<String> getCurrentAssessmentSessionIds() {
        return currentAssessmentSessionIds;
    }

    public void setCurrentAssessmentSessionIds(Set<String> currentAssessmentSessionIds) {
        this.currentAssessmentSessionIds = currentAssessmentSessionIds;
    }

    public boolean isIncludeCanceled() {
        return includeCanceled;
    }

    public void setIncludeCanceled(boolean includeCanceled) {
        this.includeCanceled = includeCanceled;
    }

    public Date getMinStartDate() {
        return minStartDate;
    }

    public void setMinStartDate(Date minStartDate) {
        this.minStartDate = minStartDate;
    }

    public Date getMaxStartDate() {
        return maxStartDate;
    }

    public void setMaxStartDate(Date maxStartDate) {
        this.maxStartDate = maxStartDate;
    }

    public Date getMinEndDate() {
        return minEndDate;
    }

    public void setMinEndDate(Date minEndDate) {
        this.minEndDate = minEndDate;
    }

    public Date getMaxEndDate() {
        return maxEndDate;
    }

    public void setMaxEndDate(Date maxEndDate) {
        this.maxEndDate = maxEndDate;
    }

    public Date getMinCreateDate() {
        return minCreateDate;
    }

    public void setMinCreateDate(Date minCreateDate) {
        this.minCreateDate = minCreateDate;
    }

    public Date getMaxCreateDate() {
        return maxCreateDate;
    }

    public void setMaxCreateDate(Date maxCreateDate) {
        this.maxCreateDate = maxCreateDate;
    }

    public Activation.Status getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Activation.Status currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Date getMinStatusUpdateDate() {
        return minStatusUpdateDate;
    }

    public void setMinStatusUpdateDate(Date minStatusUpdateDate) {
        this.minStatusUpdateDate = minStatusUpdateDate;
    }

    public Date getMaxStatusUpdateDate() {
        return maxStatusUpdateDate;
    }

    public void setMaxStatusUpdateDate(Date maxStatusUpdateDate) {
        this.maxStatusUpdateDate = maxStatusUpdateDate;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
