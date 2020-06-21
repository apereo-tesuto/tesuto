package org.cccnext.tesuto.content.dto

import org.cccnext.tesuto.content.dto.item.metadata.CompetenciesItemMetadataDto
import org.cccnext.tesuto.content.dto.item.metadata.CompetencyRefItemMetadataDto
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto
import org.cccnext.tesuto.content.dto.AssessmentDtoGenerator
import org.cccnext.tesuto.service.importer.normalize.NormalizeItemMetadataService
import org.cccnext.tesuto.service.importer.normalize.NormalizeItemMetadataServiceImpl
import org.springframework.context.ApplicationContext
import spock.lang.Shared
import spock.lang.Specification

public class NormalizeItemMetadataServiceSpec extends Specification {

    @Shared
    ApplicationContext context

    @Shared
    NormalizeItemMetadataService normalizeItemMetadataService = new NormalizeItemMetadataServiceImpl();

    @Shared
    AssessmentDtoGenerator generator = new AssessmentDtoGenerator()

    def setupSpec() {

    }

    def "will normalize itemMetadata as expected"(){
        when:
        def itemMetadata = createItemMetadata()

        normalizeItemMetadataService.normalizeItemMetadata(itemMetadata)

        then:
        verifyNormalizationItemMetada(itemMetadata)

        where:
        i << (1..5)
    }

    void verifyNormalizationItemMetada(ItemMetadataDto itemMetadataDto){
        if(itemMetadataDto != null && itemMetadataDto.competencies != null){
            verifyNormalizationCompetenciesItemMetadataDto(itemMetadataDto.competencies)
        }
    }

    void verifyNormalizationCompetenciesItemMetadataDto(CompetenciesItemMetadataDto competenciesItemMetadataDto){
        if(competenciesItemMetadataDto != null && competenciesItemMetadataDto.competencyRef != null){
            competenciesItemMetadataDto.competencyRef.each{
                verifyNormalizationCompetencyRefItemMetadataDto(it)
            }
        }
    }

    void verifyNormalizationCompetencyRefItemMetadataDto(CompetencyRefItemMetadataDto competencyRefItemMetadataDto){
        assert competencyRefItemMetadataDto.competencyMapDiscipline == competencyRefItemMetadataDto.competencyMapDiscipline.toUpperCase()
        assert competencyRefItemMetadataDto.mapDiscipline == competencyRefItemMetadataDto.mapDiscipline.toUpperCase()
    }

    def createItemMetadata(){
        new ItemMetadataDto(
                competencies: createCompetenciesItemMetadata()
        )
    }

    def createCompetenciesItemMetadata(){
        new CompetenciesItemMetadataDto(
                competencyRef: generator.randomList(5, {createCompetencyRefItemMetadata()})
        )
    }

    def createCompetencyRefItemMetadata(){
        new CompetencyRefItemMetadataDto(
                competencyMapDiscipline: generator.randomString().toLowerCase(),
                mapDiscipline: generator.randomString().toLowerCase()
        )
    }
}