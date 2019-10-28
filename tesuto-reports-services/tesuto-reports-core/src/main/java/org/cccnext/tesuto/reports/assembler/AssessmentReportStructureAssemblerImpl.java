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
package org.cccnext.tesuto.reports.assembler;

import org.cccnext.tesuto.reports.model.AssessmentReportStructure;
import org.cccnext.tesuto.reports.model.inner.JpaAssessmentReportStructure;
import org.springframework.stereotype.Component;

@Component(value = "assessmentReportStructureAssembler")
public class AssessmentReportStructureAssemblerImpl implements AssessmentReportStructureAssembler {

    @Override
    public AssessmentReportStructure assembleDto(JpaAssessmentReportStructure jpaAssessmentReportStructure) {
        if (jpaAssessmentReportStructure == null) {
            return null;
        }

        AssessmentReportStructure assessmentReportStructure = new AssessmentReportStructure();

        assessmentReportStructure.setAssessmentId(jpaAssessmentReportStructure.getAssessmentId());
        assessmentReportStructure.setParentTestletId(jpaAssessmentReportStructure.getParentTestletId());
        assessmentReportStructure.setReportOrder(jpaAssessmentReportStructure.getReportOrder());
        assessmentReportStructure.setItemsInBundle(jpaAssessmentReportStructure.getItemsInBundle());
        assessmentReportStructure.setItemRefIdentifier(jpaAssessmentReportStructure.getItemRefIdentifier());
        assessmentReportStructure.setItemScopedIdentifier(jpaAssessmentReportStructure.getItemScopedIdentifier());
        assessmentReportStructure.setItemId(jpaAssessmentReportStructure.getItemId());
        assessmentReportStructure.setResponseStructure(jpaAssessmentReportStructure.getResponseStructure());
        assessmentReportStructure.setAssessmentScopedId(jpaAssessmentReportStructure.getAssessmentScopedId());
        return assessmentReportStructure;

    }

    @Override
    public JpaAssessmentReportStructure disassembleDto(AssessmentReportStructure assessmentReportStructure) {
        if (assessmentReportStructure == null) {
            return null;
        }

        JpaAssessmentReportStructure jpaAssessmentReportStructure = new JpaAssessmentReportStructure();

        jpaAssessmentReportStructure.setAssessmentId(assessmentReportStructure.getAssessmentId());
        jpaAssessmentReportStructure.setParentTestletId(assessmentReportStructure.getParentTestletId());
        jpaAssessmentReportStructure.setReportOrder(assessmentReportStructure.getReportOrder());
        jpaAssessmentReportStructure.setItemsInBundle(assessmentReportStructure.getItemsInBundle());
        jpaAssessmentReportStructure.setItemRefIdentifier(assessmentReportStructure.getItemRefIdentifier());
        jpaAssessmentReportStructure.setItemScopedIdentifier(assessmentReportStructure.getItemScopedIdentifier());
        jpaAssessmentReportStructure.setItemId(assessmentReportStructure.getItemId());
        jpaAssessmentReportStructure.setResponseStructure(assessmentReportStructure.getResponseStructure());
        jpaAssessmentReportStructure.setAssessmentScopedId(assessmentReportStructure.getAssessmentScopedId());

        return jpaAssessmentReportStructure;
    }

}
