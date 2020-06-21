package org.cccnext.tesuto.delivery.service.scoring

import org.cccnext.tesuto.content.dto.AssessmentBaseType
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification


import org.cccnext.tesuto.delivery.model.internal.Response
import org.cccnext.tesuto.content.dto.item.*


//
// Created by jcorbin on 12/29/15.
//
class AssessmentItemScoringServiceSpec extends Specification {

    @Shared AssessmentItemScoringServiceImpl scoringService
    AssessmentCorrectResponseDto correctResponseDto
    AssessmentResponseVarDto responseVarDto
    Response candidateResponse
    AssessmentResponseProcessingDto responseProcessingDto

    // Points awarded
    def setupSpec() {
        ApplicationContext context = new ClassPathXmlApplicationContext("/testApplicationContext.xml")
        scoringService = context.getBean("assessmentItemScoringServiceImpl")
    }

    def "Match_Correct Single Identifier Point"() {
        setup:
        setupResponseVarDto(AssessmentCardinality.SINGLE,AssessmentBaseType.IDENTIFIER,["A"]);
        setupResponseProcessingDto("match_correct");
        setupResponse(["A"]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == 1
    }

    def "Match_Correct Multiple Identifier Point"() {
        setup:
        setupResponseVarDto(AssessmentCardinality.MULTIPLE,AssessmentBaseType.IDENTIFIER,["A","B"]);
        setupResponseProcessingDto("match_correct");
        setupResponse(["A","B"]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == 1
    }

    def "Match_Correct Multiple Identifier Point Out of Order"() {
        setup:
        setupResponseVarDto(AssessmentCardinality.MULTIPLE,AssessmentBaseType.IDENTIFIER,["B","A"]);
        setupResponseProcessingDto("match_correct");
        setupResponse(["A","B"]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == 1
    }

    def "Match_Correct Single String, UpperCase Scoring Template, Point"() {
        setup:
        setupResponseVarDto(AssessmentCardinality.MULTIPLE,AssessmentBaseType.STRING,["Ten","10","ten"]);
        setupResponseProcessingDto("MATCH_CORRECT");
        setupResponse(["10"]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == 0
    }

    def "Match_Correct Multiple Directed Pair Point"() {
        setup:
        setupResponseVarDto(AssessmentCardinality.MULTIPLE,AssessmentBaseType.DIRECTED_PAIR,["D M","A C","E X"]);
        setupResponseProcessingDto("match_correct");
        setupResponse(["D M","E X","A C"]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == 1
    }

    def "Match_Correct Multiple Directed Pair Point - not all matches"() {
        setup:
        setupResponseVarDto(AssessmentCardinality.MULTIPLE,AssessmentBaseType.DIRECTED_PAIR,["D M","A C","E X"]);
        setupResponseProcessingDto("match_correct");
        setupResponse(["D M","A C"]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == 0
    }


    // No Points Awarded
    def "Match_Correct Single Identifier No Point due to incorrect response"() {
        setup:
        setupResponseVarDto(AssessmentCardinality.SINGLE,AssessmentBaseType.IDENTIFIER,["A"]);
        setupResponseProcessingDto("match_correct");
        setupResponse(["B"]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == 0
    }

    def "Match_Correct Multiple Identifier No Point due to one incorrect response"() {
        setup:
        setupResponseVarDto(AssessmentCardinality.MULTIPLE,AssessmentBaseType.IDENTIFIER,["A","B"]);
        setupResponseProcessingDto("match_correct");
        setupResponse(["A","C"]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == 0
    }

    def "Match_Correct Single String null result due to empty response"() {
        setup:
        setupResponseVarDto(AssessmentCardinality.SINGLE,AssessmentBaseType.STRING,["Ten","10","ten"]);
        setupResponseProcessingDto("match_correct");
        setupResponse([]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == null
    }


    def "Match_Correct Multiple Directed Pair No Point due to extra response"() {
        setup:
        setupResponseVarDto(AssessmentCardinality.MULTIPLE,AssessmentBaseType.DIRECTED_PAIR,["D M","A C"]);
        setupResponseProcessingDto("match_correct");
        setupResponse(["D M","A C","E X"]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == 0
    }
    
    def "Match_Correct Multiple Directed Pair null result due to no response"() {
        setup:
        setupResponseVarDto(AssessmentCardinality.MULTIPLE,AssessmentBaseType.DIRECTED_PAIR,["D M","A C"]);
        setupResponseProcessingDto("match_correct");
        setupResponse([]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == null
    }

    def "Match_Correct with invalid template"() {
        setup:
        setupResponseVarDto(AssessmentCardinality.SINGLE,AssessmentBaseType.IDENTIFIER,["A"]);
        setupResponseProcessingDto("foo");
        setupResponse(["A"]);

        when:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse)

        then:
        thrown(UnsupportedScoringTemplateException)
    }

    // Helper Methods
    def setupResponseVarDto(AssessmentCardinality cardinality, AssessmentBaseType baseType, List<String> correctResponseValues) {
        correctResponseDto = new AssessmentCorrectResponseDto(values: correctResponseValues)
        responseVarDto = new AssessmentResponseVarDto(identifier: "RESPONSE", cardinality: cardinality, baseType: baseType, correctResponse: correctResponseDto)
    }

    def setupResponseProcessingDto(String template) {
        responseProcessingDto = new AssessmentResponseProcessingDto(template: template)
    }

    def setupResponse(List<String> candidateResponseValues) {
        candidateResponse = new Response(responseId: "RESPONSE", values: candidateResponseValues)
    }


}
