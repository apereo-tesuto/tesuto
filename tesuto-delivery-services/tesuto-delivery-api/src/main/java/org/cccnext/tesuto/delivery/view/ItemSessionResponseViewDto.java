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

/**
 * Created by bruce on 11/19/15.
 */
public class ItemSessionResponseViewDto {

    private String itemSessionId;
    private String responseIdentifier;
    private List<String> values;

    public String getItemSessionId() {
        return itemSessionId;
    }

    public void setItemSessionId(String itemSessionId) {
        this.itemSessionId = itemSessionId;
    }

    public String getResponseIdentifier() {
        return responseIdentifier;
    }

    public void setResponseIdentifier(String responseIdentifier) {
        this.responseIdentifier = responseIdentifier;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ItemSessionResponseViewDto))
            return false;

        ItemSessionResponseViewDto that = (ItemSessionResponseViewDto) o;

        if (getItemSessionId() != null ? !getItemSessionId().equals(that.getItemSessionId())
                : that.getItemSessionId() != null)
            return false;
        if (getResponseIdentifier() != null ? !getResponseIdentifier().equals(that.getResponseIdentifier())
                : that.getResponseIdentifier() != null)
            return false;
        return !(getValues() != null ? !getValues().equals(that.getValues()) : that.getValues() != null);

    }

    @Override
    public int hashCode() {
        int result = getItemSessionId() != null ? getItemSessionId().hashCode() : 0;
        result = 31 * result + (getResponseIdentifier() != null ? getResponseIdentifier().hashCode() : 0);
        result = 31 * result + (getValues() != null ? getValues().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ItemSessionResponse{" + "itemSessionId='" + itemSessionId + '\'' + ", responseIdentifier='"
                + responseIdentifier + '\'' + ", values=" + values + '}';
    }
}
