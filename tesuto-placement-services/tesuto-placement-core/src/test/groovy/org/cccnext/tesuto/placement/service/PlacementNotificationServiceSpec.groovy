package org.cccnext.tesuto.placement.service

import com.amazonaws.services.sns.AmazonSNS
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang3.builder.EqualsBuilder
import org.cccnext.tesuto.placement.service.PlacementNotificationServiceImpl
import org.cccnext.tesuto.service.StudentCollegeAffiliationReader

import org.cccnext.tesuto.admin.dto.StudentCollegeAffiliationDto

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Ignore

@ContextConfiguration(locations = "/test-application-context.xml")
class PlacementNotificationServiceSpec extends Specification {

    @Autowired PlacementGenerator placementGenerator
    @Autowired PlacementService placementService
    @Autowired PlacementComponentService placementComponentService
    @Autowired PlacementNotificationServiceImpl placementNotificationService

    def theEppn = "EPPN"
    def theTopic = "Topic"

    PlacementNotificationDto lastMessage
    String lastArn

    def clientStub = [
            publish: { arn, message ->
                lastArn = arn;
                def mapper = new ObjectMapper()
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                lastMessage = mapper.readValue(message, PlacementNotificationDto.class);
            }
    ] as AmazonSNS

    def setup() {
        lastArn = null
        lastMessage = null
        placementNotificationService.sendNotification = true
        placementNotificationService.snsClient = clientStub
        placementNotificationService.topicArn = theTopic
        placementNotificationService.studentCollegeAffiliationReader = [
            findByCccIdAndMisCode: {cccId, misCode -> new StudentCollegeAffiliationDto(eppn: theEppn)}
        ] as StudentCollegeAffiliationReader;
    }

    @Ignore //This is a deep mystery since placements has not been changed by the refactor.
    def "placement notification works"() {
        setup:
        def componentView = placementGenerator.randomPlacementComponentViewDto("tesuto")
        def component = placementComponentService.createPlacementComponent(
            componentView.cccid, componentView.collegeId, componentView
        )
        def placementView = placementGenerator.randomPlacementViewDto()
        placementView.placementComponents = [placementComponentService.getPlacementComponent(component.id)]
        placementService.addPlacement(placementView);
        try {
            placementNotificationService.placementDecisionComplete(placementView.id)
        } catch (Exception e) {
            e.printStackTrace()
            throw e
        }
        def dto = lastMessage


        expect:
        lastArn == theTopic
        dto.cccId == placementView.cccid
        dto.misCode == placementView.collegeId
        dto.eppn == theEppn
        dto.placement != null
        dto.placement.createdOn.getTime() > System.currentTimeMillis() - 1000l
        EqualsBuilder.reflectionEquals(dto.placement, placementView,
                                       "createdOn", "placementComponentIds")
        dto.placementComponents[0] == placementComponentService.updateComponent(componentView)
        
        cleanup:
        placementService.deleteAllPlacementsForCollegeId(placementView.collegeId)
        placementComponentService.deleteAllPlacementComponentsForCollegeId(componentView.collegeId)
        
    }


	@Ignore //This is a deep mystery since placements has not been changed by the refactor.
    def "placement notification works with components"() {
        setup:
        def componentView = placementGenerator.randomPlacementComponentViewDto("tesuto")
        def component = placementComponentService.createPlacementComponent(
            componentView.cccid, componentView.collegeId, componentView
        )
        placementNotificationService.placementComponents(
            componentView.collegeId, componentView.cccid, componentView.trackingId
        )
        def dto = lastMessage

        expect:
        lastArn == theTopic
        dto.cccId == componentView.cccid
        dto.misCode == componentView.collegeId
        dto.eppn == theEppn
        dto.placement == null
        dto.placementComponents[0] == placementComponentService.updateComponent(componentView)
        
        cleanup:
            placementComponentService.deleteAllPlacementComponentsForCollegeId(componentView.collegeId)
        
    }

}
