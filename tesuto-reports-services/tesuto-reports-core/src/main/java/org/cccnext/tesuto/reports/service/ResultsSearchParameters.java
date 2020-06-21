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
package org.cccnext.tesuto.reports.service;

import java.util.ArrayList;
import java.util.List;

import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.delivery.service.DeliverySearchParameters;

public class ResultsSearchParameters extends DeliverySearchParameters {


	public enum SearchSet {
        ALL,
        EXPIRED,
        COMPLETED
    }
    
    private ScopedIdentifier assessmentScopedIdentifier;
    
    private SearchSet searchSet = SearchSet.ALL;
    
    private String competencyMapDiscipline;
    
    private String partialIdentifier;
    
    private Boolean includeUnaffiliatedItems;
    
    private List<AssessmentDto> assessments = new ArrayList<>();
    

    public List<AssessmentDto> getAssessments() {
		return assessments;
	}

	public void setAssessments(List<AssessmentDto> assessments) {
		this.assessments = assessments;
	}

	public ScopedIdentifier getAssessmentScopedIdentifier() {
        return assessmentScopedIdentifier;
    }

    public void setAssessmentScopedIdentifier(ScopedIdentifier assessmentScopedIdentifier) {
        this.assessmentScopedIdentifier = assessmentScopedIdentifier;
    }
    
    public void setSearchSet(SearchSet searchSet) {
        this.searchSet = searchSet;
    }
    
    public Boolean searchCompletedAssessmentSessions() {
        if(searchSet == SearchSet.ALL || searchSet == SearchSet.COMPLETED) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isUsingIndexedSearch() {
        if(this.searchSet.equals(SearchSet.ALL)){
            return true;
        }
        return super.isUsingIndexedSearch();
    }

    public Boolean searchExpiredAssessmentSessions() {
        if(searchSet == SearchSet.ALL || searchSet == SearchSet.EXPIRED) {
            return true;
        }
        return false;
    }
    

    public String getCompetencyMapDiscipline() {
		return competencyMapDiscipline;
	}

	public void setCompetencyMapDiscipline(String competencyMapDiscipline) {
		this.competencyMapDiscipline = competencyMapDiscipline;
	}

	public Boolean getIncludeUnaffiliatedItems() {
		return includeUnaffiliatedItems;
	}

	public void setIncludeUnaffiliatedItems(Boolean includeUnaffiliatedItems) {
		this.includeUnaffiliatedItems = includeUnaffiliatedItems;
	}

	public SearchSet getSearchSet() {
		return searchSet;
	}

	public String getPartialIdentifier() {
		return partialIdentifier;
	}

	public void setPartialIdentifier(String partialIdentifier) {
		this.partialIdentifier = partialIdentifier;
	}
}
