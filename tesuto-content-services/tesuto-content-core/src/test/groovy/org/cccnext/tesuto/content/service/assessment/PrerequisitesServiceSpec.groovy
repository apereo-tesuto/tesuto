package org.cccnext.tesuto.content.service.assessment

import org.cccnext.tesuto.content.service.PrerequisitesService
import org.cccnext.tesuto.content.model.ScopedIdentifier
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification

public class PrerequisitesServiceSpec extends Specification {

    @Shared ApplicationContext context
    @Shared PrerequisitesService service
    @Shared Random random = new Random()

    def setupSpec() {
        context =  new ClassPathXmlApplicationContext("/testApplicationContext.xml")
        service = context.getBean(PrerequisitesService.class)
    }

    def randomString() {
        UUID.randomUUID().toString();
    }

    def randomId() {
        new ScopedIdentifier(namespace: randomString(), identifier: randomString())
    }


    def randomPrerequisites() {
        (0 .. random.nextInt(10)).collect( { randomId(); } ).toSet()
    }

    def "prerequisites for a non-existent assessment are non-null but empty"() {
        expect: service.get(randomId()).size() == 0
    }

    def "After setting prerequisites, we can read them"() {
        setup:
        def assessmentId = randomId();

        when: service.set(assessmentId, prerequisites);
        then: prerequisites == service.get(assessmentId);
        where: prerequisites << (1..5).collect { randomPrerequisites(); }
    }

}