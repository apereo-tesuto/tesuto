package org.cccnext.tesuto.content.dto

import org.cccnext.tesuto.content.dto.AssessmentComponentDto
import org.cccnext.tesuto.content.dto.AssessmentDtoGenerator
import org.cccnext.tesuto.content.dto.AssessmentPartDto
import org.cccnext.tesuto.content.dto.metadata.AssessmentMetadataDto
import org.cccnext.tesuto.content.dto.metadata.DeliveryTypeMetadataDto
import spock.lang.Shared
import spock.lang.Specification

import java.util.stream.Stream

public class AssessmentDtoSpec extends Specification {

    @Shared AssessmentDtoGenerator generator = new AssessmentDtoGenerator()

    AssessmentComponentDto getRandomDescendant(Stream<AssessmentComponentDto> components) {
        def component = generator.randomStreamMember(components)
        if (generator.randomBoolean() || component.children.count() == 0) {
            return component
        } else {
            return getRandomDescendant(component.children)
        }
    }

    def "AssessmentDto.isPaper return the correct boolean"() {
        when:
        def expected = generator.randomBoolean()
        def assessmentDto = createAssessmentForExpectedPaper(expected)

        then:
        assessmentDto.isPaper() == expected

        where:
        i << (1..10)
    }

    def "AssessmentDto.isOnline return the correct boolean"() {
        when:
        def expected = false
        def assessmentDto = createAssessmentForExpectedPaper(expected)

        then:
        assessmentDto.isOnline() == expected

        where:
        i << (1..10)
    }

    AssessmentDto createAssessmentForExpectedPaper(def expected){
        AssessmentMetadataDto assessmentMetadataDto = generator.randomNotNullAssessmentMetadata()
        assessmentMetadataDto.deliveryType = new DeliveryTypeMetadataDto()

        assessmentMetadataDto.deliveryType.paper = createYesNoTrueFalseStringForPaperExpected(expected)
        assessmentMetadataDto.deliveryType.online = createYesNoTrueFalseStringForOnlineExpected(expected)

        AssessmentDto assessmentDto = generator.randomAssessment()
        assessmentDto.assessmentMetadata = assessmentMetadataDto

        return assessmentDto
    }

    String createYesNoTrueFalseStringForPaperExpected(boolean expected){
        def trueStringPool = ["Yes", "True", "yes", "true", "yEs", "tRue"]
        def falseStringPool = ["No", "False", "no", "nO", "fAlse", "false", null, generator.randomString()]

        if(expected){
            return trueStringPool[generator.rand.nextInt(trueStringPool.size())]
        }else {
            return falseStringPool[generator.rand.nextInt(falseStringPool.size())]
        }
    }

    String createYesNoTrueFalseStringForOnlineExpected(boolean expected){
        def trueStringPool = ["Yes", "True", "yes", "true", "yEs", "tRue", null, generator.randomString()]
        def falseStringPool = ["No", "False", "no", "nO", "fAlse", "false"]

        if(expected){
            return trueStringPool[generator.rand.nextInt(trueStringPool.size())]
        }else {
            return falseStringPool[generator.rand.nextInt(falseStringPool.size())]
        }
    }

    def "AssessmentDto.getComponent returns the correct component"() {
        when:
        AssessmentComponentDto component = getRandomDescendant(assessment.children)
        Optional<AssessmentComponentDto> retrievedById = assessment.getComponent(component.id)

        then:
        retrievedById.isPresent()
        //Most of the dto's don't have equals defined, so just compare id's
        retrievedById.get().id == component.id

        where: assessment << (1..5).collect { generator.randomAssessment() }
    }


    def "AssessmentDto.getComponent returns None for nonexistent Ids"() {
        expect: !assessment.getComponent(generator.randomString()).isPresent()
        where: assessment << (1..5).collect { generator.randomAssessment() }
    }


    def "AssessmentDto.getParent returns the correct parent"() {
        when:
        AssessmentComponentDto component = getRandomDescendant(assessment.children)
        Optional<AssessmentComponentDto> parent = assessment.getParent(component.id)

        then:
        component instanceof AssessmentPartDto || parent.isPresent()
        parent.empty() || parent.get().isParentOf(component.id)

        where: assessment << (1..5).collect { generator.randomAssessment() }
    }

    def "AssessmentDto.getParent returns None for nonexistent Ids"() {
        expect: !assessment.getParent(generator.randomString()).isPresent()
        where: assessment << (1..5).collect { generator.randomAssessment() }
    }

}
