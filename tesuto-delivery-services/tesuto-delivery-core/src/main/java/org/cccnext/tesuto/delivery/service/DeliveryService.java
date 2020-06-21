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

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.cccnext.tesuto.admin.dto.UserAccountDto;
import org.cccnext.tesuto.content.dto.AssessmentBaseType;
import org.cccnext.tesuto.content.dto.AssessmentComponentDto;
import org.cccnext.tesuto.content.dto.AssessmentDto;
import org.cccnext.tesuto.content.dto.expression.AssessmentBranchRuleDto;
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto;
import org.cccnext.tesuto.content.model.DeliveryType;
import org.cccnext.tesuto.content.model.ScopedIdentifier;
import org.cccnext.tesuto.content.model.scoring.EvaluationScoringModel;
import org.cccnext.tesuto.content.service.AssessmentReader;
import org.cccnext.tesuto.content.service.scoring.ExpressionEvaluationService;
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession;
import org.cccnext.tesuto.delivery.model.internal.ItemSession;
import org.cccnext.tesuto.delivery.model.internal.Outcome;
import org.cccnext.tesuto.delivery.model.internal.Response;
import org.cccnext.tesuto.delivery.model.internal.TaskSet;
import org.cccnext.tesuto.delivery.model.internal.enums.OutcomeDeclarationType;
import org.cccnext.tesuto.delivery.service.AssessmentNotFoundException;
import org.cccnext.tesuto.delivery.service.AssessmentSessionNotFoundException;
import org.cccnext.tesuto.delivery.service.DeliveryServiceListener;
import org.cccnext.tesuto.delivery.service.scoring.OutcomeProcessingService;


import org.springframework.scheduling.annotation.EnableAsync;
import lombok.extern.slf4j.Slf4j;

/**
 * @author James Travis Stanley <jstanley@uncion.net>
 */
@Slf4j
@EnableAsync
public class DeliveryService implements TaskReader {

    private AssessmentSessionDao dao;

    private TaskSetDao taskSetDao;

    private AssessmentReader assessmentReader;

    private OutcomeProcessingService outcomeProcessingService;

    private TaskSetService taskSetService;

    private ExpressionEvaluationService expressionEvaluationService;

    private Set<DeliveryServiceListener> listeners = new HashSet<>();

    
    public AssessmentSessionDao getDao() {
        return dao;
    }

    public void setDao(AssessmentSessionDao dao) {
        this.dao = dao;
    }

    public TaskSetDao getTaskSetDao() {
        return taskSetDao;
    }

    public void setTaskSetDao(TaskSetDao taskSetDao) {
        this.taskSetDao = taskSetDao;
    }

    public AssessmentReader getAssessmentReader() {
        return assessmentReader;
    }

    public void setAssessmentReader(AssessmentReader assessmentReader) {
        this.assessmentReader = assessmentReader;
    }

    public TaskSetService getTaskSetService() {
        return taskSetService;
    }

    public void setTaskSetService(TaskSetService taskSetService) {
        this.taskSetService = taskSetService;
    }

    public ExpressionEvaluationService getExpressionEvaluationService() {
        return expressionEvaluationService;
    }

    public void setExpressionEvaluationService(ExpressionEvaluationService expressionEvaluationService) {
        this.expressionEvaluationService = expressionEvaluationService;
    }

    public OutcomeProcessingService getOutcomeProcessingService() {
        return outcomeProcessingService;
    }

    public void setOutcomeProcessingService(OutcomeProcessingService outcomeProcessingService) {
        this.outcomeProcessingService = outcomeProcessingService;
    }

    public Set<DeliveryServiceListener> getListeners() {
        return listeners;
    }

    public void setListeners(Set<DeliveryServiceListener> listeners) {
        this.listeners = listeners;
    }

    public AssessmentSession createContentAssessmentSession(String namespace, String identifier) throws AssessmentNotFoundException {
        AssessmentDto assessment = assessmentReader.readLatestPublishedVersion(namespace, identifier);
        if (assessment == null) {
            throw new AssessmentNotFoundException(StringUtils.join(namespace,"-",identifier.toString()));
        }
        return createAssessmentSession(null, assessment, DeliveryType.PAPER, null);
    }

    public String createUserAssessmentSession(String userId, ScopedIdentifier assessmentIdentifier, int assessmentVersion,
                                              DeliveryType deliveryType, Map<String, String> assessmentSettings)
                                              throws AssessmentNotFoundException {
        AssessmentDto assessment = assessmentReader.readVersion(assessmentIdentifier, assessmentVersion);
        if (assessment == null) {
            throw new AssessmentNotFoundException(assessmentIdentifier.toString() + ", version=" + assessmentVersion);
        }
        AssessmentSession session = createAssessmentSession(userId, assessment, deliveryType, assessmentSettings);
        return persistAssessmentSession(session);
    }

    public String createUserAssessmentSession(String userId, String assessmentId, DeliveryType deliveryType,
                                              Map<String, String> assessmentSettings) throws AssessmentNotFoundException {
        AssessmentDto assessment = assessmentReader.read(assessmentId);
        if (assessment == null) {
            throw new AssessmentNotFoundException("UUID:" + assessmentId);
        }
        AssessmentSession session = createAssessmentSession(userId, assessment, deliveryType, assessmentSettings);
        return persistAssessmentSession(session);
    }

    public String createUserAssessmentSession(String userId, ScopedIdentifier assessmentIdentifier, DeliveryType deliveryType,
                                              Map<String, String> assessmentSettings) throws AssessmentNotFoundException {
        AssessmentDto assessment = assessmentReader.readLatestPublishedVersion(assessmentIdentifier);
        if (assessment == null) {
            throw new AssessmentNotFoundException(assessmentIdentifier.toString());
        }
        AssessmentSession session =  createAssessmentSession(userId, assessment, deliveryType, assessmentSettings);
        return persistAssessmentSession(session);
    }

    private AssessmentSession createAssessmentSession(String userId, AssessmentDto assessment, DeliveryType deliveryType,
            Map<String, String> assessmentSettings) {
        if (assessment.getAssessmentParts().size() != 1) {
            throw new RuntimeException("Assessments should have exactly one assessment part");
        }
        AssessmentSession session = new AssessmentSession();
        session.setAssessment(assessment);
        session.setUserId(userId);
        session.setContentId(assessment.getId()); // Mongo guid
        session.setContentIdentifier(assessment.getIdentifier()); // QTI identifier provided by LSI
        session.setDeliveryType(deliveryType);
        session.setAssessmentSettings(assessmentSettings);
        session.setStartDate(new Date());
        if (assessment.getDuration() != null) {
            session.setDeadline(
                    new Date(session.getStartDate().getTime() + Math.round(assessment.getDuration()) * 1000));
        }
        session.addOutcomes(outcomeProcessingService.processExternalResourceOutcomeDeclarations(session));
        taskSetService.generateAllTaskSets(session);
        return session;
    }

    private String persistAssessmentSession(AssessmentSession session) {
        save(session);
        return session.getAssessmentSessionId();
    }

    public AssessmentSession find(String sessionId)
            throws AssessmentSessionNotFoundException, AssessmentNotFoundException {
        AssessmentSession session = dao.find(sessionId);
        AssessmentDto assessment = assessmentReader.read(session.getContentId());
        if (assessment == null) {
            throw new AssessmentNotFoundException(session.getContentId());
        }
        session.setAssessment(assessment);
        return session;
    }

    public int getAssessmentVersionForSession(String sessionId)
            throws AssessmentSessionNotFoundException, AssessmentNotFoundException {
        AssessmentSession session = find(sessionId);
        AssessmentDto assessment = assessmentReader.read(session.getContentId());
        if (assessment == null) {
            throw new AssessmentNotFoundException(session.getContentId());
        }
        return assessment.getVersion();
    }

    public void save(AssessmentSession session) {
        session.getTaskSetsToSave().forEach( taskSetId -> {
            TaskSet taskSet = session.getTaskSets().get(taskSetId);
            if (taskSet == null) {
                throw new RuntimeException("Task Set " + taskSetId + " cannto be saved -- not stored in assessment session");
            }
            taskSetDao.save(taskSet);
        });
        dao.save(session);
    }


    public TaskSet complete(AssessmentSession session, UserAccountDto requestor) {
        TaskSet current = taskSetService.getCurrentTaskSet(session);
        if (current == null) {
            return null;
        }

        Collection<String> invalidSessions = current.getAllItemSessions().stream().filter( itemSession ->
                !taskSetService.isComplete(itemSession)
        ).map(itemSession -> itemSession.getItemSessionId()).collect(Collectors.toSet());

        if (invalidSessions.size() > 0) {
            // NOTE: the UI currently parses out the array of invalidSessions so do not change without updating UI parsing logic
            throw new TaskSetCompletionValidationException("The following item sessions are not ready to be completed "
                    + invalidSessions.toString());
        }


        List<ItemSession> itemSessions = resetTaskSetScores(current);
        current.setCompletionDate(new Date());
        session.saveTaskSet(current);
        outcomeProcessingService.processAssessmentSessionOutcome(session, itemSessions);

        //update SCORE outcomes
        compileScores(session);

        // Check to see if there is a current branch or precondition to evaluate.
        if (session.getNextTaskSetId() == null && (StringUtils.isNotEmpty(session.getNextSectionId())) ){
            computeNextTaskSet(session);
        }

        session.setCurrentTaskSetId(session.getNextTaskSetId());
        // Set completion date before save
        if (session.getCurrentTaskSetId() == null) {
            session.setCompletionDate(new Date());
        }

        save(session);
        // call listeners after save of a completed session
        if (session.getCurrentTaskSetId() == null) {
            listeners.forEach(listener -> listener.completeAssessment(session.getAssessmentSessionId(), requestor));
        }

        return taskSetService.getCurrentTaskSet(session);
    }

    public TaskSet computePreviousTaskSet(AssessmentSession session, UserAccountDto requestor){
        TaskSet previousTaskSet = taskSetService.getPrevTaskSet(session);

        if (previousTaskSet == null) {
            return null;
        }

        session.setCurrentTaskSetId(previousTaskSet.getTaskSetId());

        save(session);
        return taskSetService.getCurrentTaskSet(session);
    }

    private void computeNextTaskSet(AssessmentSession session){
        AssessmentSectionDto sectionDto = null;
        if(StringUtils.isNotEmpty(session.getNextSectionId())){
            AssessmentComponentDto component = session.getAssessment().getComponent(
                    session.getNextSectionId()).get();
            if (component instanceof AssessmentSectionDto) {
                sectionDto = (AssessmentSectionDto) component;
            }
            else {
                log.warn(String.format("Processing is not attributed to a AssessmentSectionDto"
                        + ", this functionality has not been implemented\n"
                        + "NextSectionId: %s\n"
                        + "was either not found or an instance of another component", session.getNextSectionId()));
            }

            processBranchRules(session, sectionDto);
        }
    }

    private void processBranchRules(AssessmentSession session, AssessmentSectionDto sectionDto) {
        String targetComponent = "";
        if (sectionDto != null) {
            //After branching a precondition may need to be evaluated before the branch can be processed.
            EvaluationScoringModel scoringModel = new EvaluationScoringModel(session.getTestContextMap());

            //if we haven't presented the nextSectionId then a precondition must be evaluated
            if(!session.getSequence().containsKey(session.getNextSectionId())) {
                targetComponent = session.getNextSectionId();
            }
            else{
                // Evaluate branch rules
                for (AssessmentBranchRuleDto branchrule : sectionDto.getBranchRules()) {
                    // Evaluate Branch Rules
                    if (expressionEvaluationService.evaluate(branchrule, scoringModel)) {
                        // Branch Rule target must not have been presented yet.
                        // If it has, treat has failed branch rule.
                        if (!session.getSequence().containsKey(branchrule.getTarget())) {
                            targetComponent = branchrule.getTarget();
                            break;
                        }
                    }
                }
            }
            if (StringUtils.isBlank(targetComponent) || targetComponent.equals("EXIT_SECTION")) {
                targetComponent = session.getNextSectionId();
                session.setNextSectionId("");
                taskSetService.generateTaskSetsResumeParent(session, targetComponent);
            } else {
                if (targetComponent.equals("EXIT_TEST") || targetComponent.equals("EXIT_TESTPART")) {
                    session.setCurrentTaskSetId(null);
                }
                session.setNextSectionId("");
                taskSetService.generateTaskSetsByTarget(session, targetComponent);
            }
        }
    }

    private void compileScores(AssessmentSession session) {
        TaskSet currentTaskSet = taskSetService.getCurrentTaskSet(session);
        if(currentTaskSet != null && currentTaskSet.isCompleted())
            currentTaskSet.getAllItemSessions().forEach(itemSession -> {
                if(itemSession.isUseForBranchRuleEvaluation()){
                    Outcome outcome = itemSession.getOutcome("SCORE");
                    if (outcome != null) {
                        Double outcomeValue = Double.parseDouble(outcome.getValues().get(0));
                        for (AssessmentComponentDto assessmentComponentDto : session.getAssessment().getAncestors(
                                itemSession.getItemRefIdentifier())) {
                            String id = assessmentComponentDto.getId() + ".SCORE";
                            Double updatedValue = outcomeValue + session.getScore(id);
                            session.setScore(id, updatedValue);
                            session.addOutcome(new Outcome(assessmentComponentDto.getId()+".SCORE",updatedValue,
                                    AssessmentBaseType.FLOAT, OutcomeDeclarationType.IMPLICIT));
                        }
                    }
                }
            });
    }

    private List<ItemSession> resetTaskSetScores(TaskSet taskSet) {
        return taskSetService.resetTaskSetScores(taskSet);
    }

    Float scoreResponse(ItemSession itemSession, Response response) {
        return taskSetService.scoreItemSessionResponse(itemSession, response);
    }

    String findAssessmentInteractionType(String assessmentItemId, String responseId) {
        return taskSetService.findAssessmentInteractionType(assessmentItemId, responseId);
    }

    public void remove(String sessionId) {
        dao.delete(sessionId);
    }
}
