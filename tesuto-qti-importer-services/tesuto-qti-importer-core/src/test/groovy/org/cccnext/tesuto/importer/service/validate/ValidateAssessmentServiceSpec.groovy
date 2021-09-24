package org.cccnext.tesuto.content.dto

import org.apache.commons.collections.CollectionUtils
import org.cccnext.tesuto.content.dto.AssessmentDtoGenerator
import org.cccnext.tesuto.content.dto.expression.AssessmentBranchRuleDto
import org.cccnext.tesuto.content.dto.expression.AssessmentChildExpressionDto
import org.cccnext.tesuto.content.dto.section.AssessmentItemRefDto
import org.cccnext.tesuto.content.dto.expression.AssessmentParentExpressionDto
import org.cccnext.tesuto.content.dto.expression.AssessmentPreConditionDto
import org.cccnext.tesuto.content.dto.section.AssessmentOrderingDto
import org.cccnext.tesuto.content.dto.section.AssessmentSectionDto
import org.cccnext.tesuto.content.dto.section.AssessmentSelectionDto
import org.cccnext.tesuto.service.importer.validate.ValidationMessage
import org.cccnext.tesuto.service.importer.validate.ValidateAssessmentService
import org.cccnext.tesuto.util.ValidationUtil
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification
import uk.ac.ed.ph.jqtiplus.node.expression.ExpressionType

import java.util.concurrent.ConcurrentHashMap

/**
 * Created by jasonbrown on 3/1/16.
 */
public class ValidateAssessmentServiceSpec extends Specification{


    @Shared ValidateAssessmentService validateAssessmentService
    @Shared ValidationUtil validationUtil
    @Shared AssessmentDtoGenerator generator
    @Shared def tab

    int count = 0

    def setupSpec() {
        ApplicationContext context = new ClassPathXmlApplicationContext("/commonImportContext.xml")
        generator = new AssessmentDtoGenerator()
        validateAssessmentService = context.getBean("validateAssessmentService")
        validationUtil = context.getBean("validationUtil")
        validationUtil.useFullBranchRuleEvaluation = false
        tab = ''
    }

    def "given a section has shuffle enabled, a paper assessment will return an expected validation error"() {
        when:
        assessment.assessmentMetadata = generator.randomNotNullAssessmentMetadata()
        assessment.assessmentMetadata.deliveryType.paper = "YES"
        def map = createShuffle(assessment)
        def sectionId = map.sectionId
        def node = "ordering"
        def expectedValidationError = createValidationError("A paper assessment cannot have shuffle set to true in section id ${sectionId}", node)
        def list = validateAssessmentService.processAssessments(createAssessmentList(map.assessmentDto), createGarbageHashMap())

        then:
        list.contains(expectedValidationError)

        where:
        assessment << (1..10).collect({generator.randomAssessment(3)})
    }

    def "given a section that has a selection, a paper assessment will return an expected validation error"() {
        when:
        assessment.assessmentMetadata = generator.randomNotNullAssessmentMetadata()
        assessment.assessmentMetadata.deliveryType.paper = "YES"
        def map = createSelection(assessment)
        def sectionId = map.sectionId
        def node = "selection"
        def expectedValidationError = createValidationError("A paper assessment cannot have a selection in section id ${sectionId}", node)
        def list = validateAssessmentService.processAssessments(createAssessmentList(map.assessmentDto), createGarbageHashMap())

        then:
        list.contains(expectedValidationError)

        where:
        assessment << (1..10).collect({generator.randomAssessment(3)})
    }

    def "given an a section that has no branchrules or preconditions a paper assessment will return empty list of errors"() {
        when:
        def expectedList = []
        assessment.assessmentMetadata = generator.randomNotNullAssessmentMetadata()
        assessment.assessmentMetadata.deliveryType.paper = "YES"
        def assessments = createAssessmentList(assessment)


        then:
        expectedList == validateAssessmentService.processAssessments(assessments, createGarbageHashMap())

        where:
        assessment << (1..5).collect({generator.randomAssessment(3)})
    }

    def "given an a section that has a branchrule or a precondition a paper assessment will return expected validation error"() {
        when:
        assessment.assessmentMetadata = generator.randomNotNullAssessmentMetadata()
        assessment.assessmentMetadata.deliveryType.paper = "YES"
        def map = createBranchRuleOrPrecondition(assessment)
        def sectionId = map.sectionId
        def node = map.isBranchRule ? "branchRule" : "precondition"
        def nodeMessage =  map.isBranchRule ? "branch rules" : "preconditions"
        def expectedValidationError = createValidationError("A paper assessment cannot have $nodeMessage in section id ${sectionId}", node)
        def list = validateAssessmentService.processAssessments(createAssessmentList(map.assessmentDto), createGarbageHashMap())

        then:
        list.contains(expectedValidationError)

        where:
        assessment << (1..10).collect({generator.randomAssessment(3)})
    }

    def createShuffle(AssessmentDto assessmentDto) {
        def nestedSection = generator.rand.nextInt(3)
        def id = setInvalidShuffleCondition(nestedSection, assessmentDto.assessmentParts[0].assessmentSections)
        return [assessmentDto: assessmentDto, sectionId: id]
    }

    def createSelection(AssessmentDto assessmentDto) {
        def nestedSection = generator.rand.nextInt(3)
        def id = setInvalidSelectionCondition(nestedSection, assessmentDto.assessmentParts[0].assessmentSections)
        return [assessmentDto: assessmentDto, sectionId: id]
    }

    def createBranchRuleOrPrecondition(AssessmentDto assessmentDto){
        def createBranchRule = generator.randomBoolean()
        def nestedSection = generator.rand.nextInt(3) //created a randomAssessment with 3 sections

        def id = setInvalidCondition(nestedSection, assessmentDto.assessmentParts[0].assessmentSections, createBranchRule)
        return [assessmentDto: assessmentDto, sectionId: id, isBranchRule:createBranchRule]
    }

    def setInvalidShuffleCondition(int count, List<AssessmentSectionDto> sectionDtos) {
        if (count != 0) {
            setInvalidShuffleCondition(count - 1, sectionDtos.get(0).assessmentSections)
        }
        def sectionSize = sectionDtos.size()
        def randomSection = generator.rand.nextInt(sectionSize)
        AssessmentOrderingDto orderingDto = new AssessmentOrderingDto()
        orderingDto.setShuffle(true)
        sectionDtos.get(randomSection).ordering = orderingDto

        return sectionDtos.get(randomSection).id
    }

    def setInvalidSelectionCondition(int count, List<AssessmentSectionDto> sectionDtos) {
        if (count != 0) {
            setInvalidSelectionCondition(count - 1, sectionDtos.get(0).assessmentSections)
        }
        def sectionSize = sectionDtos.size()
        def randomSection = generator.rand.nextInt(sectionSize)
        sectionDtos.get(randomSection).selection = new AssessmentSelectionDto()

        return sectionDtos.get(randomSection).id
    }

    def setInvalidCondition(int count, List<AssessmentSectionDto> sectionDtos, boolean createBranchRule){
        if(count != 0){
            setInvalidCondition((count-1), sectionDtos.get(0).assessmentSections, createBranchRule)
        }
        def sectionSize = sectionDtos.size()
        def randomSection = generator.rand.nextInt(sectionSize)

        if(createBranchRule) {
            sectionDtos.get(randomSection).branchRules = createBranchRuleList()
        }else{
            sectionDtos.get(randomSection).preConditions = createPreConditionList()
        }

        return sectionDtos.get(randomSection).id
    }

    List<AssessmentPreConditionDto> createPreConditionList(){
        return [new AssessmentPreConditionDto()]
    }

    List<AssessmentBranchRuleDto> createBranchRuleList() {
        return [new AssessmentBranchRuleDto()]
    }

    List<AssessmentDto> createAssessmentList(AssessmentDto assessmentDto){
        List<AssessmentDto> assessmentDtos = new ArrayList<>(1)
        assessmentDtos << assessmentDto
        return assessmentDtos
    }

    def "given a list of assessments with branch cycles processAssessments will return list of errors"() {
        when:
        LinkedHashMap<String, List<String>> map  = createMapForExpectedBranchCycle(3, true)
        List<String> itemRefs = (1..5).collect({generator.randomString()})
        def sections = createSectionFromMap(map, false, itemRefs)
        def assessments = createAssessments(sections)

        def list = validateAssessmentService.processAssessments(assessments, createGarbageHashMap())

        then:
        list.size() > 0

        where:
        i << (1..5)
    }

    def "given a list of assessments with NO branch cycles processAssessments will return empty list"() {
        when:
        def expectedList = []
        LinkedHashMap<String, List<String>> map  = createMapForExpectedBranchCycle(3, false)
        List<String> itemRefs = (1..5).collect({generator.randomString()})
        def sections = createSectionFromMap(map, false, itemRefs)
        def assessments = createAssessments(sections)

        then:
        expectedList == validateAssessmentService.processAssessments(assessments, createGarbageHashMapFromList(itemRefs))

        where:
        i << (1..5)
    }

    def "given a section with with a list of multiple items will add all possible scores and return a sorted map"() {
        when:
        validationUtil.useFullBranchRuleEvaluation = true
        List<String> itemRefs = (1..5).collect({generator.randomString()})
        AssessmentSectionDto section = createSectionWithItemRefs(itemRefs)
        HashMap<String, SortedSet<Double>> setMap = createMapOfItemRefToScores(itemRefs)
        ConcurrentHashMap<String, List<AssessmentBranchRuleDto>> branchRulesMap = new ConcurrentHashMap<>()
        List<Double> doubleList = Arrays.asList(0.0d, 1.0d, 2.0d, 3.0d, 4.0d, 5.0d, 6.0d, 7.0d, 8.0d, 9.0d, 10.0d)
        SortedSet<Double> expectedSet = new TreeSet<Double>(doubleList)

        boolean branchRuleCanBeEvaluated = true
        branchRulesMap.put(section.getId(), randomBranchRules("EXIT_TEST", section.getId(), branchRuleCanBeEvaluated))

        then:
        try {
            validateAssessmentService.verifyBranchRuleOutcomesArePossible(section, setMap, branchRulesMap) == expectedSet
        }
        catch(Exception e){
            e.printStackTrace()
        }
        where:
        i << (1..5)
    }

    def "given a section with with a list of multiple items will add all possible scores and return map of size 2 with only maximum and minimum"() {
        when:
        validationUtil.useFullBranchRuleEvaluation = false
        List<String> itemRefs = (1..5).collect({generator.randomString()})
        AssessmentSectionDto section = createSectionWithItemRefs(itemRefs)
        HashMap<String, SortedSet<Double>> setMap = createMapOfItemRefToScores(itemRefs)
        ConcurrentHashMap<String, List<AssessmentBranchRuleDto>> branchRulesMap = new ConcurrentHashMap<>()
        List<Double> doubleList = Arrays.asList(0.0d, 10.0d)
        SortedSet<Double> expectedSet = new TreeSet<Double>(doubleList)

        boolean branchRuleCanBeEvaluated = true
        branchRulesMap.put(section.getId(), randomBranchRules("EXIT_TEST", section.getId(), branchRuleCanBeEvaluated))

        then:
        validateAssessmentService.verifyBranchRuleOutcomesArePossible(section, setMap, branchRulesMap).getValue() == expectedSet

        where:
        i << (1..5)
    }

    def "given map of branch rules that cannot be evaluated will return list of errors"() {
        when:
        List<String> itemRefs = (1..5).collect({generator.randomString()})
        AssessmentSectionDto section = createSectionWithItemRefs(itemRefs)
        HashMap<String, SortedSet<Double>> setMap = createMapOfItemRefToScores(itemRefs)
        ConcurrentHashMap<String, List<AssessmentBranchRuleDto>> branchRulesMap = new ConcurrentHashMap<>()

        boolean branchRuleCANNOTBeEvaluated = false
        branchRulesMap.put(section.getId(), randomBranchRules("EXIT_TEST", section.getId(), branchRuleCANNOTBeEvaluated))

        def expectedErrorMessage = "The branch score for a section " + section.getId() +
                " can not achieve the score which is needed for the target EXIT_TEST\n" +
                "Possible Scores for this section: [0.0, 10.0]"
        def expectedError = createValidationError(expectedErrorMessage, "branchRule")
        def list = validateAssessmentService.verifyBranchRuleOutcomesArePossible(section, setMap, branchRulesMap).getErrors()

        then:
        list.contains(expectedError)

        where:
        i << (1..5)
    }

    def "given a section that has a selection with map of branch rules that can be evaluated will return empty list of errors"() {
        when:
        def expectedList = []
        List<String> itemRefs = (1..5).collect({generator.randomString()})
        AssessmentSectionDto section = createSectionWithItemRefs(itemRefs)
        section.selection = createSelectionDto()
        HashMap<String, SortedSet<Double>> setMap = createMapOfItemRefToScores(itemRefs)
        ConcurrentHashMap<String, List<AssessmentBranchRuleDto>> branchRulesMap = new ConcurrentHashMap<>()

        boolean branchRuleCANNOTBeEvaluated = false
        branchRulesMap.put(section.getId(), randomBranchRules("EXIT_TEST", section.getId(), branchRuleCANNOTBeEvaluated))


        then:
        validateAssessmentService.verifyBranchRuleOutcomesArePossible(section, setMap, branchRulesMap).getErrors() == expectedList

        where:
        i << (1..5)
    }


    /**
     *  Property Generation
     */

    def createValidationError(def message, def node){
        new ValidationMessage(
                fileType: ValidationMessage.FileType.ASSESSMENT,
                message: message,
                node: node,
        )
    }

    AssessmentSelectionDto createSelectionDto(){
        new AssessmentSelectionDto(
                select: 1
        )
    }

    HashMap<String, SortedSet<Double>> createMapOfItemRefToScores(List<String> itemRefs){
        HashMap<String, SortedSet<Double>> setMap = new HashMap<>()
        itemRefs.each{
            List<Double> doubleList = Arrays.asList(0.0d, 1.0d, 2.0d)
            SortedSet<Double> sortedSet = new TreeSet<Double>(doubleList)
            setMap.put(it, sortedSet)
        }
        return setMap
    }

    List<AssessmentItemRefDto> createItemRefDtoList(List<String> identifiers){
        List<AssessmentItemRefDto> assessmentItemRefDtos = new ArrayList<>();
        identifiers.each{
            assessmentItemRefDtos.add(createItemRefDto(it))
        }
        return assessmentItemRefDtos
    }

    AssessmentItemRefDto createItemRefDto(String identifer){
        return new AssessmentItemRefDto(
                itemIdentifier: identifer
        )
    }

    List<AssessmentSectionDto> createSectionListWithItemRefs(List<String> itemRefs){
        List<AssessmentSectionDto> sectionDtos = new ArrayList<>()
        sectionDtos.add(createSectionWithItemRefs(itemRefs))
        return sectionDtos
    }

    AssessmentSectionDto createSectionWithItemRefs(List<String> itemRefs){
        return new AssessmentSectionDto(
                id: "my section",
                assessmentItemRefs: createItemRefDtoList(itemRefs)
        )
    }

    List<AssessmentBranchRuleDto> randomBranchRules(String targetSectionId, String fromSectionId, boolean willEvaluateTrue) {
        List<AssessmentBranchRuleDto> branchRuleDtos = new ArrayList<>()
        branchRuleDtos.add(notRandomBranchRuleDto(targetSectionId, fromSectionId, willEvaluateTrue))
        return branchRuleDtos
    }

    AssessmentBranchRuleDto notRandomBranchRuleDto(String targetSectionId, String fromSectionId, boolean willEvaluateTrue) {
        return new AssessmentBranchRuleDto(
                target: targetSectionId,
                assessmentParentExpression: notRandomParentExpression(fromSectionId, willEvaluateTrue)
        )
    }

    AssessmentParentExpressionDto notRandomParentExpression(String sectionId, boolean willEvaluateTrue) {
        return new AssessmentParentExpressionDto(
                expressionType: null,
                assessmentParentExpressionDtoList: null,
                assessmentChildExpressionDtoList: singleChildExpression(sectionId, willEvaluateTrue)
        )
    }

    List<AssessmentChildExpressionDto> singleChildExpression(String sectionId, boolean willEvaluateTrue){
        List<AssessmentChildExpressionDto> childExpressionDtos = new ArrayList<>(1)
        childExpressionDtos.add(notRandomChildExpression(sectionId, willEvaluateTrue))
        return childExpressionDtos
    }
    /**
     * Test will be set up with possible outcomes of 0, 1, 2
     * willEvaluateTrue : will ensure the possible out comes either are reachable or not reachable
     */
    AssessmentChildExpressionDto notRandomChildExpression(String parentSectionId, boolean willEvaluateTrue) {
        ExpressionType type = generator.expectedChildExpressionType()
        def value = 0d
        if(willEvaluateTrue){
            switch(type){
                case ExpressionType.GT:
                case ExpressionType.GTE:
                    value = -10000d
                    break
                case ExpressionType.EQUAL:
                    value = 0d
                    break
                case ExpressionType.LTE:
                case ExpressionType.LT:
                    value = 10000d
                    break
                default:
                    println "true failed to evalute expresion type $type"
            }
        }else{
            switch(type){
                case ExpressionType.GT:
                case ExpressionType.GTE:
                case ExpressionType.EQUAL:
                    value = 10000d
                    break
                case ExpressionType.LTE:
                case ExpressionType.LT:
                    value = -10000d
                    break
                default:
                    println "false failed to evalute expresion type $type"
            }
        }

        AssessmentChildExpressionDto childExpressionDto = new AssessmentChildExpressionDto(
                variable: parentSectionId+".SCORE"
        )
        childExpressionDto.expressionType = type
        childExpressionDto.baseValue = value

        return childExpressionDto
    }

    // Leaving in to help with debugging
    String getCount(){
        String retVal = count.toString()
        count++
        return retVal
    }

    //Garbage
    HashMap<String, SortedSet<Double>> createGarbageHashMap(){
        List<String> itemRefs = (1..5).collect({generator.randomString()})
        return createGarbageHashMapFromList(itemRefs)
    }

    HashMap<String, SortedSet<Double>> createGarbageHashMapFromList(List<String> itemRefs){
        return createMapOfItemRefToScores(itemRefs)
    }

    public Set<String> getDuplicateSectionIds(List<AssessmentSectionDto> sectionDtos){
        def map = getDuplicateSectionIdMap(sectionDtos)
        Set<String> set = []

        map.each { k, v ->
            if (v > 1) {
                set.add(k)
            }
        }

        return set
    }

    public HashMap<String, Integer> getDuplicateSectionIdMap(List<AssessmentSectionDto> sectionDtos){
        HashMap<String, Integer> sectionCountMap = []
        sectionDtos?.forEach({
            section ->
                if(sectionCountMap[section.id] == null){
                    sectionCountMap[section.id] = 1
                }else{
                    sectionCountMap[section.id]++
                }
                if(CollectionUtils.isNotEmpty(section.assessmentSections)){
                    sectionCountMap.putAll(getDuplicateSectionIds(section.assessmentSections))
                }
        })
        return sectionCountMap
    }

    // Leaving in to help with debugging
    public boolean printSections(List<AssessmentSectionDto> sectionDtos){
        sectionDtos?.forEach({
            section -> println "${tab}section id: $section.id"
                printBranchRules(section.branchRules)
                if(CollectionUtils.isNotEmpty(section.assessmentSections)){
                    println "${tab}with subsections"
                    tab += '    '
                    printSections(section.assessmentSections)
                    tab = tab.subString(0, (tab.size()-5))
                }
        })
        return false
    }

    // Leaving in to help with debugging
    public void printBranchRules(List<AssessmentBranchRuleDto> branchRuleDtos){
        branchRuleDtos?.forEach({
            branchRule -> println "${tab}branch rule target $branchRule.target"
        })
    }

    public List<AssessmentSectionDto> createSectionFromMap(LinkedHashMap<String, List<String>> map, boolean expectedLinearCycle, List<String> itemRefs){
        boolean cycleHasOccured = false
        List<AssessmentSectionDto> sectionDtos = new ArrayList<>()
        def force = map.keySet().size() - 1
        int keys = 0
        for(String key: map.keySet() ){
            sectionDtos.add(createSection(key, map.get(key), itemRefs))
            String sectionId = generator.randomString()
            if(!cycleHasOccured && expectedLinearCycle && (generator.randomBoolean() || force == keys)){
                sectionId = generator.randomMember(map.keySet())
                cycleHasOccured = true
            }
            keys++
            sectionDtos.add(createSection(sectionId, null, itemRefs)) // Make sure branches past the next successive section
        }

        return sectionDtos
    }

    public List<AssessmentSectionDto> createSectionFromMapWithItemRefs(LinkedHashMap<String, List<String>> map){
        List<AssessmentSectionDto> sectionDtos = createSectionFromMap(map, false)

        return sectionDtos
    }

    AssessmentSectionDto createSection(String id, List<String> branchRuleTargetIds, List<String> itemRefs){

        return new AssessmentSectionDto(
                id: id,
                assessmentSections: null,
                branchRules: createListOfBranchRules(branchRuleTargetIds, id),
                assessmentItemRefs: createItemRefDtoList(itemRefs)
        )
    }


    List<String> createListOfTargetIds(){
        List<String> garbageList = new ArrayList<>()
        (1..generator.randomRangeInt(2, 4)).forEach({
            garbageList.add(generator.randomString())
        })
        return garbageList
    }

    List<AssessmentBranchRuleDto> createListOfBranchRules(List<String> targetIds, String fromSectionId){
        if(targetIds == null){
            return null
        }
        List<AssessmentBranchRuleDto> assessmentBranchRuleDtos = new ArrayList<>()
        targetIds.forEach({ targetId ->
            assessmentBranchRuleDtos.add(createBranchRule(targetId, fromSectionId))
        })
        return assessmentBranchRuleDtos
    }

    AssessmentBranchRuleDto createBranchRule(String id, fromSectionId) {
        return new AssessmentBranchRuleDto(
                target: id,
                assessmentParentExpression: notRandomParentExpression(fromSectionId, true)
        )
    }

    LinkedHashMap<String, List<String>> createMapForExpectedBranchCycle(int maxdepth, boolean expectedBranchCycle) {
        String key = generator.randomString()
        LinkedHashMap<String, List<String>> map = new HashMap<>()
        return createMapSectionIdsToListOfTargetsSections(map, maxdepth, key, false, expectedBranchCycle, key)
    }

    LinkedHashMap<String, List<String>> createMapSectionIdsToListOfTargetsSections(LinkedHashMap<String, List<String>> map,
                                                                                   int maxdepth,
                                                                                   String key,
                                                                                   boolean branchCycleHasOccurred,
                                                                                   boolean expectedBranchCycle, String cycleSectionId){
        if(maxdepth <= 0){
            return null
        }

        def cycleWillHappenInThisList = ((!branchCycleHasOccurred) && expectedBranchCycle && generator.randomBoolean())
        def forceCycle = ((!branchCycleHasOccurred) && expectedBranchCycle && (maxdepth == 2))

        List<String> targets = (maxdepth > 1) ? createListOfTargetIds() : null
        if(cycleWillHappenInThisList || forceCycle){
            targets.add(cycleSectionId)
            branchCycleHasOccurred = true
        }

        if(!map.containsKey(key)) {
            map.put(key, targets)
        }
        targets?.forEach({
            createMapSectionIdsToListOfTargetsSections(map, maxdepth - 1, it, branchCycleHasOccurred, expectedBranchCycle, cycleSectionId)
        })
        return map
    }

    DefaultDirectedGraph<String, DefaultEdge> emptyGraph(){
        return new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class)
    }

    List<AssessmentDto> createAssessments(List<AssessmentSectionDto> sectionDtos){
        AssessmentDto assessment = generator.randomAssessment(1)
        assessment.assessmentMetadata = null // ensure evaluated as online
        assessment.assessmentParts.get(0).assessmentSections = sectionDtos
        List<AssessmentDto> list = new ArrayList<>(1)
        list << assessment
        return list
    }
}