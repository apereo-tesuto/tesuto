package org.cccnext.tesuto.importer.service.upload

import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification

//There are two spec classes.  The @Shared variables, setup, and cleanup are defined here.
//Note that @Shared does not mean "Shared between specs", but "Shared between methods within a spec".
public class ImportServiceSpec extends Specification {


    @Shared
    ImportService service

    def setupSpec() {
        ApplicationContext context = new ClassPathXmlApplicationContext("/commonImportContext.xml")
        service = context.getBean("importService")
    }


    def "After spring context is loaded dbFactory is namespaceAware"(){
        when:
        def factory = service.dbFactory

        then:
        factory.isNamespaceAware()
    }
}