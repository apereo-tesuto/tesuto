package org.cccnext.tesuto.preview.service

import org.cccnext.tesuto.content.service.AssessmentItemService
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext


public class AssessmentItemServiceCachedSpec extends AssessmentItemServiceBaseSpec {

    def setupSpec() {
        context =  new ClassPathXmlApplicationContext("/cached-repositories.xml")
        service = context.getBean("assessmentItemCacheService")
    }

}
