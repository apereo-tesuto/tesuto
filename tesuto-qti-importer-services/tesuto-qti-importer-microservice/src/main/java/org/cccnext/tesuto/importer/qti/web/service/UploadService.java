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
package org.cccnext.tesuto.importer.qti.web.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.metadata.CompetencyRefItemMetadataDto;
import org.cccnext.tesuto.content.service.AssessmentItemService;
import org.cccnext.tesuto.content.service.AssessmentService;
import org.cccnext.tesuto.content.service.CompetencyMapOrderService;
import org.cccnext.tesuto.delivery.service.AssessmentSessionReader;
import org.cccnext.tesuto.importer.qti.service.QtiResourceRelocator;
import org.cccnext.tesuto.importer.service.upload.BaseUploadService;
import org.cccnext.tesuto.importer.service.upload.ImportResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UploadService extends BaseUploadService {

    @Autowired
    AssessmentService assessmentService;
    
    @Autowired
    AssessmentItemService assessmentItemService;
    
    @Autowired
    AssessmentSessionReader deliveryService;

    @Autowired
    private QtiResourceRelocator resourceRelocator;

    @Autowired
    CompetencyMapOrderService competencyMapOrderService;

    @Override
    protected QtiResourceRelocator getResourceRelocator() {
        return resourceRelocator;
    }

    @Override
    protected AssessmentItemService getAssessmentItemService() {
        return assessmentItemService;
    }

    @Override
    protected AssessmentSessionReader getDeliveryService() {
        return deliveryService;
    }

    @Override
    protected AssessmentService getAssessmentService() {
        return assessmentService;
    }

    @Override
    public  ImportResults createAssessmentSessions(List<AssessmentDto> assessmentDtos,
            List<AssessmentItemDto> assessmentItemDtos, String namespace, String userId){
        Set<String> disciplines = new HashSet<String>();
        if(CollectionUtils.isNotEmpty(assessmentDtos)) {
            for(AssessmentDto a:assessmentDtos) {
                if(a.getAssessmentMetadata() != null 
                        && CollectionUtils.isNotEmpty(a.getAssessmentMetadata().getCompetencyMapDisciplines())) {
                    disciplines.addAll(a.getAssessmentMetadata().getCompetencyMapDisciplines());
                }
            }
        } 
        if(CollectionUtils.isNotEmpty(assessmentItemDtos)){
            for(AssessmentItemDto a:assessmentItemDtos) {
                if(a.getItemMetadata() != null 
                        && a.getItemMetadata().getCompetencies() != null
                        && CollectionUtils.isNotEmpty(a.getItemMetadata().getCompetencies().getCompetencyRef())) {
                    for(CompetencyRefItemMetadataDto md: a.getItemMetadata().getCompetencies().getCompetencyRef()) {
                        disciplines.add(md.getCompetencyMapDiscipline());
                    }
                }
            }
        }
        ImportResults results = super.createAssessmentSessions(assessmentDtos, assessmentItemDtos, namespace, userId);
        if(CollectionUtils.isNotEmpty(disciplines)) {
            Map<String,String> competencyMapOrders = competencyMapOrderService.createForDisciplines(disciplines);
            results.setImportedCompetencyMapOrderIds(competencyMapOrders.size() > 0 ? competencyMapOrders : null);
        }
        return results;
    }
}
