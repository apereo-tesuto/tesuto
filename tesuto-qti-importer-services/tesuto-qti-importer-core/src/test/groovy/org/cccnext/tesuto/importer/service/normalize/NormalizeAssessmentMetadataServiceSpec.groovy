package org.cccnext.tesuto.content.dto

import org.cccnext.tesuto.content.dto.item.AssessmentItemDto
import org.cccnext.tesuto.content.dto.item.metadata.CompetenciesItemMetadataDto
import org.cccnext.tesuto.content.dto.item.metadata.CompetencyRefItemMetadataDto
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto
import org.cccnext.tesuto.content.dto.AssessmentDtoGenerator
import org.cccnext.tesuto.content.dto.metadata.SectionMetadataDto
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto
import org.cccnext.tesuto.exception.MetadataFormatException
import org.cccnext.tesuto.service.importer.normalize.NormalizeAssessmentMetadataService
import org.cccnext.tesuto.service.importer.normalize.NormalizeAssessmentMetadataService
import org.cccnext.tesuto.service.importer.normalize.NormalizeAssessmentMetadataServiceImpl
import org.cccnext.tesuto.service.importer.normalize.NormalizeAssessmentMetadataServiceImpl
import org.cccnext.tesuto.util.TesutoUtils
import org.springframework.context.ApplicationContext
import spock.lang.Shared
import spock.lang.Specification

public class NormalizeAssessmentMetadataServiceSpec extends Specification {

    @Shared
    ApplicationContext context

    @Shared
    NormalizeAssessmentMetadataService normalizeAssessmentMetadataService = new NormalizeAssessmentMetadataServiceImpl()

    @Shared
    AssessmentDtoGenerator generator = new AssessmentDtoGenerator()

    def setupSpec() {

    }


    def createCompetencies(String expected){
        CompetenciesItemMetadataDto competencies = new CompetenciesItemMetadataDto(
                competencyRef: isAttributeNull(expected) ? null : createCompetencyRefItemList(expected),
                skippedCompetency: null,  //This is still not used as of 8/10/2016
                skippedCompetencyRefId: null //This is still not used as of 8/10/2016
        )
        return isAttributeNull(expected) ? null : competencies
    }

    def createCompetencyRefItemList(String expected){
        def competencyRefItemMap = []
        def randomIndex = generator.rand.nextInt(4)
        (0..4).each {
            def randomMapDiscipline = (randomIndex == it) ? expected : null
            competencyRefItemMap += createCompetencyRefItem(randomMapDiscipline)
        }
        return competencyRefItemMap
    }

    def createCompetencyRefItem(String mapDiscipline){
        CompetencyRefItemMetadataDto compentencyRef = new CompetencyRefItemMetadataDto(
                competencyId: TesutoUtils.newId(),
                competencyMapDiscipline: mapDiscipline,
                mapDiscipline: generator.randomString(), // THis is no longer used. As of 8/10/2016
                competencyRefId: null
        )
        return compentencyRef
    }

    boolean isAttributeNull(String expected){
        return (expected == null && generator.randomBoolean())
    }

    def createAssessmentItem(String expectedMapDiscipline){
        return createAssessmentItem(expectedMapDiscipline, generator.randomString())
    }

    def createAssessmentItem(String expectedMapDiscipline, String identifier){
        AssessmentItemDto item = new AssessmentItemDto(
                identifier: identifier,
                itemMetadata: isAttributeNull(expectedMapDiscipline) ? null : createItemMetadata(expectedMapDiscipline)
        )
        return item
    }

    def createItemMetadata(String expectedMapDiscipline){
        ItemMetadataDto itemMetadata = new ItemMetadataDto(
                competencies: createCompetencies(expectedMapDiscipline)
        )
        return  isAttributeNull(expectedMapDiscipline) ? null : itemMetadata
    }

    def createExpectedMapDiscipline(){
        return generator.randomBoolean() ? null : generator.randomString()
    }

    def createItemRef(String itemIdentifier){
        new AssessmentItemRefDto(
                identifier: generator.randomString(),
                itemIdentifier: itemIdentifier
        )
    }

    def createSection(String itemRefItemIdentifier){
        AssessmentSectionDto assessmentSectionDto = new AssessmentSectionDto(
                assessmentItemRefs: createItemRefList(itemRefItemIdentifier)
        )
        return isAttributeNull(itemRefItemIdentifier) ? null : assessmentSectionDto
    }

    def createItemRefList(String identifier){
        List<AssessmentItemRefDto> itemRefs = []
        def randomIndex = generator.rand.nextInt(4)
        (0..4).each {
            def id = (randomIndex == it) ? identifier : generator.randomString()
            itemRefs += createItemRef(id)
        }
        return itemRefs
    }

    def createRandomAssessmentItems(String expected, String identifier){
        List<AssessmentItemDto> itemDtoList = []
        def randomIndex = generator.rand.nextInt(4)
        (0..4).each {
            def mapDiscipline = (randomIndex == it) ? expected : generator.randomString()
            def id = (randomIndex == it) ? identifier : generator.randomString()
            itemDtoList += createAssessmentItem(mapDiscipline, id)
        }
        return itemDtoList
    }

    def createSectionMetadata(boolean expected){
        return new SectionMetadataDto(
                identifier: generator.randomString(),
                type: "ENTRY-TESTLET", //valid
                competencyMapDiscipline: expected ?  generator.randomString().toLowerCase() : null
        )
    }

    def "getCompetencyMapDisciplineFromCompetencies will return expectedMapDiscipline"() {
        when:
        def expectedMapDiscipline = createExpectedMapDiscipline()
        def competencies = createCompetencies(expectedMapDiscipline)

        then:
        expectedMapDiscipline == normalizeAssessmentMetadataService.getCompetencyMapDisciplineFromCompetencies(competencies)

        where:
        i << (1..5)
    }

    def "getCompetencyMapDisciplineFromItem will return expectedMapDiscipline"() {
        when:
        def expectedMapDiscipline = createExpectedMapDiscipline()
        def assessmentItem = createAssessmentItem(expectedMapDiscipline)

        then:
        expectedMapDiscipline == normalizeAssessmentMetadataService.getCompetencyMapDisciplineFromItem(assessmentItem)

        where:
        i << (1..5)
    }

    def "getCompetencyMapDisciplineFormItemRefs will return expectedMapDiscipline"(){
        when:
        def expectedMapDiscipline = createExpectedMapDiscipline()
        def identifier = generator.randomString()
        def assessmentItems = createRandomAssessmentItems(expectedMapDiscipline, identifier)
        def itemRefs = createItemRefList(identifier)

        normalizeAssessmentMetadataService.setItemDtos(assessmentItems)
        normalizeAssessmentMetadataService.initializeItemDtoHashMap()


        then:
        expectedMapDiscipline == normalizeAssessmentMetadataService.getCompetencyMapDisciplineFromItemRefs(itemRefs)

        where:
        i << (1..5)

    }

    def "getCompetencyMapDisciplineFromEntrySection will return expectedMapDiscipline"(){
        when:
        def expectedMapDiscipline = createExpectedMapDiscipline()
        def identifier = generator.randomString()
        def assessmentItems = createRandomAssessmentItems(expectedMapDiscipline, identifier)
        def section = createSection(identifier)

        normalizeAssessmentMetadataService.setItemDtos(assessmentItems)
        normalizeAssessmentMetadataService.initializeItemDtoHashMap()


        then:
        expectedMapDiscipline == normalizeAssessmentMetadataService.getCompetencyMapDisciplineFromEntrySection(section)

        where:
        i << (1..5)

    }

    def "isValidSectionMetadata returns expected boolean"(){
        when:
        def expectedBoolean = generator.randomBoolean()
        def sectionMetadata = createSectionMetadata(!expectedBoolean)

        then:
        expectedBoolean == normalizeAssessmentMetadataService.isInvalidSectionMetadata(sectionMetadata)

        where:
        i << (1..3)
    }

    def "when sectionMetadata is not valid refineSectionMetadata updates sectionMetadata"(){
        when:
        def sectionMetadata = createSectionMetadata(false)

        def expectedMapDiscipline = generator.randomString()
        def identifier = generator.randomString()

        def assessmentItems = createRandomAssessmentItems(expectedMapDiscipline, identifier)
        def section = createSection(identifier)

        normalizeAssessmentMetadataService.setItemDtos(assessmentItems)
        normalizeAssessmentMetadataService.initializeItemDtoHashMap()

        normalizeAssessmentMetadataService.normalizeSectionMetadata(sectionMetadata, section)

        then:
        sectionMetadata.competencyMapDiscipline == expectedMapDiscipline

        where:
        i << (1..10)

    }

    def "when sectionMetadata.competencyMapDiscipline exists refineSectionMetadata will set to competencyMapDiscipline uppercase"(){
        when:
        def sectionMetadata = createSectionMetadata(true)
        sectionMetadata.type = generator.randomString() //not tested here

        def lowerCaseCompetencyMapDiscipline = sectionMetadata.competencyMapDiscipline

        normalizeAssessmentMetadataService.normalizeSectionMetadata(sectionMetadata, createSection(generator.randomString()))

        then:
        sectionMetadata.competencyMapDiscipline == lowerCaseCompetencyMapDiscipline.toUpperCase()

        where:
        i << (1..10)

    }

    def "when sectionMetadata is valid refineSectionMetadata does not update sectionMetadata"(){
        when:
        def sectionMetadata = createSectionMetadata(true)

        def expectedMapDiscipline = generator.randomString()
        def identifier = generator.randomString()

        def assessmentItems = createRandomAssessmentItems(expectedMapDiscipline, identifier)
        def section = createSection(identifier)

        normalizeAssessmentMetadataService.setItemDtos(assessmentItems)
        normalizeAssessmentMetadataService.initializeItemDtoHashMap()

        normalizeAssessmentMetadataService.normalizeSectionMetadata(sectionMetadata, section)

        then:
        sectionMetadata.competencyMapDiscipline != expectedMapDiscipline

        where:
        i << (1..10)

    }

    def "when sectionMetadata is not valid and there are no mapDisciplines set by author in entry testlet refineSectionMetadata throws exception"(){
        when:
        def sectionMetadata = createSectionMetadata(false)

        def expectedMapDiscipline = null
        def identifier = generator.randomString()

        def assessmentItems = createRandomAssessmentItems(expectedMapDiscipline, identifier)
        def section = createSection(identifier)

        normalizeAssessmentMetadataService.setItemDtos(assessmentItems)
        normalizeAssessmentMetadataService.initializeItemDtoHashMap()

        normalizeAssessmentMetadataService.normalizeSectionMetadata(sectionMetadata, section)

        then:
        thrown(MetadataFormatException)

        where:
        i << (1..10)

    }

    def "when assessmentMetadata has a list of competencyMapDisciplines that list is transformed to an uppercase list"(){
        when:
        def assessment = createAssessmentDto()
        def assessmentMetadata = assessment.assessmentMetadata

        normalizeAssessmentMetadataService.normalizeAssessmentDtoMetadata(assessment)

        then:
        assessmentMetadata.competencyMapDisciplines.each{
            assert it == it.toUpperCase()
        }

        where:
        i << (1..10)
    }


    AssessmentDto createAssessmentDto(){
        AssessmentMetadataDto assessmentMetadataDto = new AssessmentMetadataDto(
                competencyMapDisciplines: generator.randomList(5, {generator.randomString().toLowerCase()})
        )
        AssessmentDto assessmentDto = generator.randomAssessment(1)
        assessmentDto.assessmentMetadata = assessmentMetadataDto
        return assessmentDto
    }
}