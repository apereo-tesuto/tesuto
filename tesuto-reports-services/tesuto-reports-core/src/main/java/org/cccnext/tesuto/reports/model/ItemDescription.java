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

import java.util.ArrayList;
import java.util.List;

public class ItemDescription {
    String scopedId;
    String baseIdentifier;
    String authorVersion;
    String systemVersion;
    String body = "";
    String stimulus = "";
    String commonCore = "";
    String competencies = "";
    String correctResponse = "";
    String mappedResponse = "";
    String mappedResponseValue = "";
    String minScore;
    String maxScore;
    List<String> responseContent = new ArrayList<String>();

    public String getScopedId() {
        return scopedId;
    }

    public void setScopedId(String scopedId) {
        this.scopedId = scopedId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getStimulus() {
        return stimulus;
    }

    public void setStimulus(String stimulus) {
        this.stimulus = stimulus;
    }

    public String getCommonCore() {
        return commonCore;
    }

    public void setCommonCore(String commonCore) {
        this.commonCore = commonCore;
    }

    public String getCompetencies() {
        return competencies;
    }

    public void setCompetencies(String competencies) {
        this.competencies = competencies;
    }

    public String getCorrectResponse() {
        return correctResponse;
    }

    public void setCorrectResponse(String correctResponse) {
        this.correctResponse = correctResponse;
    }

    public String getMappedResponse() {
        return mappedResponse;
    }

    public void setMappedResponse(String mappedResponse) {
        this.mappedResponse = mappedResponse;
    }

    public String getMappedResponseValue() {
        return mappedResponseValue;
    }

    public void setMappedResponseValue(String mappedResponseValue) {
        this.mappedResponseValue = mappedResponseValue;
    }

    public List<String> getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(List<String> responseContent) {
        this.responseContent = responseContent;
    }

    public String getMinScore() {
        return minScore;
    }

    public void setMinScore(String minScore) {
        this.minScore = minScore;
    }

    public String getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(String maxScore) {
        this.maxScore = maxScore;
    }

	public String getBaseIdentifier() {
		return baseIdentifier;
	}

	public String getAuthorVersion() {
		return authorVersion;
	}

	public String getSystemVersion() {
		return systemVersion;
	}

	public void setBaseIdentifier(String baseIdentifier) {
		this.baseIdentifier = baseIdentifier;
	}

	public void setAuthorVersion(String authorVersion) {
		this.authorVersion = authorVersion;
	}

	public void setSystemVersion(String systemVersion) {
		this.systemVersion = systemVersion;
	}
}
