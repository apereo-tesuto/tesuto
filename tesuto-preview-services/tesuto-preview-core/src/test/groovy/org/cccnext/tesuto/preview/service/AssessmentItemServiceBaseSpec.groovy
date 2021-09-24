package org.cccnext.tesuto.preview.service

import org.cccnext.tesuto.content.dto.item.AssessmentItemDto
import org.cccnext.tesuto.content.service.AssessmentItemService
import org.cccnext.tesuto.util.test.AssessmentGenerator
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification

public abstract class AssessmentItemServiceBaseSpec extends Specification {

    @Shared ApplicationContext context
    @Shared AssessmentItemService service
    @Shared AssessmentGenerator generator = new AssessmentGenerator()


    def "When generating the next latest version of an assessment item, the latest version is returned with version incremented"() {
        setup:
        def identifier = generator.randomId() // not generating a random id
        def results = generator.generateAssessmentItemsWithVersion(service, identifier, generator.setVersionNextVersion)
        when: def assessmentItem = service.readLatestVersion(generator.namespace, identifier)
        then: assessmentItem.version == results.max
        cleanup:
        if (results != null) {
            results.ids.each { id -> service.delete(id) }
        }
    }

}
