package org.cccnext.tesuto.importer.service.competency

import com.fasterxml.jackson.databind.JsonMappingException;
import java.net.URI;
import org.cccnext.tesuto.importer.service.competency.CompetencyImportService
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.util.ResourceUtils
import spock.lang.Shared
import spock.lang.Specification

//There are two spec classes.  The @Shared variables, setup, and cleanup are defined here.
//Note that @Shared does not mean "Shared between specs", but "Shared between methods within a spec".
public class CompentencyImportServiceSpec extends Specification {


    @Shared
    CompetencyImportService service

    def setupSpec() {
        ApplicationContext context = new ClassPathXmlApplicationContext("/commonImportContext.xml")
        service = context.getBean("competencyImportService")
    }


    def "Import version 2 competency"() {
      when:
        def competency = service.parseCompetencyResources(fileURL.toURI(), 2, "math")
      then:
        competency != null
        competency.sampleItem == null
        competency.studentDescription == null
      where:
        fileURL << ResourceUtils.getURL("classpath:competencySample/competency-v2.xml")
    }

    def "Import version 3 competency"() {
      when:
        def competency = service.parseCompetencyResources(fileURL.toURI(), 2, "math")
      then:
        competency != null
        competency.sampleItem != null
        competency.studentDescription != null
      where:
        fileURL << ResourceUtils.getURL("classpath:competencySample/competency-v3.xml")
    }

    def "Import invalid XML"() {
      when:
        def competency = service.parseCompetencyResources(fileURL.toURI(), 2, "math")
      then:
        thrown JsonMappingException
      where:
        fileURL << ResourceUtils.getURL("classpath:competencySample/competency-invalidxml.xml")
    }
}