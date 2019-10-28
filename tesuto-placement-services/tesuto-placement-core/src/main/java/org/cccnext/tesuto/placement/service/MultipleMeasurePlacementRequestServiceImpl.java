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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDisciplineDto;
import org.cccnext.tesuto.content.service.CompetencyMapDisciplineReader;
import org.cccnext.tesuto.message.service.MessagePublisher;
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto;
import org.cccnext.tesuto.user.service.StudentReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MultipleMeasurePlacementRequestServiceImpl implements MultipleMeasurePlacementRequestService {

    @Autowired
    private MessagePublisher<PlacementEventInputDto> multipleMeasurePlacementRequestor;
    
    @Autowired
    private CompetencyMapDisciplineReader competencyMapDisciplineService;
    
    @Autowired
    private StudentReader studentService;
    
    @Override
    public void requestPlacements(String cccid, boolean newPlacementOnly) {
        requestPlacements(null, cccid, new HashSet<>(), newPlacementOnly);
    }

    @Override
    public void requestPlacements(String collegeMisCode, String cccid, boolean newPlacementOnly) {
        requestPlacements(collegeMisCode, cccid, new HashSet<>(), newPlacementOnly);
    }

    @Override
    public void requestPlacements(String collegeMisCode, String cccid, String subjectArea, boolean newPlacementOnly) {
        CompetencyMapDisciplineDto competenecyMapDicipline = competencyMapDisciplineService.read(subjectArea);
        Set<String> mapDisciplines = competenecyMapDicipline == null ? new HashSet<>() : Collections.singleton(competenecyMapDicipline.getDisciplineName()); 
        requestPlacements(collegeMisCode, cccid, mapDisciplines, newPlacementOnly);
    }

    protected void requestPlacements(String collegeMisCode, String cccid, Set<String> competencyMapDiscipline, boolean newPlacementOnly) {
        Set<String> collegeMisCodes;
        if (StringUtils.isBlank(collegeMisCode)) {
            collegeMisCodes = studentService.getCollegesAppliedTo(cccid);
        } else {
            collegeMisCodes = Collections.singleton(collegeMisCode);
        }
        
        PlacementEventInputDto placementEventInputDto = createPlacementEventDto(collegeMisCodes, cccid, competencyMapDiscipline, newPlacementOnly);
        multipleMeasurePlacementRequestor.sendMessage(placementEventInputDto);
    }

    PlacementEventInputDto createPlacementEventDto(Set<String> collegeMisCodes, String cccid, Set<String> competencyMapDisciplines, boolean onlyNewPlacements) {
            PlacementEventInputDto placementEventInputDTO = new PlacementEventInputDto();
            placementEventInputDTO.setCccid(cccid);
            placementEventInputDTO.setCollegeMisCodes(collegeMisCodes);
            
            placementEventInputDTO.setProcessOnlyNewPlacements(onlyNewPlacements);
            placementEventInputDTO.setTrackingId(UUID.randomUUID().toString());
            // We need to have a client to call the placement controller to retrieve the subject area.
            // Maybe the entire placement request services should be moved to the placement module.
            // placementEventInputDTO.setSubjectArea(subjectArea);
            placementEventInputDTO.setCompetencyMapDisciplines(competencyMapDisciplines);
            return placementEventInputDTO;
    }
    
    
}
