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
import org.cccnext.tesuto.reports.model.inner.JsonBResponseStructure;

public class AssessmentReportStructure implements Dto {

    private static final long serialVersionUID = 1L;

    private String assessmentId;
    
    private String itemScopedIdentifier;

    private String itemRefIdentifier;
    
    private String itemId;
    
    private String parentTestletId;

    private Integer reportOrder;

    private Integer itemsInBundle;
       
    private String assessmentScopedId;
    
    JsonBResponseStructure responseStructure;

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getParentTestletId() {
        return parentTestletId;
    }

    public void setParentTestletId(String parentTestletId) {
        this.parentTestletId = parentTestletId;
    }

    public Integer getReportOrder() {
        return reportOrder;
    }

    public void setReportOrder(Integer reportOrder) {
        this.reportOrder = reportOrder;
    }

    public Integer getItemsInBundle() {
        return itemsInBundle;
    }

    public void setItemsInBundle(Integer itemsInBundle) {
        this.itemsInBundle = itemsInBundle;
    }

    public String getItemRefIdentifier() {
        return itemRefIdentifier;
    }

    public void setItemRefIdentifier(String itemRefIdentifier) {
        this.itemRefIdentifier = itemRefIdentifier;
    }
    
    
    public String getItemScopedIdentifier() {
        return itemScopedIdentifier;
    }

    public void setItemScopedIdentifier(String itemScopedIdentifier) {
        this.itemScopedIdentifier = itemScopedIdentifier;
    }
    
    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public JsonBResponseStructure getResponseStructure() {
        return responseStructure;
    }

    public void setResponseStructure(JsonBResponseStructure responseStructure) {
        this.responseStructure = responseStructure;
    }
    
    public String getAssessmentScopedId() {
        return assessmentScopedId;
    }

    public void setAssessmentScopedId(String assessmentScopedId) {
        this.assessmentScopedId = assessmentScopedId;
    }

    public AssessmentReportStructure updateMetadata(AssessmentReportStructure reportStructure) {
        reportStructure.setAssessmentId(assessmentId);
        reportStructure.setAssessmentScopedId(assessmentScopedId);
        reportStructure.setItemsInBundle(itemsInBundle);
        reportStructure.setParentTestletId(parentTestletId);
        return reportStructure;
        
    }

}
