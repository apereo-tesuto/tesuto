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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ItemSession implements Serializable {

    private static final long serialVersionUID = 1;

    private String itemSessionId;
    private String itemId;
    private String itemRefIdentifier;
    private int itemSessionIndex;
    private Boolean allowSkipping;
    private Boolean validateResponses;
    private boolean useForBranchRuleEvaluation;

    Map<String, Response> responses = new HashMap<>(); // responseId -> Response
    Map<String, Outcome> outcomes = new HashMap<>(); // outcomeIdentifier -> Outcome;

    public String getItemSessionId() {
        return itemSessionId;
    }

    public void setItemSessionId(String itemSessionId) {
        this.itemSessionId = itemSessionId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemRefIdentifier() {
        return itemRefIdentifier;
    }

    public void setItemRefIdentifier(String itemRefIdentifier) {
        this.itemRefIdentifier = itemRefIdentifier;
    }

    public int getItemSessionIndex() {
        return itemSessionIndex;
    }

    public void setItemSessionIndex(int itemSessionIndex) {
        this.itemSessionIndex = itemSessionIndex;
    }

    public Boolean getAllowSkipping() { return allowSkipping; }

    public boolean isAllowSkipping() { return allowSkipping == null || getAllowSkipping(); }

    public void setAllowSkipping(Boolean allowSkipping) {
        this.allowSkipping = allowSkipping;
    }

    public Boolean getValidateResponses() { return validateResponses; }

    public boolean isValidateResponses() { return validateResponses != null && validateResponses; }

    public void setValidateResponses(Boolean validateResponses) {
        this.validateResponses = validateResponses;
    }

    public Response getResponse(String responseId) {
        return responses.get(responseId);
    }

    public Map<String, Response> getResponses() {
        return responses;
    }

    public void addResponse(Response response) {
        responses.put(response.getResponseId(), response);
    }
    
    public Outcome getOutcome(String outcomeIdentifier) {
        return outcomes.get(outcomeIdentifier);
    }
    
    public Collection<Outcome> getOutcomes() { return outcomes.values(); }
    
    public void addOutcome(Outcome outcome) {
        outcomes.put(outcome.getOutcomeIdentifier(), outcome);
    }

    public boolean isAnswered() {
        return responses.size() > 0;
    }

    public boolean isUseForBranchRuleEvaluation() {
        return useForBranchRuleEvaluation;
    }

    public void setUseForBranchRuleEvaluation(Boolean useForBranchRuleEvaluation) {
        this.useForBranchRuleEvaluation = useForBranchRuleEvaluation;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
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
