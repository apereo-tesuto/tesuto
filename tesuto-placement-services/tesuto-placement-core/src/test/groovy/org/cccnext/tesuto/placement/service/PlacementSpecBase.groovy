package org.cccnext.tesuto.placement.service

import org.cccnext.tesuto.exception.ValidationException
import org.cccnext.tesuto.placement.model.Course
import org.cccnext.tesuto.placement.model.Discipline
import org.cccnext.tesuto.placement.model.DisciplineSequenceCourse
import org.cccnext.tesuto.placement.model.PlacementRevision
import org.cccnext.tesuto.placement.view.*
import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import spock.lang.Shared
import spock.lang.Specification


class PlacementSpecBase extends Specification {

    @Shared ApplicationContext context
    @Shared SubjectAreaServiceAdapter service
    @Shared SubjectAreaGenerator generator
    @Shared courseIdsToDelete = []
    @Shared courseHistoryIdsToDelete = []
    @Shared disciplineIdsToDelete = []
    @Shared misCodesToDelete = []
    @Shared DisciplineViewDto aDiscipline
    @Shared DisciplineSequenceViewDto aSequence
    @Shared PlacementAuditService placementAuditService
    @Shared SubjectAreaService subjectAreaService
    @Shared DisciplineAssembler disciplineAssembler
    @Shared DisciplineSequenceAssembler sequenceAssembler
    @Shared CourseAssembler courseAssembler
    @Shared CompetencyGroupGenerator competencyGenerator
    @Shared competencyGroupsToDelete = []
    @Shared CompetencyGroupService serviceCompetencyGroup
    @Shared CompetencyGroupAssembler competencyGroupAssembler;
    @Shared courseId

    def setupSpec() {
        context = new ClassPathXmlApplicationContext("/test-application-context.xml")
        service = context.getBean("subjectAreaServiceAdapter")
        placementAuditService = context.getBean("placementAuditService")
        subjectAreaService = context.getBean("subjectAreaService")
        
        disciplineAssembler = context.getBean("disciplineAssembler")
        sequenceAssembler = context.getBean("disciplineSequenceAssembler")
        courseAssembler = context.getBean("courseAssembler")
		competencyGroupAssembler = context.getBean("competencyGroupAssembler")
		
		generator = new SubjectAreaGenerator(subjectAreaService)

        aDiscipline = disciplineAssembler.assembleDto(generator.getADiscipline())
        aSequence = sequenceAssembler.assembleDto(generator.getASequence())
        competencyGenerator = new CompetencyGroupGenerator(context.getBean("competencyGroupService"), subjectAreaService)

        serviceCompetencyGroup = competencyGenerator.competencyGroupService
        
        courseId = competencyGenerator.getACourse().courseId
        courseIdsToDelete.add(courseId)
        disciplineIdsToDelete.add(competencyGenerator.getADiscipline().disciplineId)
    }


    def cleanupSpec() {
      try {
        competencyGroupsToDelete.each {
            serviceCompetencyGroup.delete(it)
        }
        competencyGroupsToDelete.each {
            placementAuditService.deleteAuditRows("history_competency_group", "competency_group_id", it)
        }
        courseIdsToDelete.each { service.deleteCourse(it) }
        courseIdsToDelete.each { placementAuditService.deleteAuditRows("history_course", "course_id", it) }
        courseHistoryIdsToDelete.each { placementAuditService.deleteAuditRows("history_course", "course_id", it) }

        disciplineIdsToDelete << aDiscipline.disciplineId
        disciplineIdsToDelete.each { 
			cleanAttributes(it)
			generator.service.deleteDiscipline(it)
			
		}
        disciplineIdsToDelete.each {
            placementAuditService.deleteAuditRows("history_college_discipline", "college_discipline_id", it)
        }
        context.close()
      } catch (Exception ex) {
        ex.printStackTrace()
        throw ex;
      }
    }
	
	def cleanAttributes(disciplineId) {
		def discipline = service.getDiscipline(disciplineId)
		if(discipline.competencyAttributes != null) {
			placementAuditService.deleteAuditRows("history_competency_attributes", "competency_attribute_id", discipline.competencyAttributes.competencyAttributeId)
			
		}
	}
}
