package org.cccnext.tesuto.content.dto

import org.apache.commons.collections.CollectionUtils
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto
import org.cccnext.tesuto.content.dto.item.AssessmentItemResponseMappingDto
import org.cccnext.tesuto.content.dto.item.AssessmentResponseProcessingDto
import org.cccnext.tesuto.content.dto.item.AssessmentResponseVarDto
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentChoiceInteractionDto
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentExtendedTextInteractionDto
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionDto
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionType
import org.cccnext.tesuto.content.dto.item.interaction.AssessmentTextEntryInteractionDto
import org.cccnext.tesuto.service.importer.validate.ValidateAssessmentItemService
import org.cccnext.tesuto.content.dto.AssessmentDtoGenerator
import org.cccnext.tesuto.util.ValidationUtil
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification


/**
 * Created by jasonbrown on 4/7/16.
 */
public class ValidateAssessmentItemServiceSpec extends Specification {

    @Shared
    ApplicationContext context
    @Shared
    ValidateAssessmentItemService validateAssessmentItemService
    @Shared
    ValidationUtil validationUtil
    @Shared
    AssessmentDtoGenerator generator = new AssessmentDtoGenerator()
    int count = 0;

    def setupSpec() {
        ApplicationContext context = new ClassPathXmlApplicationContext("/commonImportContext.xml")
        validateAssessmentItemService = context.getBean("validateAssessmentItemService")
        validationUtil = context.getBean("validationUtil")
        validationUtil.useFullBranchRuleEvaluation = false
    }

    //This is not random, nor are any of these for the combinations.
    //I'm going through this iteratively to ensure that they work as expected as I pulled this code from
    //http://stackoverflow.com/questions/127704/algorithm-to-return-all-combinations-of-k-elements-from-n
    def "given a list of 1 element and 1 combination of that element will return the expected list of lists"() {
        when:
        int elements = 1
        int combinations = 1
        List<Double> originalList = scores(elements)

        then:
        validateAssessmentItemService.getCombinations(combinations, originalList) == getExpectedForNOneKOne()

    }

    def "given a list of 2 elements and 1 combination of that element will return the expected list of lists"() {
        when:
        int elements = 2
        int combinations = 1
        List<Double> originalList = scores(elements)

        then:
        validateAssessmentItemService.getCombinations(combinations, originalList) == getExpectedForNTwoKOne()

    }

    def "given a list of 2 elements and 2 combinations of that element will return the expected list of lists"() {
        when:
        int elements = 2
        int combinations = 2
        List<Double> originalList = scores(elements)

        then:
        validateAssessmentItemService.getCombinations(combinations, originalList) == getExpectedForNTwoKTwo()
    }

    def "given a list of 3 elements and 1 combinations of that element will return the expected list of lists"() {
        when:
        int elements = 3
        int combinations = 1
        List<Double> originalList = scores(elements)

        then:
        validateAssessmentItemService.getCombinations(combinations, originalList) == getExpectedForNThreeKOne()
    }

    def "given a list of 3 elements and 2 combinations of that element will return the expected list of lists"() {
        when:
        int elements = 3
        int combinations = 2
        List<Double> originalList = scores(elements)

        then:
        validateAssessmentItemService.getCombinations(combinations, originalList) == getExpectedForNThreeKTwo()
    }


    def "given a list of 3 elements and 3 combinations of that element will return the expected list of lists"() {
        when:
        int elements = 3
        int combinations = 3
        List<Double> originalList = scores(elements)

        then:
        validateAssessmentItemService.getCombinations(combinations, originalList) == getExpectedForNThreeKThree()
    }

    def "given a list of 3 elements and invalid number of combinations k getCombinations will not throw exception"() {
        when:
        int elements = 3
        int combinations = 0 - generator.randomInt(1, 100)
        List<Double> originalList = scores(elements)

        then:
        validateAssessmentItemService.getCombinations(combinations, originalList) == new ArrayList() // Just an empty list

        where:
        i << (0..5)
    }

    def "given a list empty list getCombinations will not throw exception"() {
        when:
        int combinations = generator.randomInt(1, 100)
        List<Double> originalList = new ArrayList<>()

        then:
        validateAssessmentItemService.getCombinations(combinations, originalList) == new ArrayList() // Just an empty list

        where:
        i << (0..5)
    }

    def "given a list of 3 and minChoices of 1 and maxChoices 3 getSubsets will return expected list of lists"() {
        when:
        int elements = 3
        int minChoices = 1
        int maxChoices = 3
        List<Double> originalList = scores(elements)

        then:
        validateAssessmentItemService.getSubsets(originalList, minChoices, maxChoices) == getExpectedForMinOneAndMaxThree()

    }

    def "given a max choices greater than min choice getSubsets will not throw exception"() {
        when:
        int elements = 3
        int maxChoices = generator.rand.nextInt(100)
        int minChoices = maxChoices + 1
        List<Double> originalList = scores(elements)

        then:
        validateAssessmentItemService.getSubsets(originalList, minChoices, maxChoices) == new ArrayList()

        where:
        i << (0..5)
    }

    def "given an empty list getSubsets will not throw exception"() {
        when:
        int elements = 3
        int maxChoices = generator.rand.nextInt(100)
        int minChoices = maxChoices + 1
        List<Double> originalList = new ArrayList<>()

        then:
        validateAssessmentItemService.getSubsets(originalList, minChoices, maxChoices) == new ArrayList()

    }

    def "given a list of Lists with possible scores normalize set will reduce lists to a set of total scores"() {
        when:
        List<List<Double>> listOfPossibleScores = getExpectedForMinOneAndMaxThree()

        then:
        validateAssessmentItemService.normalizeSet(listOfPossibleScores) == expectedNormalize()
    }

    def "given a list of Lists with possible negative scores normalize set will reduce lists to a set of total scores"() {
        when:
        List<List<Double>> listOfPossibleScores = createAnExpectListOfListsWithNegativeScores()

        then:
        validateAssessmentItemService.normalizeSet(listOfPossibleScores) == expectedNormalizeWithNegativScores()
    }

    /* Test getMatchingInteractionForResponseVarDto */

    def "given a null response identifier will return null"() {
        when:
        def responseid = null
        List<AssessmentInteractionDto> interactionDtos = createInteractionDtoList(createListOfIds(2))

        then:
        validateAssessmentItemService.getMatchingInteractionForResponseVarDto(responseid, interactionDtos) == null
    }

    def "given a empty list will return null"() {
        when:
        def responseid = generator.randomString()
        List<AssessmentInteractionDto> emptyList = new ArrayList<>()

        then:
        validateAssessmentItemService.getMatchingInteractionForResponseVarDto(responseid, emptyList) == null
    }

    def "given a responseid that does not match will return null"() {
        when:
        def responseid = generator.randomString()
        List<AssessmentInteractionDto> interactionDtos = createInteractionDtoList(createListOfIds(2))

        then:
        validateAssessmentItemService.getMatchingInteractionForResponseVarDto(responseid, interactionDtos) == null
    }

    def "given a responseid that does match will match the interactionDto"() {
        when:
        def responseids = createListOfIds(generator.randomInt(1, 10))
        List<AssessmentInteractionDto> interactionDtos = createInteractionDtoList(responseids)
        def responseid = generator.randomMember(responseids)
        AssessmentInteractionDto foundInteraction = validateAssessmentItemService.getMatchingInteractionForResponseVarDto(responseid, interactionDtos)

        then:
        foundInteraction.responseIdentifier == responseid

        where:
        i << (1..5)
    }

    /* Test process item */

    def "given an extended text item will return an empty set"() {
        when:
        SortedSet<Double> expectedSet = new TreeSet<>()
        def numResponses = 2
        AssessmentItemDto extendedTextItem = createItemOfType(AssessmentInteractionType.EXTENDED_TEXT_INTERACTION, numResponses, false, expectedSet)

        then:
        validateAssessmentItemService.processItem(extendedTextItem) == expectedSet
    }

    def "given an item with no responseVars will return an empty set"() {
        when:
        SortedSet<Double> expectedSet = new TreeSet<>()
        def numResponses = 2
        //TODO change this to random item
        AssessmentItemDto extendedTextItem = createItemOfType(AssessmentInteractionType.EXTENDED_TEXT_INTERACTION, numResponses, false, expectedSet)
        extendedTextItem.responseVars = null

        then:
        validateAssessmentItemService.processItem(extendedTextItem) == expectedSet
    }

    def "given an item with responseVars and interactionDto is of type Match Interaction will return empty set"() {
        when:
        SortedSet<Double> expectedSet = new TreeSet<>()
        def numResponses = 2
        AssessmentItemDto matchInteractionItem = createItemOfType(AssessmentInteractionType.MATCH_INTERACTION, numResponses, false, expectedSet)

        then:
        matchInteractionItem.responseVars != null
        validateAssessmentItemService.processItem(matchInteractionItem) == expectedSet
    }

    def "given an item with a number of responseVars and template is of type match_correct will return expected set"() {
        when:
        validationUtil.useFullBranchRuleEvaluation = true
        def knownNumberOfResponseVars = generator.randomInt(1, 10)
        SortedSet<Double> expectedSet = createdASetForAKnownNumberOfResponsesThatAreNotMapped(knownNumberOfResponseVars)
        AssessmentItemDto matchCorrect = createItemOfType(AssessmentInteractionType.CHOICE_INTERACTION, knownNumberOfResponseVars, false, expectedSet)

        then:
        matchCorrect.responseVars != null
        validateAssessmentItemService.processItem(matchCorrect) == expectedSet

        where:
        i << (1..10)
    }

    def "given an item with a number of responseVars and template is of type match_correct will return set of size 2 with only min and max"() {
        when:
        validationUtil.useFullBranchRuleEvaluation = false
        def knownNumberOfResponseVars = generator.randomInt(1, 10)
        SortedSet<Double> expectedCompleteSet = createdASetForAKnownNumberOfResponsesThatAreNotMapped(knownNumberOfResponseVars)
        SortedSet<Double> expectedSet = setOfMaxAndMin(expectedCompleteSet)
        AssessmentItemDto matchCorrect = createItemOfType(AssessmentInteractionType.CHOICE_INTERACTION, knownNumberOfResponseVars, false, expectedCompleteSet)

        then:
        matchCorrect.responseVars != null
        expectedSet.size() == 2
        validateAssessmentItemService.processItem(matchCorrect) == expectedSet

        where:
        i << (1..10)
    }

    def setOfMaxAndMin(SortedSet<Double> set){
        SortedSet<Double> expectedSet = new TreeSet<>()
        expectedSet.add(set.first()) //min
        expectedSet.add(set.last())  //max
        return expectedSet
    }

    def "given a number of mappings and multiple responseVar a TextEntryInteractionDto will return expected Set"() {
        when:
        validationUtil.useFullBranchRuleEvaluation = true
        def numberOfMappings = generator.randomInt(1, 10)  // must be the same as  known number of ResponseVars response Vars
        SortedSet<Double> expectedSet = createdASetForAKnownNumberOfResponsesThatWillBeMapped(numberOfMappings)
        AssessmentItemDto textEntryInteraction = createItemOfType(AssessmentInteractionType.TEXT_ENTRY_INTERACTION, numberOfMappings, true, expectedSet)

        then:
        textEntryInteraction.responseVars != null
        compareSortedSetEpsilon(validateAssessmentItemService.processItem(textEntryInteraction), expectedSet)

        where:
        i << (1..10)
    }

    def "given a number of mappings and multiple responseVar a TextEntryInteractionDto will return set of size 2 with only min and max"() {
        when:
        validationUtil.useFullBranchRuleEvaluation = false
        def numberOfMappings = generator.randomInt(1, 10)  // must be the same as  known number of ResponseVars response Vars
        SortedSet<Double> expectedCompleteSet = createdASetForAKnownNumberOfResponsesThatWillBeMapped(numberOfMappings)
        SortedSet<Double> expectedSet = setOfMaxAndMin(expectedCompleteSet)
        AssessmentItemDto textEntryInteraction = createItemOfType(AssessmentInteractionType.TEXT_ENTRY_INTERACTION, numberOfMappings, true, expectedCompleteSet)

        then:
        textEntryInteraction.responseVars != null
        expectedSet.size() == 2
        compareSortedSetEpsilon(validateAssessmentItemService.processItem(textEntryInteraction), expectedSet)

        where:
        i << (1..10)
    }

    def "given a number of mappings and multiple responseVar a AssessmentInlineChoiceInteractionDto will return expected Set"() {
        when:
        validationUtil.useFullBranchRuleEvaluation = true
        def numberOfMappings = generator.randomInt(1, 10)  // must be the same as  known number of ResponseVars response Vars
        SortedSet<Double> expectedSet = createdASetForAKnownNumberOfResponsesThatWillBeMapped(numberOfMappings)
        AssessmentItemDto inlineChoiceInteraction = createItemOfType(AssessmentInteractionType.INLINE_CHOICE_INTERACTION, numberOfMappings, true, expectedSet)

        then:
        inlineChoiceInteraction.responseVars != null
        compareSortedSetEpsilon(validateAssessmentItemService.processItem(inlineChoiceInteraction), expectedSet)

        where:
        i << (1..10)
    }

    def "given a number of mappings and multiple responseVar a AssessmentInlineChoiceInteractionDto will return set of size 2 with max and min"() {
        when:
        validationUtil.useFullBranchRuleEvaluation = false
        def numberOfMappings = generator.randomInt(1, 10)  // must be the same as  known number of ResponseVars response Vars
        SortedSet<Double> expectedCompleteSet = createdASetForAKnownNumberOfResponsesThatWillBeMapped(numberOfMappings)
        SortedSet<Double> expectedSet = setOfMaxAndMin(expectedCompleteSet)
        AssessmentItemDto inlineChoiceInteraction = createItemOfType(AssessmentInteractionType.INLINE_CHOICE_INTERACTION, numberOfMappings, true, expectedCompleteSet)

        then:
        inlineChoiceInteraction.responseVars != null
        expectedSet.size() == 2
        compareSortedSetEpsilon(validateAssessmentItemService.processItem(inlineChoiceInteraction), expectedSet)

        where:
        i << (1..10)
    }

    def "given a number of mappings and multiple responseVar a AssessmentChoiceInteractionDto will return expected Set"() {
        when:
        validationUtil.useFullBranchRuleEvaluation = true
        def numberOfMappings = generator.randomInt(1, 10)
        SortedSet<Double> fullSet = createdASetForAKnownNumberOfResponsesThatWillBeMapped(numberOfMappings)

        AssessmentItemDto choiceInteraction = createItemOfType(AssessmentInteractionType.CHOICE_INTERACTION, numberOfMappings, true, fullSet)
        then:
        choiceInteraction.responseVars != null
        compareSortedSetEpsilon(validateAssessmentItemService.processItem(choiceInteraction), fullSet)

        where:
        i << (1..10)
    }

    def "given a number of mappings and multiple responseVar a AssessmentChoiceInteractionDto will return set of size 2 with max and min"() {
        when:
        validationUtil.useFullBranchRuleEvaluation = false
        def numberOfMappings = generator.randomInt(1, 10)
        SortedSet<Double> fullSet = createdASetForAKnownNumberOfResponsesThatWillBeMapped(numberOfMappings)
        SortedSet<Double> expectedSet = setOfMaxAndMin(fullSet)
        AssessmentItemDto choiceInteraction = createItemOfType(AssessmentInteractionType.CHOICE_INTERACTION, numberOfMappings, true, fullSet)
        then:
        choiceInteraction.responseVars != null
        expectedSet.size() == 2
        compareSortedSetEpsilon(validateAssessmentItemService.processItem(choiceInteraction), expectedSet)

        where:
        i << (1..10)
    }

    /* proccessAssessmentItems  tests */

    //TODO test beyond one type of item
    def "given a list of assessments will return an expected map"() {
        when:
        validationUtil.useFullBranchRuleEvaluation = generator.randomBoolean()
        def numberOfMappings = generator.randomInt(1, 10)
        SortedSet<Double> expectedSet = createdASetForAKnownNumberOfResponsesThatWillBeMapped(numberOfMappings)
        AssessmentItemDto choiceInteraction = createItemOfType(AssessmentInteractionType.CHOICE_INTERACTION, numberOfMappings, true, expectedSet)
        //simply processing the item here.  Doesn't mater if useFullBranchRule is true/false this should always have the right map
        def processItem = validateAssessmentItemService.processItem(choiceInteraction)
        HashMap<String, SortedSet<Double>> expectedMap = new HashMap<>()
        expectedMap.put(choiceInteraction.getIdentifier(), processItem)
        List<AssessmentItemDto> assessmentItemDtoList = createAssessmentItemDtoList(choiceInteraction)
        then:
        validateAssessmentItemService.processAssessmentItems(assessmentItemDtoList) == expectedMap

        where:
        i << (1..10)
    }

    /*  Test data generation */


    List<AssessmentItemDto> createAssessmentItemDtoList(AssessmentItemDto singleItem){
        List<AssessmentItemDto> itemDtos = new ArrayList<>()
        itemDtos.add(singleItem)
        return itemDtos
    }

    SortedSet<Double> createdASetForAKnownNumberOfResponsesThatAreNotMapped(int knownNumberOfResponses){
        SortedSet<Double> set = new TreeSet<>()
        (0..knownNumberOfResponses).forEach({
            set.add((Double) it)
        })
        return set
    }

    SortedSet<Double> createdASetForAKnownNumberOfResponsesThatWillBeMapped(int numOfMappings){
        SortedSet<Double> set = new TreeSet<>()
        Double rNum = generator.randomDouble(1.001d, 1000.001d)
        Double num = (generator.randomBoolean()) ? (0 - rNum) : rNum
        //Set will increase by num for the number of mappings
        num.trunc(3)
        (1..numOfMappings).forEach({
            def tempNum = num*it
            set.add(tempNum)
        })
        set.add(0.0d)
        return set
    }

    AssessmentItemDto createItemOfType(AssessmentInteractionType type,
                                       int numResponses, isMappedResponse, SortedSet<Double> set){
        List<String> ids = createListOfIds(numResponses)
        AssessmentItemDto itemDto = new AssessmentItemDto(
                identifier: "i001",
                namespace: "Developer"
        )
        def v = type.forValue(type.getValue())
        if(type.forValue(type.getValue()) == AssessmentInteractionType.EXTENDED_TEXT_INTERACTION) {
            itemDto.responseProcessing = null
            itemDto.responseVars = createResponseVarList(ids)
            itemDto.interactions = createExtendedTextInteractionDtoList(ids)

        }
        else if (type.forValue(type.getValue()) == AssessmentInteractionType.MATCH_INTERACTION){
            itemDto.responseProcessing = createResponseProcessingDto(type, isMappedResponse)
            itemDto.responseVars = createResponseVarList(ids)
            itemDto.interactions = createExtendedTextInteractionDtoList(ids)
        }else if(type.forValue(type.getValue()) == AssessmentInteractionType.CHOICE_INTERACTION){
            itemDto.responseProcessing = createResponseProcessingDto(type, isMappedResponse)
            itemDto.responseVars = isMappedResponse ? (numResponses == 1) ? createResponseVarListForMappedChoiceInteraction(ids, set) : createResponseVarList(ids, set) : createResponseVarList(ids)
            itemDto.interactions = createChoiceInteractionDtoList(ids, 1, 1)
        }else if(type.forValue(type.getValue()) == AssessmentInteractionType.TEXT_ENTRY_INTERACTION || type.forValue(type.getValue()) == AssessmentInteractionType.INLINE_CHOICE_INTERACTION){
            itemDto.responseProcessing = createResponseProcessingDto(type, true)
            itemDto.responseVars = createResponseVarList(ids, set)
            itemDto.interactions = createTextInteractionDtoList(ids)
        }
        return itemDto
    }



    List<String> createListOfIds(int size){
        return (1..size).collect({generator.randomString()})
    }


    AssessmentResponseProcessingDto createResponseProcessingDto(AssessmentInteractionType type, Boolean isMapResponse){
        def theTemplate;
        switch (type.forValue(type.getValue())){
            case AssessmentInteractionType.EXTENDED_TEXT_INTERACTION:
                theTemplate = null
                break
            case AssessmentInteractionType.INLINE_CHOICE_INTERACTION:
            case AssessmentInteractionType.TEXT_ENTRY_INTERACTION:
            case AssessmentInteractionType.MATCH_INTERACTION:
                theTemplate = "map_response"
                break
        //using null interaction to set a match correct
            case AssessmentInteractionType.CHOICE_INTERACTION:
                if(isMapResponse) {
                    theTemplate = "map_response"
                } else {
                    theTemplate = "match_correct"
                }
                break
        }

        return new AssessmentResponseProcessingDto(
                template: theTemplate


        )
    }

    List<AssessmentChoiceInteractionDto> createChoiceInteractionDtoList(List<String> ids, int max, int min){
        List<AssessmentChoiceInteractionDto> assessmentChoiceInteractionDtos = new ArrayList<>()
        ids.forEach({
            assessmentChoiceInteractionDtos.add(createChoiceInteractionDto(it, max, min))
        })
        return assessmentChoiceInteractionDtos
    }

    AssessmentChoiceInteractionDto createChoiceInteractionDto(String id, max, min){
        return new AssessmentChoiceInteractionDto(
                responseIdentifier: id,
                maxChoices: max,
                minChoices: min
        )
    }

    List<AssessmentTextEntryInteractionDto> createTextInteractionDtoList(List<String> ids){
        List<AssessmentTextEntryInteractionDto> assessmentTextEntryInteractionDtos = new ArrayList<>()
        ids.forEach({
            assessmentTextEntryInteractionDtos.add(createTextEntryInteractionDto(it))
        })
        return assessmentTextEntryInteractionDtos
    }

    AssessmentTextEntryInteractionDto createTextEntryInteractionDto(String identifier){
        return new AssessmentTextEntryInteractionDto(
                responseIdentifier: identifier
        )
    }

    List<AssessmentExtendedTextInteractionDto> createExtendedTextInteractionDtoList(List<String> ids){
        List<AssessmentExtendedTextInteractionDto> assessmentInteractionDtos = new ArrayList<>()
        ids.forEach({id->
            assessmentInteractionDtos.add(createExtendedTextInteractionDto(id))
        })
        return assessmentInteractionDtos
    }

    AssessmentExtendedTextInteractionDto createExtendedTextInteractionDto(String identifier){
        return new AssessmentExtendedTextInteractionDto(
                responseIdentifier: identifier
        )
    }

    List<AssessmentInteractionDto> createInteractionDtoList(List<String> ids){
        List<AssessmentInteractionDto> assessmentInteractionDtos = new ArrayList<>()
        ids.forEach({id->
            assessmentInteractionDtos.add(createInteractionDto(id))
        })
        return assessmentInteractionDtos
    }

    AssessmentInteractionDto createInteractionDto(String identifier){
        return new AssessmentInteractionDto(
                responseIdentifier: identifier
        )
    }

    List<AssessmentResponseVarDto> createResponseVarList(List<String> ids){
        List<AssessmentResponseVarDto> assessmentResponseVarDtos = new ArrayList<>()
        ids.forEach({id->
            assessmentResponseVarDtos.add(createResponseVarDto(id, null))
        })
        return assessmentResponseVarDtos
    }

    List<AssessmentResponseVarDto> createResponseVarListForMappedChoiceInteraction(List<String> ids, SortedSet<Double> set){
        List<AssessmentResponseVarDto> assessmentResponseVarDtos = new ArrayList<>()
        ids.each {id->
            SortedSet<Double> tempSet = new TreeSet<>(set)
            assessmentResponseVarDtos.add(createResponseVarDto(id, tempSet))
        }
        return assessmentResponseVarDtos
    }

    List<AssessmentResponseVarDto> createResponseVarList(List<String> ids, SortedSet<Double> set){
        SortedSet<Double> cloneSet = set.clone()
        cloneSet.remove(0.0d)
        def first = (cloneSet.first() < 0 && cloneSet.first() < cloneSet.last()) ? cloneSet.last() : cloneSet.first()
        List<AssessmentResponseVarDto> assessmentResponseVarDtos = new ArrayList<>()
        SortedSet<Double> subset = new TreeSet<>()
        subset.add(0.0d); //will always have zero
        subset.add(first) // all we need
        ids.each { id ->
            SortedSet<Double> tempSet = new TreeSet<>(subset)
            assessmentResponseVarDtos.add(createResponseVarDto(id, tempSet))
        }
        return assessmentResponseVarDtos
    }

    AssessmentResponseVarDto createResponseVarDto(String identifier, SortedSet<Double> set){
        return new AssessmentResponseVarDto(
                identifier: identifier,
                mapping: createResponseMappingDto(set)
        )
    }

    AssessmentItemResponseMappingDto createResponseMappingDto(SortedSet<Double> set){
        if(CollectionUtils.isEmpty(set)){
            return null
        }
        Map<String, Double> mapping = new HashMap<>()
        while(!set.isEmpty()){
            Double setValue = generator.randomBoolean() ? set.first() : set.last()
            mapping.put(generator.randomString(), setValue)
            set.remove(setValue)
        }
        return new AssessmentItemResponseMappingDto(
                mapping: mapping
        )
    }

    List<Double> scores(int numberOfItemsInTheList){
        List<Double> scores = new ArrayList<>()
        (0..<numberOfItemsInTheList).forEach({
            scores.add(0.1d)
        })
        return scores
    }


    List<List<Double>> getExpectedForNOneKOne(){
        List<List<Double>> listOfLists = new ArrayList<>()
        listOfLists.add(scores(1))
        return listOfLists
    }

    List<List<Double>> getExpectedForNTwoKOne(){
        List<List<Double>> listOfLists = getExpectedForNOneKOne()
        listOfLists.add(scores(1))
        return listOfLists
    }

    List<List<Double>> getExpectedForNTwoKTwo(){
        List<List<Double>> listOfLists = new ArrayList<>()
        listOfLists.add(scores(2))
        return listOfLists
    }

    List<List<Double>> getExpectedForNThreeKOne(){
        List<List<Double>> listOfLists = new ArrayList<>()
        listOfLists.add(scores(1))
        listOfLists.add(scores(1))
        listOfLists.add(scores(1))
        return listOfLists
    }

    List<List<Double>> getExpectedForNThreeKTwo(){
        List<List<Double>> listOfLists = new ArrayList<>()
        listOfLists.add(scores(2))
        listOfLists.add(scores(2))
        listOfLists.add(scores(2))
        return listOfLists
    }

    List<List<Double>> getExpectedForNThreeKThree(){
        List<List<Double>> listOfLists = new ArrayList<>()
        listOfLists.add(scores(3))
        return listOfLists
    }

    List<List<Double>> getExpectedForMinOneAndMaxThree(){
        List<List<Double>> listOfLists = getExpectedForNThreeKOne()
        listOfLists.add(scores(2))
        listOfLists.add(scores(2))
        listOfLists.add(scores(2))
        listOfLists.add(scores(3))
        return listOfLists
    }

    List<List<Double>> createAnExpectListOfListsWithNegativeScores(){
        List<List<Double>> listOfLists = Arrays.asList(
                Arrays.asList(0.5d),
                Arrays.asList(-1.0d),
                Arrays.asList(0.5d, -1.0d)
        )
        return listOfLists
    }
    Set<Double> expectedNormalizeWithNegativScores(){
        Set<Double> normalized = Arrays.asList(0.5d, -1.0d, -0.5d)
        return normalized
    }
    Set<Double> expectedNormalize(){
        Set<Double> normalized = Arrays.asList(0.1d, 0.2d, 0.3d)
        return normalized
    }

    void compareSortedSetEpsilon(SortedSet<Double> set1, SortedSet<Double> set2){
        double EPSILON = 0.01 // pretty high but I round to precision of 3 in the code
        assert set1.size() == set2.size()

        while(!set1.empty){
            assert equal(set1.first(), set2.first(), EPSILON) == true
            set1.remove(set1.first())
            set2.remove(set2.first())
        }
    }

    boolean equal(double a, double b, double eps){
        if(a == b) return true
        return Math.abs(a - b) < eps
    }
}
