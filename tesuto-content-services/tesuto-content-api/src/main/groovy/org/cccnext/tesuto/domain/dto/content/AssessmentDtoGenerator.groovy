package org.cccnext.tesuto.content.dto

import org.apache.commons.collections.CollectionUtils
import org.cccnext.tesuto.content.dto.item.*
import org.cccnext.tesuto.content.dto.item.interaction.*
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto
import org.cccnext.tesuto.content.dto.metadata.DeliveryTypeMetadataDto
import org.cccnext.tesuto.content.dto.metadata.PrerequisiteMetadataDto
import org.cccnext.tesuto.content.dto.metadata.SectionMetadataDto
import org.cccnext.tesuto.content.dto.expression.AssessmentBranchRuleDto
import org.cccnext.tesuto.content.dto.expression.AssessmentParentExpressionDto
import org.cccnext.tesuto.content.dto.expression.AssessmentChildExpressionDto
import org.cccnext.tesuto.content.dto.expression.AssessmentPreConditionDto
import org.cccnext.tesuto.content.dto.section.*
import org.cccnext.tesuto.content.dto.shared.AssessmentOutcomeDeclarationDto
import org.cccnext.tesuto.content.dto.enums.MetadataType

import org.cccnext.tesuto.util.test.Generator
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionType

import java.util.stream.Collectors

import static org.cccnext.tesuto.content.dto.item.interaction.AssessmentInteractionType.*

class AssessmentDtoGenerator extends Generator {

    def assessmentItemCounts = [:] //map assessment id -> item count
    def namespace = "test"

    //This is for maintaining a counter to show what sequence the items should be displayed in
    class Counter {
        int count;
        String title() {
            count.toString()
        }
        int incr() {
            count++
                }
    }

    Counter getCounter(){
        return new Counter()
    }

    Map randomAssessmentSettings() {
        def map = [:]
        while (randomBoolean()) {
            map[randomId()] = randomString()
        }
        return map
    }

    AssessmentDto randomAssessment(int maxSectionDepth=3) {
        def counter = new Counter()
        def assessment = new AssessmentDto(
                id: randomId(),
                identifier: randomId(),
                namespace: namespace,
                version: randomInt(1, 10),
                title: randomString(),
                language: "en",
                toolVersion: "2",
                duration: rand.nextDouble(),
                assessmentParts: [randomAssessmentPart(counter, maxSectionDepth)], //exactly one part per assessment
        )
        assessment.assessmentMetadata = randomAssessmentMetadata(randomSectionMetadataForAssessment(assessment))
        assessmentItemCounts[assessment.id] = counter.count
        return assessment
    }

    AssessmentPartDto randomAssessmentPart(Counter counter, int maxSectionDepth) {
        // Branchrules are tested in the DeliveryServiceSpec.
        // These tests currently have a max section depth of 2.
        // In an effort to reduce the time complexity of tests not in delivery module branch rules are not added.
        def testingStrategy =  (maxSectionDepth != 2) ? 0 : rand.nextInt(5)
        def assessmentPart =  new AssessmentPartDto(
                id: randomId(),
                assessmentPartNavigationMode: AssessmentPartNavigationMode.LINEAR, //only one supported
                assessmentPartSubmissionMode: AssessmentPartSubmissionMode.INDIVIDUAL, //only one one supported
                duration: rand.nextDouble(),
                assessmentSections: randomSections(counter, maxSectionDepth, testingStrategy, false),
                itemSessionControl: randomItemSessionControl()
        )
        // count and branch rules are linked
        if(testingStrategy == 1 || testingStrategy == 2 || testingStrategy == 4) {
            assessmentPart.assessmentSections = setAssessmentSectionBranchRules(assessmentPart.assessmentSections, testingStrategy)
        }
        return assessmentPart
    }

    List<AssessmentSectionDto> setAssessmentSectionBranchRules(List<AssessmentSectionDto> sectionDtos, int testingStrategy){
        if(sectionDtos == null || sectionDtos.empty){
            return sectionDtos;
        }
        // Set the target of section index 0  to section index size of the list
        sectionDtos[0].branchRules = notRandomBranchRuleDtos(sectionDtos, sectionDtos[0].id, testingStrategy)
        return sectionDtos
    }

    AssessmentItemSessionControlDto randomItemSessionControl() {
        return randomBoolean() ? null :
        new AssessmentItemSessionControlDto(
                allowSkipping: randomNullableBoolean(),
                validateResponses: randomNullableBoolean()
        )
    }

    boolean isTestingPreconditionOrBranchRule(int testingStrategy){
        return (testingStrategy > 0 && testingStrategy < 5)
    }

    /**
     * Sections must know the pattern they are going to test defined by testingStrategy (0, 1, 2, 4)
     *
     * 0 no branch rules or preconditions tested
     * 1 branches from parent section to parent section
     * 2 branches from parent section to child section
     * 3 preconditions tested
     * 4 combination of branch rule 1 and preconditions 3
     *
     * Note branching from a child section is currently not supported.
     * Note Unit tests do not currently test preconditions in the first section (ie testParts.sections[0])
     */
    List<AssessmentSectionDto> randomSections(Counter counter, int maxDepth, int testingStrategy, boolean parentSectionHasFalsePrecondition, int parentSection=0) {
        List<AssessmentSectionDto> list = new ArrayList<>()
        def randomListSize = (testingStrategy>0) ? 4 : randomInt(1, 4) //Branchrules need at least 4 sections to ensure all permutations of branching are tested.
        if (maxDepth != 0) {
            (1..randomListSize).each {
                def countThisSectionsItems = false
                // Logic for building the branchrules
                def isParentSection = (( it == 1 || it >= (randomListSize-1) ) && maxDepth == 2)
                def isOneLevelDeepTestCaseOne = (parentSection == 1 || parentSection >= randomListSize-1)
                def isOneLevelDeepTestCaseTwo = (parentSection == 1 || parentSection == randomListSize && (maxDepth != 2))
                def isOneLevelDeepTestCaseTwoThirdSection = (parentSection == randomListSize-1 && it == randomListSize && maxDepth != 2)

                if(testingStrategy == 0){
                    countThisSectionsItems = true //count everything
                }
                else if((testingStrategy == 1 && ( isParentSection || isOneLevelDeepTestCaseOne )))  {
                    countThisSectionsItems = true
                }
                else if(testingStrategy == 2 && ( isParentSection || isOneLevelDeepTestCaseTwo || isOneLevelDeepTestCaseTwoThirdSection )) {
                    countThisSectionsItems = true
                }
                else if (testingStrategy == 3 && !parentSectionHasFalsePrecondition){
                    countThisSectionsItems = true
                }else if (testingStrategy == 4 && !parentSectionHasFalsePrecondition && (isParentSection || isOneLevelDeepTestCaseOne )){
                    countThisSectionsItems = true
                }

                list << randomSection(counter, maxDepth, countThisSectionsItems, testingStrategy, parentSectionHasFalsePrecondition, it) //have to have subsections maxDepth can't be random here!
            }
        }
        return list
    }

    //When not in the the first TEST_PART Section and we can start adding preconditions
    boolean isPreconditionAllowed(int sectionIndex, int maxDepth, int testingStrategy){
        if(sectionIndex>1 && maxDepth == 2 && (testingStrategy == 3 || testingStrategy == 4) ) {
            return true;
        }
        return false;
    }

    boolean useNonRandomTitle(int sectionIndex, int maxDepth, int testingStrategy){
        if(sectionIndex==1 && maxDepth == 2 && (testingStrategy == 3 || testingStrategy == 4)) {
            return true;
        }
        return false;
    }

    AssessmentSectionDto randomSection(Counter counter, int maxDepth, boolean countThisSectionsItemRefs, int testingStrategy, boolean parentSectionHasFalsePrecondition, int parentSection) {
        boolean createFalsePrecondition = false
        if(isPreconditionAllowed(parentSection, maxDepth, testingStrategy)) {
            createFalsePrecondition = randomBoolean()
        }

        parentSectionHasFalsePrecondition = parentSectionHasFalsePrecondition || createFalsePrecondition

        countThisSectionsItemRefs = countThisSectionsItemRefs && !parentSectionHasFalsePrecondition

        def sectionTitle = randomId()
        def section = new AssessmentSectionDto(
                id   : useNonRandomTitle(parentSection, maxDepth, testingStrategy) ? sectionTitle : randomId(),
                title: randomString(),
                assessmentSections: randomSections(counter, maxDepth-1, testingStrategy, parentSectionHasFalsePrecondition, parentSection),
                preConditions: (isPreconditionAllowed(parentSection, maxDepth, testingStrategy) && parentSectionHasFalsePrecondition) ? createAssessmentPreconditionDtos(sectionTitle, createFalsePrecondition) : null,
                ordering: new AssessmentOrderingDto( shuffle: false ) //set to default
        )



        section.assessmentItemRefs =  randomList(10) { randomItemRef(counter, countThisSectionsItemRefs, testingStrategy) }
        section.assessmentComponents = createAssessmentComponentDtos(section.assessmentSections, section.assessmentItemRefs)
        return section
    }

    List<AssessmentPreConditionDto> createAssessmentPreconditionDtos(String sectionId, boolean createFalsePrecondition){
        def preconditions = new ArrayList<>()
        preconditions << createAssessmentPreconditionDto(sectionId, createFalsePrecondition)
        return preconditions
    }

    AssessmentPreConditionDto createAssessmentPreconditionDto(String sectionId, boolean createFalsePrecondition){
        return new AssessmentPreConditionDto(
                assessmentParentExpression: createAssessmentExpression(sectionId, createFalsePrecondition)
        )
    }

    //This will only create expression for a precondition
    //The goal here is not to evaluate the ExpressionEvaluationService but to evaluate delivery.
    AssessmentParentExpressionDto createAssessmentExpression(String sectionId, boolean createFalsePrecondition){
        return new AssessmentParentExpressionDto(
            expressionType: null,
            assessmentParentExpressionDtoList: null,
            assessmentChildExpressionDtoList: singleChildExpression(sectionId, createFalsePrecondition)
        )
    }


    List<AssessmentComponentDto> createAssessmentComponentDtos(List<AssessmentSectionDto> sections, List<AssessmentItemRefDto> itemRefDtos) {
        def list = []

        sections.each { section ->
            list << section
        }
        itemRefDtos.each { item ->
            list << item
        }
        return list
    }

    AssessmentItemRefDto randomItemRef(Counter count, boolean countThisItem, int testingStrategy) {
        boolean usedInBranchRuleEvaluation = isTestingPreconditionOrBranchRule(testingStrategy) && randomBoolean()
        AssessmentItemRefDto itemRefDto = new AssessmentItemRefDto(
                identifier: randomId(),
                itemIdentifier: randomItem(count, countThisItem, usedInBranchRuleEvaluation),
                categories:  usedInBranchRuleEvaluation ? null : createCategories()
        )
        return itemRefDto;
    }

    //Overridden in AssessmentGenerator
    List<String> createCategories(){
        def category = randomString()
        def categories = Arrays.asList(category)
        return categories
    }


    //overridden in AssessmentGenerator, where it actually stores an item for later retrieval
    String randomItem(Counter count, boolean  countThisItem, boolean usedInBranchRuleEvaluation) {
        return randomId()
    }


    AssessmentResponseVarDto randomResponseVar() {
        return new AssessmentResponseVarDto(
                identifier: randomId(),
                cardinality: randomCardinality(),
                baseType: randomBaseType(),
                defaultValue: new AssessmentDefaultValueDto(),
                correctResponse: randomCorrectResponse(),
                mapping: randomMappingResponse(),
        )
    }

    AssessmentOutcomeDeclarationDto randomOutcomeDeclaration() {
        return new AssessmentOutcomeDeclarationDto(
                identifier: randomId(),
                cardinality: randomCardinality(),
                baseType: randomBaseType(),
                defaultValue: new AssessmentDefaultValueDto(),
                normalMaximum: 3,
                normalMinimum: 1
        )
    }

    AssessmentResponseProcessingDto randomResponseProcessing() {
        return new AssessmentResponseProcessingDto(
                template: randomTemplate()
        )
    }

    AssessmentCardinality randomCardinality() {
        return randomMember(AssessmentCardinality.values().toList())
    }

    AssessmentBaseType randomBaseType() {
        return randomMember(AssessmentBaseType.values().toList())
    }

    AssessmentCorrectResponseDto randomCorrectResponse() {
        return new AssessmentCorrectResponseDto(
                description: randomString(),
                values: randomList(5) { randomString() }
        )
    }

    AssessmentItemResponseMappingDto randomMappingResponse() {
        return new AssessmentItemResponseMappingDto(
                mapping: randomMap(1, 5 ){ randomString() } { randomDouble(-5, 7) },
                lowerBound: randomDouble(0.0, 3.0),
                upperBound: randomDouble(3.0, 8.0),
                defaultValue: randomDouble(0.0, 1.0)
        )
    }

    String randomTemplate() {
        if(randomBoolean())
            return "map_response";
        else if(randomBoolean())
            return "match_correct";
        return "";
    }

    def minMax() {
        //returns two values between 1 and 5, one smaller than the other, either of which (but not both) may be null
        def min = null
        def max = null
        if (randomBoolean()) {
            min = randomInt(1,5)
        }
        if (randomBoolean()) {
            max = randomInt(1,5)
        }
        if (min != null && max != null && min > max) {
            min = max
        }
        if (min == null && max == null) {
            if (randomBoolean()) {
                min = randomInt(1,5)
            } else {
                max = randomInt(1,5)
            }
        }
        return [min,max]
    }


    AssessmentChoiceInteractionDto randomChoiceInteraction() {
        def minChoices = randomInt(1,2)
        new AssessmentChoiceInteractionDto(
                id: randomId(),
                type: CHOICE_INTERACTION,
                uiid: randomId(),
                responseIdentifier: randomId(),
                choices: (1..5).collect { new AssessmentSimpleChoiceDto(identifier: randomId(), content: randomString()) },
                minChoices: minChoices,
                maxChoices: minChoices+2,
                prompt: randomString()
        )
    }

    List<String> validChoiceInteractionResponse(AssessmentChoiceInteractionDto interaction) {
        int min = interaction.minChoices ?: 1
        int max = interaction.maxChoices ?: interaction.choices.size()
        int n = randomInt(min,max)
        randomSublist(n, interaction.choices).collect { it.getIdentifier() }
    }

    List<String> invalidChoiceInteractionResponse(AssessmentChoiceInteractionDto interaction) {
        if (interaction.minChoices?:1 > 1 && randomBoolean()) {
            //generate not enough choices
            return randomSublist(interaction.minChoices-1, interaction.choices).collect { it.getIdentifier() }
        } else if (interaction.maxChoices?:interaction.choices.size() < interaction.choices.size() && randomBoolean()) {
            //generate too many choices
            return randomSublist(interaction.maxChoices+1, interaction.choices).collect { it.getIdentifier() }
        } else {
            //return the correct amount of nonsense
            int n = minChoices ?: (maxChoices ?: 1)
            return (1..n).collect { randomId() }
        }
    }

    AssessmentExtendedTextInteractionDto randomExtendedTextInteraction() {
        def (minStrings, maxStrings) = minMax()
        //make sure we an generate invalid interactions
        if (minStrings == 1 && maxStrings == null) {
            minStrings = 2
        }
        new AssessmentExtendedTextInteractionDto(
                id: randomId(),
                type: EXTENDED_TEXT_INTERACTION,
                uiid: randomId(),
                responseIdentifier: randomId(),
                minStrings: minStrings,
                maxStrings: maxStrings
        )
    }

    String randomTextEntryResponse(int numWords) {
        if (numWords <= 0) {
            return " "
        } else {
            def response = (1..numWords).collect { randomWord() }
            return response.join(" ")
        }
    }

    List<String> validExtendedTextInteractionResponse(AssessmentExtendedTextInteractionDto interaction) {
        int min = interaction.minStrings ?: 1
        int max = interaction.maxStrings ?: (interaction.minStrings ?: 1)
        return [ randomTextEntryResponse(randomInt(min,max)) ]
    }

    List<String> invalidExtendedTextInteractionResponse(AssessmentExtendedTextInteractionDto interaction) {
        def n = 0
        if (interaction.minStrings != null && interaction.maxStrings != null) {
            n = (randomBoolean() ? interaction.minStrings-1 : interaction.maxStrings+1)
        } else {
            n = (interaction.minStrings == null ? interaction.maxStrings+1 : interaction.minStrings - 1)
        }
        return [ randomTextEntryResponse(n) ]
    }

    AssessmentInlineChoiceInteractionDto randomInlineChoiceInteraction() {
        new AssessmentInlineChoiceInteractionDto(
                id: randomId(),
                type: INLINE_CHOICE_INTERACTION,
                uiid: randomId(),
                responseIdentifier: randomId(),
                prompt: randomString(),
                inlineChoices: (1..5).collect { new AssessmentInlineChoiceDto(identifier: randomId(), content: randomString()) }
        )
    }

    List<String> validInlineChoiceInteractionResponse(AssessmentInlineChoiceInteractionDto interaction) {
        return [ randomString() ]
    }

    List<String> invalidInlineChoiceInteractionResponse(AssessmentInlineChoiceInteractionDto interaction) {
        //This isn't really invalid, there seems to be no invalid Inline Choice Interaction response
        return [ "" ]
    }


    AssessmentMatchInteractionDto randomMatchInteraction() {
        def totalMin = 0
        def totalMax = 0
        def  matchSets = randomListSetSize(5) {
            new AssessmentSimpleMatchSetDto(
                    matchSet: randomList(5) {
                        def (matchMin, matchMax) = minMax()
                        totalMin += matchMin?:0
                        totalMax += matchMax?:0
                        new AssessmentSimpleAssociableChoiceDto(
                                matchMax: matchMax,
                                matchMin: matchMin,
                                identifier: randomId(),
                                content: randomString()
                        )
                    }
            )
        }
        def (minAssociations, maxAssociations) = minMax()
        //Make sure the constraints are achievable
        if (minAssociations?:totalMin > totalMax) {
            minAssociations = totalMin
        }
        if (maxAssociations?:totalMax < totalMin) {
            maxAssociations = totalMax
        }
        new AssessmentMatchInteractionDto(
                id: randomId(),
                type: MATCH_INTERACTION,
                uiid: randomId(),
                responseIdentifier: randomId(),
                shuffle: randomBoolean(),
                prompt: randomString(),
                minAssociations: minAssociations,
                maxAssociations: maxAssociations,
                matchSets: matchSets
        )
    }


    List<String> validMatchInteractionResponse(AssessmentMatchInteractionDto interaction) {
        null //not using match interations, yet
    }

    List<String> invalidMatchInteractionResponse(AssessmentMatchInteractionDto interaction) {
        null //not using match interactions, yet
    }


    AssessmentTextEntryInteractionDto randomTextEntryInteraction() {
        new AssessmentTextEntryInteractionDto(
                id: randomId(),
                type: TEXT_ENTRY_INTERACTION,
                uiid: randomId(),
                responseIdentifier: randomId(),
                patternMask: '[a-z]+@[A-Z]+'
        )
    }

    List<String> validTextEntryInteractionResponse(AssessmentTextEntryInteractionDto interaction) {
        return [ randomWord() + '@' + randomWord().toUpperCase() ]
    }

    List<String> invalidTextEntryInteractionResponse(AssessmentTextEntryInteractionDto) {
        return [ randomWord() ]
    }


    AssessmentInteractionType randomInteractionType() {
        //Match interaction excluded for now, Inline interactions excluded because they can't be tested for validity
        randomMember(AssessmentInteractionType.values().toList().minus([NULL_INTERACTION, MATCH_INTERACTION, INLINE_CHOICE_INTERACTION]))
    }

    AssessmentInteractionDto randomInteractionOfType(AssessmentInteractionType type) {
        switch (type) {
            case CHOICE_INTERACTION:
                return randomChoiceInteraction()
            case EXTENDED_TEXT_INTERACTION:
                return randomExtendedTextInteraction()
            case INLINE_CHOICE_INTERACTION:
                return randomInlineChoiceInteraction()
            case MATCH_INTERACTION:
                return randomMatchInteraction()
            case TEXT_ENTRY_INTERACTION:
                return randomTextEntryInteraction()
            default:
                return new AssessmentInteractionDto(
                        id: randomId(),
                        type: type,
                        uiid: randomId(),
                        responseIdentifier: randomId()
                )
        }
    }

    AssessmentInteractionDto randomInteraction() {
        randomInteractionOfType(randomInteractionType())
    }

    List<String> randomResponseValues(AssessmentInteractionDto interaction) {
        switch (interaction.getType()) {
            case CHOICE_INTERACTION:
                return validChoiceInteractionResponse(interaction)
            case EXTENDED_TEXT_INTERACTION:
                return validExtendedTextInteractionResponse(interaction)
            case INLINE_CHOICE_INTERACTION:
                return validInlineChoiceInteractionResponse(interaction)
            case MATCH_INTERACTION:
                return validMatchInteractionResponse(interaction)
            case TEXT_ENTRY_INTERACTION:
                return validTextEntryInteractionResponse(interaction)
            default:
                throw new Exception("Unrecognized interaction type " + interaction.getType())
        }
    }

    List<String> randomInvalidResponseValues(AssessmentInteractionDto interaction) {
        switch (interaction.getType()) {
            case CHOICE_INTERACTION:
                return invalidChoiceInteractionResponse(interaction)
            case EXTENDED_TEXT_INTERACTION:
                return invalidExtendedTextInteractionResponse(interaction)
            case INLINE_CHOICE_INTERACTION:
                return invalidInlineChoiceInteractionResponse(interaction)
            case MATCH_INTERACTION:
                return invalidMatchInteractionResponse(interaction)
            case TEXT_ENTRY_INTERACTION:
                return invalidTextEntryInteractionResponse(interaction)
            default:
                throw new Exception("Unrecognized interaction type " + interaction.getType())
        }
    }


    AssessmentMetadataDto randomAssessmentMetadata(sectionMetadata) {
        AssessmentMetadataDto assessmentMetadataDto = randomNotNullAssessmentMetadata()
        assessmentMetadataDto.section = sectionMetadata

        return randomBoolean() ? null : assessmentMetadataDto
    }

    AssessmentMetadataDto randomNotNullAssessmentMetadata() {
        return new AssessmentMetadataDto(
                type: MetadataType.ASSESSMENTMETADATA,
                identifier: randomString(),
                authoringTool: randomString(),
                authoringToolVersion: randomString(),
                author: randomString(),
                displayInHistory: randomString(),
                displayGeneralInstructions: randomString(),
                displayGeneralClosing: randomString(),
                autoActivate: randomString(),
                requirePasscode: randomYesNoTrueFalse(),
                preRequisite: randomPrerequisiteMetadataDto(),
                section: randomSectionMetadatas(),
                instructions: randomString(),
                deliveryType: randomDeliveryTypeMetadata(),
                scaleAdditiveTerm: randomDouble(1, 10),
                scaleMultiplicativeTerm: randomDouble (1, 10),
                competencyMapDisciplines: Arrays.asList(randomString())
        )
    }

    DeliveryTypeMetadataDto randomDeliveryTypeMetadata() {
        return new DeliveryTypeMetadataDto(
                online: randomYesNoTrueFalse(),
                paper: randomYesNoTrueFalse()
        )
    }

    /**
     * 0 no branch rules tested
     * 1 branches from parent section to parent section
     * 2 branches from parent section to child section
     * Note branching from a child section is currently not supported.
     * @param sectionDtos
     * @param fromSectionId The Branch Rule exists in the this section
     * @param testingStrategy 0,1,2 see above
     * @return
     */
    List<AssessmentBranchRuleDto> notRandomBranchRuleDtos(List<AssessmentSectionDto> sectionDtos, String fromSectionId, int testingStrategy){
        def branchRuleDtos = new ArrayList()

        //only one branch rule per section
        if(CollectionUtils.isNotEmpty(sectionDtos)) {
            if(testingStrategy == 1 || testingStrategy == 4) {
                branchRuleDtos << notRandomBranchRuleDto(sectionDtos[sectionDtos.size() - 2].id, fromSectionId)
            }else{
                def nestedSectionsectionDtos = sectionDtos[sectionDtos.size()-2].assessmentSections
                def targetId = randomString()
                if(!nestedSectionsectionDtos.empty)
                    targetId = nestedSectionsectionDtos[nestedSectionsectionDtos.size()-1].id
                branchRuleDtos << notRandomBranchRuleDto(targetId, fromSectionId)
            }
        }

        return branchRuleDtos
    }

    AssessmentBranchRuleDto notRandomBranchRuleDto(String sectionId, String fromSectionId) {
        return new AssessmentBranchRuleDto(
                target: sectionId,
                assessmentParentExpression: notRandomParentExpression(fromSectionId)
        )
    }

    AssessmentParentExpressionDto notRandomParentExpression(String sectionId) {
        return new AssessmentParentExpressionDto(
                expressionType: expectedParentExpressionType(),
                assessmentParentExpressionDtoList: null,
                assessmentChildExpressionDtoList: singleChildExpression(sectionId, false)
        )
    }

    List<AssessmentChildExpressionDto> singleChildExpression(String sectionId, boolean createFalsePrecondition){
        return randomListSetSize(1, { notRandomChildExpression(sectionId, createFalsePrecondition) })
    }

    /**
     *  During the tests that use this all scores will be 1.0 however,
     *  inorder to prove that branching logic is working we only need to have an expression return true.
     *
     * @param parentSectionId is second to last parent section for 1, or last child section in second to last parent section for 2
     * @return
     */
    AssessmentChildExpressionDto notRandomChildExpression(String parentSectionId, boolean createFalsePrecondition) {
        return new AssessmentChildExpressionDto(
                expressionType: createFalsePrecondition ? ExpressionType.LT : ExpressionType.GT,
                baseValue: -1,
                variable: "${parentSectionId}.SCORE"
        )
    }

    AssessmentBranchRuleDto randomAssessmentBranchRuleDto() {
        return new AssessmentBranchRuleDto(
                target: randomString(),
                assessmentParentExpression: randomAssessmentParentExpressionDto(randomInt(1,5))
        )
    }

    AssessmentParentExpressionDto randomAssessmentParentExpressionDto(int numberOfNested) {
        return new AssessmentParentExpressionDto(
                expressionType: expectedParentExpressionType(),
                assessmentParentExpressionDtoList: (numberOfNested <= 0) ? null : randomParentExpressions(numberOfNested),
                assessmentChildExpressionDtoList: randomChildExpressions()
        )
    }

    AssessmentChildExpressionDto randomAssessmentChildExpressionDto() {
        return new AssessmentChildExpressionDto(
                expressionType: expectedChildExpressionType(),
                baseValue: randomFloat(1, 1000),
                variable: randomString()
        )
    }

    List<AssessmentBranchRuleDto> randomBranchRules() {
        return  randomList(5) { randomAssessmentBranchRuleDto() }
    }

    List<AssessmentParentExpressionDto> randomParentExpressions(int numberOfNested){
        return randomList(numberOfNested) { randomAssessmentParentExpressionDto(--numberOfNested) }
    }

    List<AssessmentChildExpressionDto> randomChildExpressions() {
        return randomList(10) { randomAssessmentChildExpressionDto() }
    }

    ExpressionType expectedParentExpressionType() {
        def parentExpressions = expectedParentExpressionTypes()
        return parentExpressions[rand.nextInt(parentExpressions.size())]
    }

    ExpressionType[] expectedParentExpressionTypes(){
        return [ExpressionType.AND, ExpressionType.OR]
    }

    ExpressionType expectedChildExpressionType() {
        def childExpressions = expectedChildExpressionTypes()
        return childExpressions[rand.nextInt(childExpressions.size())]
    }

    ExpressionType[] expectedChildExpressionTypes() {
        return [ExpressionType.GT, ExpressionType.GTE, ExpressionType.EQUAL, ExpressionType.LTE, ExpressionType.LT]
    }

    PrerequisiteMetadataDto randomPrerequisiteMetadataDto() {
        return randomBoolean() ? null :
                new PrerequisiteMetadataDto(
                        assessmentIdRef: randomString()
                )
    }

    String randomYesNoTrueFalse(){
        def values = ["Yes", "No", "True", "False", null, randomString()]
        return values[rand.nextInt(values.size())]
    }

    List<SectionMetadataDto> randomSectionMetadatas(){
        return randomList(5) { randomSectionMetadata(randomString()) }
    }

    List<SectionMetadataDto> randomSectionMetadataForAssessment(AssessmentDto assessment) {
        assessment.descendants.collect(Collectors.toSet()).
                findAll {
                    it instanceof AssessmentSectionDto && it.assessmentSections.empty
                }. collect { randomSectionMetadata(it.id) }
    }

    SectionMetadataDto randomSectionMetadata(String sectionId){
        //rigged to get a lot of item bundles for testing
        def metadaType = randomBoolean() ? MetadataType.ITEMBUNDLE : randomMetadataType()
        return new SectionMetadataDto(
                identifier: sectionId,
                type: metadaType
        )
    }
    ExpressionType randomExpressionType(){
        randomMember(ExpressionType.class.enumConstants)
    }

    MetadataType randomMetadataType(){
        randomMember(MetadataType.class.enumConstants)
    }
}
