package org.cccnext.tesuto.content.service

import org.cccnext.tesuto.content.dto.competency.CompetencyMapDto
import org.cccnext.tesuto.content.dto.competency.CompetencyRefDto
import org.cccnext.tesuto.content.service.CompetencyMapService
import org.cccnext.tesuto.util.test.AssessmentGenerator
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification

public class CompetencyMapServiceSpec extends Specification {

    @Shared
    ApplicationContext context
    @Shared
    CompetencyMapService service
    @Shared
    AssessmentGenerator generator = new AssessmentGenerator()

    def setupSpec() {
        context = new ClassPathXmlApplicationContext("/testApplicationContext.xml")
        service = context.getBean(CompetencyMapService.class)
    }

    def "When reading the latest version of an competency map, the latest version is returned"() {
        setup:
        String identifier = generator.randomId()
        String discipline = generator.randomString()
        String title = generator.randomString()

        def Map results = createMaps(identifier, discipline, title)

        when:
        def compMap = service.readLatestPublishedVersion(discipline)

        then:
        compMap.version == results.max

        cleanup:
        results.ids.each { id -> service.delete(id) }

        //These add a bit of time to the test. But I'm leaving in so we make sure the problem doesn't appear again
        where:
        i << (1..3)
    }

    def "When reading all competency maps, only the latest, published versions are returned"() {
        setup:
        def maxVersions = [:]
        def uniqueCompetencies = generator.randomInt(1, 5)
        def originalCompetencyCount = service.readPublishedUnique().size()

        def competencyIds = []
        (1..uniqueCompetencies).each {
            def identifier = generator.randomId()
            def discipline = generator.randomString()
            def repetitions = generator.randomInt(1, 5)
            def nextVersion = 1
            (1..repetitions).each {
                CompetencyMapDto competencyMapDto = buildCompetencyMap(identifier, discipline, "TITLE", nextVersion)
                competencyMapDto.published = generator.randomBoolean()
                nextVersion += generator.randomInt(1, 10)
                competencyMapDto.version = nextVersion
                maxVersions[identifier] = nextVersion
                def newComp = service.create(competencyMapDto)
                competencyIds += newComp.id
            }
        }

        when:
        def compMaps = service.readPublishedUnique()

        then:
        compMaps.each { compMap ->
            if (maxVersions.containsKey(compMap.identifier)) {
                compMap.version == maxVersions[compMap.identifier]
            }
        }
        compMaps.size() == originalCompetencyCount + uniqueCompetencies

        cleanup:
        competencyIds.each { id -> service.delete(id) }

        //These add a bit of time to the test. But I'm leaving in so we make sure the problem doesn't appear again
        where:
        i << (1..3)
    }

    CompetencyMapDto buildCompetencyMap(String identifier, String discipline, String title, int version) {
        CompetencyMapDto competencyMapDto = new CompetencyMapDto(
                identifier: identifier,
                title: title,
                discipline: discipline,
                competencyRefs: generator.randomList(5) {  new CompetencyRefDto(competencyIdentifier:generator.randomString(),
                        discipline: discipline,
                    version:version) },
                published: false,
                version: version
        )
        return competencyMapDto
    }

    Map createMaps(String identifier, String discipline, String title) {
        int maxVersion = generator.randomInt(5, 10)
        List<String> competencyIds = []
        (1..maxVersion).each {
            CompetencyMapDto compMap = buildCompetencyMap(identifier, discipline, title, it)
            CompetencyMapDto createdMap = service.create(compMap)
            competencyIds += createdMap.id
        }
        return [max: maxVersion, ids: competencyIds]
    }

}