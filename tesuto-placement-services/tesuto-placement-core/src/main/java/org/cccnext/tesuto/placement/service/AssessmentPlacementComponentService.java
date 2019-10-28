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

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.cccnext.tesuto.content.dto.competency.CompetencyDifficultyDto;
import org.cccnext.tesuto.content.service.CompetencyMapOrderReader;
import org.cccnext.tesuto.placement.dto.AssessmentCompletePlacementInputDto;
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto;
import org.cccnext.tesuto.placement.model.TesutoPlacementComponent;
import org.cccnext.tesuto.placement.model.PlacementEventLog;
import org.cccnext.tesuto.placement.repository.jpa.PlacementComponentRepository;
import org.cccnext.tesuto.placement.view.DisciplineSequenceViewDto;
import org.cccnext.tesuto.placement.view.DisciplineViewDto;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;
import org.cccnext.tesuto.util.TesutoUtils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A new placement component is created based on an incoming Assessment event
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
@Transactional
public class AssessmentPlacementComponentService extends AbstractPlacementComponentService {

    @Autowired
    CompetencyMapOrderReader competencyMapOrderReader;

    @Autowired
    PlacementComponentRepository placementComponentRepository;

    @Autowired
    PlacementComponentAssembler placementComponentAssembler;

    @Autowired
    private PlacementDuplicationCheckService placementDuplicationCheckService;

    @Autowired
    private PlacementEventLogService eventLogService;

    public void setPlacementDuplicationCheckService(PlacementDuplicationCheckService placementDuplicationCheckService) {
        this.placementDuplicationCheckService = placementDuplicationCheckService;
    }

    @Override
    public PlacementComponentViewDto createPlacementComponent(String collegeId, DisciplineViewDto subjectArea,
            PlacementEventInputDto eventData) throws Exception {

        AssessmentCompletePlacementInputDto placementData = (AssessmentCompletePlacementInputDto) eventData;

        PlacementComponentViewDto viewDto = null;
        VersionedSubjectAreaViewDto versionedSubjectAreaViewDto =  subjectAreaService.getPublishedVersionForSubjectArea(subjectArea.getDisciplineId());
        if (versionedSubjectAreaViewDto == null) {
            String message = String.format("Versioned Subject Areas  Not Found for disciplineId: %s", subjectArea.getDisciplineId());
            log.error(message);
            eventLogService.log(eventData.getTrackingId(),
                    eventData.getCccid(),
                    collegeId,
                    PlacementEventLog.EventType.PLACEMENT_COMPONENT_ASSESS_PROCESSING_FAIL,message);
            return null;
        }

        if (placementData.getStudentAbility() == null) {
            String errorMessage = String.format("Student ability is null.");
            log.error(errorMessage);
            eventLogService.log(eventData.getTrackingId(),
                    eventData.getCccid(),
                    versionedSubjectAreaViewDto.getDisciplineId(),
                    versionedSubjectAreaViewDto.getVersion(),
                    collegeId,
                    PlacementEventLog.EventType.PLACEMENT_COMPONENT_ASSESS_PROCESSING_FAIL,errorMessage);
            return null;
        }

        // Only process placement component with a valid versionedSubjectArea
        String competencyMapOrderId = competencyMapOrderReader.findLatestPublishedIdByCompetencyMapDiscipline(versionedSubjectAreaViewDto.getCompetencyMapDiscipline());
        List<CompetencyDifficultyDto> competencies = competencyMapOrderReader.getOrderedCompetencies(competencyMapOrderId);

        if (competencies == null || competencies.size() == 0) {
            String errorMessage = String.format("Invalid competencies found for competencyMapDiscipline %s and version %s: ",
                    versionedSubjectAreaViewDto.getCompetencyMapDiscipline(), versionedSubjectAreaViewDto.getCompetencyMapVersion());
            log.error(errorMessage);

            eventLogService.log(eventData.getTrackingId(),
                    eventData.getCccid(),
                    versionedSubjectAreaViewDto.getDisciplineId(),
                    versionedSubjectAreaViewDto.getVersion(),
                    collegeId,
                    PlacementEventLog.EventType.PLACEMENT_COMPONENT_ASSESS_PROCESSING_FAIL,errorMessage);
        }

        Map<String, CompetencyDifficultyDto> competencyDifficultyMap = placementHelperService
                .buildCompetencyDifficultyMap(competencies);

        TesutoPlacementComponent placementComponent = new TesutoPlacementComponent();
        placementComponent.setCccid(eventData.getCccid());
        placementComponent.setCollegeId(collegeId);
        placementComponent.setAssessmentSessionId( placementData.getAssessmentSessionId());
        placementComponent.setTrackingId( placementData.getTrackingId());
        placementComponent.setAssessmentTitle( placementData.getAssessmentTitle());
        placementComponent.setAssessmentDate( placementData.getCompletionDate());
        placementComponent.setStudentAbility( placementData.getStudentAbility());
        placementComponent.setSubjectAreaId(versionedSubjectAreaViewDto.getDisciplineId());
        placementComponent.setSubjectAreaVersion(versionedSubjectAreaViewDto.getVersion());
        placementComponent.setElaIndicator(placementData.getElaIndicator());
        DisciplineSequenceViewDto disciplineSequence = placementHelperService.findClosestToTransferLevel( competencyDifficultyMap,
                versionedSubjectAreaViewDto, placementComponent.getStudentAbility());

        if (disciplineSequence != null) {
            log.debug("Setting placementComponent cb21,courseGroup to: " + disciplineSequence.getCb21Code() + "," + disciplineSequence.getCourseGroup());
            placementComponent.setCourseGroup(disciplineSequence.getCourseGroup());
            placementComponent.setCb21Code(disciplineSequence.getCb21Code());
        } else {
            log.error("DisciplineSequence was null, resulting in a placementComponent with null cb21Code and courseGroup!");
            log.error("PlacementComponent: " + placementComponent);
            eventLogService.log(eventData.getTrackingId(),
                    eventData.getCccid(),
                    versionedSubjectAreaViewDto.getDisciplineId(),
                    versionedSubjectAreaViewDto.getVersion(),
                    collegeId,
                    PlacementEventLog.EventType.PLACEMENT_COMPONENT_ASSESS_NULL_DISCIPLINE_SEQUENCE,
                    "A null discipline sequence was found for transfer level, for student ability:" +  placementData.getStudentAbility());
            return null;
        }

        if (placementDuplicationCheckService.isDuplicateAssessPlacementComponent(placementComponent)) {
            eventLogService.log(eventData.getTrackingId(),
                    eventData.getCccid(),
                    versionedSubjectAreaViewDto.getDisciplineId(),
                    versionedSubjectAreaViewDto.getVersion(),
                    collegeId,
                    PlacementEventLog.EventType.PLACEMENT_COMPONENT_ASSESS_DUPLICATE,
                    "A duplicate Tesuto component already exists for the student");
            return null;
        }

        placementComponent.setTriggerData("");

        placementComponent.setId(TesutoUtils.newId());
        placementComponent.setCreatedOn(Calendar.getInstance().getTime());
        viewDto = placementComponentAssembler.assembleDto(placementComponentRepository.save(placementComponent));

        eventLogService.log(eventData.getTrackingId(),
                eventData.getCccid(),
                versionedSubjectAreaViewDto.getDisciplineId(),
                versionedSubjectAreaViewDto.getVersion(),
                collegeId,
                PlacementEventLog.EventType.PLACEMENT_COMPONENT_ASSESS_PROCESSING_COMPLETE,
                "Placement component has been created.");

        return viewDto;
    }

}
