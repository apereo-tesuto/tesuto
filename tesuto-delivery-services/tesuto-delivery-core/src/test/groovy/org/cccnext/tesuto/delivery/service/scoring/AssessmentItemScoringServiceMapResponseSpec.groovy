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
class AssessmentItemScoringMapResponseServiceSpec extends Specification {

    @Shared AssessmentItemScoringServiceImpl scoringService
    @Shared Double DOUBLE_ONE = new Double(1.0);
    // Points awarded
    def setupSpec() {
        ApplicationContext context = new ClassPathXmlApplicationContext("/testApplicationContext.xml")
        scoringService = context.getBean("assessmentItemScoringServiceImpl")
    }

    def "Map_Response Single Identifier Point"() {
        setup:
        AssessmentResponseVarDto responseVarDto = setupResponseVarDto(AssessmentCardinality.SINGLE,AssessmentBaseType.IDENTIFIER,["A":DOUBLE_ONE], 0, 2);
        AssessmentResponseProcessingDto responseProcessingDto = setupResponseProcessingDto("map_response");
        Response candidateResponse = setupResponse(["A"]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == 1
    }

    def "Map_Response Multiple Identifier Point"() {
        setup:
        AssessmentResponseVarDto responseVarDto = setupResponseVarDto(AssessmentCardinality.MULTIPLE,AssessmentBaseType.IDENTIFIER,["A":DOUBLE_ONE,"B":DOUBLE_ONE], 0, 2);
        AssessmentResponseProcessingDto responseProcessingDto = setupResponseProcessingDto("map_response");
        Response candidateResponse = setupResponse(["A","B"]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == 2
    }

    def "Map_Response Single String, UpperCase Scoring Template, Point"() {
        setup:
        AssessmentResponseVarDto responseVarDto = setupResponseVarDto(AssessmentCardinality.SINGLE,AssessmentBaseType.STRING,["Ten":DOUBLE_ONE,"10":DOUBLE_ONE,"ten":DOUBLE_ONE], 0, 2);
        AssessmentResponseProcessingDto responseProcessingDto = setupResponseProcessingDto("MAP_RESPONSE");
        Response candidateResponse = setupResponse(["10"]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == 1
    }

    def "Map_Response Multiple Directed Pair Point Hit Upper Bound"() {
        setup:
        AssessmentResponseVarDto responseVarDto = setupResponseVarDto(AssessmentCardinality.MULTIPLE,AssessmentBaseType.DIRECTED_PAIR,["D M":DOUBLE_ONE,"A C":DOUBLE_ONE,"E X":DOUBLE_ONE], 0, 2);
        AssessmentResponseProcessingDto responseProcessingDto = setupResponseProcessingDto("map_response");
        Response candidateResponse = setupResponse(["D M","A C","E X"]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == 2
    }
    
     def "Map_Response Multiple Directed Pair Point hit lower bound"() {
        setup:
        AssessmentResponseVarDto responseVarDto = setupResponseVarDto(AssessmentCardinality.MULTIPLE,AssessmentBaseType.DIRECTED_PAIR,["D M":DOUBLE_ONE,"A C":DOUBLE_ONE,"E X":DOUBLE_ONE], 1, 2);
        AssessmentResponseProcessingDto responseProcessingDto = setupResponseProcessingDto("map_response");
        Response candidateResponse = setupResponse([""]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == 1
    }


    // No Points Awarded
    def "Map_Response Single Identifier No Point due to incorrect response"() {
        setup:
        AssessmentResponseVarDto responseVarDto = setupResponseVarDto(AssessmentCardinality.SINGLE,AssessmentBaseType.IDENTIFIER,["A":DOUBLE_ONE], 0, 2);
        AssessmentResponseProcessingDto responseProcessingDto = setupResponseProcessingDto("map_response");
        Response candidateResponse = setupResponse(["B"]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == 0
    }

    def "Map_Response Multiple Identifier One Point due to one incorrect response"() {
        setup:
        AssessmentResponseVarDto responseVarDto = setupResponseVarDto(AssessmentCardinality.MULTIPLE,AssessmentBaseType.IDENTIFIER,["A":DOUBLE_ONE,"B":DOUBLE_ONE], 0, 2);
        AssessmentResponseProcessingDto responseProcessingDto = setupResponseProcessingDto("map_response");
        Response candidateResponse = setupResponse(["A","C"]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == 1
    }

    def "Map_Response Single String null result due to empty response"() {
        setup:
        AssessmentResponseVarDto responseVarDto = setupResponseVarDto(AssessmentCardinality.SINGLE,AssessmentBaseType.STRING,["Ten":DOUBLE_ONE,"10":DOUBLE_ONE,"ten":DOUBLE_ONE], 0, 2);
        AssessmentResponseProcessingDto responseProcessingDto = setupResponseProcessingDto("map_response");
        Response candidateResponse = setupResponse([]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == null
    }


    def "Map_Response Multiple Directed Pair One Point due to bad response"() {
        setup:
        AssessmentResponseVarDto responseVarDto = setupResponseVarDto(AssessmentCardinality.MULTIPLE,AssessmentBaseType.DIRECTED_PAIR,["D M":DOUBLE_ONE,"A C":DOUBLE_ONE, "E X":-DOUBLE_ONE], 0, 2);
        AssessmentResponseProcessingDto responseProcessingDto = setupResponseProcessingDto("map_response");
        Response candidateResponse = setupResponse(["D M","A C","E X"]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == 1
    }
    
    def "Blank Response Processing Template Null Result"() {
        setup:
        AssessmentResponseVarDto responseVarDto = setupResponseVarDto(AssessmentCardinality.MULTIPLE,AssessmentBaseType.DIRECTED_PAIR,["D M":DOUBLE_ONE,"A C":DOUBLE_ONE, "E X":-DOUBLE_ONE], 0, 2);
        AssessmentResponseProcessingDto responseProcessingDto = setupResponseProcessingDto("");
        Response candidateResponse = setupResponse(["D M","A C","E X"]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == null
    }
    
    def "Map_Response Multiple Directed Pair null result due to no response"() {
        setup:
        AssessmentResponseVarDto responseVarDto = setupResponseVarDto(AssessmentCardinality.MULTIPLE,AssessmentBaseType.DIRECTED_PAIR,["D M":DOUBLE_ONE,"A C":DOUBLE_ONE], 0, 2);
        AssessmentResponseProcessingDto responseProcessingDto = setupResponseProcessingDto("map_response");
        Response candidateResponse = setupResponse([]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == null
    }
    
    def "Map_Response Multiple Directed Pair Three Points no upper bound"() {
        setup:
        AssessmentResponseVarDto responseVarDto = setupResponseVarDto(AssessmentCardinality.MULTIPLE,AssessmentBaseType.DIRECTED_PAIR,["D M":DOUBLE_ONE,"A C":DOUBLE_ONE, "E X":DOUBLE_ONE], 0, null);
        AssessmentResponseProcessingDto responseProcessingDto = setupResponseProcessingDto("map_response");
        Response candidateResponse = setupResponse(["D M","A C","E X"]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == 3
    }
    
    def "Map_Response Multiple Directed Pair null result due to no response no lower bound"() {
        setup:
        AssessmentResponseVarDto responseVarDto = setupResponseVarDto(AssessmentCardinality.MULTIPLE,AssessmentBaseType.DIRECTED_PAIR,["D M":DOUBLE_ONE,"A C":DOUBLE_ONE], null, 2);
        AssessmentResponseProcessingDto responseProcessingDto = setupResponseProcessingDto("map_response");
        Response candidateResponse = setupResponse([]);

        expect:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse) == null
    }

    def "Map_Response with invalid template"() {
        setup:
        AssessmentResponseVarDto responseVarDto = setupResponseVarDto(AssessmentCardinality.SINGLE,AssessmentBaseType.IDENTIFIER,["A":DOUBLE_ONE], 0, 2);
        AssessmentResponseProcessingDto responseProcessingDto = setupResponseProcessingDto("responseProcessingDtofoo");
        Response candidateResponse = setupResponse(["A"]);

        when:
        scoringService.scoreAssessmentItem(responseVarDto, responseProcessingDto, candidateResponse)

        then:
        thrown(UnsupportedScoringTemplateException)
    }

    // Helper Methods
    def setupResponseVarDto(AssessmentCardinality cardinality, AssessmentBaseType baseType, 
                            Map<String,Double> responseMap, Double lowerBound, Double upperBound) {
        AssessmentItemResponseMappingDto mappingResponseDto = new AssessmentItemResponseMappingDto(mapping: responseMap, lowerBound:lowerBound, upperBound: upperBound, defaultValue: null)
        return new AssessmentResponseVarDto(identifier: "RESPONSE", cardinality: cardinality, baseType: baseType, mapping: mappingResponseDto)
    }

    def setupResponseProcessingDto(String template) {
        return new AssessmentResponseProcessingDto(template: template)
    }

    def setupResponse(List<String> candidateResponseValues) {
        return new Response(responseId: "RESPONSE", values: candidateResponseValues)
    }
    

}
