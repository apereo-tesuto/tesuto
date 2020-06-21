package org.cccnext.tesuto.activation.test

import org.cccnext.tesuto.activation.*
import org.cccnext.tesuto.activation.model.*
import org.cccnext.tesuto.activation.PasscodeValidationAttempt.ValidationResult
import org.cccnext.tesuto.admin.dto.UserAccountDto
import spock.lang.Shared

abstract class RemotePasscodeSpecBase extends SpecBase {

    @Shared PasscodeDao dao
    @Shared RemotePasscodeService service
    @Shared String CURRENT_USER_ID
    @Shared UserAccountDto user

    //To be called from the implementation
    def setupSpec() {
        dao = context.getBean("passcodeDao")
        service = createServiceWithConfigs()
        CURRENT_USER_ID = generator.randomString()
        user = generator.createTestUserAccountDto(CURRENT_USER_ID, "Fred " + CURRENT_USER_ID, false)
        testEventService.remotePasscodeService.remoteConfig = generator.randomConfiguration()
    }

    RemotePasscodeService createServiceWithConfigs() {
        def service = new RemotePasscodeService()
        service.remoteConfig = generator.randomConfiguration()
        service.dao = dao
        service.testEventService = testEventService
        service.testEventDao = context.getBean("testEventDao")
        service.passcodeServiceUtil = context.getBean("passcodeServiceUtil")
        return service
    }

    def "If a student is added to an event, that event's passcode should be valid for that new student"() {
        when:
        def (activationId, testEventId) = createActivationWithTestEvent()
        def testEvent = testEventService.find(testEventId)
        def userIds = generator.randomSet(5) { generator.randomId() }
        testEventService.createActivationsFor(user, testEvent, userIds)
        def activations = activationService.findActivationsByTestEventId(testEvent.testEventId)

        then:
        activations.each { service.attemptValidation(it, testEvent.getRemotePasscode()).getLeft() == PasscodeValidationAttempt.ValidationResult.VALID }

        cleanup:
        activations.each { safeDeleteActivation(it) }
        safeDeleteTestEvent(testEvent)
    }

    def "A remote passcode is invalid for a test event that has been cancelled"() {
        when:
        def (activationId, testEventId) = createActivationWithTestEvent()
        def testEvent = testEventService.find(testEventId)
        testEvent.setCanceled(true)
        testEventService.update(generator.randomId(), testEvent)
        def activation = activationService.find(activationId)

        then:
        service.attemptValidation(activation, testEvent.getRemotePasscode()).getLeft() == PasscodeValidationAttempt.ValidationResult.INVALID

        activationService.findActivationsByTestEventId(testEventId).each { safeDeleteActivation(it) }
        safeDeleteTestEvent(testEvent)
    }

    def "A remote passcode is invalid for assessments/students not associated with the event"() {
        when:
        def (activationId, testEventId) = createActivationWithTestEvent()
        def testEvent = testEventService.find(testEventId)
        def (otherActivationId, otherTestEventId) = createActivationWithTestEvent()
        def otherActivation = activationService.find(otherActivationId)

        then:
        service.attemptValidation(otherActivation, testEvent.getRemotePasscode()).getLeft() == PasscodeValidationAttempt.ValidationResult.INVALID

        cleanup:
        activationService.findActivationsByTestEventId(testEventId).each { safeDeleteActivation(it) }
        activationService.findActivationsByTestEventId(otherTestEventId).each { safeDeleteActivation(it) }
        safeDeleteTestEvent(testEvent)
        safeDeleteTestEvent(testEventService.find(otherTestEventId))
    }

    def "When a remote test event is created, a remote passcode is created for that event"() {
        when:
        def (activationId, testEventId) = createActivationWithTestEvent()
        def testEvent = testEventService.find(testEventId)

        then:
        testEvent.getRemotePasscode() != null

        cleanup:
        activationService.findActivationsByTestEventId(testEventId).each { safeDeleteActivation(it) }
        safeDeleteTestEvent(testEvent)
    }

    def "When a new remote passcode is created, the old one is made invalid"() {
        when:
        def (activationId, testEventId) = createActivationWithTestEvent()
        def testEvent = testEventService.find(testEventId)
        def oldPasscode = testEvent.getRemotePasscode()
        service.createRemotePasscode(testEventId)
        testEvent = testEventService.find(testEventId)
        def newPasscode = testEvent.getRemotePasscode()
        def activation = activationService.find(activationId)

        then:
        service.attemptValidation(activation, oldPasscode).getLeft() == PasscodeValidationAttempt.ValidationResult.INVALID
        service.attemptValidation(activation, newPasscode).getLeft() == PasscodeValidationAttempt.ValidationResult.VALID

        cleanup:
        activationService.findActivationsByTestEventId(testEventId).each { safeDeleteActivation(it) }
        safeDeleteTestEvent(testEvent)
    }

    def "A remote passcode can be used for resume"() {
        when:
        def (activationId, testEventId) = createActivationWithTestEvent()
        def activation = activationService.find(activationId)
        def testEvent = testEventService.find(testEventId)

        activationService.pause(activation, user)
        def validationResult = service.attemptValidation(activation, testEvent.getRemotePasscode())

        then:
        validationResult.getLeft() == PasscodeValidationAttempt.ValidationResult.VALID

        cleanup:
        activationService.findActivationsByTestEventId(testEventId).each { safeDeleteActivation(it) }
        safeDeleteTestEvent(testEvent)
    }

    def "When a passcode is required, every validation attempt is logged"() {
        when:
        def (activationId, testEventId) = createActivationWithTestEvent()
        def activation = activationService.find(activationId)
        def testEvent = testEventService.find(testEventId)
        def attempts = []
        def passcodes = []
        (1..generator.randomInt(1,5)).each {
            if (generator.randomBoolean()) {
                //unsuccessful attempt
                def passcodeValue = generator.randomId()
                service.attemptValidation(activation, passcodeValue)
                attempts << [passcode: passcodeValue, successful: false, userId: null]
            } else {
                //successful attempt
                def userId =  generator.randomId()
                def passcode = service.createRemotePasscode()
                passcodes << passcode
                service.attemptValidation(activation, passcode)
                attempts << [passcode: passcode, successful: true, userId: userId]
            }
        }
        def attemptLog = dao.findValidationAttempts(activation.activationId)

        then:
        attemptLog.size() == attempts.size()
        attempts.each { attempt ->
            null != attemptLog.find {
                it.passcode == attempt.passcode &&
                it.successful == attempt.succsessful &&
                it.userId == attempt.userId }
        }

        cleanup:
        activationService.findActivationsByTestEventId(testEventId).each { safeDeleteActivation(it) }
        safeDeleteTestEvent(testEvent)
    }

    def "Passcode validation fails if there have been too many recent failures (lockout)"() {
        setup:
        def service = createServiceWithConfigs()
        service.passcodeServiceUtil.maxFailedAttempts = generator.randomInt(1,3)
        service.passcodeServiceUtil.lockedFor = 2
        def (activationId, testEventId) = createActivationWithTestEvent()
        def activation = activationService.find(activationId)
        def testEvent = testEventService.find(testEventId)

        when:
        (1..service.passcodeServiceUtil.maxFailedAttempts).each {
            service.attemptValidation(activation, generator.randomString())
        }
        def passcode = service.createRemotePasscode()


        then:
        service.attemptValidation(activation, passcode).getLeft() == ValidationResult.LOCKED;
        Thread.sleep(service.passcodeServiceUtil.lockedFor*1000l)
        service.attemptValidation(activation, testEvent.getRemotePasscode()).getLeft() == ValidationResult.VALID;

        cleanup:
        activationService.findActivationsByTestEventId(testEventId).each { safeDeleteActivation(it) }
        safeDeleteTestEvent(testEvent)
    }

    def "Passcode generation does not create passcodes with ambiguous characters"() {
        when:
        def passcode = service.createRemotePasscode()
        def rejectedCharacters = "IOUV01".toCharArray()
        then:
        !doesStringContainRejectedCharacters(passcode, rejectedCharacters)
        where:
        i << (1..10)
    }
}