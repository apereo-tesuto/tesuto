package org.cccnext.tesuto.delivery.service

import org.apache.commons.lang3.StringUtils
import org.cccnext.tesuto.delivery.model.internal.*
import org.cccnext.tesuto.delivery.model.view.*
import org.cccnext.tesuto.delivery.view.*
import org.cccnext.tesuto.content.dto.AssessmentPartDto
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto
import org.cccnext.tesuto.content.dto.section.AssessmentItemSessionControlDto
import org.cccnext.tesuto.content.model.ScopedIdentifier

public class DeliveryServiceAdapterSpec extends DeliverySpecBase {

    private Map<String,TaskResponseViewDto> randomResponses(AssessmentSessionViewDto session, String namespace) {
        def taskSet = taskSetService.getCurrentTaskSet(session.internalSession)
        def responses = [:]
        taskSet.tasks.each { task ->
            responses[task.taskId] = randomResponse(task, namespace)
        }
        return responses
    }

	private TaskResponseViewDto randomResponse(Task task, String namespace) {
        def taskResponseView = new TaskResponseViewDto()
        taskResponseView.duration = generator.randomInt(1,1000)
        taskResponseView.responses = []
        task.itemSessions.each { itemSession ->
            List<String> responseIds = getResponseIds(namespace, itemSession.getItemId())
            responseIds.each { responseId ->
            def itemResponse = new ItemSessionResponseViewDto()
                itemResponse.itemSessionId = itemSession.itemSessionId
            
                itemResponse.responseIdentifier = responseId
                itemResponse.values = [ generator.randomString(), generator.randomString() ]
                taskResponseView.responses << itemResponse
            }
        }
        return taskResponseView
    }
    
    private List<String> getResponseIds(String namespace, String itemId) {
         AssessmentItemDto assessmentItem = generator.readAssessmentItem(namespace, itemId)
         List<String> responseIds = new ArrayList<String>();
         assessmentItem.getResponseVars().each { responseVar ->
             responseIds.add(responseVar.getIdentifier());
         }
         return responseIds;
    }
    
    private ItemScoreViewDto randomScoreView(String itemId) {
        ItemScoreViewDto score = new ItemScoreViewDto(
            itemSessionIndex:generator.randomInt(1,10),
            itemSessionId:generator.randomId(),
            itemId:itemId,
            responseIdentifier:generator.randomId(),
            interactionType:generator.randomId(),
            score:0.5,
        )
        return score
    }

    String createSession(String userId, ScopedIdentifier scopedIdentifier, Map settings) {
        def sessionId = service.createUserAssessmentSession(userId, scopedIdentifier, generator.randomDeliveryType(), settings)
        sessionIds << sessionId
        return sessionId
    }

    String createSession(String userId, ScopedIdentifier scopedIdentifier, int version, Map settings) {
        def sessionId = service.createUserAssessmentSession(userId, scopedIdentifier, version, generator.randomDeliveryType(), settings)
        sessionIds << sessionId
        return sessionId
    }

    String createSession(ScopedIdentifier scopedIdentifier) {
        return createSession(generator.randomId(), scopedIdentifier, generator.randomAssessmentSettings())
    }
    
     String createSession(String userId, String assessmentId, Map settings) {
        def sessionId = service.createUserAssessmentSession(userId, assessmentId, generator.randomDeliveryType(), settings)
        sessionIds << sessionId
        return sessionId
    }
    
    String createSession(String assessmentId, Map settings) {
        return createSession(generator.randomId(), assessmentId, settings)
    }


    void checkAssessmentSession(AssessmentSessionViewDto session, String sessionId, Map settings) {
        assert session.assessmentSessionId == sessionId
        assert session.assessmentSettings == settings
    }

    def "After creating an assessment session using scopedIdentifier, the assessment session is returned by find method"() {
        when:
        def userId = generator.randomId()
        def settings = generator.randomAssessmentSettings()
        def sessionId = createSession(userId, scopedIdentifier, settings)
        def session1 = adapter.find(sessionId)

        then:
        checkAssessmentSession(session1, sessionId, settings)
        //omitting checks that all the fields in the view have been set correctly, because it seems like just
        //writing the assembly code a second time

        where:
        scopedIdentifier << service.assessmentReader.read().collect { it.scopedIdentifier }
    }

    def "After creating an assessment session using scopedIdentifier and version, the assessment session is returned by find method"() {
        when:
        def userId = generator.randomId()
        def settings = generator.randomAssessmentSettings()
        def sessionId = createSession(userId, assessment.scopedIdentifier, assessment.version, settings)
        def session1 = adapter.find(sessionId)

        then:
        checkAssessmentSession(session1, sessionId, settings)
        assessment.id == session1.assessmentContentId
        //omitting checks that all the fields in the view have been set correctly, because it seems like just
        //writing the assembly code a second time

        where:
        assessment << service.assessmentReader.read()
    }

    def "After creating an assessment session using assessmentId, the assessment session is returned by find method"() {
        when:
        def userId = generator.randomId()
        def settings = generator.randomAssessmentSettings()
        def sessionId = createSession(assessmentId, settings)
        def session1 = adapter.find(sessionId)

        then:
        checkAssessmentSession(session1, sessionId, settings)
        //omitting checks that all the fields in the view have been set correctly, because it seems like just
        //writing the assembly code a second time

        where:
        assessmentId << service.assessmentReader.read().collect { it.id }
    }

    def "find with a userId returns null if the userId doesn't match"() {
        when:
        def sessionId = createSession(assessmentId)

        then:
        adapter.find(sessionId, generator.randomId()) == null

        where:
        assessmentId << service.assessmentReader.read().collect { it.scopedIdentifier }
    }


    void checkResponsesPersisted(TaskSet taskSet, Map<String,TaskResponseViewDto> responses) {
        long duration = 0l;
        responses.each { taskId, taskResponseView ->
            duration += taskResponseView.duration
            Task task = taskSet.getTask(taskId)
            assert task != null
            taskResponseView.responses.each { itemResponseView ->
                ItemSession itemSession = task.getItemSession(itemResponseView.itemSessionId)
                assert itemSession != null
                Response response = itemSession.getResponse(itemResponseView.responseIdentifier)
                assert response != null
                assert response.values == itemResponseView.values
            }
        }
        assert taskSet.duration == duration
    }

     void checkScoresPersisted(TaskSet taskSet, Map<String,TaskResponseViewDto> responses) {
         long duration = 0l;
         responses.each { taskId, taskResponseView ->
            duration += taskResponseView.duration
            Task task = taskSet.getTask(taskId)
            assert task != null
            taskResponseView.responses.each { itemResponseView ->
                ItemSession itemSession = task.getItemSession(itemResponseView.itemSessionId)
                assert itemSession != null
                Outcome outcome = itemSession.getOutcome("SCORE")
                assert outcome != null
                assert outcome.values != null
                assert outcome.values.size() == 1
                assert StringUtils.isNotBlank(outcome.values.get(0))
                //Outcome Score min= 1 and max = 3 or (max = -5000 for branch rule tests)
                assert (Float.parseFloat(outcome.values.get(0)) >= 1.0 && Float.parseFloat(outcome.values.get(0)) <= 3.0) || Float.parseFloat(outcome.values.get(0)) == -5000
            }
         }
         assert taskSet.duration == duration
    }

    def "After merging responses with the assessment session, the responses are persisted"() {
        when:
        String sessionId = createSession(assessmentId)
        AssessmentSessionViewDto session = adapter.find(sessionId)
        //TODO: test on more than just the first task set?
        String namespace = session.internalSession.assessment.namespace
        Map<String,TaskResponseViewDto> responses = randomResponses(session, namespace)
        adapter.mergeResponses(session, responses)
        AssessmentSessionViewDto reloaded = adapter.find(sessionId)
        TaskSet current = taskSetService.getCurrentTaskSet(reloaded.internalSession)

        then:
        checkResponsesPersisted(current, responses)

        where:
        assessmentId << service.assessmentReader.read().collect { it.scopedIdentifier }
    }


    def "After completing a task set, the responses are persisted and the next task set is returned"() {
        when:
        String sessionId = createSession(assessmentId)
        setSessionAllowSkipping(sessionId, true)
        setSessionRequireValidation(sessionId, false)
        AssessmentSessionViewDto session = adapter.find(sessionId)
        //TODO: test on more than just the first task set?
        String prevTaskSetId = session.internalSession.currentTaskSetId
        String namespace = session.internalSession.assessment.namespace
        Map<String,TaskResponseViewDto> responses = randomResponses(session, namespace)
        TaskSetViewDto nextTaskSet = adapter.complete(session, responses, user)
        def previousTaskSet = taskSetService.find(prevTaskSetId)
        AssessmentSessionViewDto reloaded = adapter.find(sessionId)
        AssessmentSession internal = reloaded.internalSession
        def nextTaskSetId = internal.getTaskSetIdAfter(previousTaskSet.taskSetId)

        then:
        previousTaskSet.completionDate != null //Task set is completed
        (nextTaskSet == null && internal.currentTaskSetId == null) ||
                internal.currentTaskSetId == nextTaskSet.taskSetId //the next current task set was returned correctly
        checkResponsesPersisted(previousTaskSet, responses) //make sure responses are persisted
        checkScoresPersisted(previousTaskSet, responses)
        //The next two conditions are to make sure the next task set was set correctly
        nextTaskSetId == null || nextTaskSetId == nextTaskSet.taskSetId
        nextTaskSetId == internal.currentTaskSetId

        where:
        assessmentId << service.assessmentReader.read().collect { it.scopedIdentifier }
    }

    def "Attempting to complete a task set with missing responses throws an exception"() {
        when:
        String sessionId = createSession(assessmentId)
        setSessionAllowSkipping(sessionId, false)
        AssessmentSessionViewDto session = adapter.find(sessionId)
        //TODO: test on more than just the first task set?
        String namespace = session.internalSession.assessment.namespace
        Map<String,TaskResponseViewDto> responses = randomResponses(session, namespace)
        responses.remove(responses.keySet()[0])
        adapter.complete(session, responses, user)

        then:
        thrown(TaskSetCompletionValidationException)

        where:
        assessmentId << service.assessmentReader.read().collect { it.scopedIdentifier }
    }

    def "boolean fields from AssessmentPart and ItemSessionControl are set correctly in the task sets and item sessions"() {
        when:
        AssessmentPartDto part = assessment.assessmentParts[0]
        AssessmentItemSessionControlDto control = part.itemSessionControl
        String sessionId = createSession(assessment.scopedIdentifier)
        AssessmentSessionViewDto session = adapter.find(sessionId)
        TaskSet currentTaskSet = taskSetService.getCurrentTaskSet(session.internalSession)

        then:
        currentTaskSet.navigationMode == part.assessmentPartNavigationMode
        currentTaskSet.submissionMode == part.assessmentPartSubmission
        if (control != null) {
            currentTaskSet.tasks.each { task ->
                task.itemSessions.each { itemSession ->
                    assert itemSession.allowSkipping == control.allowSkipping
                    assert itemSession.validateResponses == control.validateResponses
                }
            }
        }

        where:
        assessment << service.assessmentReader.read()
    }

    def "outcome score is calculated properly given a set of scores and outcomeDeclaration"() {
        when:
            def scores = []
            def assessmentItemId = assessmentItem.id
            (1..8).each {
                scores += randomScoreView(assessmentItemId)
            }
            def outcomes = adapter.checkOutcomes(scores)
        then:outcomes.each{ outcome ->
                outcome.min == 1
                outcome.max == 3
                outcome.score == 3
                outcome.rawScore == 4
        }

        where:
        assessmentItem << generator.itemService.read()
    }

    def "outcome score is calculated properly given a score and outcomeDeclaration"() {
        when:
            def scores = []
            def assessmentItemId = assessmentItem.id
            scores += randomScoreView(assessmentItemId)
            def outcomes = adapter.checkOutcomes(scores)

        then:outcomes.each{ outcome ->
                outcome.min == 1
                outcome.max == 3
                outcome.score == 1
                outcome.rawScore == (float)0.5
        }

        where:
        assessmentItem << generator.itemService.read()
    }
}
