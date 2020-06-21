package org.cccnext.tesuto.delivery.service.scoring.psychometrics

import org.cccnext.tesuto.delivery.model.internal.Outcome
import org.cccnext.tesuto.delivery.model.internal.enums.OutcomeDeclarationType
import org.cccnext.tesuto.content.dto.AssessmentBaseType
import org.cccnext.tesuto.delivery.service.PsychometricsCalculationService;
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.cccnext.tesuto.util.test.SessionGenerator
import spock.lang.Shared
import spock.lang.Specification
import uk.ac.ed.ph.jqtiplus.value.BaseType

class PsychometricsCalculationServiceSpec extends Specification {

    @Shared
    PsychometricsCalculationService psychometricsCalculationService
    @Shared SessionGenerator generator

    def setupSpec() {
        generator = new SessionGenerator()
        ApplicationContext context = new ClassPathXmlApplicationContext("/testApplicationContext.xml")
        psychometricsCalculationService = context.getBean("psychometricsCalculationService")
    }

    def "calculates expected average student item difficulty"(){
        when:
        def averageValue = generator.randomDouble()
        def previousOutcome = new Outcome(Outcome.CAI_AVG_ITEM_DIFFICULTY, averageValue, AssessmentBaseType.FLOAT, OutcomeDeclarationType.IMPLICIT)
        def countValue = generator.rand.nextInt(5)
        def countOutcome = new Outcome(Outcome.CAI_ITEM_DIFFICULTY_COUNT, countValue, AssessmentBaseType.FLOAT, OutcomeDeclarationType.IMPLICIT)
        def doubleList = generator.randomList(5, {generator.randomDouble()})

        def expectedAverage = (doubleList.sum() + previousOutcome.value * countOutcome.value) / (doubleList.size() + countOutcome.value)

        then:
        assertEquals(expectedAverage, psychometricsCalculationService.calculateAverageStudentItemDifficulty(doubleList, previousOutcome.getValue(), countOutcome.getValue()), 0.0001d)

        where:
        i << (1..10)
    }

    def "when an edge case occurs calculatePerformanceAsPercentage returns the expected value"(){
        when:
        def expectedValue = createExpectedEdgeCase()
        def mapOfValues = createMapOfExpectedInputValues(expectedValue)

        then:
        assertEquals(expectedValue, psychometricsCalculationService.calculatePerformanceAsPercentage(mapOfValues.pointsEarned, mapOfValues.maxPoints) , 0.0001d)

        where:
        i << (1..10)
    }

    def "calculatePerformanceAsPercentage returns the expected value"(){
        when:
        def expectedValue = createExpectedValue()
        def mapOfValues = createMapOfExpectedInputValues(expectedValue)

        then:
        assertEquals(expectedValue, psychometricsCalculationService.calculatePerformanceAsPercentage(mapOfValues.pointsEarned, mapOfValues.maxPoints) , 0.0001d)

        where:
        i << (1..10)
    }

    def createExpectedValue(){
        return generator.randomBoolean() ? createExpectedEdgeCase() : generator.randomDouble()
    }

    def createExpectedEdgeCase(){
        return generator.randomBoolean() ? 0.005 : 0.995
    }

    def createMapOfExpectedInputValues(def value){
        def pointsEarned
        def maxPoints
        if(value == 0.995d) {
            maxPoints = generator.randomBoolean() ? generator.randomDouble() : 0.0d
            pointsEarned = (maxPoints != 0.0d) ? maxPoints : generator.randomDouble()
        }else if (value == 0.005) {
            maxPoints = generator.randomDouble()
            pointsEarned = 0.0d
        }else{
            maxPoints = generator.randomDouble()
            pointsEarned = value * maxPoints
        }

        return [maxPoints: maxPoints, pointsEarned:pointsEarned]
    }

    void assertEquals(Double d1, Double d2, Double precision){
        assert (d1 != null)
        assert (d2 != null)
        assert (Math.abs(d1 - d2) < precision)
    }
}