package org.cccnext.tesuto.activation.test

import org.cccnext.tesuto.admin.service.CollegeReader;
import org.cccnext.tesuto.admin.dto.CollegeDto
import org.cccnext.tesuto.admin.dto.TestLocationDto
import org.cccnext.tesuto.admin.dto.SecurityGroupDto
import org.cccnext.tesuto.admin.dto.SecurityPermissionDto
import org.cccnext.tesuto.user.service.SecurityGroupReader
import org.cccnext.tesuto.activation.model.Activation
import org.cccnext.tesuto.activation.TestEventActivation
import org.cccnext.tesuto.activation.model.TestEvent
import org.cccnext.tesuto.admin.dto.UserAccountDto
import org.cccnext.tesuto.exception.ValidationException
import spock.lang.Shared

abstract class TestEventSpecBase extends SpecBase {

    @Shared TestEvent testEvent
    @Shared String uuid
    @Shared String CURRENT_USER_ID
    @Shared UserAccountDto user

    def setupSpec() {
        def event = generator.randomTestEvent()
        def now = new Date()
        event.startDate = now.minus(1)
        event.endDate = now.plus(1)
        testEventService.remotePasscodeService.remoteConfig = generator.randomConfiguration()
        testEvent = testEventService.find(testEventService.create(generator.randomId(),event))
        uuid = testEventService.dao.repository.findOne(testEvent.testEventId).uuid
        CURRENT_USER_ID = generator.randomString()
        user = generator.createTestUserAccountDto(CURRENT_USER_ID, "Fred " + CURRENT_USER_ID, false)

        testEventService.securityGroupReader = [ getSecurityGroupByGroupName: { name ->
            new SecurityGroupDto(securityGroupId: 1, groupName: name, description: "a group",
                    securityPermissionDtos: [
                            new SecurityPermissionDto(securityPermissionId: "permission 1", description: "foo1"),
                            new SecurityPermissionDto(securityPermissionId: "permission 2", description: "foo2")
                    ]
            )
        } ] as SecurityGroupReader

        testEventService.collegeService = [ read: { id ->
            new CollegeDto(
                    cccId: id, name: generator.randomString(),
                    testLocations: [ new TestLocationDto(id: testEvent.testLocationId), new TestLocationDto(id: generator.randomId()) ]
            )
        }] as CollegeReader
    }

    def "A testEvent can be saved and retrieved"() {
        when:
        def before = new Date()
        def creatorId = generator.randomId()
        def savedEventId = testEventService.create(creatorId, event)
        event.testEventId = savedEventId
        def savedEvent = testEventService.find(savedEventId)
        def after = new Date()

        then:
        savedEvent == event
        event.createdBy == creatorId
        event.updatedBy == creatorId
        event.createDate.time >= before.time
        event.createDate.time <= after.time
        event.updateDate == event.createDate

        cleanup: if (savedEventId != null) testEventService.delete(event.testEventId)
        where: event << [ generator.randomTestEvent() ]
    }

    def "Creating a testEvent with an invalid phone number throws a ValidationException"() {
        when:
        event.proctorPhone += "x"
        testEventService.create(generator.randomId(), event)

        then: thrown(ValidationException)

        where: event << [ generator.randomTestEvent() ]
    }

    def "Creating a testEvent with an invalid email address throws a ValidationException"() {
        when:
        event.proctorEmail = generator.randomString()
        testEventService.create(generator.randomId(), event)

        then: thrown(ValidationException)

        where: event << [ generator.randomTestEvent() ]
    }

    def "Updating a TestEvent works"() {
        when:
        def before = new Date()
        def savedEventId = testEventService.create(generator.randomId(), event)
        event.testEventId = savedEventId
        def newValues = generator.randomTestEvent()
        event.startDate = newValues.startDate
        event.endDate = newValues.endDate
        event.remotePasscode = "TS"
        event.proctorEmail = newValues.proctorEmail
        event.proctorFirstName = newValues.proctorFirstName
        event.proctorLastName = newValues.proctorLastName
        event.proctorPhone = newValues.proctorPhone
        event.assessmentScopedIdentifiers = newValues.assessmentScopedIdentifiers
        String updater = generator.randomId()
        testEventService.update(updater, event)
        def updated = testEventService.find(event.testEventId)
        def after = new Date()

        then:
        updated == event
        event.updatedBy == updater
        event.updateDate.time >= before.time
        event.updateDate.time <= after.time
        event.updateDate != event.createDate

        cleanup: if (event.testEventId != null) testEventService.delete(event.testEventId)
        where: event << [ generator.randomTestEvent() ]
    }


    def "Updating a test event updates activations associated with that event"() {
        when:
        def (activationId, testEventId) = createActivationWithTestEvent()
        def testEvent = testEventService.find(testEventId)
        def newEvent = generator.randomTestEvent()
        testEvent.startDate = newEvent.startDate
        testEvent.endDate = newEvent.endDate
        testEvent.proctorEmail = newEvent.proctorEmail
        testEvent.proctorFirstName = newEvent.proctorFirstName
        testEvent.proctorLastName = newEvent.proctorLastName
        testEvent.proctorPhone = newEvent.proctorPhone
        testEvent.assessmentScopedIdentifiers = newEvent.assessmentScopedIdentifiers
        testEventService.updateTestEvent(user, testEvent)
		
        def activations = activationService.findActivationsByTestEventId(testEventId)
        def activation = activations[0]

        then:
        ((TestEventActivation) activation).getTestEvent() == testEvent

        cleanup:
        activationService.findActivationsByTestEventId(testEvent.getTestEventId()).each { safeDeleteActivation(it) }
        safeDeleteTestEvent(testEvent)

    }

    def "Searching for test events by college id returns test events with the correct college id"() {
        when:
        def correctEvent = generator.randomTestEvent()
        def correctId = testEventService.create(generator.randomId(), correctEvent)
        def wrongEvent = generator.randomTestEvent()
        wrongEvent.collegeId = correctEvent.collegeId + "x"
        def wrongId = testEventService.create(generator.randomId(), wrongEvent)
        def events = testEventService.findByCollegeId(correctEvent.collegeId)

        then:
        events.any { it.testEventId == correctId }
        events.every { it.testEventId != wrongId }
        events.every { it.collegeId == correctEvent.collegeId }

        cleanup:
        if (correctId != null) testEventService.delete(correctId)
        if (wrongId != null) testEventService.delete(wrongId)

    }

    def creator() {
        generator.createTestUserAccountDto(generator.randomId(), generator.randomString(), false)
    }

    def "After adding a user to a testEvent, the user has an activation for every Assessment in the test event"() {
        when:
        def userIds = generator.randomSet(5) { generator.randomId() }
        testEventService.createActivationsFor(creator(), testEvent, userIds)
        def activations = activationService.findActivationsByTestEventId(testEvent.testEventId)
        def userAssessmentPairs = activations.findAll { it.status == Activation.Status.READY } collect { [it.userId, it.assessmentScopedIdentifier ]}.toSet()
        then:
        userIds.every { userId ->
            testEvent.assessmentScopedIdentifiers.every { assessmentScopedIdentifier ->
                userAssessmentPairs.contains([userId, assessmentScopedIdentifier])
            }
        }
    }

    def "After removing a user from a testEvent, the user's activations are canceled"() {
        when:
        def userIds = generator.randomSet(5) { generator.randomId() }
        testEventService.createActivationsFor(creator(), testEvent, userIds)
        def userId = userIds[0]
        userIds.remove(userId)
        testEventService.createActivationsFor(creator(), testEvent, userIds)
        def activations = activationService.findActivationsByTestEventId(testEvent.testEventId)
        def userActivations = activations.findAll { it.userId == userId }
        then:
        userActivations.size() > 0
        userActivations.each { it.status == Activation.Status.DEACTIVATED }
    }

    def "After removing and then adding a user, the user has a READY activation"() {
        when:
        def userIds = generator.randomSet(5) { generator.randomId() }
        testEventService.createActivationsFor(creator(), testEvent, userIds)
        def userId = userIds[0]
        userIds.remove(userId)
        testEventService.createActivationsFor(creator(), testEvent, userIds)
        userIds.add(userId)
        testEventService.createActivationsFor(creator(), testEvent, userIds)
        def activations = activationService.findActivationsByTestEventId(testEvent.testEventId)
        def userActivations = activations.findAll { it.userId == userId }
        then:
        userActivations.size() > 0
        userActivations.each { it.status == Activation.Status.READY }
    }

    def "After canceling at TestEvent, the testEvent is as expected"() {
        when:
        def savedEventId = testEventService.create(generator.randomId(), event)
        def savedTestEvent = testEventService.find(savedEventId)
        def canceledBy = creator()

        def beforeDate = new Date()
        testEventService.cancelTestEvent(canceledBy, savedTestEvent)
        def afterDate = new Date()
        def canceledTestEvent = testEventService.find(savedEventId)

        then:
        canceledTestEvent.canceled == true
        canceledTestEvent.updatedBy == canceledBy.userAccountId
        canceledTestEvent.updateDate >= beforeDate
        canceledTestEvent.updateDate <= afterDate

        cleanup: if (savedEventId != null) testEventService.delete(savedEventId)

        where: event << [ generator.randomTestEvent() ]
    }

    def "A TestEvent can  be retrieved by uuid"() {
        expect: testEvent == testEventService.findByUuid(uuid)
    }

    def "A remote proctor is created from a test event correctly"() {
        setup:
        def user = testEventService.createRemoteProctorFromTestEvent(uuid, testEvent)
        def group = testEventService.securityGroupReader.getSecurityGroupByGroupName('REMOTE_PROCTOR')

        expect:
        user.firstName == testEvent.proctorFirstName
        user.lastName == testEvent.proctorLastName
        user.emailAddress == testEvent.proctorEmail
        user.securityGroupDtos == [ group ].toSet()
        user.grantedAuthorities == group.grantedAuthorities
        user.testLocations.collect { it.id } == [ testEvent.testLocationId ]
    }

    def "After canceling at TestEvent, all associated Activations are as expected"() {
        when:
        def userIds = generator.randomSet(5) { generator.randomId() }
        testEventService.createActivationsFor(creator(), testEvent, userIds)

        def requestor = creator()
        testEventService.cancelActivationsFor(requestor, testEvent)
        def activations = activationService.findActivationsByTestEventId(testEvent.testEventId)

        then:
        activations.size() > 0
        activations.each {
            checkLatestStatusChangeHistory(it, requestor)
        }
    }

    def "Updating a test event leaves the uuid unchanged" () {
        setup:
        def event = generator.randomTestEvent()
        def savedEventId = testEventService.create(generator.randomId(), event)
        def uuid = testEventService.dao.repository.findOne(savedEventId).uuid
        event.testEventId = savedEventId
        event.name  = generator.randomString()
        String updater = generator.randomId()
        testEventService.update(updater, event)
        def updated = testEventService.dao.repository.findOne(savedEventId)

        expect:
        uuid == updated.uuid
        event.name == updated.name

        cleanup: testEventService.delete(savedEventId)
    }


    void checkLatestStatusChangeHistory(def activation, def requestor){
        def activationStatusChanges = activation.statusChangeHistory
        def latestStatusStatusChange = activationStatusChanges[0]

        assert latestStatusStatusChange.userId == requestor.userAccountId
        assert latestStatusStatusChange.reason == "Canceled Test Event"
        assert latestStatusStatusChange.newStatus == Activation.Status.DEACTIVATED
    }


    def cleanupSpec() {
        activationService.findActivationsByTestEventId(testEvent.testEventId).each {
            activationService.delete(it.activationId)
        }
        testEventService.delete(testEvent.testEventId)
    }

}
