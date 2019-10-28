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
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.cccnext.tesuto.domain.model.AbstractEntity;

@Entity
@Table(schema="public",name = "report_assessment_structure")
@IdClass(AssessmentReportStructureId.class)
public class JpaAssessmentReportStructure extends AbstractEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "assessment_id", nullable = false, length = 100)
    @Id
    private String assessmentId;

    @Column(name = "item_id")
    @Id
    private String itemId;

    @Column(name = "assessment_scoped_id")
    private String assessmentScopedId;

    @Column(name = "item_scoped_identifier")
    private String itemScopedIdentifier;

    @Column(name = "item_ref_identifier")
    private String itemRefIdentifier;

    @Column(name = "parent_testlet_id", nullable = true, length = 100)
    private String parentTestletId;

    @Column(name = "report_order")
    private Integer reportOrder;

    @Column(name = "items_in_bundle")
    private Integer itemsInBundle;

    @Column(name = "response_structure")
    @Convert(converter = JsonBResponseStructureConverter.class)
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

    public String getItemScopedIdentifier() {
        return itemScopedIdentifier;
    }

    public void setItemScopedIdentifier(String itemScopedIdentifier) {
        this.itemScopedIdentifier = itemScopedIdentifier;
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
}
