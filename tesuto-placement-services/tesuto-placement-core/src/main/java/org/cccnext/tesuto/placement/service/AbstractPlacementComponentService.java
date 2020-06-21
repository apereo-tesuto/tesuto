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
package org.cccnext.tesuto.placement.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto;
import org.cccnext.tesuto.placement.model.PlacementEventLog;
import org.cccnext.tesuto.placement.service.PlacementComponentEventService;
import org.cccnext.tesuto.placement.view.DisciplineViewDto;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;


import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public abstract class AbstractPlacementComponentService implements PlacementComponentEventService {

    @Autowired
    protected SubjectAreaServiceAdapter subjectAreaService;

    @Autowired
    protected PlacementHelperService placementHelperService;

    @Autowired
    protected PlacementEventLogService placementLogService;

    @Override
    public Collection<PlacementComponentViewDto> processPlacementEvent(PlacementEventInputDto eventData) throws Exception {

        Collection<PlacementComponentViewDto> componentViewDtos = new ArrayList<PlacementComponentViewDto>();

        Set<String> collegeMisCodes = eventData.getCollegeMisCodes();
        if (collegeMisCodes == null) {
            collegeMisCodes = placementHelperService.getCollegeMisCodes(eventData.getCccid());
        }
        if (CollectionUtils.isEmpty(collegeMisCodes)) {
            placementLogService.log(eventData.getTrackingId(), eventData.getCccid(),
                    null, PlacementEventLog.EventType.PLACEMENT_REQUEST_NO_COLLEGES,
                    String.format("No colleges were found for student %s, placement request aborted", eventData.getCccid()));
            return null;
        }
     // Student may be associated to multiple colleges
        for (String collegeMisCode: collegeMisCodes) {
         // Assessment may be associated to multiple competency map disciplines (i.e. English, ESL)
            for (String competencyMapDiscipline : eventData.getCompetencyMapDisciplines()) {
                Set<DisciplineViewDto> disciplines = subjectAreaService.getDisciplinesByCollegeIdAndDisciplineMap(collegeMisCode, competencyMapDiscipline);
                if (CollectionUtils.isEmpty(disciplines)) {
                    log.error("For college {} no college disciplines were found for competency map discipline {}", collegeMisCode, competencyMapDiscipline);
                    placementLogService.log(eventData.getTrackingId(), eventData.getCccid(),
                            null, PlacementEventLog.EventType.PLACEMENT_REQUEST_NO_COLLEGE_DISCIPLINES,
                            String.format("For college %s no college disciplines were found for competency map discipline %s", collegeMisCode, competencyMapDiscipline));
                    continue;
                }
             // Generate a placement component for each unique college, discpline association
                for (DisciplineViewDto discipline : disciplines) {
                    PlacementComponentViewDto viewDto = createPlacementComponent(collegeMisCode, discipline, eventData);
                    if (viewDto != null) {
                        componentViewDtos.add(viewDto);
                    }
                }
            }
        }
        return componentViewDtos;
    }
}
