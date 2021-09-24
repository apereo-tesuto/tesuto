package org.cccnext.tesuto.activation.test

import org.cccnext.tesuto.activation.*
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.data.mongodb.core.MongoOperations
import spock.lang.Shared

public class MongoActivationDaoSpec  extends ActivationSpecBase {

    @Shared ApplicationContext context


    @Override
    ActivationDao createDao() {
        MongoOperations template = context.getBean("mongoTemplate")
        return new MongoActivationDao(template)
    }

    def setupSpec() {
        context =  new ClassPathXmlApplicationContext("/testApplicationContext.xml")
        super.doSetupSpec();
    }
}
