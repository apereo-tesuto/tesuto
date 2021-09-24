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
package org.cccnext.tesuto.reports.model;

import org.cccnext.tesuto.domain.dto.Dto;

public class ResponseRecord implements Dto {

    private static final long serialVersionUID = 1L;

    private String attemptId;
    
    private String itemRefIdentifier;
    
    private String itemId;

    private int attemptIndex;
    
    private int itemVersion;

    private Long duration;

    private String responses;

    private String responseIdentifiers;

    private Double outcomeScore;

    private char singleCharacterResponse;

    public String getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(String attemptId) {
        this.attemptId = attemptId;
    }

    public String getItemRefIdentifier() {
        return itemRefIdentifier;
    }

    public void setItemRefIdentifier(String itemRefIdentifier) {
        this.itemRefIdentifier = itemRefIdentifier;
    }

    public int getAttemptIndex() {
        return attemptIndex;
    }

    public void setAttemptIndex(int attemptIndex) {
        this.attemptIndex = attemptIndex;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getResponses() {
        return responses;
    }

    public void setResponses(String responses) {
        this.responses = responses;
    }

    public Double getOutcomeScore() {
        return outcomeScore;
    }

    public void setOutcomeScore(Double outcomeScore) {
        this.outcomeScore = outcomeScore;
    }

    public char getSingleCharacterResponse() {
        return singleCharacterResponse;
    }

    public void setSingleCharacterResponse(char singleCharacterResponse) {
        this.singleCharacterResponse = singleCharacterResponse;
    }

    public String getResponseIdentifiers() {
        return responseIdentifiers;
    }

    public void setResponseIdentifiers(String responseIdentifiers) {
        this.responseIdentifiers = responseIdentifiers;
    }
    
    public int getItemVersion() {
        return itemVersion;
    }

    public void setItemVersion(int itemVersion) {
        this.itemVersion = itemVersion;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

}
