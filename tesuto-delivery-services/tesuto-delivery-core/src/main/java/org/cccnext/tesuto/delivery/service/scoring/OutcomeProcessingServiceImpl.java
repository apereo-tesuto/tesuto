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
package org.cccnext.tesuto.delivery.service.scoring;

import org.apache.commons.collections.CollectionUtils;
import org.cccnext.tesuto.content.dto.AssessmentBaseType;
import org.cccnext.tesuto.content.dto.AssessmentComponentDto;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto;
import org.cccnext.tesuto.content.dto.item.metadata.enums.ItemBankStatusType;
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto;
import org.cccnext.tesuto.content.dto.shared.AssessmentOutcomeDeclarationDto;
import org.cccnext.tesuto.content.service.AssessmentItemReader;
import org.cccnext.tesuto.content.service.AssessmentReader;
import org.cccnext.tesuto.content.service.UseItemCategoryReader;
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.model.internal.ItemSession;
import org.cccnext.tesuto.delivery.model.internal.Outcome;
import org.cccnext.tesuto.delivery.model.internal.enums.OutcomeDeclarationType;
import org.cccnext.tesuto.delivery.service.AssessmentSessionDao;
import org.cccnext.tesuto.delivery.service.DeliverySearchParameters;
import org.cccnext.tesuto.delivery.service.PsychometricsCalculationService;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This service will handle all outcome processing for delivery. This includes pre/post/during delivery.
 *
 * Currently Assess only handles IMPLICITLY defined outcomes. These are outcomes that CCC has a strict need for and
 * Assess calculates for every assessment package. Note: The external resource outcome is only created if the author
 * has an appropriately defined outcomeDeclaration.
 *
 * In the future authored outcomeProcessing will be handled here.
 *
 * Created by Jason Brown jbrown@unicon.net on 8/24/16.
 */
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class OutcomeProcessingServiceImpl implements OutcomeProcessingService {
	
    AssessmentReader assessmentReader;

    AssessmentSessionDao dao;

    AssessmentItemReader assessmentItemReader;

    UseItemCategoryReader categoryReader;

    PsychometricsCalculationService psychometricsCalculationService;

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

    public AssessmentItemReader getAssessmentItemReader() {
        return assessmentItemReader;
    }

    public void setAssessmentItemReader(AssessmentItemReader assessmentItemReader) {
        this.assessmentItemReader = assessmentItemReader;
    }

    public UseItemCategoryReader getCategoryReader() {
        return categoryReader;
    }

    public void setCategoryReader(UseItemCategoryReader categoryReader) {
        this.categoryReader = categoryReader;
    }

    public PsychometricsCalculationService getPsychometricsCalculationService() {
        return psychometricsCalculationService;
    }

    public void setPsychometricsCalculationService(PsychometricsCalculationService psychometricsCalculationService) {
        this.psychometricsCalculationService = psychometricsCalculationService;
    }

    /**
     *
     * Assess handles outcomeDeclarations set with specific identifiers by the author.
     * This method handles outcomeDeclarations with identifiers that can be parsed using the following format:
     *
     * external-assessmentIdentifier-outcomeIdentifier
     *
     * This should only be called once at the creation of the Session.
     *
     * @param session
     */
    @Override
    public List<Outcome> processExternalResourceOutcomeDeclarations(AssessmentSession session) {
        AssessmentDto assessmentDto = session.getAssessment();
        List<Outcome> outcomeList = new ArrayList<>();
        HashMap<String, List<AssessmentSession>> sessionMap = new HashMap<>(); // session.contentIdentifier -> list of sessions with that contentIdentifier

        if(CollectionUtils.isNotEmpty(assessmentDto.getOutcomeDeclarations())) {
            assessmentDto.getOutcomeDeclarations().forEach(od -> {
                String[] tokens = od.getIdentifier().split("\\.");
                if (tokens.length == 3 && "external".equalsIgnoreCase(tokens[0])) {
                    String contentIdentifier = tokens[1]; // assessment.Identifier or session.contentIdentifier
                    String outcomeIdentifier = tokens[2];

                    //Only search once
                    if(CollectionUtils.isEmpty(sessionMap.get(contentIdentifier))) {
                        DeliverySearchParameters searchParameters = new DeliverySearchParameters();

                        List<String> userIds = new ArrayList<>(1);
                        userIds.add(session.getUserId());
                        searchParameters.setUserIds(userIds);

                        List<String> contentIdentifiers = new ArrayList<>(1);
                        contentIdentifiers.add(tokens[1]);
                        searchParameters.setContentIdentifiers(contentIdentifiers);

                        sessionMap.put(contentIdentifier, dao.search(searchParameters));
                    }
                    outcomeList.add(createExternalResourceOutcome(outcomeIdentifier, od, sessionMap.get(contentIdentifier)));

                }
            });
        }
        return outcomeList;
    }


    private Outcome createExternalResourceOutcome(String outcomeIdentifier, AssessmentOutcomeDeclarationDto od, List<AssessmentSession> externalSessions){
        Double defaultValue = null;
        if(od.getDefaultValue() != null && CollectionUtils.isNotEmpty(od.getDefaultValue().getValues())){
            defaultValue = Double.valueOf(od.getDefaultValue().getValues().get(0));
        }

        Outcome outcome = new Outcome(od.getIdentifier(), defaultValue, od.getBaseType(), OutcomeDeclarationType.IMPLICIT);
        if (CollectionUtils.isNotEmpty(externalSessions)) {
            Double max = null;
            for (AssessmentSession s : externalSessions) {
                Outcome sessionOutcome = s.getOutcome(outcomeIdentifier);
                if(sessionOutcome != null && (max == null || max < sessionOutcome.getValue())){
                    max = sessionOutcome.getValue();
                    outcome.setValue(max);
                }
            }
        }

        if(od.getNormalMaximum() != null && od.getNormalMaximum() < outcome.getValue()){
            outcome.setValue(od.getNormalMaximum());
        }

        if(od.getNormalMinimum() != null && od.getNormalMinimum() > outcome.getValue()){
            outcome.setValue(od.getNormalMinimum());
        }
        return outcome;
    }

    /**
     * Session outcomes must be calculated in order.
     *
     */
    @Override
    public void processAssessmentSessionOutcome(AssessmentSession session, List<ItemSession> itemSessions) {
        calculateSessionOutcome(Outcome.CAI_POINTS_SCORE, session, itemSessions);
        calculateSessionOutcome(Outcome.CAI_PERCENT_SCORE, session, itemSessions);
        calculateSessionOutcome(Outcome.CAI_ODDS_SUCCESS, session, itemSessions);
        calculateSessionOutcome(Outcome.CAI_AVG_ITEM_DIFFICULTY, session, itemSessions);
        calculateSessionOutcome(Outcome.CAI_ITEM_DIFFICULTY_COUNT, session, itemSessions);
        calculateSessionOutcome(Outcome.CAI_STUDENT_ABILITY, session, itemSessions);
        calculateSessionOutcome(Outcome.CAI_REPORTED_SCALE, session, itemSessions);
    }

    @Override
    public void calculateSessionOutcome(String outcomeIdentifier, AssessmentSession session, List<ItemSession> itemSessions) {
        Outcome previousOutcome = session.getOutcome(outcomeIdentifier);
        if (previousOutcome == null) {
            previousOutcome = new Outcome(outcomeIdentifier, 0.0d, AssessmentBaseType.FLOAT, OutcomeDeclarationType.IMPLICIT);
        }

        switch (outcomeIdentifier) {
        case Outcome.CAI_POINTS_SCORE:
            calculatePointScoreOutcome(previousOutcome, itemSessions, session);
            session.addOutcome(previousOutcome);
            break;
        case Outcome.CAI_PERCENT_SCORE:
            calculatePercentageScoreOutcome(previousOutcome, session);
            session.addOutcome(previousOutcome);
            break;
        case Outcome.CAI_ODDS_SUCCESS:
            calculateOddsSuccessOutcome(previousOutcome, session);
            session.addOutcome(previousOutcome);
            break;
        case Outcome.CAI_AVG_ITEM_DIFFICULTY:
            calculateAvgStudentItemDifficultyOutcome(previousOutcome, itemSessions, session);
            session.addOutcome(previousOutcome);
            break;
        case Outcome.CAI_ITEM_DIFFICULTY_COUNT:
            calculateCalibratedDifficultyCount(previousOutcome, itemSessions, session);
            session.addOutcome(previousOutcome);
            break;
        case Outcome.CAI_STUDENT_ABILITY:
            calculateStudentAbilityOutcome(previousOutcome, session);
            session.addOutcome(previousOutcome);
            break;
        case Outcome.CAI_REPORTED_SCALE:
            if(isAssessmentMetadataValid(session.getAssessment())) {
                calculateReportedScale(previousOutcome, session);
                session.addOutcome(previousOutcome);
            }
            break;
        default:
            throw new IllegalArgumentException(String.format("%s is not a supported Outcome", outcomeIdentifier));
        }
    }

    private void calculatePointScoreOutcome(Outcome previousOutcome, List<ItemSession> itemSessions, AssessmentSession session) {
        if(previousOutcome.getNormalMaximum() == null){
           previousOutcome.setNormalMaximum(0.0d);
        }

        if(previousOutcome.getNormalMinimum() == null){
            previousOutcome.setNormalMinimum(0.0d);
        }

        previousOutcome.setNormalMaximum(getMaxPointsPossible(itemSessions, session) + previousOutcome.getNormalMaximum());
        previousOutcome.setNormalMinimum(getMinPointsPossible(itemSessions, session) + previousOutcome.getNormalMinimum());
        previousOutcome.setValue(getPointsEarned(itemSessions, session) + previousOutcome.getValue());
    }

    private void calculatePercentageScoreOutcome(Outcome previousOutcome, AssessmentSession session) {
        Outcome pointScoreOutcome = session.getOutcome(Outcome.CAI_POINTS_SCORE);
        previousOutcome.setValue(psychometricsCalculationService.calculatePerformanceAsPercentage(pointScoreOutcome.getValue(), pointScoreOutcome.getNormalMaximum()));
    }

    private void calculateOddsSuccessOutcome(Outcome previousOutcome, AssessmentSession session){
        Outcome performancePercentageOutcome = session.getOutcome(Outcome.CAI_PERCENT_SCORE);
        previousOutcome.setValue(psychometricsCalculationService.calculateOdds(performancePercentageOutcome.getValue()));
    }

    private void calculateAvgStudentItemDifficultyOutcome(Outcome previousOutcome, List<ItemSession> itemSessions, AssessmentSession session) {
        List<Double> calibratedDifficulties = getCalibratedDifficulties(itemSessions, session);
        if(CollectionUtils.isEmpty(calibratedDifficulties)) {
            log.info("Calibrated Difficulties were empty for itemsessions {}", itemSessions);
            return;
        }
        Outcome countOutcome = session.getOutcome(Outcome.CAI_ITEM_DIFFICULTY_COUNT);
        previousOutcome.setValue(psychometricsCalculationService.calculateAverageStudentItemDifficulty(calibratedDifficulties, previousOutcome.getValue(), countOutcome != null?countOutcome.getValue() : null));
    }

    private void calculateCalibratedDifficultyCount(Outcome previousOutcome, List<ItemSession> itemSessions, AssessmentSession session) {
        List<Double> calibratedDifficulties = getCalibratedDifficulties(itemSessions, session);
        previousOutcome.setValue(previousOutcome.getValue() + calibratedDifficulties.size());
    }

    private void calculateStudentAbilityOutcome(Outcome previousOutcome, AssessmentSession session) {
        Outcome OddsOutcome = session.getOutcome(Outcome.CAI_ODDS_SUCCESS);
        Outcome avgStudentItemDifficultyOutcome = session.getOutcome(Outcome.CAI_AVG_ITEM_DIFFICULTY);
        previousOutcome.setValue(psychometricsCalculationService.calculatedStudentAbility(OddsOutcome.getValue(), avgStudentItemDifficultyOutcome.getValue()));
    }


    private void calculateReportedScale(Outcome previousOutcome, AssessmentSession session) {
        Outcome studentAbilityOutcome = session.getOutcome(Outcome.CAI_STUDENT_ABILITY);
        Double additiveTerm = session.getAssessment().getAssessmentMetadata().getScaleAdditiveTerm();
        Double multiplicativeTerm = session.getAssessment().getAssessmentMetadata().getScaleMultiplicativeTerm();
        previousOutcome.setValue(psychometricsCalculationService.calculateReportingScale(studentAbilityOutcome.getValue(), additiveTerm, multiplicativeTerm));
    }

    private Double getMaxPointsPossible(List<ItemSession> itemSessions, AssessmentSession session){
        Stream<ItemSession> itemSessionStream = filterItemSessions(itemSessions, session).filter(is -> is.getOutcome(Outcome.SCORE).getNormalMaximum() != null);
        Double max = itemSessionStream.mapToDouble(is -> (Double) is.getOutcome(Outcome.SCORE)
                            .getNormalMaximum()).sum();
        return max;
    }

    private Double getMinPointsPossible(List<ItemSession> itemSessions, AssessmentSession session){
        Stream<ItemSession> itemSessionStream = filterItemSessions(itemSessions, session).filter(is -> is.getOutcome(Outcome.SCORE).getNormalMinimum() != null);
        Double min = itemSessionStream.mapToDouble(is -> (Double) is.getOutcome(Outcome.SCORE)
                .getNormalMinimum()).sum();
        return min;
    }
    private Double getPointsEarned(List<ItemSession> itemSessions, AssessmentSession session){
        return filterItemSessions(itemSessions, session)
                .mapToDouble(is -> is.getOutcome(Outcome.SCORE)
                        .getSumOfValues()).sum();
    }

    private List<Double> getCalibratedDifficulties(List<ItemSession> itemSessions, AssessmentSession session){
        return filterItemSessions(itemSessions, session)
                .map(is -> getCalibratedDifficulty(is))
                        .collect(Collectors.toList());

    }

    private Stream<ItemSession> filterItemSessions(List<ItemSession> itemSessions, AssessmentSession session) {
        Stream<ItemSession> stream =  itemSessions.stream()
                .filter(is -> isUsedForOutcomeProcessing(is, session))
                .filter(is -> is.getOutcome(Outcome.SCORE) != null);
        return stream;
    }

    //Expects all nulls to be filtered out by the isUsedForOutcomeProcessing method
    private Double getCalibratedDifficulty(ItemSession itemSession){
        AssessmentItemDto itemDto = assessmentItemReader.read(itemSession.getItemId());
        return itemDto.getItemMetadata().getCalibratedDifficulty();
    }

    private boolean isAssessmentMetadataValid(AssessmentDto assessmentDto){
        if(assessmentDto != null &&
                assessmentDto.getAssessmentMetadata() != null &&
                assessmentDto.getAssessmentMetadata().getScaleAdditiveTerm() != null &&
                assessmentDto.getAssessmentMetadata().getScaleMultiplicativeTerm() != null) {
            return true;
        }
        return false;
    }

    private boolean isUsedForOutcomeProcessing(ItemSession itemSession, AssessmentSession session){

        if(itemSession.getOutcome(Outcome.SCORE) == null){
            return false;
        }

        if(!isValidItemForOutcomeProcessing(assessmentItemReader.read(itemSession.getItemId()))){
            return false;
        }
        return isValidAssessmentSessionForOutcomeProcessing(session,itemSession.getItemRefIdentifier(), session.getAssessment().getNamespace());
    }

    private boolean isValidItemForOutcomeProcessing(AssessmentItemDto itemDto){
        if(itemDto == null){
            return false;
        }
        if(assessmentItemReader.getOutcomeDeclaration(itemDto, Outcome.SCORE) == null){
            return false;
        }

        return isValidMetadataForOutcomeProcessing(itemDto.getItemMetadata());
    }

    private boolean isValidMetadataForOutcomeProcessing(ItemMetadataDto itemMetadataDto){
        if(itemMetadataDto == null
                || itemMetadataDto.getCalibratedDifficulty() == null
                || !ItemBankStatusType.AVAILABLE.equals(itemMetadataDto.getItemBankStatusType())){
            return false;
        }
        return true;
    }

    private boolean isValidAssessmentSessionForOutcomeProcessing(AssessmentSession session, String itemRefIdentifier, String namespace){
        Optional<AssessmentComponentDto> component = session.getAssessment().getComponent(itemRefIdentifier);
        if(!component.isPresent() || !(component.get() instanceof  AssessmentItemRefDto)){
            return false;
        }else{
            AssessmentItemRefDto itemRefDto = (AssessmentItemRefDto) component.get();
            return categoryReader.isCategoryUsedInPlacementModelEvaluation(itemRefDto.getCategories(), namespace);
        }
    }
}
