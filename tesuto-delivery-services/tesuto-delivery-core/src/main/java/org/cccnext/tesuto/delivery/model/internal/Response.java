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
package org.cccnext.tesuto.delivery.model.internal;

import java.io.Serializable;
import java.util.List;

/**
 * Created by bruce on 11/10/15.
 */
public class Response implements Serializable {

    private static final long serialVersionUID = 1;

    private String responseId;
    private List<String> values;

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
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
        if (!(o instanceof Response))
            return false;

        Response response = (Response) o;

        if (getResponseId() != null ? !getResponseId().equals(response.getResponseId())
                : response.getResponseId() != null)
            return false;
        return !(getValues() != null ? !getValues().equals(response.getValues()) : response.getValues() != null);

    }

    @Override
    public int hashCode() {
        int result = getResponseId() != null ? getResponseId().hashCode() : 0;
        result = 31 * result + (getValues() != null ? getValues().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Response{" + "responseId='" + responseId + '\'' + ", values=" + values + '}';
    }
}
