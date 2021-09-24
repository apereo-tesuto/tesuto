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
package org.cccnext.tesuto.delivery.service;

import org.apache.commons.collections.CollectionUtils;

import java.util.Date;
import java.util.List;

public class DeliverySearchParameters {
    public enum ValidParameters {
        VALID, INVALID, EMPTY
    }

    Date startDateLowerBound;
    Date startDateUpperBound;
    Date completionDateLowerBound;
    Date completionDateUpperBound;
    List<String> contentIds;
    List<String> contentIdentifiers;
    List<String> userIds;
    List<String> ids;

    Integer limit = null;

    Integer skip = null;

    List<String> fields;

    ValidParameters hasValidParameters() {
        if (contentIds != null) {
            if (contentIds.isEmpty()) {
                return ValidParameters.INVALID;
            }
            return ValidParameters.VALID;
        }

        if (contentIdentifiers != null) {
            if (contentIdentifiers.isEmpty()) {
                return ValidParameters.INVALID;
            }
            return ValidParameters.VALID;
        }

        if (userIds != null) {
            if (userIds.isEmpty()) {
                return ValidParameters.INVALID;
            }
            return ValidParameters.VALID;
        }

        if (ids != null) {
            if (ids.isEmpty()) {
                return ValidParameters.INVALID;
            }
            return ValidParameters.VALID;
        }

        if (startDateLowerBound != null) {
            return ValidParameters.VALID;
        }
        if (startDateUpperBound != null) {
            return ValidParameters.VALID;
        }

        if (completionDateLowerBound != null) {
            return ValidParameters.VALID;
        }

        if (completionDateUpperBound != null) {
            return ValidParameters.VALID;
        }

        return ValidParameters.EMPTY;
    }

    public boolean isUsingIndexedSearch(){
        if(CollectionUtils.isEmpty(this.ids)
                && CollectionUtils.isEmpty(this.userIds)
                && CollectionUtils.isEmpty(this.contentIds)
                && CollectionUtils.isEmpty(this.contentIdentifiers)){
            return false;
        }
        return true;
    }

    public Date getStartDateLowerBound() {
        return startDateLowerBound;
    }

    public void setStartDateLowerBound(Date startDateLowerBound) {
        this.startDateLowerBound = startDateLowerBound;
    }

    public Date getStartDateUpperBound() {
        return startDateUpperBound;
    }

    public void setStartDateUpperBound(Date startDateUpperBound) {
        this.startDateUpperBound = startDateUpperBound;
    }

    public Date getCompletionDateLowerBound() {
        return completionDateLowerBound;
    }

    public void setCompletionDateLowerBound(Date completionDateLowerBound) {
        this.completionDateLowerBound = completionDateLowerBound;
    }

    public Date getCompletionDateUpperBound() {
        return completionDateUpperBound;
    }

    public void setCompletionDateUpperBound(Date completionDateUpperBound) {
        this.completionDateUpperBound = completionDateUpperBound;
    }

    public List<String> getContentIds() {
        return contentIds;
    }

    public void setContentIds(List<String> contentIds) {
        this.contentIds = contentIds;
    }

    public List<String> getContentIdentifiers() {
        return contentIdentifiers;
    }

    public void setContentIdentifiers(List<String> contentIdentifiers) {
        this.contentIdentifiers = contentIdentifiers;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getSkip() {
        return skip;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

}
