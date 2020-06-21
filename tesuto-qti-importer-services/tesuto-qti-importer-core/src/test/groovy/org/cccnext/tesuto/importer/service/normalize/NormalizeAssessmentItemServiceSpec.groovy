package org.cccnext.tesuto.content.dto

import org.cccnext.tesuto.content.dto.item.AssessmentItemDto
import org.cccnext.tesuto.content.dto.shared.AssessmentOutcomeDeclarationDto
import org.cccnext.tesuto.service.importer.normalize.NormalizeAssessmentItemServiceImpl
import org.cccnext.tesuto.service.importer.normalize.NormalizeItemMetadataService
import org.cccnext.tesuto.service.importer.normalize.NormalizeItemMetadataServiceImpl
import org.cccnext.tesuto.content.dto.AssessmentDtoGenerator
import org.springframework.context.ApplicationContext
import spock.lang.Shared
import spock.lang.Specification

public class NormalizeAssessmentItemServiceSpec extends Specification {

    @Shared
    ApplicationContext context
    @Shared
    NormalizeAssessmentItemServiceImpl normalizeAssessmentItemService = new NormalizeAssessmentItemServiceImpl();
    @Shared
    NormalizeItemMetadataService normalizeItemMetadataService = new NormalizeItemMetadataServiceImpl();

    @Shared
    AssessmentDtoGenerator generator = new AssessmentDtoGenerator()

    def setupSpec() {
        normalizeAssessmentItemService.normalizeItemMetadataService = normalizeItemMetadataService
    }

    def "when an a list of outcomeDeclarations contains an outcome of with an expected searchType then updateOutcomeDeclarationMaxAndMin will update the expected outcomes"() {
        setup:
        String searchTypeToUpdate = generator.randomString()
        List<AssessmentOutcomeDeclarationDto> outcomeDeclarationsList = generator.randomList(5, {createOutcomeDeclaration(searchTypeToUpdate)})
        outcomeDeclarationsList.addAll(generator.randomList(5, {createOutcomeDeclaration(generator.randomString())}))
        Double expectedMax = generator.randomDouble(1, 100)
        Double expectedMin = generator.randomDouble(-1000, 100) // there is not validation here

        when:
        normalizeAssessmentItemService.updateOutcomeDeclarationMaxAndMin(outcomeDeclarationsList, searchTypeToUpdate, expectedMax, expectedMin)

        then:
        verifyOutcomes(outcomeDeclarationsList, searchTypeToUpdate, expectedMax, expectedMin)
    }

    def "when a list of outcomeDeclarations contains an outcome with an expected searchType with max and mins already set no update will occur"() {
        Double expectedMax = generator.randomDouble(1, 1000)
        Double expectedMin = generator.randomDouble(-1000, 100)

        String searchTypeToUpdate = generator.randomString()
        List<AssessmentOutcomeDeclarationDto> outcomeDeclarationsList = generator.randomList(5, {createOutcomeDeclaration(searchTypeToUpdate, expectedMax, expectedMin)})
        outcomeDeclarationsList.addAll(generator.randomList(5, {createOutcomeDeclaration(generator.randomString())}))  //add garbage data max and min is null and will not change.

        Double maxThatWillNotNormalize = generator.randomDouble(1, 1000)
        Double minThatWillNotNormalize = generator.randomDouble(-1000, 100)

        when:
        normalizeAssessmentItemService.updateOutcomeDeclarationMaxAndMin(outcomeDeclarationsList, searchTypeToUpdate, maxThatWillNotNormalize, minThatWillNotNormalize)

        then:
        verifyOutcomes(outcomeDeclarationsList, searchTypeToUpdate, expectedMax, expectedMin)
    }

    void verifyOutcomes(List<AssessmentOutcomeDeclarationDto> outcomeDeclarationDtos, String searchTypeToUpdate, Double expectedMax, Double expectedMin) {
        outcomeDeclarationDtos.forEach{
            o->
                if (o.identifier == searchTypeToUpdate){
                    assert o.normalMaximum == expectedMax
                    assert o.normalMinimum == expectedMin
                } else {
                    assert o.normalMaximum == null
                    assert o.normalMinimum == null
                }

        }
    }

    def "a list of assessments is refined as expected"() {
        setup:
        def itemIdentifiers = generator.randomList(5, {generator.randomString()})
        def map = createMap(itemIdentifiers)
        def assessmentItems = createItemList(itemIdentifiers)

        when:
        normalizeAssessmentItemService.normalizeOutcomeMaxMin(assessmentItems, map)

        then:
        assessmentItems.forEach{
            verifyOutcomes(it.outcomeDeclarationDtos, "SCORE", map[it.identifier].last(), map[it.identifier].first())
        }
    }

    void verifyItems(List<AssessmentItemDto> itemDtos, HashMap<String, SortedSet<Double>> map){
        itemDtos.forEach{
           item ->
              verifyOutcomes(item, map)
        }
    }

    HashMap<String, SortedSet<Double>> createMap(List<String> itemIdentifiers){
        HashMap<String, SortedSet<Double>> map = new HashMap<>()
        itemIdentifiers.forEach {
            identifier -> map.put(identifier, createdRandomSortedSet())
        }
        return map
    }

    SortedSet<Double> createdRandomSortedSet(){
        SortedSet<Double> sortedSet = new TreeSet<>();
        (1..generator.randomInt(5, 10)).each {
            sortedSet.add(generator.randomDouble(-100, 100))
        }
        //max cannot be zero for tests
        if(sortedSet.last() == 0){
            sortedSet.add(1.0)
        }

        //min cannot be zero for tests
        if(sortedSet.first() == 0){
            sortedSet.add(-1.0)
        }
        return sortedSet;
    }

    List<AssessmentItemDto> createItemList(List<String> identifers){
        List<AssessmentItemDto> assessmentItemDtoList = new ArrayList<>()
        identifers.forEach{ identifier -> assessmentItemDtoList.add(createItemDto(identifier)) }
        return assessmentItemDtoList
    }

    AssessmentItemDto createItemDto(String identifier){
        AssessmentItemDto itemDto = new AssessmentItemDto(
                identifier: identifier,
                id: generator.randomString(),
                namespace: generator.randomString(),
                outcomeDeclarationDtos: createOutcomeDeclarations()
                //TODO add more properties if tests require.
        )
        return itemDto
    }

    List<AssessmentOutcomeDeclarationDto> createOutcomeDeclarations(){
        List<AssessmentOutcomeDeclarationDto> outcomeDeclarationDtos = new ArrayList<>()
        outcomeDeclarationDtos.add(createOutcomeDeclaration("SCORE"))
        outcomeDeclarationDtos.add(createOutcomeDeclaration(generator.randomString()))
        return outcomeDeclarationDtos
    }


    AssessmentOutcomeDeclarationDto createOutcomeDeclaration(String identifier){
        return createOutcomeDeclaration(identifier, null, null)
    }

    AssessmentOutcomeDeclarationDto createOutcomeDeclaration(String identifier, def max, def min){
        return new AssessmentOutcomeDeclarationDto(
                identifier: identifier,
                normalMaximum: max,
                normalMinimum: min,
        )
    }
}