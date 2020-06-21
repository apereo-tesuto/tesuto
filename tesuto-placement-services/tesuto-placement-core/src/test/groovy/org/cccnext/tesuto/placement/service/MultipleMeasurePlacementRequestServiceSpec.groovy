package org.cccnext.tesuto.placement.service;

import org.cccnext.tesuto.util.test.Generator

import org.cccnext.tesuto.message.service.MessagePublisher
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto
import org.cccnext.tesuto.content.service.CompetencyMapDisciplineReader
import org.cccnext.tesuto.user.service.StudentReader
import org.cccnext.tesuto.content.dto.competency.CompetencyMapDisciplineDto
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto

import spock.lang.Shared
import spock.lang.Specification

class MultipleMeasurePlacementRequestServiceSpec  extends Specification {

    MessagePublisher multipleMeasurePlacementRequestor
    CompetencyMapDisciplineReader competencyMapDisciplineService
    StudentReader studentService
    Generator generator
    
    MultipleMeasurePlacementRequestServiceImpl service 

    def setup() {
        multipleMeasurePlacementRequestor = Mock()
        competencyMapDisciplineService = Mock()
        studentService = Mock()
        service = new MultipleMeasurePlacementRequestServiceImpl(
            multipleMeasurePlacementRequestor: multipleMeasurePlacementRequestor,
            competencyMapDisciplineService: competencyMapDisciplineService,
            studentService: studentService
        )
        generator = new Generator()
    }    

    def  "Trigger is activate with cccid and new placement"() {
       given:
           def cccid = generator.randomId()
           def newPlacementsOnly = generator.randomBoolean()
           def colleges = ["ZZ1", "ZZ2"]
       when:
           service.requestPlacements(cccid, newPlacementsOnly)
        then:
          1 * studentService.getCollegesAppliedTo(cccid) >> colleges
          1 * multipleMeasurePlacementRequestor.sendMessage({ PlacementEventInputDto p ->
             p?.cccid == cccid && p?.processOnlyNewPlacements == newPlacementsOnly && p?.collegeMisCodes.containsAll(colleges) && p?.competencyMapDisciplines.isEmpty()  
          }) 
    }
    
    def  "Trigger is activate with cccid, collegeId and new placement"() {
       given:
           def cccid = generator.randomId()
           def newPlacementsOnly = generator.randomBoolean()
           def college = "ZZ1"
       when:
           service.requestPlacements(cccid, college, newPlacementsOnly)
        then:
          0 * studentService.getCollegesAppliedTo(cccid)
          1 * multipleMeasurePlacementRequestor.sendMessage(_) 
    }
    
    def  "Trigger is activate with cccid, collegeId, subjectArea and new placement"() {
       given:
           def cccid = generator.randomId()
           def newPlacementsOnly = generator.randomBoolean()
           def subjectArea = "English"
           def college = "ZZ1"
       when:
           service.requestPlacements(cccid, college, subjectArea, newPlacementsOnly)
        then:
          0 * studentService.getCollegesAppliedTo(cccid)
          1 * competencyMapDisciplineService.read(subjectArea) >> new CompetencyMapDisciplineDto( disciplineName: "ENG")
          1 * multipleMeasurePlacementRequestor.sendMessage(_) 
    }
}