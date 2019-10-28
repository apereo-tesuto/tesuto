package org.cccnext.tesuto.content.service.assessment

import org.springframework.context.ApplicationContext
import org.springframework.dao.DuplicateKeyException
import org.cccnext.tesuto.content.service.AssessmentItemService
import org.springframework.context.support.ClassPathXmlApplicationContext
import com.mongodb.MongoException

public class AssessmentItemServiceSpec extends AssessmentItemServiceBaseSpec {

    def setupSpec() {
        context =  new ClassPathXmlApplicationContext("/testApplicationContext.xml")
        service = context.getBean(AssessmentItemService.class)
    }
    
    def "When reading the latest version of an assessment item, the latest version is returned"() {
        setup:
        def identifier = generator.randomId()
        def results = generator.generateAssessmentItemsWithVersion(service, 
                                                                  identifier, 
                                                                  generator.setVersionRandomly)
        when: def assessmentItem = service.readLatestVersion(generator.namespace, identifier)
        then: assessmentItem.version == results.max
        cleanup: results.ids.each { id -> service.delete(id) }
    }
    
    def "When saving item with same version, exception is thrown"() {
        setup:
        def item = generator.randomItem("Duplicate Item")
        def savedItem = service.create(item)
            item.setId(null)
        when: service.create(item)
        then: thrown(DuplicateKeyException)
        cleanup: service.delete(savedItem.id)
    }

}
