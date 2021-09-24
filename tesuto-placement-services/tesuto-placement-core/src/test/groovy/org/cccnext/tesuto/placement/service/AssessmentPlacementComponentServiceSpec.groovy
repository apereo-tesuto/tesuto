package org.cccnext.tesuto.placement.service;

import spock.lang.Specification;

import org.cccnext.tesuto.placement.dto.AssessmentCompletePlacementInputDto
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto
import org.cccnext.tesuto.placement.model.CompetencyGroup
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import org.cccnext.tesuto.placement.service.AssessmentPlacementComponentService
import org.cccnext.tesuto.placement.dto.AssessmentCompletePlacementInputDto
import org.cccnext.tesuto.placement.dto.PlacementEventInputDto
import org.cccnext.tesuto.placement.service.AssessmentPlacementComponentService

class AssessmentPlacementComponentServiceSpec extends Specification {

    @Shared generator
    @Shared CompetencyAttributesGenerator attributeGenerator
    @Shared ApplicationContext context
    @Shared DisciplineAssembler disciplineAssembler
    @Shared CourseAssembler courseAssembler
    @Shared CompetencyGroupAssembler competencyGroupAssembler
    @Shared DisciplineSequenceAssembler sequenceAssembler
    @Shared CompetencyAttributesAssembler attributeAssembler
    @Shared AssessmentPlacementComponentService service

    def setupSpec() {
        this.generator = new SubjectAreaGenerator(null)
        this.attributeGenerator = new CompetencyAttributesGenerator()
        this.context = new ClassPathXmlApplicationContext("test-application-context.xml")
        this.disciplineAssembler = context.getBean("disciplineAssembler")
        this.courseAssembler = context.getBean("courseAssembler")
        this.sequenceAssembler = context.getBean("disciplineSequenceAssembler")
        this.attributeAssembler = context.getBean("competencyAttributesAssembler");
        this.service = context.getBean("assessmentPlacementComponentService")
        this.service.placementDuplicationCheckService = Mock (PlacementDuplicationCheckService) 
    }

    def PlacementEventInputDto makeAssessmentPlacementEventInputDto() { 
    	AssessmentCompletePlacementInputDto eventInputDto = new AssessmentCompletePlacementInputDto();
    	eventInputDto.cccid = "A123456";
    	eventInputDto.collegeMisCodes = ["ZZ1"]
 		eventInputDto.subjectArea =  generator.randomDisciplineViewDto();
	    eventInputDto.competencyMapDisciplines = ["ENGLISH"];
		eventInputDto.processOnlyNewPlacements =true;
		eventInputDto.trackingId = UUID.randomUUID().toString()
        eventInputDto.studentAbility = generator.randomDouble(-1,3.0)
		return eventInputDto;
    }
    
	def  "After recieving a request for processPlacementEvent, event is processed"() { 
	    //TODO add multiple addition support objects
		when:  def componentEvents = service.processPlacementEvent(eventInputDto)
		then:  componentEvents.size() >= 0
		where: eventInputDto << [makeAssessmentPlacementEventInputDto()]
	}
	
	def  "After recieving EventDataInput Placement Component is Create"() { 
	    //TODO add multiple addition support objects
		when:  def componentEvents = service.processPlacementEvent(eventInputDto)
		then:  componentEvents.size() >= 0
		where: eventInputDto << [makeAssessmentPlacementEventInputDto()]
	}
	

}
