                                                                                            package org.cccnext.tesuto.content.service.assessment

import org.cccnext.tesuto.content.dto.AssessmentDto
import org.cccnext.tesuto.content.model.ScopedIdentifier
import org.cccnext.tesuto.content.service.AssessmentService
import org.cccnext.tesuto.util.test.AssessmentGenerator
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification

public abstract class AssessmentServiceBaseSpec extends Specification {

    @Shared ApplicationContext context
    @Shared AssessmentService service
    @Shared AssessmentGenerator generator = new AssessmentGenerator()
    
    def "When generating the next cached latest version of an assessment, the latest version is returned with version incremented"() {
        setup:
        def identifier = generator.randomId()
        Map results = generator.generateAssessmentsWithVersion(service,
                    identifier,
                    generator.setVersionNextVersion)
        when: def assessment = service.readLatestPublishedVersion(generator.namespace, identifier)
        then: assessment.version == results.max
        cleanup: if (results != null) results.ids.each { id -> service.delete(id)}
    }

    def "When reading the cached latest version of an assessment metadata, the latest assessment metadata is returned is from the latest assessment"() {
        setup:
        def identifier =  generator.randomId()
        def Map results = generator.generateAssessmentsWithVersion(service,
                                                                  identifier,
                                                                  generator.setVersionNextVersion)
        def assessment = service.readLatestPublishedVersion(new ScopedIdentifier(generator.namespace, identifier))
        def expected = assessment.assessmentMetadata

        when:
        def actual = service.readLatestPublishedVersionMetadata(new ScopedIdentifier(generator.namespace, identifier))

        then:
        actual == expected

        cleanup: results.ids.each { id -> service.delete(id)}
    }

    def "When reading the cached latest version of an assessment metadata and the assessment is null, will return null"() {
        setup:
        def identifier =  generator.randomId()
        def expected = null

        when:
        def actual = service.readLatestPublishedVersionMetadata(new ScopedIdentifier(generator.randomString(), identifier))

        then:
        actual == expected
    }
}
