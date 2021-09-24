package org.cccnext.tesuto.content.dto

import org.cccnext.tesuto.content.dto.competency.CompetencyMapDisciplineDto
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto
import org.cccnext.tesuto.content.dto.AssessmentDtoGenerator
import org.cccnext.tesuto.exception.MetadataFormatException
import org.cccnext.tesuto.service.importer.validate.ValidationMessage
import org.cccnext.tesuto.service.importer.validate.ValidateAssessmentMetadataService
import org.cccnext.tesuto.importer.qti.service.validate.ValidateAssessmentMetadataServiceImpl
import spock.lang.Shared
import spock.lang.Specification

import java.util.stream.Collectors

public class ValidateAssessmentMetadataServiceSpec extends Specification {

    @Shared
    ValidateAssessmentMetadataService validationService = new ValidateAssessmentMetadataServiceImpl()

    @Shared
    AssessmentDtoGenerator generator = new AssessmentDtoGenerator()

    def setupSpec() {

    }

    def createCompetencyMapDisciplineDtos(String discipline){
        def competencyMapDisciplineDtos = []
        def disciplines = createListOfStringsWithDiscipline(discipline)
        disciplines.each { d -> competencyMapDisciplineDtos += createCompetencyMapDisciplineDto(d) }
        return competencyMapDisciplineDtos
    }

    def createCompetencyMapDisciplineDtos(List<String> disciplines){
        def competencyMapDisciplineDtos = []
        disciplines.each {d -> competencyMapDisciplineDtos += createCompetencyMapDisciplineDtos(d)}
        return competencyMapDisciplineDtos
    }

    def createListOfStringsWithDiscipline(String discipline){
        def list = generator.randomList(5, {generator.randomString()}) //data before
        list += discipline
        list.addAll(generator.randomList(5, {generator.randomString()})) //data after
        return list
    }

    def createCompetencyMapDisciplineDto(String disciplineName){
        new CompetencyMapDisciplineDto(
                disciplineName: disciplineName.toUpperCase(),  //Will be stored in uppercase in db
                id: generator.randomInt(1, 1000)
        )
    }

    def createInvalidAssessmentMetadataMap(List<String> keys) {
        def map = [:]
        keys.each {
            map[it] = createAssessmentMetadata(null)
        }
        return map
    }

    def createAssessmentMetadataMap(List<String> keys, List<String> disciplines){
        def map = [:]
        keys.each{
            map[it] = createAssessmentMetadata(disciplines)
        }
        return map
    }

    def createAssessmentMetadata(List<String> disciplines){
        new AssessmentMetadataDto(
                identifier: generator.randomString(),
                competencyMapDisciplines: disciplines
        )
    }

    def createInvalidMetadataMap(HashMap validMap){
        def keySet = validMap.keySet()
    }

    def "when a discipline exists within a list of competencyMapDisciplineDto will return empty list of errors"() {
        when:
        def expectedList = []
        def discipline = generator.randomString()
        def competencyMapDisciplineDtos = createCompetencyMapDisciplineDtos(discipline)

        then:
        expectedList == validationService.verifyDiscipline(discipline, competencyMapDisciplineDtos)
    }

    def "when a discipline DNE within a list of competencyMapDisciplineDto will return list of errors"() {
        when:
        def discipline = generator.randomString()
        def competencyMapDisciplineDtos = createCompetencyMapDisciplineDtos(generator.randomString())
        def expectedErrorMessage = createValidationError(discipline, competencyMapDisciplineDtos)
        def list = validationService.verifyDiscipline(discipline, competencyMapDisciplineDtos)

        then:
        list.contains(expectedErrorMessage)
    }

    def createValidationError(def discipline, def competencyMapDisciplineDtos){
        def valid = competencyMapDisciplineDtos.stream().map{it -> it.getDisciplineName()}.collect(Collectors.toList())
        new ValidationMessage(
                message: createErrorMessage(discipline, valid),
                node: "competencyMapDiscipline",
                fileType: ValidationMessage.FileType.ASSESSMENT_METADATA
        )
    }

    def createErrorMessage(def discipline, def competencyMapDisciplineDtos){
        return String.format("The competency map discipline %s is not supported use one of the following: %s", discipline, competencyMapDisciplineDtos)
    }

    def "when an assessmentMetadata contains only values within the list of competencyMapDisciplineDtos will return empty list of errors"() {
        when:
        def expectedList = []
        def validCompetencyMapDisciplines = generator.randomList(5, {generator.randomString().toUpperCase()})
        def competencyMapDisciplineDtos = createCompetencyMapDisciplineDtos(validCompetencyMapDisciplines)
        def assessmentMetadata = createAssessmentMetadata(validCompetencyMapDisciplines)

        then:
        expectedList == validationService.processAssessmentMetadata(assessmentMetadata, competencyMapDisciplineDtos)
    }

    def "when an assessmentMetadata contains values beyond that within the list of competencyMapDisciplineDtos return list of errors"() {
        when:
        def validCompetencyMapDisciplines = generator.randomList(5, { generator.randomString() })
        def competencyMapDisciplineDtos = createCompetencyMapDisciplineDtos(validCompetencyMapDisciplines)
        def invalidCompetencyMapDiscipline = generator.randomString()
        validCompetencyMapDisciplines += invalidCompetencyMapDiscipline
        def assessmentMetadata = createAssessmentMetadata(validCompetencyMapDisciplines)

        def expectedErrorMessage = createValidationError(invalidCompetencyMapDiscipline, competencyMapDisciplineDtos)
        def list = validationService.processAssessmentMetadata(assessmentMetadata, competencyMapDisciplineDtos)

        then:
        list.contains(expectedErrorMessage)
    }

    def "when metadata.competencyMapDisciplines is null will processAssessmentMetadata will return empty list of errors"() {
        when:
        def expectedList = []
        def validCompetencyMapDisciplines = generator.randomList(5, { generator.randomString() })
        def competencyMapDisciplineDtos = createCompetencyMapDisciplineDtos(validCompetencyMapDisciplines)

        def assessmentMetadata = createAssessmentMetadata(null)

        then:
        expectedList == validationService.processAssessmentMetadata(assessmentMetadata, competencyMapDisciplineDtos)

    }

    def "when map contains assessmentMetadata that have a valid list of competencyMapDisciplines will return empty list of errors"() {
        when:
        def expectedList = []
        def keys = generator.randomList(5, { generator.randomString() })
        def validCompetencyMapDisciplines = generator.randomList(5, { generator.randomString() })
        def competencyMapDisciplineDtos = createCompetencyMapDisciplineDtos(validCompetencyMapDisciplines)
        def assessmentMetadataMap = createAssessmentMetadataMap(keys, validCompetencyMapDisciplines)

        then:
        expectedList == validationService.processMetadataMap(assessmentMetadataMap, competencyMapDisciplineDtos)
    }

    def "when map contains an assessmentMetadata that has an invalid list of competencyMapDisciplines return list of errors"() {
        when:
        def keys = generator.randomList(5, { generator.randomString() })
        def validCompetencyMapDisciplines = generator.randomList(5, { generator.randomString() })
        def competencyMapDisciplineDtos = createCompetencyMapDisciplineDtos(validCompetencyMapDisciplines)
        def invalidCompetencyMapDiscipline = generator.randomString()
        validCompetencyMapDisciplines += invalidCompetencyMapDiscipline
        def validMap = createAssessmentMetadataMap(keys, validCompetencyMapDisciplines)

        def expectedErrorMessage = createValidationError(invalidCompetencyMapDiscipline, competencyMapDisciplineDtos)
        def list = validationService.processMetadataMap(validMap, competencyMapDisciplineDtos)

        then:
        list.contains(expectedErrorMessage)
    }

    def "when metadata.competencyMapDisciplines is null processMetadataMap will return empty list of errors"() {
        when:
        def expectedList = []
        def keys = generator.randomList(5, { generator.randomString() })
        def validCompetencyMapDisciplines = generator.randomList(5, { generator.randomString() })
        def competencyMapDisciplineDtos = createCompetencyMapDisciplineDtos(validCompetencyMapDisciplines)
        def inValidMap = createInvalidAssessmentMetadataMap(keys)

        then:
        expectedList == validationService.processMetadataMap(inValidMap, competencyMapDisciplineDtos)
    }

    def "when metadata is null will return empty list of errors"() {
        when:
        def expectedList = []
        def validCompetencyMapDisciplines = generator.randomList(5, { generator.randomString() })
        def competencyMapDisciplineDtos = createCompetencyMapDisciplineDtos(validCompetencyMapDisciplines)

        then:
        expectedList == validationService.processMetadataMap(null, competencyMapDisciplineDtos)
    }

    def "when all identifiers in the metadataMap matches a corresponding assessment identifier will return empty list of errors"(){
        when:
        def expectedList = []
        def assessmentIds = generator.randomList(5, { generator.randomString() })
        def assessments = createAssessments(assessmentIds)
        def assessmentMetadataMap = createMetadataMapWithValidCompetencyMapDisciplines(assessmentIds)

        then:
        expectedList == validationService.validateMetadataMapKeysMatchAssessmentIdentifiers(assessmentMetadataMap, assessments);
    }

    def "when an identifier in the metadataMap does not match a corresponding assessment identifier will return list of errors"(){
        when:
        def assessmentIds = generator.randomList(5, { generator.randomString() })
        def assessments = createAssessments(assessmentIds)
        def invalidId = generator.randomString()
        assessmentIds += invalidId
        def assessmentMetadataMap = createMetadataMapWithValidCompetencyMapDisciplines(assessmentIds)

        def expectedErrorMessage = createValidationError(invalidId)
        def list = validationService.validateMetadataMapKeysMatchAssessmentIdentifiers(assessmentMetadataMap, assessments);

        then:
        list.contains(expectedErrorMessage)
    }

    def createValidationError(def identifier){
        new ValidationMessage(
                message: createErrorMessage(identifier),
                node: "identifier",
                fileType: ValidationMessage.FileType.ASSESSMENT_METADATA
        )
    }

    def createErrorMessage(def identifier) {
        return String.format("There is not a matching assessment for the metadata with the identifier: %s", identifier);
    }

    def createMetadataMapWithValidCompetencyMapDisciplines(def ids){
        def validCompetencyMapDisciplines = generator.randomList(5, { generator.randomString() })
        return createAssessmentMetadataMap(ids, validCompetencyMapDisciplines)
    }

    def createAssessments(def ids){
        def assessments = []
        ids.each{
            assessments << createAssessment(it)
        }
        return assessments
    }

    def createAssessment(def identifier){
        new AssessmentDto(
                identifier: identifier
        )
    }
}
