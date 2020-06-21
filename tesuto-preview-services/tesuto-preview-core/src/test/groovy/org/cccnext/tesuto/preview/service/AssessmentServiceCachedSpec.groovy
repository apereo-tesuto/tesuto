package org.cccnext.tesuto.preview.service

import org.springframework.context.ApplicationContext
import org.cccnext.tesuto.content.service.AssessmentService
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.cccnext.tesuto.content.dto.AssessmentDto

public class AssessmentServiceCachedSpec extends AssessmentServiceBaseSpec {

    def setupSpec() {
        context =  new ClassPathXmlApplicationContext("/cached-repositories.xml")
        service = context.getBean("assessmentCacheService")
    }
    
    def "When creating an inline assessment in preview mode, it can be found using readLatestPublishedVersion"() {
        setup:
        def namespace = generator.namespace;
        def assessmentItems = []
        def assessmentIds = []
        assessmentItems.add(generator.randomItem("title"))
        AssessmentDto assessment = service.generateLinearAssessmentFromAssessmentItems(assessmentItems, namespace)
        AssessmentDto newAssessment = service.create(assessment);
        assessmentIds += newAssessment.id
        def identifier = assessment.identifier
        when: def foundAssessment = service.readLatestPublishedVersion(namespace, identifier)
        then: foundAssessment.version == 1
        cleanup: assessmentIds.each { id -> service.delete(id) }
    }

}
