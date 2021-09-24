package org.cccnext.tesuto.delivery.service

import org.apache.commons.collections.CollectionUtils
import org.cccnext.tesuto.delivery.model.internal.AssessmentSession
import org.cccnext.tesuto.delivery.model.internal.Outcome
import org.cccnext.tesuto.delivery.model.internal.Response
import org.cccnext.tesuto.delivery.model.internal.TaskSet
import org.cccnext.tesuto.content.dto.AssessmentDto
import org.cccnext.tesuto.content.dto.AssessmentPartDto
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionDto
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionType
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto
import org.cccnext.tesuto.content.dto.section.AssessmentItemSessionControlDto
import org.cccnext.tesuto.content.dto.section.AssessmentOrderingDto
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto
import org.cccnext.tesuto.content.model.DeliveryType
import org.cccnext.tesuto.content.dto.enums.MetadataType
import spock.lang.Shared

import java.util.stream.Collectors

import static org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionType.*

/**
 * Created by bruce on 12/3/15.
 */
class DeliveryServiceSpec extends DeliverySpecBase {

    @Shared onlineSessions = []
    @Shared paperSessions = []
    @Shared allSessions = []

    @Override
    def setupSpec() {
        service.assessmentReader.read().each { assessment ->
            createSession(assessment, DeliveryType.ONLINE)
            createSession(assessment, DeliveryType.PAPER)
        }
    }

    def now() { return new Date(System.currentTimeMillis() + 1000) } // an extra second to avoid some race conditions
    def notLongAgo() { return new Date(System.currentTimeMillis() - 5000) }


    AssessmentSession createSession(AssessmentDto assessment, DeliveryType type) {
        def sessionId = service.createUserAssessmentSession(generator.randomId(),
                assessment.scopedIdentifier, type, generator.randomAssessmentSettings())
        sessionIds << sessionId
        AssessmentSession session = service.find(sessionId)
        if (type == DeliveryType.ONLINE) {
            onlineSessions << session
        } else {
            paperSessions << session
        }
        allSessions << session
        return session
    }


    Response randomResponse(AssessmentInteractionDto interaction, boolean valid) {
        new Response(
                responseId: interaction.responseIdentifier,
                values: (valid ? generator.randomResponseValues(interaction) :generator.randomInvalidResponseValues(interaction))
        )
    }

    def "Retrieving an assessment version for an assessment returns the version properly"() {
        when:
        def assessment = generator.randomAssessment(1)
        def session = createSession(assessment, DeliveryType.ONLINE)

        def version = service.getAssessmentVersionForSession(session.getAssessmentSessionId())

        then:
        version == assessment.getVersion()
    }

    def "When an assessmentSession outcome has a period or dollar sign in the key we can retrieve it from the session"(){
        when:
        def expectedOutcome = createOutcome()
        def expectedOutcomeIdentifier = expectedOutcome.outcomeIdentifier

        session.addOutcome(expectedOutcome)

        then:
        expectedOutcome == session.getOutcome(expectedOutcomeIdentifier)

        where: session << allSessions
    }

    def createOutcome(){
        def invalidChar = (generator.randomBoolean()) ? '.' : '$'
        new Outcome(
                outcomeIdentifier: generator.randomId() + invalidChar + generator.randomId(),
                normalMaximum: generator.randomDouble(1, 5),
                normalMinimum: generator.randomDouble(1, -5)
        )
    }

    def "Assessment sessions must be serializable"() {
        setup:
        File file = File.createTempFile("test", ".tmp")
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))

        expect: out.writeObject(session) //This just shouldn't blow up

        cleanup: file.delete()

        where: session << allSessions
    }


    def "After creating an assessment session, I should be able to retrieve it by id and it should be created correctly"() {

        when:
        def userId = generator.randomId()
        def settings = generator.randomAssessmentSettings()
        def deliveryType = generator.randomDeliveryType()
        def sessionId = service.createUserAssessmentSession(userId, assessmentId, deliveryType, settings)
        sessionIds << sessionId
        def session = service.find(sessionId)

        then:
        session != null
        session.assessmentSessionId == sessionId
        session.userId == userId
        session.assessmentSettings == settings
        session.contentId == service.assessmentReader.readLatestPublishedVersion(assessmentId).id
        session.startDate.before(now())
        session.startDate.after(notLongAgo())
        session.deliveryType == deliveryType
        session.completionDate == null
        session.assessment != null

        where:
        assessmentId << service.assessmentReader.read().collect { it.scopedIdentifier }
    }


    //Check whether an uncompleted task set is properly formed
    void checkTaskSet(TaskSet taskSet, int expectedFirstItem, int expectedTaskSetIndex, int taskNum) {
        assert taskSet.taskSetIndex == expectedTaskSetIndex
        assert taskSet.completionDate == null
        if (taskNum > 0) {
            assert taskSet.tasksOrderedByIndex.size() == taskNum
        }
        def task = taskSet.tasksOrderedByIndex.get(0)
        def itemSession = task.itemSessionsOrderedByIndex.get(0)
        assert itemSession.itemSessionIndex == expectedFirstItem
        def item = generator.itemService.read(itemSession.itemId)
        assert item.id + ":" + item.title == item.id + ":" + expectedFirstItem.toString()
    }


    void addResponses(AssessmentSession session, boolean valid) {
        //provide some response to every itemSession that requires it
        TaskSet taskSet = taskSetService.getCurrentTaskSet(session)
        taskSet.tasks.each { task ->
            task.itemSessions.each { itemSession ->
                AssessmentItemDto item = itemService.read(itemSession.itemId)
                item.interactions.each { interaction ->
                    itemSession.addResponse(randomResponse(interaction, valid))
                }
            }
        }
        session.saveTaskSet(taskSet.taskSetId)
    }


    //step through an online AssessmentSession verifying that it is sending us to task sets in the correct sequence, that have
    //been constructed properly,and are completing properly
    void stepThroughAssessmentSession(AssessmentSession session) {
        int expectedFirstItemSessionIndex = 1
        int expectedTaskSetIndex = 0
        while (session.currentTaskSetId != null && expectedFirstItemSessionIndex < 1000) {
            def taskSet = taskSetService.getCurrentTaskSet(session)
            checkTaskSet(taskSet, expectedFirstItemSessionIndex, ++expectedTaskSetIndex, 1)
            def numberOfTasksInPrevTaskSet = taskSet.tasksOrderedByIndex.size()
            def lastTaskInPrevTaskSet = taskSet.getTasksOrderedByIndex().get(numberOfTasksInPrevTaskSet - 1)
            expectedFirstItemSessionIndex += lastTaskInPrevTaskSet.getItemSessionsOrderedByIndex().size()
            addResponses(session, true)
            service.save(session)
            def next = service.complete(session, user)
            session = service.find(session.assessmentSessionId) //reload to get state as it is in the database
            taskSet = taskSetService.getTaskSet(taskSet.taskSetId, session) //reload the task, too
            assert next == taskSetService.getCurrentTaskSet(session)
            assert taskSet.completionDate != null && taskSet.completionDate.before(now())

        }
        assert (expectedFirstItemSessionIndex - 1) == generator.assessmentItemCounts[session.contentId]
    }

    //Assessments are randomly constructed. Some have branch rules while others do not.
    //Branchrule tests are not truly random.  Branch rules will always jump from the first section to the last section.
    def "Walk through an online assessment  session item by item"() {
        when:
        def newSession = service.find(session.assessmentSessionId)

        then:
        newSession.currentTaskSetId != null
        try {
            stepThroughAssessmentSession(newSession)
        } catch (Exception e) {
            e.printStackTrace()
            throw e
        }

        where:
        session << onlineSessions
    }

    def "Creating an assessment session for a non-existent assessment throws an AssessmentNotFoundException"() {
        when: service.createUserAssessmentSession(generator.randomId(),
                generator.randomScopedIdentifier(),
                generator.randomDeliveryType(),
                generator.randomAssessmentSettings())
        then: thrown(AssessmentNotFoundException)
    }


    def "Looking for a non-existing assessment session results in AssessmentSessionNotFoundException"() {
        when: service.find(generator.randomId())
        then: thrown(AssessmentSessionNotFoundException)
    }


    def "boolean fields from AssessmentPart and ItemSessionControl are set correctly in the task sets and item sessions"() {
        when:
        AssessmentPartDto part = assessment.assessmentParts[0]
        AssessmentItemSessionControlDto control = part.itemSessionControl
        String sessionId = service.createUserAssessmentSession(generator.randomId(),
                assessment.scopedIdentifier,
                generator.randomDeliveryType(),
                generator.randomAssessmentSettings())
        sessionIds << sessionId
        AssessmentSession session = service.find(sessionId)
        then:
        session.taskSets != null
        session.taskSetIds.each { taskSetId ->
            TaskSet taskSet = taskSetService.find(taskSetId)
            assert taskSet.navigationMode == part.assessmentPartNavigationMode
            assert taskSet.submissionMode == part.assessmentPartSubmission
            if (control != null) {
                taskSet.tasks.each { task ->
                    task.itemSessions.each { itemSession ->
                        assert itemSession.allowSkipping == control.allowSkipping
                        assert itemSession.validateResponses == control.validateResponses
                    }
                }
            }
        }

        where:
        assessment << service.assessmentReader.read()
    }

    def  itemRefsDescendedFromitemBundle(itemBundle) {
        itemBundle.getDescendants().
                collect(Collectors.toSet()).
                findAll { it instanceof AssessmentItemRefDto }.
                collect { it.id }
    }

    def tasksContainingItemRefs(session, itemRefIds) {
        session.taskSetIds.collect { taskSetId ->
            TaskSet taskSet = taskSetService.find(taskSetId)
            taskSet.tasks.findAll { task ->
                null != task.itemSessions.find { itemSession ->
                    itemRefIds.contains(itemSession.itemRefIdentifier)
                }
            }
        }.flatten()
    }

    def "All items in a itemBundle are part of the same task"() {
        when:
        def assessment = service.assessmentReader.read(session.contentId)
        def itemBundleTasks = assessment.assessmentMetadata?.section.findAll {
            sectionData -> sectionData.type == MetadataType.ITEMBUNDLE.toString()
        } collect {
            itemBundleData -> assessment.getComponent(itemBundleData.identifier).get()
        } collect { itemBundle ->
            tasksContainingItemRefs(session, itemRefsDescendedFromitemBundle(itemBundle))
        }

        then:
        itemBundleTasks.findAll { taskSet ->
            taskSet.size() > 1
        }.size() == 0

        where:
        session << onlineSessions
    }


    def sectionOf(itemSession, assessment) {
        assessment.getParent(itemSession.itemRefIdentifier)
    }


    def "A task consists of a single item session, or item sessions from a itemBundle"() {
        when:
        def assessment = service.assessmentReader.read(session.contentId)
        def tasks = null
        tasks = session.taskSetIds.collect { taskSetService.find(it) } collect { it.tasks } flatten()

        then:
        tasks.each { task ->
            task.itemSessions.size() == 1 ||
                    task.itemSessions.each {
                        assessment.assessmentMetadata.isSectionItemBundle(sectionOf(it, assessment).get().id)
                    }
        }

        where:
        session << allSessions
    }


    def "A paper assessment session is properly formed"() {
        expect:
        session.taskSetIds.size() == 1
        checkTaskSet(taskSetService.getCurrentTaskSet(session), 1, 1, 0)

        where:
        session << paperSessions
    }

    def "If an assessment section has shuffle components will deliver all components out of order"(){
        when:
        assessment.assessmentParts[0].assessmentSections = setAssessmentComponentsToShuffle(assessment.assessmentParts[0].assessmentSections)
        def sessionId = service.createUserAssessmentSession(generator.randomString(), assessment.id, DeliveryType.ONLINE, generator.randomAssessmentSettings())
        sessionIds << sessionId
        def session = service.find(sessionId) //reload to get state as it is in the database

        then:
        verifyEachComponentIsDeliveredWithShuffledComponents(session)

        where:
        assessment << service.assessmentReader.read()
    }


    List<AssessmentSectionDto> setAssessmentComponentsToShuffle(List<AssessmentSectionDto> assessmentSectionDtos){

        AssessmentOrderingDto orderingDto = new AssessmentOrderingDto(
                shuffle: true
        )

        for(AssessmentSectionDto sectionDto: assessmentSectionDtos) {
            sectionDto.setOrdering(orderingDto)
            sectionDto.setBranchRules(null)
            if (CollectionUtils.isNotEmpty(sectionDto.assessmentSections)) {
                sectionDto.assessmentSections = setAssessmentComponentsToShuffle(sectionDto.assessmentSections)
            }
        }
        return assessmentSectionDtos
    }

    void verifyEachComponentIsDeliveredWithShuffledComponents(AssessmentSession session) {
        def listOfItems = []
        while (session.currentTaskSetId != null) {
            def taskSet = taskSetService.getCurrentTaskSet(session)
            def task = taskSet.tasksOrderedByIndex[0]
            def itemSessions = task.itemSessionsOrderedByIndex

            itemSessions.each{
                AssessmentItemDto item = generator.itemService.read(it.itemId)
                listOfItems << item.title.toInteger()
            }
            addResponses(session, true)
            service.save(session)
            service.complete(session, user)
            session = service.find(session.assessmentSessionId) //reload to get state as it is in the database
        }

        assert listOfItems.max() == generator.assessmentItemCounts[session.contentId]

        //verify every item will be delivered
        for(int i = 1; i<=generator.assessmentItemCounts[session.contentId]; i++){
            assert listOfItems.contains(i)
        }
    }


    def "If an itemSession in the current taxsk set does not allow skipping, the complete method throws an exception"() {
        when:
        AssessmentSession session = createSession(assessment, DeliveryType.ONLINE)
        setSessionAllowSkipping(session.assessmentSessionId, false)
        setSessionRequireValidation(session.assessmentSessionId, false) //make sure the exception is from skipping
        service.complete(service.find(session.assessmentSessionId), user)
        then:
        thrown(TaskSetCompletionValidationException)
        where:
        assessment << service.assessmentReader.read()
    }

    def "If an itemSession in the current taskset requires validation and a response is invalid, then complete method throws an exception"() {
        when:
        AssessmentSession session = createSession(assessment, DeliveryType.ONLINE)
        setSessionAllowSkipping(session.assessmentSessionId, true) //make sure the exception is not from skipping
        setSessionRequireValidation(session.assessmentSessionId, true)
        session = service.find(session.assessmentSessionId)
        addResponses(session, false)
        service.save(session)
        service.complete(session, user)
        then:
        thrown(TaskSetCompletionValidationException)
        where:
        assessment << service.assessmentReader.read()
    }


    def "Interaction validation fails on invalid responses"() {
        when:
        def interaction = generator.randomInteractionOfType(interactionType)
        def responses = generator.randomInvalidResponseValues(interaction)
        then:
        !interaction.validateResponses(responses)
        where:
        interactionType << AssessmentInteractionType.values().minus([NULL_INTERACTION, MATCH_INTERACTION, INLINE_CHOICE_INTERACTION])
    }
}
