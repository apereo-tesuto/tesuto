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
package org.cccnext.tesuto.delivery.view;

import java.util.List;
import java.util.Map;

/**
 * Created by bruce on 11/18/15.
 */
public class ItemSessionViewDto {

    private String itemSessionId;
    private String language;
    private Boolean allowSkipping;
    private Boolean validateResponses;
    private List<ItemSessionResponseViewDto> responses;
    private AssessmentItemViewDto assessmentItem;
    private int itemSessionIndex;

    public String getItemSessionId() {
        return itemSessionId;
    }

    public void setItemSessionId(String itemSessionId) {
        this.itemSessionId = itemSessionId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isAllowSkipping() {
        return allowSkipping;
    }

    public void setAllowSkipping(boolean allowSkipping) {
        this.allowSkipping = allowSkipping;
    }

    public Boolean getAllowSkipping() {
        return allowSkipping;
    }

    public void setAllowSkipping(Boolean allowSkipping) {
        this.allowSkipping = allowSkipping;
    }

    public Boolean getValidateResponses() {
        return validateResponses;
    }

    public void setValidateResponses(Boolean validateResponses) {
        this.validateResponses = validateResponses;
    }

    public List<ItemSessionResponseViewDto> getResponses() {
        return responses;
    }

    public void setResponses(List<ItemSessionResponseViewDto> responses) {
        this.responses = responses;
    }

    public AssessmentItemViewDto getAssessmentItem() {
        return assessmentItem;
    }

    public void setAssessmentItem(AssessmentItemViewDto assessmentItem) {
        this.assessmentItem = assessmentItem;
    }

    public int getItemSessionIndex() {

        return itemSessionIndex;
    }

    public void setItemSessionIndex(int itemSessionIndex) {
        this.itemSessionIndex = itemSessionIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ItemSessionViewDto))
            return false;

        ItemSessionViewDto that = (ItemSessionViewDto) o;

        if (getItemSessionIndex() != that.getItemSessionIndex())
            return false;
        if (getItemSessionId() != null ? !getItemSessionId().equals(that.getItemSessionId())
                : that.getItemSessionId() != null)
            return false;
        if (getLanguage() != null ? !getLanguage().equals(that.getLanguage()) : that.getLanguage() != null)
            return false;
        if (getAllowSkipping() != null ? !getAllowSkipping().equals(that.getAllowSkipping())
                : that.getAllowSkipping() != null)
            return false;
        if (getValidateResponses() != null ? !getValidateResponses().equals(that.getValidateResponses())
                : that.getValidateResponses() != null)
            return false;
        if (responses != null ? !responses.equals(that.responses) : that.responses != null)
            return false;
        return getAssessmentItem() != null ? getAssessmentItem().equals(that.getAssessmentItem())
                : that.getAssessmentItem() == null;

    }

    @Override
    public int hashCode() {
        int result = getItemSessionId() != null ? getItemSessionId().hashCode() : 0;
        result = 31 * result + (getLanguage() != null ? getLanguage().hashCode() : 0);
        result = 31 * result + (getAllowSkipping() != null ? getAllowSkipping().hashCode() : 0);
        result = 31 * result + (getValidateResponses() != null ? getValidateResponses().hashCode() : 0);
        result = 31 * result + (responses != null ? responses.hashCode() : 0);
        result = 31 * result + (getAssessmentItem() != null ? getAssessmentItem().hashCode() : 0);
        result = 31 * result + getItemSessionIndex();
        return result;
    }

    @Override
    public String toString() {
        return "ItemSessionViewDto{" + "itemSessionId='" + itemSessionId + '\'' + ", language='" + language + '\''
                + ", allowSkipping=" + allowSkipping + ", validateResponses=" + validateResponses + ", responses="
                + responses + ", assessmentItem=" + assessmentItem + ", itemSessionIndex=" + itemSessionIndex + '}';
    }
}
