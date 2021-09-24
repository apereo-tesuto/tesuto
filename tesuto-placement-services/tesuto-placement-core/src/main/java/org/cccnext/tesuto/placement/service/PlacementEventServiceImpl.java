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
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementActionResult;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementMapKeys;
import org.cccnext.tesuto.exception.PoorlyFormedRequestException;
import org.cccnext.tesuto.placement.dto.AssessmentCompletePlacementInputDto;
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto;
import org.cccnext.tesuto.placement.model.PlacementEventLog;
import org.cccnext.tesuto.placement.service.PlacementEventService;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.placement.view.PlacementViewDto;
import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;
import org.cccnext.tesuto.rules.service.RuleSetReader;
import org.ccctc.common.droolscommon.action.result.ActionResult;
import org.ccctc.common.droolscommon.action.result.ErrorActionResult;
import org.ccctc.common.droolscommon.exceptions.ExceptionUtils;
import org.dozer.Mapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
public class PlacementEventServiceImpl implements PlacementEventService {
    
    @Value("${placement.rules.engine.id}")
    private String engineId;

    @Autowired
    private AssessmentPlacementComponentService assessmentPlacementComponentService;

    @Autowired
    RuleSetReader ruleSetReader;

    @Autowired
    private PlacementService placementService;

    @Autowired
    private PlacementComponentService placementComponentService;

    @Autowired
    private PlacementNotificationService placementNotificationService;

    @Autowired

    private SubjectAreaServiceAdapter subjectAreaService;

    @Autowired
    PlacementEventLogService placementLogService;

    @Autowired
    PlacementDuplicationCheckService placementDuplicationCheckService;

    @Autowired
    private Mapper mapper;

    @Override
    public Collection<PlacementComponentViewDto> processPlacementEvent(PlacementEventInputDto eventData) throws Exception {
        Collection<PlacementComponentViewDto> componentViewDtos = new ArrayList<PlacementComponentViewDto>();
        if (eventData instanceof AssessmentCompletePlacementInputDto) {
            componentViewDtos = assessmentPlacementComponentService.processPlacementEvent((AssessmentCompletePlacementInputDto)eventData);
        }
        return componentViewDtos;
    }

    public void requestPlacementForComponent(PlacementComponentViewDto placementComponent) {
        PlacementActionResult eventData = mapper.map( placementComponent, PlacementActionResult.class);
        if (placementComponent.getVersionedSubjectAreaViewDto() != null) {
            eventData.setSubjectAreaId(placementComponent.getVersionedSubjectAreaViewDto().getDisciplineId());
            eventData.setSubjectAreaVersion(placementComponent.getVersionedSubjectAreaViewDto().getVersion());
        }
        requestPlacementForActionResult(eventData);
    }

    /**
     * This is an asynchronous call to the rules service to initiate the creation of a placement. A successful rule
     * execution will call addPlacementComponent to create the placement.
     *
     * @throws RetryableMessageException if the caller should try to re-execute the placement component creation.
     * @throws Exception if the problem cannot be resolved by re-executing the call
     */
    public void requestPlacementForActionResult(PlacementActionResult eventData) {
        String cccid = eventData.getCccid();
        String collegeMisCode = eventData.getCollegeId();
        Integer subjectAreaId = eventData.getSubjectAreaId();
        Integer subjectAreaVersion = eventData.getSubjectAreaVersion();

        placementLogService.log(eventData.getTrackingId(), cccid, subjectAreaId, subjectAreaVersion,
                collegeMisCode, PlacementEventLog.EventType.PLACEMENT_PROCESSING_START,
                "Placement Creation Rules engine shows starting of rules application.");

        Map<String, String> facts = initFactMap(eventData, PlacementMapKeys.EVENT_TRIGGER_PLACEMENT);

        VersionedSubjectAreaViewDto subjectArea = subjectAreaService.getVersionedSubjectAreaDto(subjectAreaId, subjectAreaVersion);
        facts.put(PlacementMapKeys.RULE_SET_ID_KEY, subjectArea.getCompetencyAttributes().getMmPlacementLogic());

        try {
            List<ActionResult> actionResults = ruleSetReader.executeRulesEngine(engineId, facts);
            StringBuilder comment = new StringBuilder();
            Boolean wasSuccessful = true;

            for(ActionResult result:actionResults) {
                comment.append(result.getActionName()).append("\n");
                if(result instanceof ErrorActionResult) {
                    comment.append(result.getMessage()).append("\n");
                    wasSuccessful = false;
                }
                if(result.getActionName().equals(PlacementRulesActionCode.FAILED_ON_VALIDATE)) {
                    placementLogService.log(eventData.getTrackingId(), eventData.getCccid(), subjectArea.getDisciplineId(), subjectArea.getVersion(),
                            eventData.getCollegeId(), PlacementEventLog.EventType.PLACEMENT_PROCESSING_FAIL,
                            "Placement Rules engine failed for the following reason{s}: \n" + PlacementRulesActionCode.FAILED_ON_VALIDATE + comment.toString());
                    placementNotificationService.placementComponents(collegeMisCode, cccid, eventData.getTrackingId());
                    return;
                }

                if(result.getActionName().equals(PlacementRulesActionCode.PLACEMENT_SERVICE_DOWN)) {
                    String message = "Placement Rules engine failed for the following reason{s}: \n" + comment.toString();
                    placementLogService.log(eventData.getTrackingId(), eventData.getCccid(), subjectArea.getDisciplineId(), 0, eventData.getCollegeId(),
                            PlacementEventLog.EventType.PLACEMENT_PROCESSING_FAIL, message);
                    throw new RetryableMessageException(message);
                }

                if( StringUtils.startsWithIgnoreCase(result.getActionName(), PlacementRulesActionCode.RULE_ARE_LOADING)) {
                    String message = "Placement Rules eare still loading, retry message.";
                    placementLogService.log(eventData.getTrackingId(), eventData.getCccid(), subjectArea.getDisciplineId(), 0, eventData.getCollegeId(),
                            PlacementEventLog.EventType.PLACEMENT_PROCESSING_FAIL, message);
                    throw new RetryableMessageException(message);
                }

                if(result.getActionName().equals(PlacementRulesActionCode.NO_ACTIONS_FOUND)) {
                    String message = "Placement Rules engine failed for the following reason{s}: \n" + PlacementRulesActionCode.NO_ACTIONS_FOUND +  comment.toString();
                    placementLogService.log(eventData.getTrackingId(), eventData.getCccid(), subjectArea.getDisciplineId(), subjectArea.getVersion(),
                            eventData.getCollegeId(), PlacementEventLog.EventType.PLACEMENT_PROCESSING_FAIL, message);
                    placementNotificationService.placementComponents(collegeMisCode, cccid, eventData.getTrackingId());
                    return;
                }
            }
            if(!wasSuccessful) {
                String message = "Placement Rules engine failed for the following reason{s}: \n" + " WAS UNSUCCESSFUL " + comment.toString();
                placementLogService.log(eventData.getTrackingId(), eventData.getCccid(),  subjectArea.getDisciplineId(), subjectArea.getVersion(),
                        eventData.getCollegeId(), PlacementEventLog.EventType.PLACEMENT_PROCESSING_FAIL, message);
                placementNotificationService.placementComponents(collegeMisCode, cccid, eventData.getTrackingId());
                throw new StoreMessageException(message);
            } else {
                placementLogService.log(eventData.getTrackingId(), eventData.getCccid(),  subjectArea.getDisciplineId(), subjectArea.getVersion(),
                        eventData.getCollegeId(), PlacementEventLog.EventType.PLACEMENT_PROCESSING_COMPLETE,
                        "Placement Rules engine shows completion of rules application, engine results should be returned asynchronously.");
                placementNotificationService.placementComponents(collegeMisCode, cccid, eventData.getTrackingId());
            }
        } catch (RetryableMessageException | StoreMessageException re) {
            throw re;
        } catch (PoorlyFormedRequestException pe) {
            // the rules engine might fail with a error, which might be solved in the future. We will put the message aback on the queue.
            placementLogService.log(eventData.getTrackingId(), eventData.getCccid(), subjectArea.getDisciplineId(), subjectArea.getVersion(),
                    eventData.getCollegeId(), PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_PROCESSING_FAIL,
                    "Multiple Measure Rules engine failed for:" + pe.getMessage());
            throw new RetryableMessageException(pe);
        } catch (Exception exception) {
            placementLogService.log(eventData.getTrackingId(), eventData.getCccid(), subjectArea.getDisciplineId(), subjectArea.getVersion(),
                    eventData.getCollegeId(), PlacementEventLog.EventType.PLACEMENT_PROCESSING_FAIL,
                    "Placement Rules engine threw an exception:" + ExceptionUtils.getFullException(exception));
        }
    }

    /*
     * (non-Javadoc)
     * @see org.cccnext.tesuto.service.placement.PlacementEventService#processPlacementCreation(java.util.Collection)
     */
    @Override
    public void addPlacement(Collection<PlacementActionResult> placementResults) {
        for (PlacementActionResult placementActionResult : placementResults) {
            try {
                placementLogService.log(placementActionResult.getTrackingId(), placementActionResult.getCccid(), placementActionResult.getSubjectAreaId(),
                        placementActionResult.getSubjectAreaVersion(), placementActionResult.getCollegeId(), PlacementEventLog.EventType.PLACEMENT_PROCESSING_COMPLETE,
                        "Placement Rules engine shows completed fact collection of rules application.");

                PlacementViewDto placement = createPlacementViewDto(placementActionResult);

                if (placementDuplicationCheckService.isDuplicateNewPlacement(placement)) {
                    // here we actually have all the instructions to create a new placement.
                    placementLogService.log(placementActionResult.getTrackingId(), placementActionResult.getCccid(), placementActionResult.getSubjectAreaId(),
                            placementActionResult.getSubjectAreaVersion(), placementActionResult.getCollegeId(),
                            PlacementEventLog.EventType.PLACEMENT_DUPLICATE,
                            "A duplicate placement aready exists for the student.");

                    // duplicate placement should also still trigger placement assignment
                    requestAssignedPlacement(placement);
                    return;
                }
                // here we actually have all the instructions to create a new placement.
                placementLogService.log(placementActionResult.getTrackingId(), placementActionResult.getCccid(), placementActionResult.getSubjectAreaId(),
                        placementActionResult.getSubjectAreaVersion(), placementActionResult.getCollegeId(), PlacementEventLog.EventType.PLACEMENT_SAVE_START,
                        "Placement Rules engine shows start creating placement.");

                placementService.addPlacement(placement);

                // after a successful save we request an assignment
                requestAssignedPlacement(placement);

                placementLogService.log(placementActionResult.getTrackingId(), placementActionResult.getCccid(), placementActionResult.getSubjectAreaId(),
                        placementActionResult.getSubjectAreaVersion(), placementActionResult.getCollegeId(), PlacementEventLog.EventType.PLACEMENT_SAVE_COMPLETE,
                        "Placement Rules engine complete creating placement.");
            } catch (RuntimeException re) {
                placementLogService.log(placementActionResult.getTrackingId(), placementActionResult.getCccid(), placementActionResult.getSubjectAreaId(),
                        placementActionResult.getSubjectAreaVersion(), placementActionResult.getCollegeId(), PlacementEventLog.EventType.PLACEMENT_SAVE_FAILURE,
                        "Placement Rules engine failed creating placement.");
                throw re;
            }
        }
    }

    private PlacementViewDto createPlacementViewDto(PlacementActionResult placementActionResult) {
        PlacementViewDto placement = mapper.map(placementActionResult, PlacementViewDto.class);
        VersionedSubjectAreaViewDto versionedSubjectArea =  subjectAreaService.getVersionedSubjectAreaDto(placementActionResult.getSubjectAreaId(), placementActionResult.getSubjectAreaVersion());
        placement.setSubjectAreaName(versionedSubjectArea.getTitle());
        placement.setDisciplineId(placementActionResult.getSubjectAreaId());
        placement.setCreateRuleSetId(placementActionResult.getRuleSetId());
        placement.setAssignedRuleSetId(null);
        placement.setAssignedDate(null);
        placement.setAssigned(false);
        placement.setElaIndicator(placementActionResult.getElaIndicator());
        placement.setCreatedOn(Calendar.getInstance().getTime());
        List<PlacementComponentViewDto> components = placementComponentService.getPlacementComponents(placementActionResult.getPlacementComponentIds());
        placement.setPlacementComponents(new HashSet<PlacementComponentViewDto>(components));
        placement.setVersionedSubjectAreaViewDto(versionedSubjectArea);
        return placement;
    }

    /*
     * (non-Javadoc)
     * @see org.cccnext.tesuto.service.placement.PlacementEventService#requestAssignedPlacement(org.cccnext.tesuto.domain.multiplemeasures.PlacementActionResult)
     */
    public void requestAssignedPlacement(PlacementViewDto placement) {
        PlacementActionResult eventData = mapper.map(placement, PlacementActionResult.class);
        eventData.setPlacementId( placement.getId());
        if (placement.getVersionedSubjectAreaViewDto() != null) {
            eventData.setSubjectAreaId(placement.getVersionedSubjectAreaViewDto().getDisciplineId());
            eventData.setSubjectAreaVersion(placement.getVersionedSubjectAreaViewDto().getVersion());
        }
        requestAssignedPlacement(eventData);
    }

    /**
     * This is an asynchronous call to the rules service to initiate the assignment of a placement. A successful rule
     * execution will call assignPlacement to create the placement.
     *
     * @throws RetryableMessageException if the caller should try to re-execute the placement component creation.
     * @throws Exception if the problem cannot be resolved by re-executing the call
     */
    public void requestAssignedPlacement(PlacementActionResult eventData) {
        String cccid = eventData.getCccid();
        String collegeMisCode = eventData.getCollegeId();
        Integer subjectAreaId = eventData.getSubjectAreaId();
        Integer subjectAreaVersion = eventData.getSubjectAreaVersion();

        placementLogService.log(eventData.getTrackingId(), cccid, subjectAreaId, subjectAreaVersion,
                collegeMisCode, PlacementEventLog.EventType.ASSIGN_PLACEMENT_PROCESSING_START,
                "Placement Assignment Rules engine shows starting of rules application.");

        // need to collect all the placements for the student/college/subject area
        Map<String,String> facts = initFactMap(eventData, PlacementMapKeys.EVENT_TRIGGER_ASSIGNMENT);

        VersionedSubjectAreaViewDto subjectArea = subjectAreaService.getVersionedSubjectAreaDto(subjectAreaId, subjectAreaVersion);

        facts.put(PlacementMapKeys.RULE_SET_ID_KEY, subjectArea.getCompetencyAttributes().getMmAssignedPlacementLogic());

        try {
            List<ActionResult> actionResults = ruleSetReader.executeRulesEngine(engineId, facts);
            StringBuilder comment = new StringBuilder();
            Boolean wasSuccessful = true;

            for(ActionResult result:actionResults) {
                comment.append(result.getActionName()).append("\n");
                if(result instanceof ErrorActionResult) {
                    comment.append(result.getMessage()).append("\n");
                    wasSuccessful = false;
                }
                if(result.getActionName().equals(PlacementRulesActionCode.FAILED_ON_VALIDATE)) {
                    placementLogService.log(eventData.getTrackingId(), cccid, subjectAreaId, subjectAreaVersion,
                            collegeMisCode, PlacementEventLog.EventType.ASSIGN_PLACEMENT_PROCESSING_FAIL,
                            "Placement Assignment Rules engine failed for the following reason{s}: \n" + PlacementRulesActionCode.FAILED_ON_VALIDATE + comment.toString());
                    placementNotificationService.placementComponents(collegeMisCode, cccid, eventData.getTrackingId());
                    return;
                }

                if(result.getActionName().equals(PlacementRulesActionCode.PLACEMENT_SERVICE_DOWN)) {
                    String message = "Placement Assignment engine failed for the following reason{s}: " + PlacementRulesActionCode.PLACEMENT_SERVICE_DOWN
                            + "\n" + comment.toString();
                    placementLogService.log(eventData.getTrackingId(), eventData.getCccid(), subjectArea.getDisciplineId(), 0, eventData.getCollegeId(),
                            PlacementEventLog.EventType.ASSIGN_PLACEMENT_PROCESSING_FAIL, message);
                    throw new RetryableMessageException(message);
                }

                if (StringUtils.startsWithIgnoreCase( result.getActionName(),PlacementRulesActionCode.RULE_ARE_LOADING)) {
                    String message = "Placement Rules eare still loading, retry message.";
                    placementLogService.log(eventData.getTrackingId(), eventData.getCccid(), subjectArea.getDisciplineId(), 0, eventData.getCollegeId(),
                            PlacementEventLog.EventType.ASSIGN_PLACEMENT_PROCESSING_FAIL, message);
                    throw new RetryableMessageException(message);
                }

                if(result.getActionName().equals("NO_ACTIONS_FOUND")) {
                    String message = "Placement Assignment Rules engine failed for the following reason{s}: \n NO_ACTIONS_FOUND " +  comment.toString();
                    placementLogService.log(eventData.getTrackingId(), cccid, subjectAreaId, subjectAreaVersion,
                            collegeMisCode, PlacementEventLog.EventType.ASSIGN_PLACEMENT_PROCESSING_FAIL, message);
                    placementNotificationService.placementComponents(collegeMisCode, cccid, eventData.getTrackingId());
                    return;
                }
            }
            if(!wasSuccessful) {
                String message = "Placement Assignment Rules engine failed for the following reason{s}: \n" + " Reason Unknown " + comment.toString();
                placementLogService.log(eventData.getTrackingId(), cccid, subjectAreaId, subjectAreaVersion,
                        collegeMisCode, PlacementEventLog.EventType.ASSIGN_PLACEMENT_PROCESSING_FAIL, message);
                throw new StoreMessageException(message);
            } else {
                placementLogService.log(eventData.getTrackingId(), cccid, subjectAreaId, subjectAreaVersion,
                        collegeMisCode, PlacementEventLog.EventType.ASSIGN_PLACEMENT_PROCESSING_COMPLETE,
                        "Placement Assignment Rules engine shows completion of rules application, engine results should be returned asynchronously.");
            }
        } catch ( RetryableMessageException | StoreMessageException e) {
            throw e;
        } catch (PoorlyFormedRequestException pe) {
            // the rules engine might fail with a error, which might be solved in the future. We will put the message aback on the queue.
            placementLogService.log(eventData.getTrackingId(), cccid, subjectAreaId, subjectAreaVersion,
                    collegeMisCode, PlacementEventLog.EventType.ASSIGN_PLACEMENT_PROCESSING_FAIL,
                    "Placement Assignment Rules engine failed for:" + ExceptionUtils.getFullException(pe));
            throw new RetryableMessageException(pe);
        } catch (Exception exception) {
            placementLogService.log(eventData.getTrackingId(), cccid, subjectAreaId, subjectAreaVersion,
                    collegeMisCode, PlacementEventLog.EventType.ASSIGN_PLACEMENT_PROCESSING_FAIL,
                    "Placement Assignment Rules engine failed for:" + ExceptionUtils.getFullException(exception));
        }
    }

    @Override
    public void assignPlacement(PlacementViewDto placementViewDto) {
        try {
            // here we actually have all the instructions to create a new placement.
            placementLogService.log(placementViewDto.getTrackingId(), placementViewDto.getCccid(), placementViewDto.getDisciplineId(),
                    placementViewDto.getSubjectAreaVersion(), placementViewDto.getCollegeId(), PlacementEventLog.EventType.ASSIGN_PLACEMENT_PROCESSING_COMPLETE,
                    "Placement Creation Rules engine shows start assigning placement.");

            placementViewDto.setAssigned(true);

            if (placementDuplicationCheckService.isDuplicateAssignedPlacement(placementViewDto)) {
                placementLogService.log(placementViewDto.getTrackingId(), placementViewDto.getCccid(), placementViewDto.getDisciplineId(),
                        placementViewDto.getSubjectAreaVersion(), placementViewDto.getCollegeId(), PlacementEventLog.EventType.ASSIGN_PLACEMENT_DUPLICATE,
                        "Placement Creation Rules engine shows duplicate assigning placement.");
                return;
            }
            placementLogService.log(placementViewDto.getTrackingId(), placementViewDto.getCccid(), placementViewDto.getDisciplineId(),
                    placementViewDto.getSubjectAreaVersion(), placementViewDto.getCollegeId(), PlacementEventLog.EventType.ASSIGN_PLACEMENT_SAVE_START,
                    "Placement Creation Rules engine shows start assigning placement.");
            placementService.updateAssignedPlacements(placementViewDto);
            placementLogService.log(placementViewDto.getTrackingId(), placementViewDto.getCccid(), placementViewDto.getDisciplineId(),
                    placementViewDto.getSubjectAreaVersion(), placementViewDto.getCollegeId(), PlacementEventLog.EventType.ASSIGN_PLACEMENT_SAVE_COMPLETE,
                    "Placement Creation Rules engine complete assigning placement.");
        } catch (RuntimeException re) {
            placementLogService.log(placementViewDto.getTrackingId(), placementViewDto.getCccid(), placementViewDto.getDisciplineId(),
                    placementViewDto.getSubjectAreaVersion(), placementViewDto.getCollegeId(), PlacementEventLog.EventType.ASSIGN_PLACEMENT_SAVE_FAILURE,
                    "Placement Creation Rules engine failed assigning placement.");
            throw re;
        }
    }

    /*
     * (non-Javadoc)
     * @see org.cccnext.tesuto.service.placement.PlacementEventService#processPlacementCreation(java.util.Collection)
     */
    @Override
    public void assignPlacement(PlacementActionResult placementActionResult) {
        PlacementViewDto placementViewDto = new PlacementViewDto();
        placementViewDto.setId(placementActionResult.getPlacementId());
        placementViewDto.setAssignedRuleSetId(placementActionResult.getRuleSetId());
        placementViewDto.setAssignedDate(Calendar.getInstance().getTime());
        assignPlacement(placementViewDto);
    }

    private Map<String, String> initFactMap(PlacementActionResult eventData, String eventTrigger) {
        Map<String,String> facts = new HashMap<>();
        facts.put(PlacementMapKeys.TRACKING_ID_KEY, eventData.getTrackingId());
        facts.put(PlacementMapKeys.CCCID_KEY, eventData.getCccid());
        facts.put(PlacementMapKeys.MISCODE_KEY, eventData.getCollegeId());
        facts.put(PlacementMapKeys.SUBJECT_AREA_KEY, String.valueOf(eventData.getSubjectAreaId()));
        facts.put(PlacementMapKeys.SUBJECT_AREA_VERSION_KEY, String.valueOf(eventData.getSubjectAreaVersion()));
        facts.put(PlacementMapKeys.EVENT_TRIGGER_KEY, eventTrigger);
        return facts;
    }
}
