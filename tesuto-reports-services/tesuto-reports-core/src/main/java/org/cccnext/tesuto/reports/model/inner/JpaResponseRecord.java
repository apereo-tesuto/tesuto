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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.cccnext.tesuto.domain.model.AbstractEntity;

@Entity
@Table(schema="public",name = "report_response_record")
@IdClass(ResponseRecordId.class)
public class JpaResponseRecord extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "response_attempt_id")
    private String responseAttemptId;

    @Id
    @Column(name = "item_ref_identifier")
    private String itemRefIdentifier;
    
    @Id
    @Column(name = "item_id")
    private String itemId;

    @Id
    @Column(name = "attempt_index")
    private int attemptIndex;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "responses")
    private String responses;

    @Column(name = "responseIdentifiers")
    private String responseIdentifiers;

    @Column(name = "outcome_score")
    private Double outcomeScore;

    @Column(name = "single_character_response")
    private char singleCharacterResponse;

    public String getResponseAttemptId() {
        return responseAttemptId;
    }

    public void setResponseAttemptId(String attemptId) {
        this.responseAttemptId = attemptId;
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
    
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

}
