package org.cccnext.tesuto.activation.test

import org.cccnext.tesuto.activation.*
import org.cccnext.tesuto.activation.model.*
import org.cccnext.tesuto.admin.viewdto.StudentViewDto
import org.cccnext.tesuto.user.service.StudentService
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification

abstract class SpecBase extends Specification {

    @Shared ApplicationContext context
    @Shared ActivationService activationService
    @Shared ActivationSearchService searchService
    @Shared ActivationTestGenerator generator
    @Shared TestEventServiceImpl testEventService

    def setupSpec() {
        try {
            context = new ClassPathXmlApplicationContext("/testApplicationContext.xml")
            activationService = context.getBean("activationService")
            activationService.minDurationDays = 7
            activationService.minExpirationHour = 23
            activationService.studentService = [
                    getStudentById: { cccid -> new StudentViewDto(cccid: cccid) }
            ] as StudentService
            searchService = context.getBean("activationSearchService")
            testEventService = context.getBean("testEventService")
            generator = new ActivationTestGenerator(activationService)
            generator.testEventService = testEventService
        } catch (Exception e) {
            e.printStackTrace()
            throw e
        }
    }

    boolean doesStringContainRejectedCharacters(String string, char[] rejectedCharacters) {
        boolean contained = false;
        for (character in rejectedCharacters) {
            if (string.contains(character.toString())) {
                contained = true;
                break;
            }
        }
        return contained;
    }

    void safeDeleteActivation(Activation activation) {
        if (activation != null) {
            activationService.delete(activation.activationId)
        }
    }

    void safeDeleteTestEvent(TestEvent testEvent) {
        if (testEvent != null) {
            testEventService.delete(testEvent.getTestEventId())
        }
    }

    def createActivationWithTestEvent() {
        def testEventId = testEventService.create(user.userAccountId, generator.randomTestEvent())
        def event = testEventService.find(testEventId)
        def activations = testEventService.createActivationsFor(user, event, [ generator.randomId() ].toSet())
        [ activations[0].activationId, testEventId ]
    }

}
