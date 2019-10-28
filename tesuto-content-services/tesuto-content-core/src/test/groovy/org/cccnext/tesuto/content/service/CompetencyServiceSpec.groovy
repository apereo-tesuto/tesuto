package org.cccnext.tesuto.content.service


import org.cccnext.tesuto.content.dto.competency.CompetencyDto
import org.cccnext.tesuto.content.dto.competency.CompetencyRefDto
import org.cccnext.tesuto.util.test.AssessmentGenerator

import org.cccnext.tesuto.content.service.CompetencyService
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification

public class CompetencyServiceSpec extends Specification {

    @Shared ApplicationContext context
    @Shared CompetencyService service
    @Shared AssessmentGenerator generator = new AssessmentGenerator()

    def setupSpec() {
        context =  new ClassPathXmlApplicationContext("testApplicationContext.xml")
        service = context.getBean(CompetencyService.class)
    }

    def "When reading the latest version of an competency, the latest version is returned"() {
        setup:
        String identifier = generator.randomId()
        String discipline = generator.randomString()
        String description = generator.randomString()

        def Map results = createMaps(identifier, discipline, description)

        when:
        def compMap = service.readLatestPublishedVersion(discipline, identifier)

        then:
        compMap.version == results.max

        cleanup:
        results.ids.each { id -> service.delete(id)}

        where:
        i << (1..3)
    }

    def "When reading all competencies, only the latest, published versions are returned"() {
        setup:
        def maxVersions = [:]
        def uniqueCompetencies = generator.randomInt(1, 5)
        def originalCompetencyCount = service.readPublishedUnique().size()

        def competencyIds = []
        (1..uniqueCompetencies).each {
            def identifier = generator.randomId()
            def discipline = generator.randomString()
            def repetitions = generator.randomInt(1, 5)
            def description = generator.randomString()
            def nextVersion = 1
            (1..repetitions).each {
                CompetencyDto competencyDto = buildCompetencyDto(identifier, discipline, description, nextVersion, null)
                competencyDto.published = generator.randomBoolean()
                nextVersion += generator.randomInt(1, 10)
                competencyDto.version = nextVersion
                maxVersions[identifier] = nextVersion
                def newComp = service.create([competencyDto])[0]
                competencyIds += newComp.id
            }
        }

        when:
        def comps = service.readPublishedUnique()

        then:
        comps.each {competencyDto ->
            if(maxVersions.containsKey(competencyDto.identifier)) {
                competencyDto.version == maxVersions[competencyDto.identifier]
            }}
        comps.size() == originalCompetencyCount + uniqueCompetencies

        cleanup:
        competencyIds.each { id -> service.delete(id) }

        where:
        i << (1..3)
    }

    def "When reading competency with refs, ref version match"() {
        setup:
        String identifier = generator.randomId()
        String discipline = generator.randomString()
        String description = generator.randomString()


        when:
        def savedDtos = buildDtosWithRefs(identifier, discipline, description)

        then:
        savedDtos.each { dto -> dto.childCompetencyDtoRefs.each {child -> child.version == dto.version}}

        cleanup:
        savedDtos.each { dto -> service.delete(dto.id)}

        where:
        i << (1..3)
    }



    Map createMaps(String identifier, String discipline, String description){
        int maxVersion = generator.randomInt(5, 10)
        List<String> competencyIds = []
        (1..maxVersion).each{
            CompetencyDto comp = buildCompetencyDto(identifier, discipline, description, it, null)
            CompetencyDto savedComp = service.create([comp])[0]
            competencyIds += savedComp.id
        }
        return [max:maxVersion, ids:competencyIds]
    }

    CompetencyDto buildCompetencyDto(String identifier, String discipline, String description, int version, CompetencyRefDto[] refs){
        CompetencyDto competencyDto = new CompetencyDto(
                identifier: identifier,
                description: description,
                discipline: discipline,
                version: version == 0 ? service.getNextVersion(discipline, identifier) : version,
                published: generator.randomBoolean(),
                childCompetencyDtoRefs: refs
        )
        return competencyDto
    }

    List<CompetencyDto> buildDtosWithRefs(String identifier, String discipline, String description){
        int maxVersion = generator.randomInt(5, 10)
        List<CompetencyDto> savedDtos = [];
        (1..maxVersion).each{
            CompetencyDto comp = buildCompetencyDto(identifier, discipline, description, it, null)
            List<CompetencyDto> dtosToSave = [];
            dtosToSave.add(comp)
            dtosToSave.addAll(buildCompetencyDtosWithRefs(comp))

            savedDtos.addAll(service.create(dtosToSave))
        }

        return savedDtos
    }

    CompetencyDto[] buildCompetencyDtosWithRefs(CompetencyDto dto){
        int i = 0
        def List<CompetencyDto> dtos = []
        (1..5).each {
            i++
            CompetencyDto ref = buildCompetencyDto(dto.identifier + i, dto.discipline, dto.description + 1, 0, null)
            dtos.add(ref)
        }
        dto.childCompetencyDtoRefs = convertCompetency(dtos)
        return dtos;
    }

    List<CompetencyRefDto> convertCompetency(List<CompetencyDto> dtos) {
        List<CompetencyRefDto> ref = []
        dtos.each({dto -> ref.add(buildCompetencyRefDto(dto))})
        return ref;

    }
    CompetencyRefDto buildCompetencyRefDto(CompetencyDto competency){
        new CompetencyRefDto(
                competencyIdentifier: competency.identifier,
                discipline: competency.discipline,
                version: competency.version
        )
    }
}
