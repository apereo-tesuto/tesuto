package org.cccnext.tesuto.content.dto

import org.cccnext.tesuto.content.dto.item.AssessmentItemDto
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto
import org.cccnext.tesuto.service.importer.validate.ValidationMessage
import org.cccnext.tesuto.service.importer.validate.ValidateItemMetadataService
import org.cccnext.tesuto.importer.qti.service.validate.ValidateItemMetadataServiceImpl
import spock.lang.Shared
import spock.lang.Specification

public class ValidateItemMetadataServiceSpec extends Specification {

    @Shared
    ValidateItemMetadataService validationService = new ValidateItemMetadataServiceImpl()

    @Shared
    AssessmentDtoGenerator generator = new AssessmentDtoGenerator()

    def setupSpec() {

    }

    def "when all identifiers in the metadataMap matches a corresponding item identifier will return empty list of errors"(){
        when:
        def expectedList = []
        def itemIds = generator.randomList(5, { generator.randomString() })
        def items = createItems(itemIds)
        def itemMetadataMap = createAssessmentMetadataMap(itemIds)

        then:
        expectedList == validationService.validateMetadataMapKeysMatchItemIdentifiers(itemMetadataMap, items);
    }

    def "when and identifier in the metadataMap does not match a corresponding item identifier will return list of errors"(){
        when:
        def itemIds = generator.randomList(5, { generator.randomString() })
        def items = createItems(itemIds)
        def invalidId = generator.randomString()
        itemIds += invalidId
        def itemMetadataMap = createAssessmentMetadataMap(itemIds)

        def expectedErrorMessage = createValidationError(invalidId)
        def list = validationService.validateMetadataMapKeysMatchItemIdentifiers(itemMetadataMap, items);

        then:
        list.contains(expectedErrorMessage)
    }

    def createValidationError(def identifier){
        new ValidationMessage(
                message: createErrorMessage(identifier),
                node: "identifier",
                fileType: ValidationMessage.FileType.ITEM_METADATA
        )
    }

    def createErrorMessage(def identifier) {
        return String.format("There is not a matching item for the itemMetadata with the identifier: %s", identifier);
    }

    def createAssessmentMetadataMap(List<String> keys){
        def map = [:]
        keys.each{
            map[it] = createItemMetadata(it)
        }
        return map
    }

    def createItemMetadata(def identifier){
        new ItemMetadataDto(
                identifier: identifier
        )
    }

    def createItems(def ids){
        def items = []
        ids.each{
            items << createItem(it)
        }
        return items
    }

    def createItem(def identifier){
        new AssessmentItemDto(
                identifier: identifier
        )
    }
}
