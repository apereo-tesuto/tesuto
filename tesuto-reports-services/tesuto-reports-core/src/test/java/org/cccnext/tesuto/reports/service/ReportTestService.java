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
import java.util.LinkedHashMap;
import java.util.List;

import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.content.service.AssessmentService;
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.service.AssessmentSessionDao;
import org.cccnext.tesuto.reports.service.ResultsSearchParameters;
import org.joda.time.DateTime;

import com.amazonaws.util.CollectionUtils;

public class ReportTestService {
    
    private AssessmentService assessmentService;
    
    private AssessmentSessionDao assessmentSessionnDao;
    
    public AssessmentSessionDao getAssessmentSessionDao() {
        return assessmentSessionnDao;
    }

    public void setAssessmentSessionDao(AssessmentSessionDao assessmentSessionnDao) {
        this.assessmentSessionnDao = assessmentSessionnDao;
    }

    public AssessmentService getAssessmentService() {
        return assessmentService;
    }
    
    public List<String> getSequenceValue(ScopedIdentifier scopedIdentifier, Integer number) {
        List<String> sequenceValues = new ArrayList<String>();
        ResultsSearchParameters searchParameters = new ResultsSearchParameters();
        
        DateTime completionDateLowerBound = DateTime.now().minusDays(4).withTimeAtStartOfDay();
        searchParameters.setCompletionDateLowerBound(completionDateLowerBound.toGregorianCalendar().getTime());

        searchParameters.setAssessmentScopedIdentifier(scopedIdentifier);
        updateSearchParameters(searchParameters);
        List<String> fields = new ArrayList<String>();
        fields.add("assessmentSessionId");
        
        fields.add("sequence");
        searchParameters.setFields(fields);
        
        List<AssessmentSession> attempts = assessmentSessionnDao.search(searchParameters);
        for(AssessmentSession attempt: attempts) {
            LinkedHashMap<String,String> hashMap = (LinkedHashMap<String,String>)attempt.getSequence();
            String[] array = new String[hashMap.size()];
            String[] values = (String[])hashMap.values().toArray((Object[])array);
            sequenceValues.add(values[number]);
        }
        
        return sequenceValues;
    }

    private void updateSearchParameters(ResultsSearchParameters searchParameters) {
        List<AssessmentDto> assessments = null;
        if(searchParameters.getAssessmentScopedIdentifier() != null) {
           assessments =  assessmentService.read(searchParameters.getAssessmentScopedIdentifier());
           if(assessments == null) {
               return;
           }
           List<String> assessmentIds = new ArrayList<String>();
           if(!CollectionUtils.isNullOrEmpty(searchParameters.getContentIds())) {
               assessmentIds.addAll(searchParameters.getContentIds());
           }
           for(AssessmentDto assessment:assessments) {
               assessmentIds.add(assessment.getId());
           }
           searchParameters.setContentIds(assessmentIds);
        }
        return;
    }

}
