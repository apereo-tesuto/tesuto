package org.cccnext.tesuto.delivery.service.scoring

import org.cccnext.tesuto.delivery.model.internal.AssessmentSession
import org.cccnext.tesuto.delivery.model.internal.Outcome
import org.cccnext.tesuto.delivery.model.internal.enums.OutcomeDeclarationType
import org.cccnext.tesuto.content.dto.AssessmentBaseType
import org.cccnext.tesuto.content.dto.item.AssessmentDefaultValueDto
import org.cccnext.tesuto.content.dto.item.AssessmentItemDto
import org.cccnext.tesuto.content.dto.item.metadata.ItemMetadataDto
import org.cccnext.tesuto.content.dto.item.metadata.enums.ItemBankStatusType
import org.cccnext.tesuto.content.dto.shared.AssessmentOutcomeDeclarationDto
import org.cccnext.tesuto.content.service.AssessmentItemReader
import org.cccnext.tesuto.delivery.service.AssessmentSessionDao
import org.cccnext.tesuto.util.test.SessionGenerator
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification
import uk.ac.ed.ph.jqtiplus.value.BaseType

class OutcomeProcessingServiceSpec extends Specification {

    @Shared OutcomeProcessingServiceImpl outcomeProcessingService
    @Shared AssessmentItemReader itemService
    @Shared SessionGenerator generator
    @Shared AssessmentSessionDao assessmentSessionDao

    def setupSpec() {
        generator = new SessionGenerator()
        ApplicationContext context = new ClassPathXmlApplicationContext("/testApplicationContext.xml")
        outcomeProcessingService = context.getBean("outcomeProcessingService")
        itemService = context.getBean("assessmentItemReader")
        generator.categoryService = context.getBean("categoryService")
        assessmentSessionDao = context.getBean("assessmentSessionDao")
    }

    def "when an externalSessions outcome value is greater than the outcomeDeclarations normalMaximum will return outcome with normalMaximum"(){
        when:
        def outcomeIdentifier = generator.randomString()
        def normalMaximum = generator.randomDouble(-1, -10)
        def outcomeDeclaration = createOutcomeDeclaration(outcomeIdentifier, generator.randomDouble(), null, normalMaximum)

        def max = generator.randomDouble(11, 15)
        def randomDoubleList = createRandomListOfDoubles(3, max)
        def expectedOutcome = createOutcome(outcomeDeclaration, normalMaximum)
        def sessions = createSessions(outcomeDeclaration, randomDoubleList, generator.randomString(), generator.randomString())

        then:
        expectedOutcome == outcomeProcessingService.createExternalResourceOutcome(outcomeIdentifier, outcomeDeclaration, sessions)

        where:
        i << (1..10)
    }

    def "when an externalSessions outcome value is less than the outcomeDeclarations normalMinimum will return outcome with normalMinimum"(){
        when:
        def outcomeIdentifier = generator.randomString()
        def normalMinimum = generator.randomDouble(8, 10)
        def outcomeDeclaration = createOutcomeDeclaration(outcomeIdentifier, generator.randomDouble(), normalMinimum, null)

        def max = generator.randomDouble(8, 10)
        def randomDoubleList = createRandomListOfDoubles(3, max)
        def randomNegativeDoubleList = []
        randomDoubleList.each({randomNegativeDoubleList.add(it*-1)})
        def expectedOutcome = createOutcome(outcomeDeclaration, normalMinimum)
        def sessions = createSessions(outcomeDeclaration, randomNegativeDoubleList, generator.randomString(), generator.randomString())

        then:
        expectedOutcome == outcomeProcessingService.createExternalResourceOutcome(outcomeIdentifier, outcomeDeclaration, sessions)

        where:
        i << (1..10)
    }

    def "when an externalSessions have an an expected outcomeIdentifier will return expected outcome"() {
        when:
        def outcomeIdentifier = generator.randomString()
        def outcomeDeclaration = createOutcomeDeclaration(outcomeIdentifier, generator.randomDouble(), null, null)
        def max = generator.randomDouble(8, 10)
        def randomDoubleList = createRandomListOfDoubles(3, max)
        def expectedOutcome = createOutcome(outcomeDeclaration, max)
        def sessions = createSessions(outcomeDeclaration, randomDoubleList, generator.randomString(), generator.randomString())

        then:
        expectedOutcome == outcomeProcessingService.createExternalResourceOutcome(outcomeIdentifier, outcomeDeclaration, sessions)

        where:
        i << (1..10)
    }

    def "when externalSessions do not have an expected outcomeIdentifier or externalSessions is empty will return expected outcome"() {
        when:
        def outcomeIdentifier = generator.randomString()
        def defaultValue = generator.randomDouble()
        def outcomeDeclaration = createOutcomeDeclaration(outcomeIdentifier, defaultValue, null, null)
        def expectedOutcome = createOutcome(outcomeDeclaration, defaultValue)

        def randomOutcomeDeclaration = createOutcomeDeclaration(generator.randomString(), generator.randomDouble(), null, null)
        def sessions = generator.randomBoolean() ? null : createSessions(randomOutcomeDeclaration, createRandomListOfDoubles(3, 10), generator.randomString(), generator.randomString())

        then:
        expectedOutcome == outcomeProcessingService.createExternalResourceOutcome(outcomeIdentifier, outcomeDeclaration, sessions)

        where:
        i << (1..10)
    }

    def "when (external) outcomeDeclaration is as expected and user has previously taken assessment(s) with the outcomeIdentifier will return expected list of outcomes"(){
        when:
        def contentIdentifier = generator.randomWord()
        def outcomeIdentifier = generator.randomWord()
        def expectedValue = generator.randomDouble(10, 20)
        def outcomeDeclaration = createOutcomeDeclaration(("external." + contentIdentifier + "." + outcomeIdentifier), generator.randomDouble(), null, null)
        def session = createSessionWithOutcomeDeclarations(outcomeDeclaration)

        createSomeSessions(session.userId, outcomeIdentifier, contentIdentifier, expectedValue)

        def expectedOutcomes = [new Outcome(("external." + contentIdentifier + "." + outcomeIdentifier), expectedValue, outcomeDeclaration.baseType, OutcomeDeclarationType.IMPLICIT)]

        then:
        expectedOutcomes == outcomeProcessingService.processExternalResourceOutcomeDeclarations(session)

        where:
        i << (1..10)
    }

    def createSomeSessions(def userId, def outcomeIdentifier, def contentIdentifier, def expectedValue) {
        // random sessions
        def randomOutcomeDeclaration = createOutcomeDeclaration(generator.randomString(), generator.randomDouble(), null, null)
        def randomSessions = createSessions(randomOutcomeDeclaration, createRandomListOfDoubles(3, 10), generator.randomWord(), generator.randomWord())

        // these are not random and needed for the test
        def outcomeDeclaration = createOutcomeDeclaration(outcomeIdentifier, expectedValue, null, null)
        randomSessions.add(createSession(outcomeDeclaration, expectedValue, userId, contentIdentifier))
        return randomSessions.each { assessmentSessionDao.create(it) }
    }

    def createSessionWithOutcomeDeclarations(def outcomeDeclaration){
        def session = createAssessmentSession()
        //TODO add more than one outcomeDeclaration
        session.assessment.outcomeDeclarations = generator.randomListSetSize(1, {outcomeDeclaration})
        return session
    }
    def createRandomListOfDoubles(def size, def maximum){
        def list = []
        (0..<size).each{
            list.add(generator.randomDouble())
        }
        list.add(maximum)
        return list
    }

    def createSessions(def outcomeDeclaration, def values, def userId, def contentIdentifier){
        List<AssessmentSession> sessions = new ArrayList<>();
        values.each{
            sessions.add(createSession(outcomeDeclaration, it, userId, contentIdentifier))
        }
        return sessions
    }

    def createSession(def outcomeDeclaration, def value, def userId, def contentIdentifier){
        AssessmentSession session = new AssessmentSession(
                assessmentSessionId: generator.randomString(),
                userId: userId,
                contentIdentifier: contentIdentifier
        )
        session.addOutcome(createOutcome(outcomeDeclaration, value))
        return session;
    }

    def createOutcome(AssessmentOutcomeDeclarationDto outcomeDeclarationDto, def value){
        new Outcome(
                outcomeIdentifier: outcomeDeclarationDto.identifier,
                value: value,
                baseType: outcomeDeclarationDto.baseType,
                declarationType: OutcomeDeclarationType.IMPLICIT
        )
    }

    def createOutcomeDeclaration(def outcomeIdentifier, def defaultValue, def min, def max) {
        new AssessmentOutcomeDeclarationDto(
                baseType: generator.randomMember(AssessmentBaseType.values()),
                identifier: outcomeIdentifier,
                defaultValue: createDefaultValue(defaultValue),
                normalMinimum: min,
                normalMaximum: max
        )
    }

    def createDefaultValue(def value){
        new AssessmentDefaultValueDto(
                values: [value.toString()]
        )
    }

    def "when an itemDto is expected will return the expected boolean"(){
       when:
       def expectedBoolean = generator.randomBoolean()
       def itemDto = createItem(expectedBoolean)

       then:
       expectedBoolean == outcomeProcessingService.isValidItemForOutcomeProcessing(itemDto)

       where:
       i << (1..5)
    }

    def "when an assessmentSession has a valid will return true"(){
        when:
        def itemRefIds = generator.randomList(5, {generator.randomString()})
        def namespace = generator.randomString()
        def assessmentSession = generator.createAssessmentSession(namespace, itemRefIds)

        then:
        itemRefIds.each{
            true == outcomeProcessingService.isValidAssessmentSessionForOutcomeProcessing(assessmentSession, it, namespace)
        }

        where:
        i << (1..5)
    }

    def "when an itemSession and assessmentSession is expected will return expected value boolean"(){
        when:
        def expected = generator.randomBoolean()
        def itemDto = createItem(true)
        itemService.create(itemDto)
        def sessionMap = createSessionMap(expected, itemDto)

        then:
        expected == outcomeProcessingService.isUsedForOutcomeProcessing(sessionMap.itemSession, sessionMap.assessmentSession)

        cleanup:
        itemService.delete(itemDto.id)

        where:
        i << (1..5)
    }

    def "will update Rasch Perfomance outcome as expected"(){
        when:
        def testedOutcome = Outcome.CAI_PERCENT_SCORE
        def rawScoreOutcome = new Outcome(Outcome.CAI_POINTS_SCORE, generator.randomDouble(), AssessmentBaseType.FLOAT, OutcomeDeclarationType.IMPLICIT)
        rawScoreOutcome.normalMaximum = generator.randomDouble()
        def assessmentSession = createAssessmentSession(rawScoreOutcome)

        def expectedValue = rawScoreOutcome.value / rawScoreOutcome.normalMaximum

        outcomeProcessingService.calculateSessionOutcome(testedOutcome, assessmentSession, null)

        then:
        expectedValue.round(4) == assessmentSession.getOutcome(testedOutcome).value

        where:
        i << (1..5)
    }

    // Does not check for divide by zero... will be addressed in future ticket.
    def "will update Odds outcome as expected"(){
        when:
        def testedOutcome = Outcome.CAI_ODDS_SUCCESS
        def raschPerformanceOutcome = new Outcome(Outcome.CAI_PERCENT_SCORE, generator.randomDouble(), AssessmentBaseType.FLOAT, OutcomeDeclarationType.IMPLICIT)
        def assessmentSession = createAssessmentSession(raschPerformanceOutcome)

        def expectedValue = raschPerformanceOutcome.value  /  (1 - raschPerformanceOutcome.value)

        outcomeProcessingService.calculateSessionOutcome(testedOutcome, assessmentSession, null)

        then:
        assertEquals(expectedValue, assessmentSession.getOutcome(testedOutcome).value, 0.000001d)

        where:
        i << (1..5)
    }

    def "will update Student Ability outcome as expected"(){
        when:
        def testedOutcome = Outcome.CAI_STUDENT_ABILITY
        def oddsOutcome = new Outcome(Outcome.CAI_ODDS_SUCCESS, generator.randomDouble(), AssessmentBaseType.FLOAT, OutcomeDeclarationType.IMPLICIT)
        def avgDifficultyOutcome = new Outcome(Outcome.CAI_AVG_ITEM_DIFFICULTY, generator.randomDouble(), AssessmentBaseType.FLOAT, OutcomeDeclarationType.IMPLICIT)
        def assessmentSession = createAssessmentSession(oddsOutcome)
        assessmentSession.addOutcome(avgDifficultyOutcome)

        def expectedValue = Math.log(oddsOutcome.value) + avgDifficultyOutcome.value

        outcomeProcessingService.calculateSessionOutcome(testedOutcome, assessmentSession, null)

        then:
        assertEquals(expectedValue, assessmentSession.getOutcome(testedOutcome).value, 0.000001d)

        where:
        i << (1..5)
    }

    def "will update Reported Scale outcome as expected"(){
        when:
        def testedOutcome = Outcome.CAI_REPORTED_SCALE
        def studentAbilityOutcome = new Outcome(Outcome.CAI_STUDENT_ABILITY, generator.randomDouble(), AssessmentBaseType.FLOAT, OutcomeDeclarationType.IMPLICIT)
        def assessmentSession = createAssessmentSession(studentAbilityOutcome)
        def additiveTerm = assessmentSession.assessment.assessmentMetadata.scaleAdditiveTerm
        def multiplicativeTerm = assessmentSession.assessment.assessmentMetadata.scaleMultiplicativeTerm
        assessmentSession.addOutcome(studentAbilityOutcome)

        def studentAbility = studentAbilityOutcome.value

        def expectedValue = additiveTerm + multiplicativeTerm * studentAbility

        outcomeProcessingService.calculateSessionOutcome(testedOutcome, assessmentSession, null)

        then:
        assertEquals(expectedValue, assessmentSession.getOutcome(testedOutcome).value, 0.000001d)

        where:
        i << (1..5)
    }

    def "when assessment is not set as expected Reported Scale outcome is not calculated"(){
        when:
        def testedOutcome = Outcome.CAI_REPORTED_SCALE
        def studentAbilityOutcome = new Outcome(Outcome.CAI_STUDENT_ABILITY, generator.randomDouble(), AssessmentBaseType.FLOAT, OutcomeDeclarationType.IMPLICIT)
        def assessmentSession = invalidateAssessment(createAssessmentSession(studentAbilityOutcome))
        assessmentSession.addOutcome(studentAbilityOutcome)


        outcomeProcessingService.calculateSessionOutcome(testedOutcome, assessmentSession, null)

        then:
        assessmentSession.getOutcome(testedOutcome) == null

        where:
        i << (1..5)
    }

    def "when an assessmentSession and list of itemSessions is expected will add all the expected outcomes to the assessmentSession"(){
        when:
        def itemDto = createItem(true)
        itemService.create(itemDto)
        def sessionMap = createSessionMap(true, itemDto)

        outcomeProcessingService.processAssessmentSessionOutcome(sessionMap.assessmentSession, [sessionMap.itemSession])

        then:
        verifyAllOutcomesHaveBeenCalculated(sessionMap.assessmentSession)

        cleanup:
        itemService.delete(itemDto.id)

        where:
        i << (1..10)
    }

    def invalidateAssessment(AssessmentSession assessmentSession){
        def invalidationType = generator.rand.nextInt(3)
        switch (invalidationType){
            case 0:
                assessmentSession.assessment = null
                break
            case 1:
                assessmentSession.assessment.assessmentMetadata = null
                break
            case 2:
                // either one scaled value is null or both scaled values are null
                if (generator.randomBoolean()) {
                    assessmentSession.assessment.assessmentMetadata.scaleMultiplicativeTerm = null
                } else if (generator.randomBoolean()) {
                    assessmentSession.assessment.assessmentMetadata.scaleAdditiveTerm = null
                } else {
                    assessmentSession.assessment.assessmentMetadata.scaleMultiplicativeTerm = null
                    assessmentSession.assessment.assessmentMetadata.scaleAdditiveTerm = null
                }
                break
        }
        return assessmentSession
    }


    void verifyAllOutcomesHaveBeenCalculated(def assessmentSession){
        assert assessmentSession.getOutcome(Outcome.CAI_POINTS_SCORE) != null
        assert assessmentSession.getOutcome(Outcome.CAI_PERCENT_SCORE) != null
        assert assessmentSession.getOutcome(Outcome.CAI_ODDS_SUCCESS) != null
        assert assessmentSession.getOutcome(Outcome.CAI_AVG_ITEM_DIFFICULTY) != null
        assert assessmentSession.getOutcome(Outcome.CAI_ITEM_DIFFICULTY_COUNT) != null
        assert assessmentSession.getOutcome(Outcome.CAI_STUDENT_ABILITY) != null
        assert assessmentSession.getOutcome(Outcome.CAI_REPORTED_SCALE) != null
    }

    def createSessionMap(def expected, def itemDto){
        def flag = generator.randomBoolean()
        def itemSession = createItemSession(expected ? expected : flag, itemDto)
        def assessmentSession = createAssessmentSession(expected ? expected : !flag, itemSession)
        return [itemSession: itemSession, assessmentSession: assessmentSession]
    }

    def createAssessmentSession(def expected, def itemSession){
        def itemRefId = itemSession.itemRefIdentifier
        if(!expected){
            itemRefId = generator.randomString()
        }
        generator.createAssessmentSession(generator.randomString(), [itemRefId])
    }


    def createItemSession(def expected, def itemDto){
        def outcome = "SCORE"
        def itemId = itemDto.id
        if(!expected) {
            if(generator.randomBoolean()){
                outcome = generator.randomString()
            }else{
                itemId = generator.randomString()
            }
        }
        return generator.createItemSession(generator.randomDouble(), outcome, itemId, itemDto.id)
    }

   def createItem(def isValid){
       def item = new AssessmentItemDto(
               id: generator.randomString(),
               itemMetadata: createItemMetadata(true),
               outcomeDeclarationDtos: createOutcomeDeclarations(true),
               identifier: generator.randomString()
       )
       if(!isValid){
           if(generator.randomBoolean()){
               item.itemMetadata = createItemMetadata(false)
           }else if(generator.randomBoolean()){
               item.outcomeDeclarationDtos = createOutcomeDeclarations(false)
           }else{
                item = null
           }
       }
       return item
   }

    def createItemMetadata(def isValid){
        def itemMetadata = new ItemMetadataDto(
                calibratedDifficulty: generator.randomDouble(),
                itemBankStatusType: ItemBankStatusType.AVAILABLE
        )
        if(!isValid){
            if(generator.randomBoolean()){
                itemMetadata.calibratedDifficulty = null
            }else{
                itemMetadata.itemBankStatusType = getInvalidItemBankStatusType()
            }
        }
        return itemMetadata
    }

    def createOutcomeDeclarations(def isValid){
        def outcomeDeclarations = []
        outcomeDeclarations.add(createOutcomeDeclaration(isValid));
        outcomeDeclarations.addAll(generator.randomList(3, {createOutcomeDeclaration(false)}))
        return outcomeDeclarations
    }

    def createOutcomeDeclaration(def isValid){
        new AssessmentOutcomeDeclarationDto(
                identifier: isValid ? "SCORE" : generator.randomString()
        )
    }

    def getInvalidItemBankStatusType(){
        def itemBankStatus = generator.randomMember(ItemBankStatusType.class.enumConstants)
        if(itemBankStatus == ItemBankStatusType.AVAILABLE){
            return getInvalidItemBankStatusType()
        }
        return itemBankStatus
    }

    def createAssessmentSession(){
        def session = new AssessmentSession(
                assessmentSessionId: generator.randomString(),
                userId: generator.randomString(),
                contentIdentifier: generator.randomString(),
                assessment: generator.randomAssessment(1)
        )
        session.assessment.assessmentMetadata = generator.randomNotNullAssessmentMetadata()
        return session
    }

    def createAssessmentSession(Outcome outcome){
        def session = createAssessmentSession()
        session.addOutcome(outcome)
        return session
    }

    void assertEquals(Double d1, Double d2, Double precision) {
        assert (d1 != null)
        assert (d2 != null)
        assert (Math.abs(d1 - d2) < precision)
    }
}
