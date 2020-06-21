package org.cccnext.tesuto.content.service

import org.cccnext.tesuto.content.dto.competency.CompetencyMapDisciplineDto
import org.cccnext.tesuto.content.service.CompetencyMapDisciplineService
import org.cccnext.tesuto.util.test.AssessmentGenerator
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification

public class CompetencyMapDisciplineServiceSpec extends Specification{

    @Shared ApplicationContext context
    @Shared CompetencyMapDisciplineService competencyMapDisciplineService
    @Shared AssessmentGenerator generator = new AssessmentGenerator()

    def setupSpec() {
        context = new ClassPathXmlApplicationContext("/testApplicationContext.xml")
        competencyMapDisciplineService = context.getBean(CompetencyMapDisciplineService.class)
    }

    def "when discipline exists read by discipline will return expected discipline"() {
        setup:
        def discipline = createRandomDisciplineDto()
        when:
        def createdDiscipline = competencyMapDisciplineService.create(discipline)

        then:
        discipline == competencyMapDisciplineService.read(discipline.disciplineName)

        cleanup:
        competencyMapDisciplineService.delete(createdDiscipline)
    }

    def "when discipline DNE read by discipline will return null"() {
        setup:
        def discipline = createRandomDisciplineDto()
        def disciplineName = generator.randomBoolean() ? null : generator.randomString()

        when:
        def createdDiscipline = competencyMapDisciplineService.create(discipline)

        then:
        null == competencyMapDisciplineService.read(disciplineName)

        cleanup:
        competencyMapDisciplineService.delete(createdDiscipline)

        where:
        i << (1..5)
    }

    def "when a list of disciplines exist read will return all disciplines in that list (others beyond this also exist)"() {
        setup:
        def disciplines = createRandomListOfDisciplines()


        when:
        def createdDisciplines = competencyMapDisciplineService.create(disciplines)
        def foundDisciplines = competencyMapDisciplineService.read()

        then:
        disciplines.forEach { d ->
            foundDisciplines.contains(d)
        }

        cleanup:
        competencyMapDisciplineService.delete(createdDisciplines)

        where:
        i << (1..5)
    }

    List<CompetencyMapDisciplineDto> createRandomListOfDisciplines(){
        return generator.randomList(5, {createRandomDisciplineDto() })
    }
    CompetencyMapDisciplineDto createRandomDisciplineDto(){
        CompetencyMapDisciplineDto disciplineDto = new CompetencyMapDisciplineDto(
                disciplineName: generator.randomString()
        )
        return disciplineDto
    }
}