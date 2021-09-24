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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.cccnext.tesuto.placement.view.VersionedSubjectAreaViewDto;

import org.cccnext.tesuto.domain.multiplemeasures.PlacementComponentActionResult;
import org.cccnext.tesuto.domain.multiplemeasures.PlacementMapKeys;
import org.cccnext.tesuto.exception.PoorlyFormedRequestException;
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto;
import org.cccnext.tesuto.placement.model.MmapPlacementComponent;
import org.cccnext.tesuto.placement.model.PlacementEventLog;
import org.cccnext.tesuto.placement.repository.jpa.PlacementComponentRepository;
import org.cccnext.tesuto.placement.view.CompetencyAttributesViewDto;
import org.cccnext.tesuto.placement.view.DisciplineSequenceViewDto;
import org.cccnext.tesuto.placement.view.DisciplineViewDto;
import org.cccnext.tesuto.placement.view.MmapDataSourceType;
import org.cccnext.tesuto.placement.view.PlacementComponentViewDto;
import org.cccnext.tesuto.rules.service.RuleServiceWebServiceQueries;
import org.cccnext.tesuto.rules.service.RuleSetReader;
import org.cccnext.tesuto.util.TesutoUtils;
import org.ccctc.common.droolscommon.action.result.ActionResult;
import org.ccctc.common.droolscommon.action.result.ErrorActionResult;
import org.ccctc.common.droolscommon.model.RuleSetDTO;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@Service
@Transactional
public class MultipleMeasurePlacementComponentService extends AbstractPlacementComponentService {

    
    @Value("${placement.rules.engine.id}")
    private String engineId;

    @Autowired
    private RuleSetReader client;

    @Autowired
    private PlacementComponentRepository placementComponentRepository;

    @Autowired
    private PlacementComponentAssembler placementComponentAssembler;

    @Autowired
    private PlacementDuplicationCheckService placementDuplicationCheckService;

    @Autowired
    private RuleServiceWebServiceQueries ruleServiceWebServiceQueries;

    /**
     * This is an asynchronous call to the rules service to initiate the creation of a placement component.
     * The implementation always returns null. A successful rule execution will call addPlacementComponent
     * to create the placement.
     *
     * @return null
     * @throws RetryableMessageException if the caller should try to re-execute the placement component creation.
     * @throws Exception if the problem cannot be resolved by re-executing the call
     */
    @Override
    public PlacementComponentViewDto createPlacementComponent(String collegeId, DisciplineViewDto discipline,
            PlacementEventInputDto eventData) throws Exception {

        if(!discipline.getCompetencyAttributes().isOptInMultiMeasure()){
            placementLogService.log(eventData.getTrackingId(), eventData.getCccid(), discipline.getDisciplineId(), 0, collegeId,
                    PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_OPT_OUT,
                    "Subject Area opted out of multiple measures.");
            return null;
        }

        VersionedSubjectAreaViewDto versionedSubjectArea = subjectAreaService.getPublishedVersionForSubjectArea(discipline.getDisciplineId());
        if (versionedSubjectArea == null) {
            String message = String.format("Versioned Subject Areas  Not Found for disciplineId: %s", discipline.getDisciplineId());
            log.error(message);
            placementLogService.log(eventData.getTrackingId(),
                    eventData.getCccid(),discipline.getDisciplineId(), 0,
                    discipline.getCollegeId(),
                    PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_ADD_FAIL,
                    "Unable to find a versioned subject area.");
            return null;
        }

        if (versionedSubjectArea.getCompetencyAttributes() == null) {
            String message = String.format("No CompetencyAttributes found for Subject Area with disciplineId: %s", discipline.getDisciplineId());
            log.error( message);
            placementLogService.log(eventData.getTrackingId(),
                    eventData.getCccid(),discipline.getDisciplineId(), 0,
                    discipline.getCollegeId(),
                    PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_ADD_FAIL,
                    "Subject area does not have competency attributes.");
            return null;
        }
        CompetencyAttributesViewDto attr = versionedSubjectArea.getCompetencyAttributes() ;

        placementLogService.log(eventData.getTrackingId(), eventData.getCccid(), discipline.getDisciplineId(), 0, collegeId,
                PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_PROCESSING_START,
                "Multiple Measure Rules engine shows starting of rules application.");

        //TODO refactor to have logic use competencyAttributes as object
        Map<String, String> facts = new HashMap<>();
        facts.put(PlacementMapKeys.CCCID_KEY, eventData.getCccid());
        facts.put(PlacementMapKeys.MISCODE_KEY, collegeId);
        facts.put(PlacementMapKeys.SUBJECT_AREA_KEY, discipline.getDisciplineId().toString());
        facts.put(PlacementMapKeys.TRACKING_ID_KEY, eventData.getTrackingId());
        facts.put(PlacementMapKeys.RULE_SET_ID_KEY, attr.getMmDecisionLogic());
        facts.put(PlacementMapKeys.OPT_IN_KEY, new Boolean(attr.isUseSelfReportedDataForMM()).toString());
        facts.put(PlacementMapKeys.PREREQUISITE_GENERAL_EDUCATION_KEY, attr.getPrerequisiteGeneralEducation());
        facts.put(PlacementMapKeys.PREREQUISITED_STATISTICS_KEY, attr.getPrerequisiteStatistics());
        facts.put(PlacementMapKeys.EVENT_TRIGGER_KEY, PlacementMapKeys.EVENT_TRIGGER_COMPONENT);
        facts.put(PlacementMapKeys.HIGHEST_READING_LEVEL_COURSE_KEY,(attr.getHighestLevelReadingCourse() == null ? "" :  attr.getHighestLevelReadingCourse()));

        try {
            List<ActionResult> results = client.executeRulesEngine(engineId, facts);
            StringBuilder comment = new StringBuilder();
            Boolean wasSuccessful = true;
            for(ActionResult result:results) {
                comment.append(result.getActionName()).append("\n");
                if(result instanceof ErrorActionResult) {
                    comment.append(result.getMessage()).append("\n");
                    wasSuccessful = false;
                }
                if(result.getActionName().equals(PlacementRulesActionCode.FAILED_ON_VALIDATE)) {
                    placementLogService.log(eventData.getTrackingId(), eventData.getCccid(), discipline.getDisciplineId(), 0, collegeId,
                            PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_FAILED_ON_VALIDATE,
                            "Multiple Measure Rules engine failed for the following reason{s}: \n" +comment.toString());
                    return null;
                }

                if(result.getActionName().equals(PlacementRulesActionCode.VARIABLE_SET_NOT_FOUND)) {
                    String message = "Multiple Measure Rules engine failed for the following reason{s}: \n" + comment.toString();
                    placementLogService.log(eventData.getTrackingId(), eventData.getCccid(), discipline.getDisciplineId(), 0, collegeId,
                            PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_FAILED_ON_VALIDATE, message);
                    throw new RetryableMessageException(message);
                }

                if (StringUtils.startsWithIgnoreCase( result.getActionName(),PlacementRulesActionCode.RULE_ARE_LOADING)) {
                    String message = "Multiple Measure Rules are still loading, retry message.";
                    placementLogService.log(eventData.getTrackingId(), eventData.getCccid(), discipline.getDisciplineId(), 0, collegeId,
                            PlacementEventLog.EventType.ASSIGN_PLACEMENT_PROCESSING_FAIL, message);
                    throw new RetryableMessageException(message);
                }

                if(result.getActionName().equals(PlacementRulesActionCode.PLACEMENT_SERVICE_DOWN)) {
                    String message = "Multiple Measure Rules engine failed for the following reason{s}: \n" + comment.toString();
                    placementLogService.log(eventData.getTrackingId(), eventData.getCccid(), discipline.getDisciplineId(), 0, collegeId,
                            PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_FAILED_ON_VALIDATE,
                            message);
                    throw new RetryableMessageException(message);
                }
                if(result.getActionName().equals(PlacementRulesActionCode.NO_ACTIONS_FOUND)) {
                    String message = "Multiple Measure Rules engine failed for the following reason{s}: \n" + comment.toString();
                    placementLogService.log(eventData.getTrackingId(), eventData.getCccid(), discipline.getDisciplineId(), 0, collegeId,
                            PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_PROCESSING_COMPLETE_NO_PLACEMENT_GENERATED,
                            message);
                    return null;
                }
            }
            if(!wasSuccessful) {
                String message = "Multiple Measure Rules engine failed for the following reason{s}: \n" +comment.toString();
                placementLogService.log(eventData.getTrackingId(), eventData.getCccid(), discipline.getDisciplineId(), 0, collegeId,
                        PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_PROCESSING_FAIL, message);
                throw new StoreMessageException(message);
            } else {
                placementLogService.log(eventData.getTrackingId(), eventData.getCccid(), discipline.getDisciplineId(), 0, collegeId,
                        PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_PROCESSING_COMPLETE,
                        "Multiple Measure Rules engine shows completion of rules application, engine results should be returned asynchronously.");
            }
        } catch (RetryableMessageException | StoreMessageException re) {
            throw re;
        } catch (PoorlyFormedRequestException pe) {
            // the rules engine might fail with a error, which might be solved in the future. We will put the message aback on the queue.
            placementLogService.log(eventData.getTrackingId(), eventData.getCccid(), discipline.getDisciplineId(), 0, collegeId,
                    PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_PROCESSING_FAIL,
                    "Multiple Measure Rules engine failed for:" + pe.getMessage());
            throw new RetryableMessageException(pe);
        } catch (Exception exception) {
            placementLogService.log(eventData.getTrackingId(), eventData.getCccid(), discipline.getDisciplineId(), 0, collegeId,
                    PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_PROCESSING_FAIL,
                    "Multiple Measure Rules engine failed for:" + exception.getMessage());
            return null;
        }

        return null;
    }

    public PlacementComponentViewDto addPlacementComponent(PlacementComponentActionResult placementData)
            throws JsonProcessingException, IOException, ScriptException {

        Integer subjectAreaId = Integer.parseInt(placementData.getSubjectArea());
        if(placementData.getInsufficientData()) {
            placementLogService.log(placementData.getTrackingId(), placementData.getCccid(),
                    subjectAreaId, 0, placementData.getCollegeId(),
                    PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_INSUFFICIENT_DATA,
                    "Multiple Measure Rules engine did not have sufficient user information to create a valid placement component.");
            return null;
        }

        placementLogService.log(placementData.getTrackingId(), placementData.getCccid(),
                subjectAreaId, 0, placementData.getCollegeId(),
                PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_ADD_START,
                "Multiple Measure Rules engine has returned component placement precursor.");

        PlacementComponentViewDto viewDto = null;

        MmapPlacementComponent placementComponent = new MmapPlacementComponent();

        VersionedSubjectAreaViewDto versionedSubjectArea = subjectAreaService.getPublishedVersionForSubjectArea(subjectAreaId);
        if (versionedSubjectArea == null) {
            log.error(String.format("Versioned Subject Areas  Not Found for disciplineId: %s", subjectAreaId));
            placementLogService.log(placementData.getTrackingId(),
                    placementData.getCccid(),subjectAreaId, 0,
                    placementData.getCollegeId(),
                    PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_ADD_FAIL,
                    "Unable to find a versioned subject area.");
            return null;
        }
        DisciplineSequenceViewDto disciplineSequence = placementHelperService.findClosestToTransferLevel(
                versionedSubjectArea.getDisciplineSequences(),
                placementData.getLevelsBelowTransfer(),
                placementData.getPrograms());

        placementComponent.setCccid(placementData.getCccid());
        placementComponent.setCollegeId(placementData.getCollegeId());
        placementComponent.setSubjectAreaId(versionedSubjectArea.getDisciplineId());
        placementComponent.setSubjectAreaVersion(versionedSubjectArea.getVersion());
        placementComponent.setMmapRowNumber(Integer.parseInt(placementData.getRowNumber()));
        placementComponent.setMmapRuleSetId(placementData.getRuleSetId());
        placementComponent.setMmapRuleSetRowId(placementData.getRuleSetRowId());
        placementComponent.setMmapRuleId(placementData.getRuleId());
        placementComponent.setMmapCourseCategories(new ArrayList<String>(placementData.getPrograms()));
        placementComponent.setTrackingId(placementData.getTrackingId());
        placementComponent.setMmapVariableSetId(placementData.getMultipleMeasureVariableSetId());
        placementComponent.setDataSource(placementData.getDataSource());
        placementComponent.setDataSourceDate(placementData.getDataSourceDate());
        placementComponent.setDataSourceType(MmapDataSourceType.parseEnum(placementData.getDataSourceType()));
        placementComponent.setMmapCourseCategories( new ArrayList<String>(placementData.getPrograms()));
        placementComponent.setStandalonePlacement(placementData.isStandalonePlacement());
        if (disciplineSequence != null) {
            log.debug("Setting placementComponent cb21,courseGroup to: " + disciplineSequence.getCb21Code() + "," + disciplineSequence.getCourseGroup());
            placementComponent.setCourseGroup(disciplineSequence.getCourseGroup());
            placementComponent.setCb21Code(disciplineSequence.getCb21Code());
        } else {
            log.error("DisciplineSequence was null, resulting in a placementComponent with null cb21Code and courseGroup!");
            log.error("PlacementComponent: " + placementComponent);
            placementLogService.log(placementData.getTrackingId(),
                    placementData.getCccid(),
                    versionedSubjectArea.getDisciplineId(),
                    versionedSubjectArea.getVersion(),
                    placementData.getCollegeId(),
                    PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_NULL_DISCIPLINE_SEQUENCE,
                    "A null discipline sequence was found for transfer level:" + placementData.getLevelsBelowTransfer() + " and programs: " +  placementData.getPrograms());
            return null;
        }

        if (placementDuplicationCheckService.isDuplicateMmapPlacementComponent(placementComponent)) {
            placementLogService.log(placementData.getTrackingId(), placementData.getCccid(),
                    subjectAreaId, 0, placementData.getCollegeId(),
                    PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_DUPLICATE,
                    "A duplicate MMAP component already exists for the student.");
            // return the first duplicated record which will trigger a placement
            viewDto = placementDuplicationCheckService.firstDuplicateMmapPlacementComponent(placementComponent);
        } else {
            placementComponent.setId(TesutoUtils.newId());
            placementComponent.setCreatedOn(Calendar.getInstance().getTime());
            placementComponent.setTriggerData("");
            placementLogService.log(placementData.getTrackingId(),
                    placementData.getCccid(),
                    versionedSubjectArea.getDisciplineId(),
                    versionedSubjectArea.getVersion(),
                    placementData.getCollegeId(),
                    PlacementEventLog.EventType.PLACEMENT_COMPONENT_MM_ADD_COMPLETE,
                    "Multiple Measure Placement component has been created.");
            if (placementComponent.getMmapRuleSetId() != null && placementComponent.getMmapRuleSetTitle() == null) {
                // set the title value
                RuleSetDTO ruleSetDto = ruleServiceWebServiceQueries.findByRuleSetId(placementComponent.getMmapRuleSetId());
                placementComponent.setMmapRuleSetTitle(ruleSetDto.getTitle());
            }
            viewDto = placementComponentAssembler.assembleDto(placementComponentRepository.save(placementComponent));
        }

        return viewDto;
    }

}
