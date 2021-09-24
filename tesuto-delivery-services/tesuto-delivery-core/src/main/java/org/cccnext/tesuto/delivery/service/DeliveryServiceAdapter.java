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

import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto;
import org.cccnext.tesuto.content.dto.shared.AssessmentOutcomeDeclarationDto;
import org.cccnext.tesuto.content.model.DeliveryType;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.content.service.AssessmentItemReader;
import org.cccnext.tesuto.delivery.model.internal.Activity;
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.model.internal.ItemSession;
import org.cccnext.tesuto.delivery.model.internal.Outcome;
import org.cccnext.tesuto.delivery.model.internal.Response;
import org.cccnext.tesuto.delivery.model.internal.Task;
import org.cccnext.tesuto.delivery.model.internal.TaskSet;
import org.cccnext.tesuto.delivery.model.view.AssessmentSessionViewDto;
import org.cccnext.tesuto.delivery.service.AssessmentSessionNotFoundException;
import org.cccnext.tesuto.delivery.service.DeliveryServiceApi;
import org.cccnext.tesuto.delivery.service.TaskNotFoundException;
import org.cccnext.tesuto.delivery.view.ItemOutcomeViewDto;
import org.cccnext.tesuto.delivery.view.ItemScoreViewDto;
import org.cccnext.tesuto.delivery.view.ItemSessionResponseViewDto;
import org.cccnext.tesuto.delivery.view.TaskResponseViewDto;
import org.cccnext.tesuto.delivery.view.TaskSetViewDto;
import org.cccnext.tesuto.domain.dto.ScopedIdentifierDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is for use by controllers. It mediates between the view model used
 * in the controller and the internal model used in the service.
 */

public class DeliveryServiceAdapter implements DeliveryServiceApi {

    private AssemblyService assemblyService;
    private DeliveryService deliveryService;
    private AssessmentItemReader assessmentItemReader;
    private TaskSetService taskSetService;

    public AssemblyService getAssemblyService() {
        return assemblyService;
    }

    public void setAssemblyService(AssemblyService assemblyService) {
        this.assemblyService = assemblyService;
    }

    public DeliveryService getDeliveryService() {
        return deliveryService;
    }

    public void setDeliveryService(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    public AssessmentItemReader getAssessmentItemReader() {
        return assessmentItemReader;
    }

    public void setAssessmentItemReader(AssessmentItemReader assessmentItemReader) {
        this.assessmentItemReader = assessmentItemReader;
    }

    public TaskSetService getTaskSetService() {
        return taskSetService;
    }

    public void setTaskSetService(TaskSetService taskSetService) {
        this.taskSetService = taskSetService;
    }

    /**
     * Create an assessment session in some appropriate storage
     *
     * @param userId
     * @param assessmentId
     * @param settings
     * @return assessmentSessionId
     */
    @Override
    public String createUserAssessmentSession(String userId, ScopedIdentifierDto assessmentId, DeliveryType deliveryType,
                                              Map<String, String> settings) {
        return deliveryService.createUserAssessmentSession(userId,  new ScopedIdentifier(assessmentId), deliveryType, settings);
    }

    /**
     * Create an assessment session with a specific assessment version in some appropriate storage
     *
     * @param userId
     * @param assessmentId
     * @param settings
     * @return assessmentSessionId
     */
    @Override
    public String createUserAssessmentSession(String userId, ScopedIdentifierDto assessmentId, int assessmentVersion,
                                              DeliveryType deliveryType,
                                              Map<String, String> settings) {
        return deliveryService.createUserAssessmentSession(userId, new ScopedIdentifier(assessmentId), assessmentVersion, deliveryType, settings);
    }

    /**
     * Create a generic assessment session. Is not persisted.
     *
     * @param assessmentId
     * @param namespace
     * @return
     */
    @Override
    public Object createContentAssessmentSession(String namespace, String identifier) {
        return createContentAssessmentSessionView(namespace, identifier);
    }

    /**
     * Retrieve an assessmentSessionId. The session is just intended for
     * serialization so the method is untyped to avoid pulling the whole
     * assessment session view model out of the delivery module.
     */
    @Override
    public Object findAssessmentSession(String sessionId) throws AssessmentSessionNotFoundException {
        return find(sessionId);
    }

    @Override
    public int getAssessmentVersionForSession(String sessionId) throws AssessmentSessionNotFoundException {
        return deliveryService.getAssessmentVersionForSession(sessionId);
    }

    public AssessmentSessionViewDto createContentAssessmentSessionView(String namespace, String identifier) {
        AssessmentSession session = deliveryService.createContentAssessmentSession(namespace, identifier);
        return assemblyService.assembleAssessmentSession(session);
    }

    public AssessmentSessionViewDto find(String sessionId) {
        return assemblyService.assembleAssessmentSession(deliveryService.find(sessionId));
    }

    // Only returns an assessment session if it is for the designated user
    public AssessmentSessionViewDto find(String sessionId, String userId) {
        AssessmentSession session = deliveryService.find(sessionId);
        if (!session.getUserId().equals(userId)) {
            return null;
        } else {
            return assemblyService.assembleAssessmentSession(session);
        }
    }

    private void doResponseMerge(AssessmentSession session, Map<String, TaskResponseViewDto> responses) {
        TaskSet current = taskSetService.getCurrentTaskSet(session);
        session.saveTaskSet(current);
        Long duration = 0l;
        for(String id: responses.keySet()) {
            Task task = current.getTask(id);
            if (task == null) {
                throw new TaskNotFoundException(session.getAssessmentSessionId(), current.getTaskSetId(), id);
            }
            TaskResponseViewDto response = responses.get(id);
            assemblyService.mergeResponse(response, task);
            duration += response.getDuration();
        }
        current.setDuration(duration);
    }

    public void mergeResponses(AssessmentSessionViewDto session, Map<String, TaskResponseViewDto> responses) {
        AssessmentSession internal = session.getInternalSession();
        doResponseMerge(internal, responses);
        deliveryService.save(internal);
    }

    public List<ItemScoreViewDto> checkResponseScores(AssessmentSessionViewDto session, String taskSetId,
            Map<String, TaskResponseViewDto> responses) {
        AssessmentSession internal = session.getInternalSession();
        TaskSet taskSet = taskSetService.getTaskSet(taskSetId, internal);
        List<ItemScoreViewDto> scores = new ArrayList<ItemScoreViewDto>();
        taskSet.getTasks().forEach(task -> scores.addAll(checkResponseScores(task, responses)));
        return scores;
    }

    public List<ItemScoreViewDto> checkResponseScores(AssessmentSessionViewDto session, String taskSetId, String taskId,
            Map<String, TaskResponseViewDto> responses) {
        AssessmentSession internal = session.getInternalSession();
        TaskSet taskSet = taskSetService.getTaskSet(taskSetId, internal);
        List<ItemScoreViewDto> scores = checkResponseScores(taskSet.getTask(taskId), responses);
        return scores;
    }

    private List<ItemScoreViewDto> checkResponseScores(Task task, Map<String, TaskResponseViewDto> responses) {
        List<ItemScoreViewDto> scores = new ArrayList<ItemScoreViewDto>();
        responses.values().forEach(taskResponse -> scores.addAll(checkResponseScores(task, taskResponse)));
        return scores;
    }

    private List<ItemScoreViewDto> checkResponseScores(Task task, TaskResponseViewDto taskResponse) {
        List<ItemScoreViewDto> scores = new ArrayList<ItemScoreViewDto>();
        for (ItemSessionResponseViewDto itemSessionResponse : taskResponse.getResponses()) {
            ItemSession itemSession = task.getItemSession(itemSessionResponse.getItemSessionId());
            Response response = assemblyService.assembleResponse(itemSessionResponse);
            Float score = deliveryService.scoreResponse(itemSession, response);
            scores.add(buildScore(score, itemSession, response));
        }
        return scores;
    }

    private ItemScoreViewDto buildScore(Float score, ItemSession itemSession, Response response) {
        ItemScoreViewDto scoreDto = new ItemScoreViewDto();
        scoreDto.setScore(score);
        scoreDto.setResponseIdentifier(response.getResponseId());
        scoreDto.setItemSessionIndex(itemSession.getItemSessionIndex());
        scoreDto.setItemSessionId(itemSession.getItemSessionId());
        scoreDto.setItemId(itemSession.getItemId());
        String interactionType = deliveryService.findAssessmentInteractionType(itemSession.getItemId(),
                response.getResponseId());
        scoreDto.setInteractionType(interactionType);
        return scoreDto;
    }

    public void addActivities(AssessmentSessionViewDto session, List<Activity> activities) {
        // Not done
    }

    public TaskSetViewDto complete(AssessmentSessionViewDto session, Map<String, TaskResponseViewDto> responses, UserAccountDto requestor) {
        AssessmentSession internal = session.getInternalSession();
        doResponseMerge(internal, responses);
        TaskSet nextTaskSet = deliveryService.complete(internal, requestor);
        return assemblyService.assembleTaskSet(nextTaskSet);
    }

    public TaskSetViewDto getPrevious(AssessmentSessionViewDto session, Map<String, TaskResponseViewDto> responses, UserAccountDto requestor) {
        AssessmentSession internal = session.getInternalSession();
        doResponseMerge(internal, responses);
        TaskSet previousTaskSet = deliveryService.computePreviousTaskSet(internal, requestor);
        return assemblyService.assembleTaskSet(previousTaskSet);
    }

    public Collection<ItemOutcomeViewDto> checkOutcomes(List<ItemScoreViewDto> scores) {
        Map<String, ItemOutcomeViewDto> outcomes = new HashMap<String, ItemOutcomeViewDto>();
        for (ItemScoreViewDto score : scores) {
            ItemOutcomeViewDto outcome;
            if (outcomes.containsKey(score.getItemSessionId())) {
                outcome = outcomes.get(score.getItemSessionId());
            } else {
                outcome = new ItemOutcomeViewDto();
                outcome.setItemSessionId(score.getItemSessionId());
                outcome.setItemId(score.getItemId());
                AssessmentItemDto assessmentItem = assessmentItemReader.read(score.getItemId());

                AssessmentOutcomeDeclarationDto outcomeDeclaration = assessmentItemReader.getOutcomeDeclaration(assessmentItem, Outcome.SCORE);
                if (outcomeDeclaration != null && outcomeDeclaration.getNormalMaximum() != null) {
                    outcome.setMax(outcomeDeclaration.getNormalMaximum().floatValue());
                }
                if (outcomeDeclaration != null && outcomeDeclaration.getNormalMinimum() != null) {
                    outcome.setMin(outcomeDeclaration.getNormalMinimum().floatValue());
                }
                outcomes.put(score.getItemSessionId(), outcome);
            }

            outcome.setRawScore(outcome.getRawScore() + score.getScore());
        }
        for (ItemOutcomeViewDto outcome : outcomes.values()) {
            if (outcome.getMin() != null && outcome.getRawScore() < outcome.getMin()) {
                outcome.setScore(outcome.getMin());
            } else if (outcome.getMax() != null && outcome.getRawScore() > outcome.getMax()) {
                outcome.setScore(outcome.getMax());
            } else {
                outcome.setScore(outcome.getRawScore());
            }
        }
        return outcomes.values();
    }
}
