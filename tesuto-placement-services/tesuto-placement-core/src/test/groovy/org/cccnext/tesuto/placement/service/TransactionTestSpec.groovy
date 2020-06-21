package org.cccnext.tesuto.placement.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import spock.lang.Shared
import spock.lang.Specification

@ContextConfiguration(locations = "/test-application-context.xml")
class TransactionTestSpec extends Specification {

    @Autowired
    SubjectAreaService subjectAreaService;

    @Shared int disciplineId

    SubjectAreaGenerator generator

    def setup() {
        generator = new SubjectAreaGenerator(subjectAreaService)
    }

    @Transactional
    def "After creating a discipline, it is returned by getDisciplines()" () {
        when:
            def created = subjectAreaService.createDiscipline(generator.randomDiscipline())
            disciplineId = created.disciplineId
        then:
            subjectAreaService.getDisciplines().contains(created)
    }

    @Transactional
    def "Discipline cannot be found after in next test" () {
        expect:
            subjectAreaService.getDiscipline(disciplineId) == null
    }
}