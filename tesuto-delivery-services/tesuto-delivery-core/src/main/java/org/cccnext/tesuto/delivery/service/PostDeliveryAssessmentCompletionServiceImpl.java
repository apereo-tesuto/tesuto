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
package org.cccnext.tesuto.delivery.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto;
import org.cccnext.tesuto.content.service.AssessmentReader;
import org.cccnext.tesuto.content.service.CompetencyMapOrderReader;
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.model.internal.Outcome;
import org.cccnext.tesuto.message.service.MessagePublisher;
import org.cccnext.tesuto.placement.dto.AssessmentCompletePlacementInputDto;
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto;
import org.dozer.Mapper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class PostDeliveryAssessmentCompletionServiceImpl implements PostDeliveryAssessmentCompletionService {

    private final String ENTRY_TESTLET = "ENTRY-TESTLET";

    AssessmentReader assessmentReader;

    AssessmentSessionDao dao;

    private CompetencyMapOrderReader competencyMapOrderReader;

    private boolean sendNotification;

    private MessagePublisher<PlacementEventInputDto> assessmentCompletePublisher;
    
    private MessagePublisher<PlacementEventInputDto> multipleMeasurePlacementRequestor;
    
    @Autowired
    private Mapper mapper;

    public AssessmentSessionDao getDao() {
        return dao;
    }

    public void setDao(AssessmentSessionDao dao) {
        this.dao = dao;
    }

    public AssessmentReader getAssessmentReader() {
        return assessmentReader;
    }

    public void setAssessmentReader(AssessmentReader assessmentReader) {
        this.assessmentReader = assessmentReader;
    }
    

    public CompetencyMapOrderReader getCompetencyMapOrderReader() {
        return competencyMapOrderReader;
    }

    public void setCompetencyMapOrderReader(CompetencyMapOrderReader competencyMapOrderReader) {
        this.competencyMapOrderReader = competencyMapOrderReader;
    }

    public boolean isSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(boolean sendNotification) {
        this.sendNotification = sendNotification;
    }

    public void setAssessmentCompletePublisher(MessagePublisher<PlacementEventInputDto> assessmentCompletePublisher) {
        this.assessmentCompletePublisher = assessmentCompletePublisher;
    }

    public void setMultipleMeasurePlacementRequestor(MessagePublisher<PlacementEventInputDto> multipleMeasurePlacementRequestor) {
        this.multipleMeasurePlacementRequestor = multipleMeasurePlacementRequestor;
    }

    public void setMapper(Mapper mapper) {
        this.mapper = mapper;
    }

    @Async
    @Override
    public void completeAssessment(String assessmentSessionId, UserAccountDto requestor) {
        AssessmentSession assessmentSession = dao.find(assessmentSessionId);
        assessmentSession.setCompetencyMapDisciplineFromEntryTestlet(determineCompetencyMapDisciplineFromEntryTestlet(assessmentSession));
        addCompetencyMapOrderIds(assessmentSession);
        dao.save(assessmentSession);
        requestPlacements(assessmentSession, false);
    }

    @Transactional
    @Override
    public void completeAssessmentSync(String assessmentSessionId, UserAccountDto requestor) {
        AssessmentSession assessmentSession = dao.find(assessmentSessionId);
        assessmentSession.setCompetencyMapDisciplineFromEntryTestlet(determineCompetencyMapDisciplineFromEntryTestlet(assessmentSession));
        addCompetencyMapOrderIds(assessmentSession);
        dao.save(assessmentSession);
        requestPlacements(assessmentSession, false);
    }

    @Override
    public void requestPlacements(AssessmentSession assessmentSession, boolean newPlacementsOnly) {
        requestPlacements(assessmentSession, null, newPlacementsOnly);
    }

    @Override
    public void requestPlacements(AssessmentSession assessmentSession, String collegeMisCode, boolean newPlacementsOnly) {
        AssessmentCompletePlacementInputDto assessmentCompletePlacementDecisionDTO = generateAssessmentCompletePlacementDto(assessmentSession, newPlacementsOnly);
        if (StringUtils.isNotBlank( collegeMisCode)) {
            assessmentCompletePlacementDecisionDTO.setCollegeMisCodes(Collections.singleton(collegeMisCode));
        }
        
        if(sendNotification && assessmentCompletePlacementDecisionDTO != null) {
            assessmentCompletePublisher.sendMessage(assessmentCompletePlacementDecisionDTO);
            PlacementEventInputDto placementEventInputDto = getPlacementEventInputDto(assessmentCompletePlacementDecisionDTO);
            multipleMeasurePlacementRequestor.sendMessage(placementEventInputDto);
        }
    }

    private PlacementEventInputDto getPlacementEventInputDto(AssessmentCompletePlacementInputDto assessmentCompletePlacementDecisionDTO) {
        return mapper.map(assessmentCompletePlacementDecisionDTO, PlacementEventInputDto.class);
    }
    
    public AssessmentCompletePlacementInputDto generateAssessmentCompletePlacementDto(AssessmentSession session, boolean onlyNewPlacements){

        if(getAssessment(session) != null
                && session.getAssessment().getAssessmentMetadata() != null
                && session.getAssessment().getAssessmentMetadata().isGeneratePlacement()) {
            AssessmentCompletePlacementInputDto placementEventInputDTO = new AssessmentCompletePlacementInputDto();
            placementEventInputDTO.setAssessmentSessionId(session.getAssessmentSessionId());
            placementEventInputDTO.setStudentAbility(session.getOutcome(Outcome.CAI_STUDENT_ABILITY).getValue());
            placementEventInputDTO.setCccid(session.getUserId());
            placementEventInputDTO.setAssessmentTitle(session.getAssessment().getTitle());
            placementEventInputDTO.setCompletionDate(session.getCompletionDate());
            placementEventInputDTO.setProcessOnlyNewPlacements(onlyNewPlacements);
            placementEventInputDTO.setTrackingId(UUID.randomUUID().toString());
            placementEventInputDTO.setElaIndicator(session.getCompetencyMapDisciplineFromEntryTestlet());
            if (getAssessment(session).getAssessmentMetadata() != null
                    && getAssessment(session).getAssessmentMetadata().getCompetencyMapDisciplines() != null) {
                placementEventInputDTO.setCompetencyMapDisciplines(new HashSet<>(getAssessment(session).getAssessmentMetadata().getCompetencyMapDisciplines()));
            }
            return placementEventInputDTO;
        }
        return null;
    }

    public String determineCompetencyMapDisciplineFromEntryTestlet(AssessmentSession session){
        String sectionIdentifier = null;
        if(getAssessment(session) != null && getAssessment(session).getAssessmentMetadata() != null) {
            sectionIdentifier = getSectionIdentifier(session.getSequence(), getAssessment(session).getAssessmentMetadata());
        }
        return sectionIdentifier;
    }

    // Expects section and metadata to be valid.
    private String getSectionIdentifier(Map<String, String> sequence, AssessmentMetadataDto metadataDto){
        for (String key : sequence.keySet()) {
            if (isEntryTestlet(key, metadataDto)) {
                return metadataDto.getSectionCompetencyMapDiscipline(key);
            }
        }
        return null;
    }

    private boolean isEntryTestlet(String key, AssessmentMetadataDto metadataDto){
        return (ENTRY_TESTLET).equalsIgnoreCase(metadataDto.getSectionType(key));
    }

    private void addCompetencyMapOrderIds(AssessmentSession session) {
        if(session.getCompetencyMapDisciplineFromEntryTestlet() != null) {
            Map<String,String> competencyMapOrderIds = new HashMap<>();
            String competencyMapOrderId = competencyMapOrderReader.findLatestPublishedIdByCompetencyMapDiscipline(session.getCompetencyMapDisciplineFromEntryTestlet());
            if(competencyMapOrderId != null) {
                competencyMapOrderIds.put(session.getCompetencyMapDisciplineFromEntryTestlet(), competencyMapOrderId);
                session.setCompetencyMapOrderIds(competencyMapOrderIds);
            }
        } else if(getAssessment(session).getAssessmentMetadata() != null) {
            if(CollectionUtils.isNotEmpty(session.getAssessment().getAssessmentMetadata().getCompetencyMapDisciplines())) {
                Map<String,String> competencyMapOrderIds = new HashMap<>();
                for(String competencyMapDiscipline :getAssessment(session).getAssessmentMetadata().getCompetencyMapDisciplines()) {
                    String competencyMapOrderId = competencyMapOrderReader.findLatestPublishedIdByCompetencyMapDiscipline(competencyMapDiscipline);
                    if(competencyMapOrderId != null) {
                        competencyMapOrderIds.put(competencyMapDiscipline, competencyMapOrderId);
                    }
                }
                if(!competencyMapOrderIds.isEmpty()) {
                    session.setCompetencyMapOrderIds(competencyMapOrderIds);
                }
            }
        }
    }

    private AssessmentDto getAssessment(AssessmentSession session) {
        if(session.getAssessment() == null) {
            session.setAssessment(assessmentReader.read(session.getContentId()));
        }
        return session.getAssessment();
    }
}
