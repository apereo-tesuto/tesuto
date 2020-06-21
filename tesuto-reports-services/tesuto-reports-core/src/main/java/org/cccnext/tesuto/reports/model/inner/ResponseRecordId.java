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
package org.cccnext.tesuto.reports.model.inner;

import java.io.Serializable;

public class ResponseRecordId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String responseAttemptId;

    private String itemRefIdentifier;

    private Integer attemptIndex;
    
    private String itemId;

    public String getResponseAttemptId() {
        return responseAttemptId;
    }

    public void setResponseAttemptId(String responseAttemptId) {
        this.responseAttemptId = responseAttemptId;
    }


    public String getItemRefIdentifier() {
        return itemRefIdentifier;
    }

    public void setItemRefIdentifier(String itemRefIdentifier) {
        this.itemRefIdentifier = itemRefIdentifier;
    }
    public Integer getAttemptIndex() {
        return attemptIndex;
    }

    public void setAttemptIndex(Integer attemptIndex) {
        this.attemptIndex = attemptIndex;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

}
