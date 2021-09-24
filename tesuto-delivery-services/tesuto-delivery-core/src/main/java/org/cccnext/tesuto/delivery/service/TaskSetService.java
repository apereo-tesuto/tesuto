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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.content.dto.AssessmentBaseType;
import org.cccnext.tesuto.content.dto.AssessmentComponentDto;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.AssessmentPartDto;
import org.cccnext.tesuto.content.dto.AssessmentPartNavigationMode;
import org.cccnext.tesuto.content.dto.expression.AssessmentPreConditionDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.item.AssessmentResponseVarDto;
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto;
import org.cccnext.tesuto.content.dto.section.AssessmentItemSessionControlDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto;
import org.cccnext.tesuto.content.dto.shared.AssessmentOutcomeDeclarationDto;
import org.cccnext.tesuto.content.model.DeliveryType;
import org.cccnext.tesuto.content.model.scoring.EvaluationScoringModel;
import org.cccnext.tesuto.content.service.AssessmentItemReader;
import org.cccnext.tesuto.content.service.UseItemCategoryReader;
import org.cccnext.tesuto.content.service.scoring.ExpressionEvaluationService;
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.model.internal.ItemSession;
import org.cccnext.tesuto.delivery.model.internal.Outcome;
import org.cccnext.tesuto.delivery.model.internal.Response;
import org.cccnext.tesuto.delivery.model.internal.Task;
import org.cccnext.tesuto.delivery.model.internal.TaskSet;
import org.cccnext.tesuto.delivery.model.internal.enums.OutcomeDeclarationType;
import org.cccnext.tesuto.delivery.service.scoring.AssessmentItemScoringService;
import org.cccnext.tesuto.util.TesutoUtils;


import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
public class TaskSetService {

    private AssessmentItemReader assessmentItemReader;
    private AssessmentItemScoringService scoringService;
    private ExpressionEvaluationService expressionEvaluationService;
    private UseItemCategoryReader categoryReader;
    private SelectionService selectionService;
    private TaskSetDao taskSetDao;

    public AssessmentItemReader getAssessmentItemReader() {
        return assessmentItemReader;
    }

    public void setAssessmentItemReader(AssessmentItemReader assessmentItemReader) {
        this.assessmentItemReader = assessmentItemReader;
    }

    public AssessmentItemScoringService getScoringService() {
        return scoringService;
    }

    public ExpressionEvaluationService getExpressionEvaluationService() {
        return expressionEvaluationService;
    }

    public void setExpressionEvaluationService(ExpressionEvaluationService expressionEvaluationService) {
        this.expressionEvaluationService = expressionEvaluationService;
    }

    public void setScoringService(AssessmentItemScoringService scoringService) {
        this.scoringService = scoringService;
    }

    public UseItemCategoryReader getCategoryReader() {
        return categoryReader;
    }

    public void setCategoryReader(UseItemCategoryReader categoryReader) {
        this.categoryReader = categoryReader;
    }

    public SelectionService getSelectionService() {
        return selectionService;
    }

    public void setSelectionService(SelectionService selectionService) {
        this.selectionService = selectionService;
    }

    public TaskSetDao getTaskSetDao() {
        return taskSetDao;
    }

    public void setTaskSetDao(TaskSetDao taskSetDao) {
        this.taskSetDao = taskSetDao;
    }

    public TaskSet find(String taskSetId) {
        return taskSetDao.find(taskSetId);
    }

    public TaskSet getTaskSet(String taskSetId, AssessmentSession session) {
        if (taskSetId == null) {
            return null;
        }
        Map<String,TaskSet> cache = session.getTaskSets();
        if (cache.get(taskSetId) == null) {
            cache.put(taskSetId, getTaskSetDao().find(taskSetId));
        }
        return cache.get(taskSetId);
    }


    public TaskSet getCurrentTaskSet(AssessmentSession session) {
        return getTaskSet(session.getCurrentTaskSetId(), session);
    }

    public TaskSet getPrevTaskSet(AssessmentSession session) {
        return getTaskSet(session.getPrevTaskSetId(), session);
    }


    public void generateAllTaskSets(AssessmentSession assessmentSession) {

        if (assessmentSession.getDeliveryType().equals(DeliveryType.PAPER)) {
            generateTaskSet(assessmentSession);
        }

        if (assessmentSession.getNavigationMode() == null
                || assessmentSession.getNavigationMode().equals(AssessmentPartNavigationMode.LINEAR)
                || assessmentSession.getNavigationMode().equals(AssessmentPartNavigationMode.NONLINEAR)) {
            AssessmentDto assessment = assessmentSession.getAssessment();

            for (AssessmentPartDto assessmentPart : assessment.getAssessmentParts()) {
                // Skip part if already presented
                if (!assessmentSession.addNewSequence(assessmentPart.getId())) {
                    continue;
                }

                if (CollectionUtils.isNotEmpty(assessmentPart.getAssessmentSections())) {
                    for (AssessmentSectionDto assessmentSection : assessmentPart.getAssessmentSections()) {
                        if(StringUtils.isNotBlank(assessmentSession.getNextSectionId())){
                            break;
                        }

                        generateTaskSetSectionDetails(assessmentSection, assessmentSession, "");
                    }
                }
            }
        }
    }

    /**
     * Method used to move to a new section (targeted section) and begin taskSet generation
     *
     * @param assessmentSession
     * @param targetIdentifier the id of the component to add to the taskSet
     */
    public void generateTaskSetsByTarget(AssessmentSession assessmentSession, String targetIdentifier) {
        AssessmentDto assessmentDto = assessmentSession.getAssessment();
        if (assessmentDto.getComponent(targetIdentifier).isPresent()) {
            AssessmentComponentDto assessmentComponentDto = assessmentDto.getComponent(targetIdentifier).get();

            AssessmentSectionDto assessmentSectionDto = (AssessmentSectionDto) assessmentComponentDto;

            generateTaskSetSectionDetails(assessmentSectionDto, assessmentSession, "");

            if (StringUtils.isBlank(assessmentSession.getNextSectionId()) || !isPreconditionMet(assessmentSession, assessmentSectionDto)) {
                generateTaskSetsResumeParent(assessmentSession, targetIdentifier);
            }
        }
    }

    /**
     * The parent is thought to be the parent of the resumeIdentifier
     * example:
     * Section A with a nested sections Section B and Section C
     * The parent of Section B and Section C is parent A
     *
     * @param assessmentSession
     * @param resumeIdentifier the child id of the parent to be resumed
     */
    public void generateTaskSetsResumeParent(AssessmentSession assessmentSession, String resumeIdentifier) {
        //TODO It is assumed that a non-linear assessment will not branch.  Import Validation has not been implemented.
        if (assessmentSession.getNavigationMode() == null
                || assessmentSession.getNavigationMode().equals(AssessmentPartNavigationMode.LINEAR)) {

            if (StringUtils.isNotBlank(resumeIdentifier)) {

                AssessmentDto assessmentDto = assessmentSession.getAssessment();
                Optional component = assessmentDto.getParent(resumeIdentifier);

                if (component.isPresent()) {
                    AssessmentComponentDto assessmentComponentDto = (AssessmentComponentDto) component.get();

                    if (assessmentComponentDto instanceof AssessmentSectionDto) {
                        // Parent is a section, loop over children
                        AssessmentSectionDto assessmentSectionDto = (AssessmentSectionDto) assessmentComponentDto;

                        generateTaskSetSectionDetails(assessmentSectionDto, assessmentSession, resumeIdentifier);

                        // If no branch rules, go to the next parent up
                        if (StringUtils.isBlank(assessmentSession.getNextSectionId())) {
                            generateTaskSetsResumeParent(assessmentSession, assessmentSectionDto.getId());
                        }
                    } else {
                        if (assessmentComponentDto instanceof AssessmentPartDto) {
                            // Parent is an assessmentPart, loop over children
                            AssessmentPartDto assessmentPartDto = (AssessmentPartDto) assessmentComponentDto;
                            boolean foundSkipped = false;

                            for (AssessmentSectionDto assessmentSectionDto : assessmentPartDto
                                    .getAssessmentSections()) {

                                // Skip sections until we skip past resumed
                                // identifier
                                if (!foundSkipped) {
                                    if (resumeIdentifier.equals(assessmentSectionDto.getId())) {
                                        foundSkipped = true;
                                    }
                                    continue;
                                }

                                generateTaskSetSectionDetails(assessmentSectionDto, assessmentSession, "");
                                // Break if a branch rule is in effect
                                if (isNextSectionIdForBranchRule(assessmentSession, assessmentSectionDto)) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isNextSectionIdForBranchRule(AssessmentSession session, AssessmentSectionDto sectionDto){
        if(StringUtils.isEmpty(session.getNextSectionId())){
            return false;
        }
        if(session.getNextSectionId().equals(sectionDto.getId()) && CollectionUtils.isNotEmpty(sectionDto.getBranchRules())){
            return true;
        }
        return false;
    }

    private AssessmentItemSessionControlDto getNearestItemSessionControl(AssessmentDto assessment,
            AssessmentComponentDto assessmentComponentDto) {
        AssessmentItemSessionControlDto itemSessionControl = null;
        if (assessmentComponentDto.getItemSessionControl() != null) {
            itemSessionControl = assessmentComponentDto.getItemSessionControl();
        } else {
            Optional assessmentComponentWrapper = assessment.getParent(assessmentComponentDto.getId());
            if (assessmentComponentWrapper.isPresent()) {
                itemSessionControl = getNearestItemSessionControl(assessment,
                        (AssessmentComponentDto) assessmentComponentWrapper.get());
            }
        }
        return itemSessionControl;
    }

    // QTI sections can be nested and method will be called recursively
    private void generateTaskSetSectionDetails(AssessmentSectionDto assessmentSectionDto,
            AssessmentSession assessmentSession, String skipIdentifier) {
        if (assessmentSectionDto == null) {
            return;
        }

        // Check for section preconditions
        //TODO It is assumed that a non-linear assessment will not branch.  Import Validation has not been implemented.
        if ((assessmentSession.getNavigationMode() == null
                || assessmentSession.getNavigationMode().equals(AssessmentPartNavigationMode.LINEAR))
                && !isPreconditionMet(assessmentSession, assessmentSectionDto)) {
                //if all the preconditions are not met then stop processing here.
                return;
        }
        // no preconditions found reset precondition id
        assessmentSession.setNextSectionId("");

        // Add to presentation sequence if new, otherwise exit method.
        if (assessmentSession.addNewSequence(assessmentSectionDto.getId())) {
            boolean foundSkipped = false;
            boolean isFirstItem = true;
            boolean isItemBundle = false;

            boolean isShuffle = (assessmentSectionDto.getOrdering() == null ) ? false : assessmentSectionDto.getOrdering().isShuffle(); // default false

            // Flag section as item bundle
            if (assessmentSession.getAssessment().getAssessmentMetadata() != null && assessmentSession.getAssessment()
                    .getAssessmentMetadata().isSectionItemBundle(assessmentSectionDto.getId()))
                isItemBundle = true;

            if (CollectionUtils.isNotEmpty(assessmentSectionDto.getAssessmentComponents())) {

                if(isShuffle){
                    Collections.shuffle(assessmentSectionDto.getAssessmentComponents(), new SecureRandom());
                }

                if(assessmentSectionDto.getSelection() != null){
                    assessmentSectionDto.setAssessmentComponents(selectionService.calculateSectionComponents(assessmentSectionDto));
                }

                for (AssessmentComponentDto assessmentComponent : assessmentSectionDto.getAssessmentComponents()) {
                    // If skip identifier exists, loop through until past that
                    // identifier.
                    if (!foundSkipped && StringUtils.isNotBlank(skipIdentifier)) {
                        if (skipIdentifier.equals(assessmentComponent.getId())) {
                            foundSkipped = true;
                        }
                        // Skip processing of this object
                        continue;
                    }

                    if (assessmentComponent instanceof AssessmentItemRefDto) {

                        // Add to presentation sequence if new, otherwise skip
                        // to next component.
                        if (!assessmentSession.addNewSequence(assessmentComponent.getId())) {
                            continue;
                        }

                        Task task = null;

                        if (!isItemBundle || (isItemBundle && isFirstItem)) {
                            TaskSet taskSet = assessmentSession.getDeliveryType().equals(DeliveryType.PAPER)
                                    ? getCurrentTaskSet(assessmentSession) : generateTaskSet(assessmentSession);
                            assessmentSession.incrementTaskCount();
                            task = generateTask(assessmentSession.getTaskCount());
                            String stimulus = (CollectionUtils.isEmpty(assessmentSectionDto.getRubricBlocks())) ? null
                                    : assessmentSectionDto.getRubricBlocks().get(0).getContent();
                            task.setStimulus(stimulus);
                            taskSet.addTask(task);
                            assessmentSession.saveTaskSet(taskSet);
                        } else {
                            //TODO: getTasks() is not ordered! -- Is this a bug?
                            task = (Task) getPrevTaskSet(assessmentSession).getTasks().toArray()[0];
                            assessmentSession.saveTaskSet(getPrevTaskSet(assessmentSession));
                        }

                        AssessmentItemRefDto assessmentItemRefDto = (AssessmentItemRefDto) assessmentComponent;
                        assessmentSession.incrementItemSessionCount();
                        AssessmentItemSessionControlDto itemSessionControl = getNearestItemSessionControl(
                                assessmentSession.getAssessment(), assessmentItemRefDto);
                        ItemSession itemSession = generateItemSession(assessmentItemRefDto,
                                assessmentSession.getAssessment().getNamespace(),
                                assessmentSession.getItemSessionCount(), itemSessionControl);
                        task.addItemSession(itemSession);

                        isFirstItem = false;

                    } else {
                        if (assessmentComponent instanceof AssessmentSectionDto) {

                            if(!isPreconditionMet(assessmentSession, assessmentComponent)) {
                                //stop processing, move to next component
                                continue;
                            }
                            AssessmentSectionDto assessmentSectionDtoChild = (AssessmentSectionDto) assessmentComponent;
                            generateTaskSetSectionDetails(assessmentSectionDtoChild, assessmentSession, "");

                        }
                    }
                }
            }
            // Check for section branch rules
            //TODO It is assumed that a non-linear assessment will not branch.  Import Validation has not been implemented.
            if ((assessmentSession.getNavigationMode() == null
                    || assessmentSession.getNavigationMode().equals(AssessmentPartNavigationMode.LINEAR))
                    && CollectionUtils.isNotEmpty(assessmentSectionDto.getBranchRules())) {
                assessmentSession.setNextSectionId(assessmentSectionDto.getId());
            }
        }
    }



    public boolean isPreconditionMet(AssessmentSession assessmentSession, AssessmentComponentDto assessmentComponentDto){
        //evaluate precondition

        //TODO add evaluation for item
        if(assessmentComponentDto instanceof AssessmentSectionDto){
            AssessmentSectionDto assessmentSectionDto = (AssessmentSectionDto) assessmentComponentDto;
            if(CollectionUtils.isNotEmpty(assessmentSectionDto.getPreConditions())){
                EvaluationScoringModel esm = new EvaluationScoringModel(assessmentSession.getTestContextMap());
                for(AssessmentPreConditionDto preConditionDto: assessmentSectionDto.getPreConditions()){
                    if(!expressionEvaluationService.evaluate(preConditionDto, esm)){
                       return  false;
                    }
                }
            }

        }
        return true;
    }

    private void setNextTaskSet(AssessmentSession assessmentSession, TaskSet taskSet) {
        if (StringUtils.isBlank(assessmentSession.getPrevTaskSetId())) {
            assessmentSession.setCurrentTaskSetId(taskSet.getTaskSetId());
        }
    }


    private TaskSet generateTaskSet(AssessmentSession session) {
        TaskSet taskSet = new TaskSet();
        taskSet.setTaskSetId(TesutoUtils.newId());
        taskSet.setNavigationMode(session.getNavigationMode());
        taskSet.setSubmissionMode(session.getSubmissionMode());
        session.addTaskSet(taskSet);
        session.saveTaskSet(taskSet.getTaskSetId());
        taskSet.setTaskSetIndex(session.getTaskSetCount());
        setNextTaskSet(session, taskSet);
        session.setPrevTaskSetId(taskSet.getTaskSetId());
        return taskSet;
    }

    private Task generateTask(int taskIndex) {
        Task task = new Task();
        task.setTaskId(TesutoUtils.newId());
        task.setTaskIndex(taskIndex);
        return task;
    }

    private ItemSession generateItemSession(AssessmentItemRefDto assessmentItemRef, String namespace,
            int itemSessionIndex, AssessmentItemSessionControlDto control) {
        ItemSession itemSession = new ItemSession();
        itemSession.setItemSessionId(TesutoUtils.newId());
        AssessmentItemDto assessmentItem = assessmentItemReader.readLatestPublishedVersion(namespace,
                assessmentItemRef.getItemIdentifier());
        itemSession.setItemId(assessmentItem.getId());
        itemSession.setItemRefIdentifier(assessmentItemRef.getIdentifier());
        boolean isUsedInEvaluation = categoryReader.isCategoryUsedInBranchRuleEvaluation(
                assessmentItemRef.getCategories(), namespace);
        itemSession.setUseForBranchRuleEvaluation(isUsedInEvaluation);
        itemSession.setItemSessionIndex(itemSessionIndex);

        if (control != null) {
            if (control.getAllowSkipping() != null) {
                itemSession.setAllowSkipping(control.getAllowSkipping());
            }
            if (control.getValidateResponses() != null) {
                itemSession.setValidateResponses(control.getValidateResponses());
            }
        }

        return itemSession;
    }

    //It's the responsibility of the caller to make sure the TaskSet is saved!
    public List<ItemSession> resetTaskSetScores(TaskSet taskSet) {
        List<ItemSession> itemSessions = getAllItemSessions(taskSet);
        itemSessions.stream().forEach(is -> resetItemSessionScore(is));

        itemSessions.stream().forEach(is -> is.getResponses().values().stream()
                .map(r -> scoreItemSessionResponse(is, r)).forEach(score -> addResponseScores(is, score)));
        itemSessions.stream().forEach(is -> modulateScore(is));
        return itemSessions;
    }

    private void addResponseScores(ItemSession itemSession, Float score) {
        Outcome scoredOutcome = itemSession.getOutcome(Outcome.SCORE);
        Float value = Float.parseFloat(scoredOutcome.getValues().get(0));
        value += score;
        scoredOutcome.replaceValues(value);
    }

    private void modulateScore(ItemSession itemSession) {
        Outcome scoredOutcome = itemSession.getOutcome(Outcome.SCORE);
        Float value = Float.parseFloat(scoredOutcome.getValues().get(0));
        AssessmentItemDto assessmentItem = assessmentItemReader.read(itemSession.getItemId());
        AssessmentOutcomeDeclarationDto outcomeDeclaration = assessmentItemReader.getOutcomeDeclaration(assessmentItem, Outcome.SCORE);
        if (outcomeDeclaration != null && outcomeDeclaration.getNormalMinimum() != null) {
            if (value < outcomeDeclaration.getNormalMinimum()) {
                value = outcomeDeclaration.getNormalMinimum().floatValue();
            }
        }

        if (outcomeDeclaration != null && outcomeDeclaration.getNormalMaximum() != null) {
            if (value > outcomeDeclaration.getNormalMaximum()) {
                value = outcomeDeclaration.getNormalMaximum().floatValue();
            }
            scoredOutcome.setNormalMinimum(outcomeDeclaration.getNormalMinimum());
            scoredOutcome.setNormalMaximum(outcomeDeclaration.getNormalMaximum());
        }
        scoredOutcome.replaceValues(value);
    }

    public List<ItemSession> getAllItemSessions(TaskSet taskSet) {
        List<ItemSession> itemSessions = new ArrayList<ItemSession>();
        taskSet.getTasks().stream().forEach(t -> t.getItemSessions().forEach(i -> itemSessions.add(i)));
        return itemSessions;

    }

    public TaskSet nextTaskSet(AssessmentSession session) {
        return getTaskSet(session.getNextTaskSetId(), session);
    }

    private void resetItemSessionScore(ItemSession itemSession) {
        Outcome outcome = itemSession.getOutcome(Outcome.SCORE);
        if (outcome == null) {
            outcome = new Outcome();
            outcome.setOutcomeIdentifier(Outcome.SCORE);
            outcome.setBaseType(AssessmentBaseType.FLOAT);
            outcome.setDeclarationType(OutcomeDeclarationType.IMPLICIT);
            itemSession.addOutcome(outcome);
        }
        String initialValue = Outcome.SCORE_DEFAULT_VALUE;
        outcome.replaceValues(initialValue);
    }

    Float scoreItemSessionResponse(ItemSession itemSession, Response response) {
        AssessmentItemDto assessmentItem = assessmentItemReader.read(itemSession.getItemId());
        AssessmentResponseVarDto responseVar = getResponseVar(assessmentItem, response.getResponseId());
        Float score = scoringService.scoreAssessmentItem(responseVar, assessmentItem.getResponseProcessing(), response);
        if (score == null) {
            score = getDefaultScoreValue(assessmentItem);
        }
        return score;
    }

    private Float getDefaultScoreValue(AssessmentItemDto assessmentItem) {
        AssessmentOutcomeDeclarationDto outcomeDeclarationDto = assessmentItemReader.getOutcomeDeclaration(assessmentItem, Outcome.SCORE);
        String defaultValue = Outcome.SCORE_DEFAULT_VALUE;
        if (outcomeDeclarationDto != null && outcomeDeclarationDto.getDefaultValue() != null
                && CollectionUtils.isNotEmpty(outcomeDeclarationDto.getDefaultValue().getValues())) {
            defaultValue = outcomeDeclarationDto.getDefaultValue().getValues().get(0);

        }
        return Float.parseFloat(defaultValue);
    }

    private AssessmentResponseVarDto getResponseVar(AssessmentItemDto assessmentItem, String responseIdentifier) {
        Optional<AssessmentResponseVarDto> result = assessmentItem.getResponseVars().stream()
                .filter(r -> r.getIdentifier().equals(responseIdentifier)).findFirst();
        return result.isPresent() ? result.get() : null;
    }

    public String findAssessmentInteractionType(String assessmentItemId, String responseId) {
        AssessmentItemDto assessmentItem = assessmentItemReader.read(assessmentItemId);
        List<AssessmentInteractionDto> assessmentItemInteractions = assessmentItem.getInteractions();

        for (AssessmentInteractionDto assessmentInteractionDto : assessmentItemInteractions) {
            if (assessmentInteractionDto.getResponseIdentifier().equals(responseId)) {
                return assessmentInteractionDto.getType().getValue();
            }
        }
        return "Error: interaction does not have associative identifier.";
    }

    private boolean isInteractionValidated(ItemSession itemSession, AssessmentInteractionDto interaction) {
        Response response = itemSession.getResponses().get(interaction.getResponseIdentifier());
        return response == null || interaction.validateResponses(response.getValues());
    }

    boolean isComplete(ItemSession itemSession) {
        if (!itemSession.isAllowSkipping() && !itemSession.isAnswered()) {
            log.debug("ItemSession {} has not been answered and does not allow skipping.", itemSession.getItemSessionId());
            return false;
        } else if (itemSession.isValidateResponses()) {
            AssessmentItemDto item = assessmentItemReader.read(itemSession.getItemId());
            boolean isValid = item.getInteractions().stream().allMatch(interaction ->
                isInteractionValidated(itemSession, interaction)
            );
            return isValid;
        } else {
            return true;
        }
    }

}
